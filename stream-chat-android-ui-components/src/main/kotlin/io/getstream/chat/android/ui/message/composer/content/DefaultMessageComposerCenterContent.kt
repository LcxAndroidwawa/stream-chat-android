/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.chat.android.ui.message.composer.content

import android.content.Context
import android.os.Build
import android.text.Editable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.ChannelCapabilities
import io.getstream.chat.android.common.composer.MessageComposerState
import io.getstream.chat.android.common.state.Reply
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.chat.android.ui.ChatUI
import io.getstream.chat.android.ui.R
import io.getstream.chat.android.ui.common.extensions.internal.createStreamThemeWrapper
import io.getstream.chat.android.ui.common.extensions.internal.streamThemeInflater
import io.getstream.chat.android.ui.common.style.setTextStyle
import io.getstream.chat.android.ui.databinding.StreamUiMessageComposerDefaultCenterContentBinding
import io.getstream.chat.android.ui.message.composer.MessageComposerContext
import io.getstream.chat.android.ui.message.composer.MessageComposerView
import io.getstream.chat.android.ui.message.composer.MessageComposerViewStyle
import io.getstream.chat.android.ui.message.composer.attachment.AttachmentPreviewFactoryManager
import io.getstream.chat.android.ui.message.composer.attachment.AttachmentPreviewViewHolder

/**
 * Represents the default content shown at the center of [MessageComposerView].
 */
@ExperimentalStreamChatApi
public class DefaultMessageComposerCenterContent : FrameLayout, MessageComposerContent {
    /**
     * Generated binding class for the XML layout.
     */
    private lateinit var binding: StreamUiMessageComposerDefaultCenterContentBinding

    /**
     * The style for [MessageComposerView].
     */
    private lateinit var style: MessageComposerViewStyle

    /**
     * Text change listener invoked each time after text was changed.
     */
    public var textInputChangeListener: (String) -> Unit = {}

    /**
     * Click listener for the clear input button.
     */
    public var clearInputButtonClickListener: () -> Unit = {}

    /**
     * Click listener for the remove attachment button.
     */
    public var attachmentRemovalListener: (Attachment) -> Unit = {}

    /**
     * Adapter used to render attachments previews list.
     */
    private val attachmentsAdapter: AttachmentPreviewAdapter by lazy {
        AttachmentPreviewAdapter(ChatUI.attachmentPreviewFactoryManager) { attachmentRemovalListener(it) }
    }

    public constructor(context: Context) : this(context, null)

    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr
    ) {
        init()
    }

    /**
     * Initializes the initial layout of the view.
     */
    private fun init() {
        binding = StreamUiMessageComposerDefaultCenterContentBinding.inflate(streamThemeInflater, this)
        binding.messageEditText.doAfterTextChanged { editable: Editable? ->
            textInputChangeListener(editable?.toString() ?: "")
        }
        binding.clearCommandButton.setOnClickListener {
            clearInputButtonClickListener()
        }
        binding.attachmentsRecyclerView.adapter = attachmentsAdapter
    }

    /**
     * Initializes the content view with with [MessageComposerContext].
     *
     * @param messageComposerContext The context of this [MessageComposerView].
     */
    override fun attachContext(messageComposerContext: MessageComposerContext) {
        this.style = messageComposerContext.style

        binding.messageInputContainer.background = style.messageInputBackgroundDrawable
        binding.messageEditText.isVerticalScrollBarEnabled = style.messageInputScrollbarEnabled
        binding.messageEditText.isVerticalFadingEdgeEnabled = style.messageInputScrollbarFadingEnabled
        binding.messageEditText.setTextStyle(style.messageInputTextStyle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            style.messageInputCursorDrawable?.let {
                binding.messageEditText.textCursorDrawable = it
            }
        }
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        binding.messageEditText.apply {
            val currentValue = text.toString()
            val newValue = state.inputValue
            if (newValue != currentValue) {
                setText(state.inputValue)
                // placing cursor at the end of the text
                setSelection(length())
            }
        }

        val canSendMessage = state.ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)

        if (canSendMessage) {
            binding.messageEditText.isEnabled = true
            binding.messageEditText.hint = context.getString(R.string.stream_ui_message_input_hint)
            binding.messageEditText.maxLines = style.messageInputMaxLines
        } else {
            binding.messageEditText.isEnabled = false
            binding.messageEditText.hint = context.getString(R.string.stream_ui_message_cannot_send_messages_hint)
            binding.messageEditText.maxLines = 1
        }

        attachmentsAdapter.setAttachments(state.attachments)

        val action = state.action
        if (action is Reply) {
            val message = action.message
            binding.messageReplyView.setMessage(
                message,
                ChatUI.currentUserProvider.getCurrentUser()?.id == message.user.id,
                null,
            )
            binding.messageReplyView.isVisible = true
        } else {
            binding.messageReplyView.isVisible = false
        }

        binding.selectedAttachmentsContainer.isVisible = state.attachments.isNotEmpty()
    }
}

/**
 * [RecyclerView.Adapter] responsible for displaying attachment previews in a RecyclerView.
 *
 * @param factoryManager A manager for registered attachment preview factories.
 * @param attachmentRemovalListener Click listener for the remove attachment button.
 */
private class AttachmentPreviewAdapter(
    private val factoryManager: AttachmentPreviewFactoryManager,
    private val attachmentRemovalListener: (Attachment) -> Unit,
) : RecyclerView.Adapter<AttachmentPreviewViewHolder>() {

    /**
     * The attachments that will be displayed in the list.
     */
    private val attachments = mutableListOf<Attachment>()

    /**
     * Replaces all the items and notifies that the data set has changed.
     *
     * @param attachments
     */
    fun setAttachments(attachments: List<Attachment>) {
        this.attachments.clear()
        this.attachments.addAll(attachments)
        notifyDataSetChanged()
    }

    /**
     * Creates and instantiates a new instance of [AttachmentPreviewViewHolder].
     *
     * @param parentView The parent container.
     * @return An instance of attachment preview ViewHolder.
     */
    override fun onCreateViewHolder(parentView: ViewGroup, viewType: Int): AttachmentPreviewViewHolder {
        return factoryManager.onCreateViewHolder(parentView, viewType, attachmentRemovalListener)
    }

    /**
     * Binds the created View in the ViewHolder to the attachment data.
     *
     * @param holder The ViewHolder which should be updated to represent the attachment.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: AttachmentPreviewViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    /**
     * Returns the view type associated with the factory that will handle this attachment.
     *
     * @param position The position of the item in the list.
     */
    override fun getItemViewType(position: Int): Int {
        return factoryManager.getItemViewType(attachments[position])
    }

    /**
     * Returns the number of attachment items.
     *
     * @return The number of attachments.
     */
    override fun getItemCount(): Int = attachments.size
}
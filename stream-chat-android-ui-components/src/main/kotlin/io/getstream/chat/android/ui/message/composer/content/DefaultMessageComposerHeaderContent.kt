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
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import io.getstream.chat.android.common.composer.MessageComposerState
import io.getstream.chat.android.common.state.Edit
import io.getstream.chat.android.common.state.Reply
import io.getstream.chat.android.ui.R
import io.getstream.chat.android.ui.common.extensions.internal.createStreamThemeWrapper
import io.getstream.chat.android.ui.common.extensions.internal.streamThemeInflater
import io.getstream.chat.android.ui.databinding.StreamUiMessageComposerDefaultHeaderContentBinding
import io.getstream.chat.android.ui.message.composer.MessageComposerComponent
import io.getstream.chat.android.ui.message.composer.MessageComposerView
import io.getstream.chat.android.ui.message.composer.MessageComposerViewStyle

/**
 * Represents the default content shown at the top of [MessageComposerView].
 */
public class DefaultMessageComposerHeaderContent : FrameLayout, MessageComposerComponent {
    /**
     * Generated binding class for the XML layout.
     */
    private lateinit var binding: StreamUiMessageComposerDefaultHeaderContentBinding

    /**
     * The style for [MessageComposerView].
     */
    private lateinit var style: MessageComposerViewStyle

    /**
     * Click listener for the dismiss action button.
     */
    public var dismissActionClickListener: () -> Unit = {}

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
        binding = StreamUiMessageComposerDefaultHeaderContentBinding.inflate(streamThemeInflater, this)
        binding.dismissInputModeButton.setOnClickListener { dismissActionClickListener() }
    }

    /**
     * Applies the given style to the message composer.
     *
     * @param style The style that will be applied to the component.
     */
    override fun applyStyle(style: MessageComposerViewStyle) {
        this.style = style

        binding.dismissInputModeButton.setImageDrawable(style.dismissModeIconDrawable)
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        when (state.action) {
            is Reply -> {
                binding.inputModeHeaderContainer.isVisible = true
                binding.inputModeTitleTextView.text = context.getString(R.string.stream_ui_message_input_reply)
                binding.inputModeIcon.setImageDrawable(style.replyModeIconDrawable)
            }
            is Edit -> {
                binding.inputModeHeaderContainer.isVisible = true
                binding.inputModeTitleTextView.text = context.getString(R.string.stream_ui_message_list_edit_message)
                binding.inputModeIcon.setImageDrawable(style.editModeIconDrawable)
            }
            else -> {
                binding.inputModeHeaderContainer.isVisible = false
            }
        }
    }
}
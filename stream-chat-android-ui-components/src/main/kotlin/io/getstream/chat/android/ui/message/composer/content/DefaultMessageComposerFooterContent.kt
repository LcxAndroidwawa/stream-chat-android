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
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.chat.android.ui.common.extensions.internal.createStreamThemeWrapper
import io.getstream.chat.android.ui.common.extensions.internal.streamThemeInflater
import io.getstream.chat.android.ui.common.style.setTextStyle
import io.getstream.chat.android.ui.databinding.StreamUiMessageComposerDefaultFooterContentBinding
import io.getstream.chat.android.ui.message.composer.MessageComposerComponent
import io.getstream.chat.android.ui.message.composer.MessageComposerView
import io.getstream.chat.android.ui.message.composer.MessageComposerViewStyle

/**
 * Represents the default content shown at the bottom of [MessageComposerView].
 */
@ExperimentalStreamChatApi
public class DefaultMessageComposerFooterContent : FrameLayout, MessageComposerComponent {
    /**
     * Generated binding class for the XML layout.
     */
    private lateinit var binding: StreamUiMessageComposerDefaultFooterContentBinding

    /**
     * The style for [MessageComposerView].
     */
    private lateinit var style: MessageComposerViewStyle

    /**
     * Selection listener for the "also send to channel" checkbox.
     */
    public var alsoSendToChannelSelectionListener: (Boolean) -> Unit = {}

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
        binding = StreamUiMessageComposerDefaultFooterContentBinding.inflate(streamThemeInflater, this)
        binding.alsoSendToChannel.setOnCheckedChangeListener { _, _ ->
            alsoSendToChannelSelectionListener(binding.alsoSendToChannel.isChecked)
        }
    }

    /**
     * Applies the given style to the message composer.
     *
     * @param style The style that will be applied to the component.
     */
    override fun applyStyle(style: MessageComposerViewStyle) {
        this.style = style

        // For simplicity, the text is the same for distinct and named channels
        binding.alsoSendToChannel.text = style.alsoSendToChannelCheckboxText
        binding.alsoSendToChannel.setTextStyle(style.alsoSendToChannelCheckboxTextStyle)
        style.alsoSendToChannelCheckboxDrawable?.let { binding.alsoSendToChannel.buttonDrawable = it }
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        val isThreadModeActive = state.messageMode is MessageMode.MessageThread
        binding.alsoSendToChannel.isVisible = style.alsoSendToChannelCheckboxVisible && isThreadModeActive
        binding.alsoSendToChannel.isChecked = state.alsoSendToChannel
    }
}

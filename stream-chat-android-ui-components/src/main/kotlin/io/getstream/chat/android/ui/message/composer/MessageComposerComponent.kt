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

package io.getstream.chat.android.ui.message.composer

import io.getstream.chat.android.common.composer.MessageComposerState

/**
 * An interface implemented by [MessageComposerView] and its children that is
 * used for state propagation.
 */
public interface MessageComposerComponent {
    /**
     * Applies the given style to the message composer.
     *
     * @param style The style that will be applied to the component.
     */
    public fun applyStyle(style: MessageComposerViewStyle)

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    public fun renderState(state: MessageComposerState)
}
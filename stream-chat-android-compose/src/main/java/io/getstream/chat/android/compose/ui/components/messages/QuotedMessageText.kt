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

package io.getstream.chat.android.compose.ui.components.messages

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.getstream.sdk.chat.model.ModelType
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.compose.R
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.util.buildAnnotatedMessageText
import io.getstream.chat.android.compose.ui.util.isFile

/**
 * Default text element for quoted messages, with extra styling and padding for the chat bubble.
 *
 * @param message Message to show.
 * @param modifier Modifier for styling.
 * @param quoteMaxLines Max number of lines quoted text can have.
 */
@Composable
public fun QuotedMessageText(
    message: Message,
    modifier: Modifier = Modifier,
    quoteMaxLines: Int = DefaultQuoteMaxLines,
) {
    val attachment = message.attachments.firstOrNull()

    val quotedMessageText = when {
        message.text.isNotBlank() -> message.text

        attachment != null -> when {
            attachment.name != null -> attachment.name

            attachment.text != null -> attachment.text

            attachment.type == ModelType.attach_image -> {
                stringResource(R.string.stream_compose_quoted_message_image_tag)
            }
            attachment.type == ModelType.attach_giphy -> {
                stringResource(R.string.stream_compose_quoted_message_giphy_tag)
            }
            attachment.isFile() -> {
                stringResource(R.string.stream_compose_quoted_message_file_tag)
            }
            else -> message.text
        }

        else -> message.text
    }

    checkNotNull(quotedMessageText) {
        "quotedMessageText is null. Cannot display invalid message title."
    }

    val styledText = buildAnnotatedMessageText(quotedMessageText)

    val horizontalPadding = ChatTheme.dimens.quotedMessageTextHorizontalPadding
    val verticalPadding = ChatTheme.dimens.quotedMessageTextVerticalPadding

    Text(
        modifier = modifier
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
            .clipToBounds(),
        text = styledText,
        style = ChatTheme.typography.bodyBold,
        maxLines = quoteMaxLines,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * The max length of quote message. After that it gets ellipsized.
 */
private const val DefaultQuoteMaxLines: Int = 3

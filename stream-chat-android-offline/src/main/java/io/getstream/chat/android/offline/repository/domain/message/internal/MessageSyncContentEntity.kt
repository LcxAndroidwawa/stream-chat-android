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

package io.getstream.chat.android.offline.repository.domain.message.internal

import com.squareup.moshi.JsonClass

internal sealed class MessageSyncContentEntity

@JsonClass(generateAdapter = true)
internal class MessageSyncNoneEntity : MessageSyncContentEntity() {
    override fun toString(): String = "MessageSyncNoneEntity"
}

internal sealed class MessageSyncInProgressEntity : MessageSyncContentEntity()
internal sealed class MessageSyncFailedEntity : MessageSyncContentEntity()

@JsonClass(generateAdapter = true)
internal class MessageAwaitingAttachmentsEntity : MessageSyncInProgressEntity() {
    override fun toString(): String = "MessageAwaitingAttachmentsEntity"
}

@JsonClass(generateAdapter = true)
internal data class MessageModerationFailedEntity(
    val violations: List<ViolationEntity>,
) : MessageSyncFailedEntity() {
    internal data class ViolationEntity(
        val code: Int,
        val messages: List<String>,
    )
}

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

package io.getstream.chat.android.offline.channel.controller

import io.getstream.chat.android.client.attachments.AttachmentUrlValidator
import io.getstream.chat.android.client.channel.state.ChannelStateLogic
import io.getstream.chat.android.client.models.ChannelUserRead
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.SynchronizedCoroutineTest
import io.getstream.chat.android.offline.plugin.logic.channel.internal.ChannelLogic
import io.getstream.chat.android.offline.plugin.state.channel.internal.ChannelMutableStateImpl
import io.getstream.chat.android.offline.randomChannel
import io.getstream.chat.android.offline.randomChannelDeletedEvent
import io.getstream.chat.android.offline.randomChannelUpdatedEvent
import io.getstream.chat.android.offline.randomMember
import io.getstream.chat.android.offline.randomMemberAddedEvent
import io.getstream.chat.android.offline.randomMessage
import io.getstream.chat.android.offline.randomMessageReadEvent
import io.getstream.chat.android.offline.randomMessageUpdateEvent
import io.getstream.chat.android.offline.randomNewMessageEvent
import io.getstream.chat.android.offline.randomNotificationMarkReadEvent
import io.getstream.chat.android.offline.randomReactionNewEvent
import io.getstream.chat.android.offline.randomTypingStartEvent
import io.getstream.chat.android.offline.randomTypingStopEvent
import io.getstream.chat.android.offline.randomUser
import io.getstream.chat.android.offline.repository.builder.internal.RepositoryFacade
import io.getstream.chat.android.test.TestCoroutineRule
import io.getstream.chat.android.test.randomString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

@ExperimentalCoroutinesApi
internal class WhenHandleEvent : SynchronizedCoroutineTest {

    @get:Rule
    val testCoroutines = TestCoroutineRule()
    private val channelId = randomString()
    private val currentUser = User(id = CURRENT_USER_ID)
    private val userFlow = MutableStateFlow(currentUser)

    override fun getTestScope(): TestScope = testCoroutines.scope

    private val repos: RepositoryFacade = mock()
    private val attachmentUrlValidator: AttachmentUrlValidator = mock()

    private lateinit var channelLogic: ChannelLogic
    private val channelMutableState: ChannelMutableStateImpl = ChannelMutableStateImpl(
        "type1", channelId, testCoroutines.scope, userFlow,
        MutableStateFlow(
            mapOf(currentUser.id to currentUser)
        )
    )
    private val channelStateLogic: ChannelStateLogic = mock {
        on(it.writeChannelState()) doReturn channelMutableState
    }

    @BeforeEach
    fun setUp() {
        whenever(attachmentUrlValidator.updateValidAttachmentsUrl(any(), any())) doAnswer { invocation ->
            invocation.arguments[0] as List<Message>
        }

        channelLogic = ChannelLogic(
            repos,
            false,
            channelStateLogic
        )
    }

    // User watching event
    @Test
    fun `when user watching event arrives, last message should upsert messages, increment count and appear`() = runTest {
        val user = User()
        val newDate = Date(Long.MAX_VALUE)
        val newMessage = randomMessage(
            id = "thisId",
            createdAt = newDate,
            silent = false,
            showInChannel = true
        )

        whenever(attachmentUrlValidator.updateValidAttachmentsUrl(any(), any())) doReturn listOf(newMessage)

        val userStartWatchingEvent = randomNewMessageEvent(user = user, createdAt = newDate, message = newMessage)

        channelLogic.handleEvent(userStartWatchingEvent)

        verify(channelStateLogic).upsertMessages(listOf(newMessage))
        verify(channelStateLogic).incrementUnreadCountIfNecessary(newMessage)
        verify(channelStateLogic).setHidden(false)
    }

    // Message update
    @Test
    fun `when a message update for a non existing message arrives, it is added`() = runTest {
        val messageId = randomString()
        val message = randomMessage(
            id = messageId,
            user = User(id = "otherUserId"),
            silent = false,
            showInChannel = true
        )

        val messageUpdateEvent = randomMessageUpdateEvent(message = message)

        channelLogic.handleEvent(messageUpdateEvent)

        verify(channelStateLogic).upsertMessages(listOf(message))
    }


    // Todo: Move this to ChannelStateLogic test
    // @Test
    // fun `when a message update event is outdated, it should be ignored`() = runTest {
    //     val messageId = randomString()
    //     val createdAt = randomDate()
    //     val createdLocallyAt = randomDateBefore(createdAt.time)
    //     val updatedAt = randomDateAfter(createdAt.time)
    //     val oldUpdatedAt = randomDateBefore(updatedAt.time)
    //     val recentMessage = randomMessage(
    //         id = messageId,
    //         user = User(id = "otherUserId"),
    //         createdAt = createdAt,
    //         createdLocallyAt = createdLocallyAt,
    //         updatedAt = updatedAt,
    //         updatedLocallyAt = updatedAt,
    //         deletedAt = null,
    //         silent = false,
    //         showInChannel = true
    //     )
    //     val oldMessage = randomMessage(
    //         id = messageId,
    //         user = User(id = "otherUserId"),
    //         createdAt = createdAt,
    //         createdLocallyAt = createdLocallyAt,
    //         updatedAt = oldUpdatedAt,
    //         updatedLocallyAt = oldUpdatedAt,
    //         deletedAt = null,
    //         silent = false,
    //         showInChannel = true
    //     )
    //     channelLogic.upsertMessages(listOf(recentMessage))
    //     val messageUpdateEvent = randomMessageUpdateEvent(message = oldMessage)
    //
    //     channelLogic.handleEvent(messageUpdateEvent)
    //
    //     channelMutableState.messages.value shouldBeEqualTo listOf(recentMessage)
    //     channelMutableState.messages.value shouldNotBeEqualTo listOf(oldMessage)
    // }

    // Member added event
    @Test
    fun `when member is added, it should be propagated`(): Unit = runTest {
        val user = randomUser()
        val member = randomMember(user = user)
        val memberAddedEvent = randomMemberAddedEvent(user = user, member = member)

        channelLogic.handleEvent(memberAddedEvent)

        verify(channelStateLogic).upsertMember(member)
    }

    // Typing events
    @Test
    fun `when events of start and stop tying arrive, it should be correctly propagated`() = runTest {
        val user1 = randomUser()
        val user2 = randomUser()

        val typingStartEvent1 = randomTypingStartEvent(user = user1, channelId = channelId)
        val typingStopEvent = randomTypingStopEvent(user = user2, channelId = channelId)

        channelLogic.run {
            handleEvent(typingStartEvent1)
            verify(channelStateLogic).setTyping(user1.id, typingStartEvent1)

            handleEvent(typingStopEvent)
            verify(channelStateLogic).setTyping(user2.id, null)
        }
    }

    // Read event
    @Test
    fun `when read notification event arrives, it should be correctly propagated`() = runTest {
        val readEvent = randomNotificationMarkReadEvent(user = currentUser)

        channelLogic.handleEvent(readEvent)

        verify(channelStateLogic).updateRead(ChannelUserRead(currentUser, readEvent.createdAt))
    }

    // Read event notification
    @Test
    fun `when read event arrives, it should be correctly propagated`() = runTest {
        val readEvent = randomMessageReadEvent(user = currentUser)

        channelLogic.handleEvent(readEvent)

        verify(channelStateLogic).updateRead(ChannelUserRead(currentUser, readEvent.createdAt))
    }

    // Reaction event
    @Test
    fun `when reaction event arrives, the message of the event should be upsert`(): Unit = runTest {
        val message = randomMessage(
            showInChannel = true,
            silent = false,
        )
        val reactionEvent = randomReactionNewEvent(user = randomUser(), message = message)
        whenever(attachmentUrlValidator.updateValidAttachmentsUrl(any(), any())) doReturn listOf(message)

        channelLogic.handleEvent(reactionEvent)

        // Message is propagated
        verify(channelStateLogic).upsertMessages(listOf(message))
    }

    // Channel deleted event
    @Test
    fun `when channel is deleted, messages are deleted too`() = runTest {
        val deleteChannelEvent = randomChannelDeletedEvent()

        channelLogic.handleEvent(deleteChannelEvent)

        verify(channelStateLogic).removeMessagesBefore(deleteChannelEvent.createdAt)
        verify(channelStateLogic).deleteChannel(deleteChannelEvent.createdAt)
    }

    // Todo: Move this to ChannelStateLogic test
    // @Test
    // fun `when channel is deleted, the status is updated`() = runTest {
    //     val channel = randomChannel()
    //     val deleteChannelEvent = randomChannelDeletedEvent(channel = channel)
    //     val updateChannelEvent = randomChannelUpdatedEvent(channel = channel)
    //
    //     channelLogic.handleEvent(updateChannelEvent)
    //     channelLogic.handleEvent(deleteChannelEvent)
    //
    //
    //
    //     val channelFlowValue = channelMutableState.channelData.value
    //     channelFlowValue.channelId shouldBeEqualTo channel.id
    //     channelFlowValue.deletedAt shouldBeEqualTo deleteChannelEvent.createdAt
    // }

    private companion object {
        private const val CURRENT_USER_ID = "currentUserId"
    }
}

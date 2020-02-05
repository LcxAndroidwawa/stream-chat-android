package io.getstream.chat.android.client.api

import io.getstream.chat.android.client.api.models.CompletableResponse
import io.getstream.chat.android.client.utils.ProgressCallback
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.call.ChatCall
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Device
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.Reaction
import io.getstream.chat.android.client.api.models.QueryUsers
import io.getstream.chat.android.client.api.models.*
import java.io.File

interface ChatApi {

    fun setConnection(userId: String, connectionId: String)

    //region CDN calls

    fun sendFile(
        channelType: String,
        channelId: String,
        file: File,
        mimeType: String,
        callback: ProgressCallback
    )

    fun sendFile(
        channelType: String,
        channelId: String,
        file: File,
        mimeType: String
    ): ChatCall<String>

    fun deleteFile(channelType: String, channelId: String, url: String): ChatCall<Unit>

    fun deleteImage(channelType: String, channelId: String, url: String): ChatCall<Unit>

    //endregion

    //region Device calls

    fun addDevice(request: AddDeviceRequest): ChatCall<Unit>
    fun deleteDevice(deviceId: String): ChatCall<Unit>
    fun getDevices(): ChatCall<List<Device>>

    //endregion

    fun searchMessages(request: SearchMessagesRequest): ChatCall<List<Message>>
    fun getRepliesMore(messageId: String, firstId: String, limit: Int): ChatCall<List<Message>>
    fun getReplies(messageId: String, limit: Int): ChatCall<List<Message>>
    fun getReactions(messageId: String, offset: Int, limit: Int): ChatCall<List<Reaction>>
    fun deleteReaction(messageId: String, reactionType: String): ChatCall<Message>
    fun deleteMessage(messageId: String): ChatCall<Message>
    fun sendAction(request: SendActionRequest): ChatCall<Message>
    fun getMessage(messageId: String): ChatCall<Message>
    fun sendMessage(
        channelType: String,
        channelId: String,
        message: Message
    ): ChatCall<Message>

    fun updateMessage(
        message: Message
    ): ChatCall<Message>

    fun queryChannels(query: QueryChannelsRequest): ChatCall<List<Channel>>
    fun stopWatching(
        channelType: String,
        channelId: String
    ): ChatCall<Unit>

    fun queryChannel(
        channelType: String,
        channelId: String = "",
        query: ChannelQueryRequest
    ): ChatCall<Channel>

    fun updateChannel(
        channelType: String,
        channelId: String,
        request: UpdateChannelRequest
    ): ChatCall<Channel>

    fun markRead(channelType: String, channelId: String, messageId: String): ChatCall<Unit>
    fun showChannel(channelType: String, channelId: String): ChatCall<Unit>
    fun hideChannel(
        channelType: String,
        channelId: String,
        clearHistory: Boolean = false
    ): ChatCall<Unit>

    fun rejectInvite(channelType: String, channelId: String): ChatCall<Channel>
    fun acceptInvite(channelType: String, channelId: String, message: String): ChatCall<Channel>
    fun deleteChannel(channelType: String, channelId: String): ChatCall<Channel>
    fun markAllRead(): ChatCall<EventResponse>

    fun setGuestUser(userId: String, userName: String): ChatCall<TokenResponse>

    fun getUsers(queryUsers: QueryUsers): ChatCall<QueryUserListResponse>

    fun addMembers(
        channelType: String,
        channelId: String,
        members: List<String>
    ): ChatCall<ChannelResponse>

    fun removeMembers(
        channelType: String,
        channelId: String,
        members: List<String>
    ): ChatCall<ChannelResponse>

    fun muteUser(
        targetId: String
    ): ChatCall<MuteUserResponse>

    fun unMuteUser(
        targetId: String
    ): ChatCall<MuteUserResponse>

    fun flag(
        targetId: String
    ): ChatCall<FlagResponse>

    fun banUser(
        targetId: String,
        timeout: Int,
        reason: String,
        channelType: String,
        channelId: String
    ): ChatCall<CompletableResponse>

    fun unBanUser(
        targetId: String,
        channelType: String,
        channelId: String
    ): ChatCall<CompletableResponse>
}
package io.getstream.chat.android.client.models

data class Command(
    var name: String? = null,
    var description: String? = null,
    var args: String? = null,
    var set: String? = null
)
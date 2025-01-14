// ktlint-disable filename

package io.getstream.chat.docs.kotlin.compose.channels

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.compose.state.channels.list.ViewInfo
import io.getstream.chat.android.compose.ui.channels.info.SelectedChannelMenu
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory

/**
 * [Usage](https://getstream.io/chat/docs/sdk/android/compose/channel-components/selected-channel-menu/#usage)
 */
private object SelectedChannelMenuUsageSnippet {

    class MyActivity : AppCompatActivity() {
        val factory by lazy {
            ChannelViewModelFactory(
                ChatClient.instance(),
                QuerySort.desc("last_updated"),
                Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(ChatClient.instance().getCurrentUser()?.id ?: ""))
                )
            )
        }

        val listViewModel: ChannelListViewModel by viewModels { factory }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                ChatTheme {
                    MyCustomUi()
                }
            }
        }

        @Composable
        fun MyCustomUi() {
            // Data for the component
            val user by listViewModel.user.collectAsState()
            val selectedChannelState by listViewModel.selectedChannel
            val currentlySelectedChannel = selectedChannelState

            Box(modifier = Modifier.fillMaxSize()) {

                // The rest of your content

                if (currentlySelectedChannel != null) {
                    val isMuted = listViewModel.isChannelMuted(currentlySelectedChannel.cid)

                    SelectedChannelMenu(
                        modifier = Modifier
                            .fillMaxWidth() // Fill width
                            .wrapContentHeight() // Wrap height
                            .align(Alignment.BottomCenter), // Aligning the content to the bottom
                        selectedChannel = currentlySelectedChannel,
                        isMuted = isMuted,
                        currentUser = user,
                        onChannelOptionClick = { listViewModel.performChannelAction(it) },
                        onDismiss = { listViewModel.dismissChannelAction() }
                    )
                }
            }
        }
    }
}

/**
 * [Handling Actions](https://getstream.io/chat/docs/sdk/android/compose/channel-components/selected-channel-menu/#handling-actions)
 */
private object SelectedChannelMenuHandlingActionsSnippet {

    class MyActivity : AppCompatActivity() {
        val factory by lazy {
            ChannelViewModelFactory(
                ChatClient.instance(),
                QuerySort.desc("last_updated"),
                Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(ChatClient.instance().getCurrentUser()?.id ?: ""))
                )
            )
        }

        private val listViewModel: ChannelListViewModel by viewModels { factory }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                ChatTheme {
                    MyCustomUi()
                }
            }
        }

        @Composable
        fun MyCustomUi() {
            // Data for the component
            val user by listViewModel.user.collectAsState()
            val selectedChannelState by listViewModel.selectedChannel
            val currentlySelectedChannel = selectedChannelState

            Box(modifier = Modifier.fillMaxSize()) {

                // The rest of your content

                if (currentlySelectedChannel != null) {
                    val isMuted = listViewModel.isChannelMuted(currentlySelectedChannel.cid)

                    SelectedChannelMenu(
                        modifier = Modifier
                            .fillMaxWidth() // Fill width
                            .wrapContentHeight() // Wrap height
                            .align(Alignment.BottomCenter), // Aligning the content to the bottom
                        selectedChannel = currentlySelectedChannel,
                        isMuted = isMuted,
                        currentUser = user,
                        onChannelOptionClick = { action ->
                            if (action is ViewInfo) {
                                // Start the channel info screen
                            } else {
                                listViewModel.performChannelAction(action)
                            }
                        },
                        onDismiss = { listViewModel.dismissChannelAction() }
                    )
                }
            }
        }
    }
}

/**
 * [Customization](https://getstream.io/chat/docs/sdk/android/compose/channel-components/selected-channel-menu/#customization)
 */
private object SelectedChannelMenuCustomizationSnippet {

    class MyActivity : AppCompatActivity() {
        val factory by lazy {
            ChannelViewModelFactory(
                ChatClient.instance(),
                QuerySort.desc("last_updated"),
                Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.`in`("members", listOf(ChatClient.instance().getCurrentUser()?.id ?: ""))
                )
            )
        }

        val listViewModel: ChannelListViewModel by viewModels { factory }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContent {
                ChatTheme {
                    MyCustomUi()
                }
            }
        }

        @Composable
        fun MyCustomUi() {
            // Data for the component
            val user by listViewModel.user.collectAsState()
            val selectedChannelState by listViewModel.selectedChannel
            val currentlySelectedChannel = selectedChannelState

            Box(modifier = Modifier.fillMaxSize()) {

                // The rest of your content

                if (currentlySelectedChannel != null) {
                    val isMuted = listViewModel.isChannelMuted(currentlySelectedChannel.cid)

                    SelectedChannelMenu(
                        modifier = Modifier
                            .padding(16.dp) // Adding padding to the component
                            .fillMaxWidth() // Fill width
                            .wrapContentHeight() // Wrap height
                            .align(Alignment.Center), // Centering the component
                        shape = RoundedCornerShape(16.dp), // Rounded corners for all sides
                        selectedChannel = currentlySelectedChannel,
                        isMuted = isMuted,
                        currentUser = user,
                        onChannelOptionClick = { listViewModel.performChannelAction(it) },
                        onDismiss = { listViewModel.dismissChannelAction() }
                    )
                }
            }
        }
    }
}

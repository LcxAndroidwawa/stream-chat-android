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
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.common.composer.MessageComposerState
import io.getstream.chat.android.ui.R
import io.getstream.chat.android.ui.common.extensions.internal.createStreamThemeWrapper
import io.getstream.chat.android.ui.common.extensions.internal.streamThemeInflater
import io.getstream.chat.android.ui.common.internal.SimpleListAdapter
import io.getstream.chat.android.ui.common.style.setTextStyle
import io.getstream.chat.android.ui.databinding.StreamUiItemMentionBinding
import io.getstream.chat.android.ui.databinding.StreamUiSuggestionListViewBinding
import io.getstream.chat.android.ui.message.composer.MessageComposerComponent
import io.getstream.chat.android.ui.message.composer.MessageComposerView
import io.getstream.chat.android.ui.message.composer.MessageComposerViewStyle

/**
 * Represents the default mention suggestion list popup shown above [MessageComposerView].
 */
public class DefaultMessageComposerMentionSuggestionsContent : FrameLayout, MessageComposerComponent {
    /**
     * Generated binding class for the XML layout.
     */
    private lateinit var binding: StreamUiSuggestionListViewBinding

    /**
     * The style for [MessageComposerView].
     */
    private lateinit var style: MessageComposerViewStyle

    /**
     * Adapter used to render mention suggestions.
     */
    private lateinit var adapter: MentionsAdapter

    /**
     * Selection listener invoked when a mention is selected.
     */
    public var mentionSelectionListener: (User) -> Unit = {}

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
        binding = StreamUiSuggestionListViewBinding.inflate(streamThemeInflater, this)
        binding.suggestionsCardView.isVisible = true
        binding.commandsTitleTextView.isVisible = false
    }

    /**
     * Applies the given style to the message composer.
     *
     * @param style The style that will be applied to the component.
     */
    override fun applyStyle(style: MessageComposerViewStyle) {
        this.style = style

        adapter = MentionsAdapter(style) { mentionSelectionListener(it) }
        binding.suggestionsRecyclerView.adapter = adapter

        binding.suggestionsCardView.setCardBackgroundColor(style.mentionSuggestionsBackgroundColor)
    }

    /**
     * Invoked when the state has changed and the UI needs to be updated accordingly.
     *
     * @param state The state that will be used to render the updated UI.
     */
    override fun renderState(state: MessageComposerState) {
        adapter.setItems(state.mentionSuggestions)
    }
}

/**
 * [RecyclerView.Adapter] responsible for displaying mention suggestions in a RecyclerView.
 *
 * @param style The style for [MessageComposerView].
 * @param mentionSelectionListener The listener invoked when a mention is selected from the list.
 */
private class MentionsAdapter(
    private val style: MessageComposerViewStyle,
    private inline val mentionSelectionListener: (User) -> Unit,
) : SimpleListAdapter<User, MentionsViewHolder>() {

    /**
     * Creates and instantiates a new instance of [MentionsViewHolder].
     *
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new [MentionsViewHolder] instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentionsViewHolder {
        return StreamUiItemMentionBinding
            .inflate(parent.streamThemeInflater, parent, false)
            .let { MentionsViewHolder(it, style, mentionSelectionListener) }
    }
}

/**
 * [RecyclerView.ViewHolder] used for rendering mention items.
 *
 * @param binding Generated binding class for the XML layout.
 * @param style The style for [MessageComposerView].
 * @param mentionSelectionListener The listener invoked when a mention is selected.
 */
private class MentionsViewHolder(
    val binding: StreamUiItemMentionBinding,
    style: MessageComposerViewStyle,
    val mentionSelectionListener: (User) -> Unit,
) : SimpleListAdapter.ViewHolder<User>(binding.root) {

    init {
        binding.usernameTextView.setTextStyle(style.mentionSuggestionItemUsernameTextStyle)
        binding.mentionNameTextView.setTextStyle(style.mentionSuggestionItemMentionTextStyle)
        binding.mentionsIcon.setImageDrawable(style.mentionSuggestionItemIconDrawable)
    }

    /**
     * Updates [itemView] elements for a given [User] object.
     *
     * @param item Single mention suggestion represented by [User] class.
     */
    override fun bind(item: User) {
        binding.root.setOnClickListener { mentionSelectionListener(item) }
        // Workaround for race condition caused by Coil trying to load stale avatar on layout.
        binding.avatarView.doOnLayout {
            binding.avatarView.setUserData(item)
        }
        binding.usernameTextView.text = item.name
        binding.mentionNameTextView.text = context.getString(
            R.string.stream_ui_mention,
            item.name.lowercase()
        )
    }
}
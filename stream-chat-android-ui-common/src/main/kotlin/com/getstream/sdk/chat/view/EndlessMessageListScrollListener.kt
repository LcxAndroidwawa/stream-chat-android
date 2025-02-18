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

package com.getstream.sdk.chat.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Scroll listener which checks the layout manager of the MessageListView, listens for scrolling gestures
 * and triggers pagination when reaching the end top of the list.
 *
 * @param loadMoreThreshold The number of items or positions ahead of the end of the list where we can trigger the
 * pagination.
 * @param loadMoreListener The handler which is called when pagination should be triggered.
 */
public class EndlessMessageListScrollListener(
    private val loadMoreThreshold: Int,
    private inline val loadMoreListener: () -> Unit,
) : RecyclerView.OnScrollListener() {

    init {
        require(loadMoreThreshold >= 0) { "Load more threshold must not be negative" }
    }

    /**
     * Helper flag which marks the state if we should disable pagination.
     */
    private var paginationEnabled: Boolean = false

    /**
     * Helper flag which marks  if we should wait for the scroll state reset.
     */
    private var scrollStateReset: Boolean = true

    /**
     * Whenever we scroll, if the pagination is enabled, we check the scroll direction and validity.
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!paginationEnabled) {
            return
        }

        val layoutManager = recyclerView.layoutManager
        if (layoutManager !is LinearLayoutManager) {
            throw IllegalStateException("EndlessScrollListener supports only LinearLayoutManager")
        }

        checkScrollUp(dy, layoutManager, recyclerView)
    }

    /**
     * Checks if the scroll is going up and if the threshold number of items has been shown. If the scroll is downwards,
     * then it stops the check.
     */
    private fun checkScrollUp(dy: Int, layoutManager: LinearLayoutManager, recyclerView: RecyclerView) {
        if (dy >= 0) {
            // Scrolling downwards
            return
        }

        handleScrollUp(layoutManager, recyclerView)
    }

    /**
     * Handles a valid scroll up. If the threshold has been met and the scroll state has been reset previously, we
     * trigger pagination.
     */
    private fun handleScrollUp(layoutManager: LinearLayoutManager, recyclerView: RecyclerView) {
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        if (scrollStateReset && firstVisiblePosition <= loadMoreThreshold) {
            scrollStateReset = false
            recyclerView.post {
                if (paginationEnabled) {
                    loadMoreListener()
                }
            }
        }
    }

    /**
     * Handles scroll state changes where it waits for a state reset and new scroll gestures.
     */
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE ||
            newState == RecyclerView.SCROLL_STATE_DRAGGING
        ) {
            scrollStateReset = true
        }
    }

    /**
     * Manually enables pagination in certain UI components and events.
     */
    public fun enablePagination() {
        paginationEnabled = true
    }

    /**
     * Manually disables pagination in certain UI components and events.
     */
    public fun disablePagination() {
        paginationEnabled = false
    }
}

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

package io.getstream.logging

/**
 * A [StreamLogger] container that can hold onto multiple other loggers.
 */
public class CompositeStreamLogger(
    private val children: List<StreamLogger>,
) : StreamLogger {

    public constructor(vararg children: StreamLogger) : this(children.toList())

    override fun log(
        priority: Priority,
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        children.forEach { childLogger ->
            childLogger.log(priority, tag, message, throwable)
        }
    }
}

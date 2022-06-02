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

package io.getstream.chat.android.client.api.models.querysort

import io.getstream.chat.android.client.api.models.querysort.internal.SortAttribute
import io.getstream.chat.android.client.api.models.querysort.internal.SortSpecification
import io.getstream.chat.android.client.extensions.snakeToLowerCamelCase

public class QuerySortByMap<T : QueryableByMap> : BaseQuerySort<T>() {

    override fun comparatorFromFieldSort(
        firstSort: SortAttribute.FieldSortAttribute<T>,
        sortDirection: SortDirection,
    ): Comparator<T> {
        throw IllegalArgumentException("FieldSortAttribute can't be used with QuerySortByMap")
    }

    override fun comparatorFromNameAttribute(
        name: SortAttribute.FieldNameSortAttribute<T>,
        sortDirection: SortDirection,
    ): Comparator<T> =
        name.name.comparator(sortDirection)

    private fun add(sortSpecification: SortSpecification<T>): QuerySortByMap<T> {
        sortSpecifications = sortSpecifications + sortSpecification
        return this
    }

    public fun asc(fieldName: String): QuerySortByMap<T> {
        return add(SortSpecification(SortAttribute.FieldNameSortAttribute(fieldName), SortDirection.ASC))
    }

    public fun desc(fieldName: String): QuerySortByMap<T> {
        return add(SortSpecification(SortAttribute.FieldNameSortAttribute(fieldName), SortDirection.DESC))
    }

    private fun String.comparator(sortDirection: SortDirection): Comparator<T> =
        Comparator { o1, o2 ->
            val field = this.snakeToLowerCamelCase()

            val fieldOne = o1.toMap()[field] as? Comparable<Any>
            val fieldTwo = o2.toMap()[field] as? Comparable<Any>

            compare(fieldOne, fieldTwo, sortDirection)
        }

    public companion object {
        public fun <R : QueryableByMap> ascByName(fieldName: String): QuerySortByMap<R> =
            QuerySortByMap<R>().asc(fieldName)

        public fun <R : QueryableByMap> descByName(fieldName: String): QuerySortByMap<R> =
            QuerySortByMap<R>().desc(fieldName)

        public fun <R : QueryableByMap> QuerySortByMap<R>.ascByName(fieldName: String): QuerySortByMap<R> =
            asc(fieldName)

        public fun <R : QueryableByMap> QuerySortByMap<R>.descByName(fieldName: String): QuerySortByMap<R> =
            desc(fieldName)
    }
}

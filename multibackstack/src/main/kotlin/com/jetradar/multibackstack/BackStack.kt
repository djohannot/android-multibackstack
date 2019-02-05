/*
 * Copyright (C) 2016 JetRadar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jetradar.multibackstack

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class BackStack (val hostId: Int, private val entriesStack : Stack<BackStackEntry> = Stack()) : Parcelable{

    fun push(entry: BackStackEntry) {
        entriesStack.push(entry)
    }

    fun pop(): BackStackEntry? {
        return if (empty()) null else entriesStack.pop()
    }

    fun empty(): Boolean {
        return entriesStack.empty()
    }

    fun size(): Int {
        return entriesStack.size
    }
}
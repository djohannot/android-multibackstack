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
import androidx.core.util.Pair
import kotlinx.android.parcel.Parcelize
import java.util.*

class BackStackManager(private val backStacks: LinkedList<BackStack> = LinkedList()) {

    fun push(hostId: Int, entry: BackStackEntry) {
        var backStack = peekBackStack(hostId)
        if (backStack == null) {
            backStack = BackStack(hostId)
            backStacks.push(backStack)
        }
        backStack.push(entry)
    }

    fun pop(hostId: Int): BackStackEntry? {
        val backStack = peekBackStack(hostId) ?: return null
        return pop(backStack)
    }

    fun pop(): Pair<Int, BackStackEntry>? {
        val backStack = peekBackStack() ?: return null
        return Pair.create(backStack.hostId, pop(backStack))
    }

    protected fun pop(backStack: BackStack): BackStackEntry {
        val entry = backStack.pop()!!
        if (backStack.empty()) {
            backStacks.remove(backStack)
        }
        return entry
    }

    fun clear(hostId: Int): Boolean {
        val backStack = getBackStack(hostId) ?: return false
        backStacks.remove(backStack)
        return true
    }

    fun backStackSize(hostId: Int): Int {
        val backStack = getBackStack(hostId)
        return backStack?.size() ?: 0
    }

    fun resetToRoot(hostId: Int): Boolean {
        val backStack = getBackStack(hostId) ?: return false
        resetToRoot(backStack)
        return true
    }

    fun resetToRoot(backStack: BackStack) {
        while (true) {
            val entry = backStack.pop()!!
            if (backStack.empty()) {
                backStack.push(entry)
                return
            }
        }
    }

    private fun peekBackStack(hostId: Int): BackStack? {
        val index = indexOfBackStack(hostId)
        if (index == UNDEFINED_INDEX) {
            return null
        }
        val backStack = backStacks[index]
        if (index != FIRST_INDEX) {
            backStacks.removeAt(index)
            backStacks.push(backStack)
        }
        return backStack
    }

    private fun peekBackStack(): BackStack? {
        return backStacks.peek()
    }

    private fun getBackStack(hostId: Int): BackStack? {
        val index = indexOfBackStack(hostId)
        return if (index == UNDEFINED_INDEX) {
            null
        } else backStacks[index]
    }

    private fun indexOfBackStack(hostId: Int): Int {
        val size = backStacks.size
        for (i in 0 until size) {
            if (backStacks[i].hostId == hostId) {
                return i
            }
        }
        return UNDEFINED_INDEX
    }

    fun saveState(): Parcelable {
        return SavedState(backStacks)
    }

    fun restoreState(state: Parcelable?) {
        if (state != null) {
            val savedState = state as SavedState?
            backStacks.addAll(savedState!!.backStacks)
        }
    }

    @Parcelize
    internal class SavedState(val backStacks: MutableList<BackStack>) : Parcelable

    companion object {
        private const val FIRST_INDEX = 0
        private const val UNDEFINED_INDEX = -1
    }
}
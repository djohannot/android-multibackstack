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

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.fragment.app.Fragment

abstract class BackStackActivity : AppCompatActivity() {

    protected var backStackManager: BackStackManager? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        backStackManager = BackStackManager()
    }

    override fun onDestroy() {
        backStackManager = null
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_BACK_STACK_MANAGER, backStackManager!!.saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        backStackManager!!.restoreState(savedInstanceState.getParcelable(STATE_BACK_STACK_MANAGER))
    }

    /**
     * @return false if failed to put fragment in back stack. Relates to issue:
     * java.lang.IllegalStateException: Fragment is not currently in the FragmentManager at
     * android.support.v4.app.FragmentManagerImpl.saveFragmentInstanceState(FragmentManager.java:702)
     */
    protected fun pushFragmentToBackStack(hostId: Int, fragment: Fragment): Boolean {
        try {
            val entry = BackStackEntry.create(supportFragmentManager, fragment)
            backStackManager!!.push(hostId, entry)
            return true
        } catch (e: Exception) {
            Log.e("MultiBackStack", "Failed to add fragment to back stack", e)
            return false
        }

    }

    protected fun popFragmentFromBackStack(hostId: Int): Fragment? {
        val entry = backStackManager!!.pop(hostId)
        return entry?.toFragment(this)
    }

    protected fun popFragmentFromBackStack(): Pair<Int, Fragment>? {
        val pair = backStackManager!!.pop()
        return if (pair != null) Pair.create(pair.first, pair.second!!.toFragment(this)) else null
    }

    /**
     * @return false if back stack is missing.
     */
    protected fun resetBackStackToRoot(hostId: Int): Boolean {
        return backStackManager!!.resetToRoot(hostId)
    }

    /**
     * @return false if back stack is missing.
     */
    protected fun clearBackStack(hostId: Int): Boolean {
        return backStackManager!!.clear(hostId)
    }

    /**
     * @return the number of fragments in back stack.
     */
    protected fun backStackSize(hostId: Int): Int {
        return backStackManager!!.backStackSize(hostId)
    }

    companion object {
        private val STATE_BACK_STACK_MANAGER = "back_stack_manager"
    }
}
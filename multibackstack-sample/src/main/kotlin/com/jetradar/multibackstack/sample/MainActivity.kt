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

package com.jetradar.multibackstack.sample

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.jetradar.multibackstack.BackStackActivity

class MainActivity : BackStackActivity(), BottomNavigationBar.OnTabSelectedListener {

    private var bottomNavBar: BottomNavigationBar? = null
    private var curFragment: Fragment? = null
    private var curTabId: Int = 0

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)
        setUpBottomNavBar()

        if (state == null) {
            bottomNavBar!!.selectTab(MAIN_TAB_ID, false)
            showFragment(rootTabFragment(MAIN_TAB_ID))
        }
    }

    private fun setUpBottomNavBar() {
        bottomNavBar = findViewById(R.id.bottom_navigation)
        bottomNavBar!!
                .addItem(BottomNavigationItem(R.drawable.ic_search_24dp, R.string.search))
                .addItem(BottomNavigationItem(R.drawable.ic_favorite_24dp, R.string.favorites))
                .addItem(BottomNavigationItem(R.drawable.ic_profile_24dp, R.string.profile))
                .initialise()
        bottomNavBar!!.setTabSelectedListener(this)
    }

    private fun rootTabFragment(tabId: Int): Fragment {
        when (tabId) {
            0 -> return ItemListFragment.newInstance(getString(R.string.search))
            1 -> return ItemListFragment.newInstance(getString(R.string.favorites))
            2 -> return ItemListFragment.newInstance(getString(R.string.profile))
            else -> throw IllegalArgumentException()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        curFragment = supportFragmentManager.findFragmentById(R.id.content)
        curTabId = savedInstanceState.getInt(STATE_CURRENT_TAB_ID)
        bottomNavBar!!.selectTab(curTabId, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_TAB_ID, curTabId)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        val pair = popFragmentFromBackStack()
        if (pair != null) {
            backTo(pair.first!!, pair.second!!)
        } else {
            super.onBackPressed()
        }
    }

    override fun onTabSelected(position: Int) {
        if (curFragment != null) {
            pushFragmentToBackStack(curTabId, curFragment!!)
        }
        curTabId = position
        var fragment = popFragmentFromBackStack(curTabId)
        if (fragment == null) {
            fragment = rootTabFragment(curTabId)
        }
        replaceFragment(fragment)
    }

    override fun onTabReselected(position: Int) {
        backToRoot()
    }

    override fun onTabUnselected(position: Int) {}

    @JvmOverloads
    fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        if (curFragment != null && addToBackStack) {
            pushFragmentToBackStack(curTabId, curFragment!!)
        }
        replaceFragment(fragment)
    }

    private fun backTo(tabId: Int, fragment: Fragment) {
        if (tabId != curTabId) {
            curTabId = tabId
            bottomNavBar!!.selectTab(curTabId, false)
        }
        replaceFragment(fragment)
        supportFragmentManager.executePendingTransactions()
    }

    private fun backToRoot() {
        if (isRootTabFragment(curFragment!!, curTabId)) {
            return
        }
        resetBackStackToRoot(curTabId)
        val rootFragment = popFragmentFromBackStack(curTabId)!!
        backTo(curTabId, rootFragment)
    }

    private fun isRootTabFragment(fragment: Fragment, tabId: Int): Boolean {
        return fragment.javaClass == rootTabFragment(tabId).javaClass
    }

    private fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.replace(R.id.content, fragment)
        tr.commitAllowingStateLoss()
        curFragment = fragment
    }

    companion object {
        private val STATE_CURRENT_TAB_ID = "current_tab_id"
        private val MAIN_TAB_ID = 0
    }
}
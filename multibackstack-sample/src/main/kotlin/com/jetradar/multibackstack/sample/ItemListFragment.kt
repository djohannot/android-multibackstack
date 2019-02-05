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
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView

import java.util.ArrayList
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.ListFragment

class ItemListFragment : ListFragment() {

    private var items: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val section = arguments!!.getString(ARG_SECTION)
        items = createItemsForSection(section)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpActionBar()

        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, items!!)
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        (activity as MainActivity).showFragment(ItemFragment.newInstance(items!![position]))
    }

    private fun createItemsForSection(section: String?): List<String> {
        val itemsNumber = 10
        val items = ArrayList<String>(itemsNumber)
        for (i in 0 until itemsNumber) {
            items.add(section + " " + (i + 1))
        }
        return items
    }

    private fun setUpActionBar() {
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        actionBar.setTitle(R.string.app_name)
    }

    companion object {
        private val ARG_SECTION = "section"

        fun newInstance(section: String): ItemListFragment {
            val fragment = ItemListFragment()
            val args = Bundle()
            args.putString(ARG_SECTION, section)
            fragment.arguments = args
            return fragment
        }
    }
}
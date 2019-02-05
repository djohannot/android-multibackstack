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

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.Fragment.SavedState
import androidx.fragment.app.FragmentManager
import kotlinx.android.parcel.Parcelize

@Parcelize
class BackStackEntry(private val fname: String, private val state: SavedState?, private val args: Bundle?) : Parcelable {

    fun toFragment(context: Context): Fragment {
        val f = Fragment.instantiate(context, fname)
        f.setInitialSavedState(state)
        f.arguments = args
        return f
    }

    companion object {
        fun create(fm : FragmentManager, fragment : Fragment) : BackStackEntry {
            return BackStackEntry(fragment.javaClass.name, fm.saveFragmentInstanceState(fragment), fragment.arguments)
        }
    }
}
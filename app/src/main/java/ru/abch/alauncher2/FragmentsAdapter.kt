package ru.abch.alauncher2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) MainFragment.newInstance() else AppsFragment.newInstance()
    }

    override fun getItemCount(): Int {
        return 2
    }
}
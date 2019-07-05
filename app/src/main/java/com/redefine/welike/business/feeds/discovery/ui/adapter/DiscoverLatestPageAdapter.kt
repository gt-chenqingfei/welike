package com.redefine.welike.business.feeds.discovery.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

class DiscoverLatestPageAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    val fragments = ArrayList<Fragment>()

    private var mCurrentItem: Fragment? = null


    fun setFragmentVisible(userVisibleHint: Boolean) {

        for (fragment in fragments) {
            if (userVisibleHint)
                fragment.onResume()
            else fragment.onPause()
        }

        mCurrentItem?.userVisibleHint = userVisibleHint

    }


    override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: Any) {
        super.setPrimaryItem(container, position, fragment)

        mCurrentItem = fragment as Fragment
    }

    fun initFragments(fragmentPages: List<Fragment>) {
        fragments.clear()
        fragments.addAll(fragmentPages)
        notifyDataSetChanged()
    }

    fun insertFragments(fragmentPages: List<Fragment>) {
        fragments.addAll(2, fragmentPages)
        notifyDataSetChanged()
    }


    override fun getItem(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun getCount(): Int {
        return fragments.size
    }

}
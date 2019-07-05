package com.redefine.welike.business.browse.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

class DiscoverPageAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    val fragments = ArrayList<Fragment>()

    val mTitles = ArrayList<String>()

    private var mCurrentItem: Fragment? = null

    fun initFragments(fragmentPages: List<Fragment>) {
        fragments.clear()
        fragments.addAll(fragmentPages)
        notifyDataSetChanged()
    }

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

    fun insertFragments(fragmentPages: List<Fragment>) {
        fragments.addAll(fragments.size, fragmentPages)
        notifyDataSetChanged()
    }

    fun setTitles(titles: ArrayList<String>) {

        mTitles.clear()

        mTitles.addAll(titles)

        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles.get(position)
    }

}
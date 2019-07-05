package com.redefine.welike.business.feeds.discovery.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup

class DiscoverPageAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    val fragments = ArrayList<Fragment>()

    private var onlyLast = false //控制 adapter 是否显示最后一个。

    private var mCurrentItem: Fragment? = null

    fun getLastIndex(): Int {
        return fragments.size - 1
    }

    fun onlyShowLast(value: Boolean) {
        if (onlyLast != value) {
            onlyLast = value
            notifyDataSetChanged()
        }
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
        return fragments.size - if (onlyLast) 0 else 1
    }


//    override fun getItem(position: Int): Fragment {
//        val bundle = Bundle().also {
//            it.putString("TAB", tabs[position])
//        }
//        NewFeedFragment.create(tabs[position])
//        return TabContentFragment().also { it.arguments = bundle }
//    }

//    override fun getCount(): Int {
//        return tabs.size
////        return 1
//    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        return tabs[position]
//    }

//    fun add(tab: String) {
//        tabs.add(tab)
//        notifyDataSetChanged()
//    }
}
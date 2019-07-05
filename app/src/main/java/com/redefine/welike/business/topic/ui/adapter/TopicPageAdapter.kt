package com.redefine.welike.business.topic.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.ViewGroup
import com.redefine.welike.business.feeds.discovery.ui.page.NewFeedFragment

class TopicPageAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    val fragments = ArrayList<Fragment>()

    private var mCurrentItem: Fragment? = null

    fun initFragments(fragmentPages: List<Fragment>) {
        fragments.clear()
        fragments.addAll(fragmentPages)
        notifyDataSetChanged()
    }

    fun insertFragments(fragmentPage: Fragment) {
        fragments.add(fragmentPage)
        notifyDataSetChanged()
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: Any) {
        super.setPrimaryItem(container, position, fragment)

        mCurrentItem = fragment as Fragment
    }


    override fun getItemPosition(`object`: Any): Int {
        return FragmentStatePagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun getCount(): Int {
        return fragments.size
    }


}
package com.redefine.welike.business.easypost.ui.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.redefine.welike.business.easypost.ui.ImageFragment

class ImagePagerAdapter(var tabs: List<ImageFragment> = ArrayList(), manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    override fun getItem(position: Int): ImageFragment? {
        if (tabs.isEmpty()) {
            return null
        }
        return tabs[position]
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    fun notifyDataSetChange(tabs: List<ImageFragment>) {
        this.tabs = tabs
        notifyDataSetChanged()
    }

}
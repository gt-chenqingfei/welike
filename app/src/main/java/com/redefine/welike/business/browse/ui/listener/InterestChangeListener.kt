package com.redefine.welike.business.browse.ui.listener

import com.redefine.welike.business.user.management.bean.Interest

/**
 * Created by honglin on 2018/7/20.
 */
interface InterestChangeListener {

    fun onInterestSelectChange(interest: Interest)

    fun onConfirm()

    fun onCancel()

}
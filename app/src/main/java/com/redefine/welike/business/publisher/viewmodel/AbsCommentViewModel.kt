package com.redefine.welike.business.publisher.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.business.publisher.management.bean.CheckState
import com.redefine.welike.business.publisher.management.bean.DraftBase

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */
open abstract class AbsCommentViewModel<T : DraftBase> : AbsPublishViewModel<T>() {

    val mCheckedLiveData: MutableLiveData<CheckState> = MutableLiveData()

    var isTextOverLimit: Boolean = false
    var isTextEmpty: Boolean = true


    fun updateChecked(checkState: CheckState) {
        mCheckedLiveData.postValue(checkState)
    }

    override fun getInputTextMaxOverLimit(): Int {
        return GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_OVER_LIMIT
    }

    override fun getInputTextMaxWarnLimit(): Int {
        return GlobalConfig.PUBLISH_COMMENT_INPUT_TEXT_MAX_WARN_LIMIT
    }
}
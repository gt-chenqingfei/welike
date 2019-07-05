package com.redefine.welike.business.publisher.management

import com.redefine.welike.business.publisher.management.bean.DraftBase

/**
 * @author qingfei.chen
 * @date 2019/1/18
 * Copyright (C) 2018 redefine , Inc.
 */
interface PublishSubject<T : DraftBase> {
    fun publish(draft: T?)
}
package com.redefine.welike.net

import com.redefine.foundation.framework.Event
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.constant.EventIdConstant
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

/**
 * Name: ObserverExt
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-27 02:02
 */


fun <T> Observable<T>.subscribeExt(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable {
    return this.subscribe({
        if (it is Result<*>) {
            if (it.code == ErrorCode.ERROR_NETWORK_AUTH_NOT_MATCH) {
                EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_LOGOUT_EVENT, null))
            }
        }
        onNext(it)
    }, onError)
}
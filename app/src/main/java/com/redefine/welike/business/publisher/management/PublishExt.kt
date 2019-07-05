package com.redefine.welike.business.publisher.management

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

object UiDispatcher {
    private val mHandler = Handler(Looper.getMainLooper())
    fun dispatch(block: () -> Unit) {
        mHandler.post(block)
    }

    fun dispatchDelay(runnable: Runnable, ms: Long) {
        mHandler.postDelayed(runnable, ms)
    }

    fun dispatchDelay(block: () -> Unit, ms: Long) {
        mHandler.postDelayed(block, ms)
    }

    fun dispatch(runnable: Runnable) {
        mHandler.post(runnable)
    }
}

object BgDispatcher {
    private val mCachedThreads = Executors.newCachedThreadPool()!!

    fun dispatch(block: () -> Unit) {
        mCachedThreads.execute(block)
    }

    fun dispatch(runnable: Runnable) {
        mCachedThreads.execute(runnable)
    }
}

package com.redefine.welike.business.im

import com.redefine.im.threadTry
import com.redefine.im.w
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 临时方案，代替 MessageBoxCountObserver
 */
object AllCountManager {
    /**
     * All the listeners.
     */
    val listeners = ArrayList<MessageCountListener>()
    //lock for all work.
    val lock = ReentrantLock()

    fun register(listener: MessageCountListener) {
        threadTry {
            lock.withLock {
                if (!listeners.contains(listener)) {
                    listeners.add(listener)
                }
            }
        }
    }

    fun unregister(listener: MessageCountListener) {
        threadTry {
            lock.withLock { listeners.remove(listener) }
        }
    }

    private fun allCount(): Int {
        var result = 0
        val setting = CountManager.getEventCounts()
        if (setting != null) {
            try {
                val mention = setting.mentionCount ?: 0
                val comment = setting.commentCount ?: 0
                val like = setting.likeCount ?: 0
                val imCount = CountManager.getMessageUnreadCount()
                result = mention + comment + like + imCount
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun notifyChanged() {
        threadTry {
            val count = allCount()
            lock.withLock {
                listeners.forEach {
                    w("AllCountManager.notifyChanged")
                    it.onChanged(count)
                }
            }
        }
    }


}

interface MessageCountListener {

    fun onChanged(count: Int)

}
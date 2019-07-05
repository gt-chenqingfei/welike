package com.redefine.welike.business.publisher.management

import com.redefine.welike.business.publisher.d
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.draft.*
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.task.*
import com.redefine.welike.business.publisher.w
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.bean.RegisterAndLoginModel

/**
 * Name: PublishManager
 * Author: liwenbo
 * Email:
 * Comment: //发布器发布流程主入口
 * Date: 2018-07-10 16:33
 */
class PublishManager private constructor() {

    private val waitingQueue = mutableListOf<IPublishTask>()

    private var currentTask: IPublishTask? = null

    companion object {
        const val TAG: String = "PublishManager"
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = PublishManager()
    }

    fun publish(postDraft: IPublishDraft, listener: IPublishCallback) {

        var callback = { publishTask: IPublishTask, publishMessage: PublishMessage ->
            callbackDispatch(publishTask, publishMessage, listener)
        }
        val postTask: IPublishTask

        postTask = when (postDraft) {
            is PublishPostDraft -> PublishPostTask(postDraft, callback)
            is PublishCommentDraft -> PublishCommentTask(postDraft, callback)
            is PublishForwardDraft -> PublishForwardTask(postDraft, callback)
            is PublishReplyDraft -> PublishReplyTask(postDraft, callback)
            else -> {
                e(TAG, "Feed publish  error! Not support Category yet .")
                return
            }
        }

        addOrReplace(postTask)
        next()
    }

    private fun callbackDispatch(publishTask: IPublishTask, publishMessage: PublishMessage,
                                 listener: IPublishCallback) {
        when (publishMessage.state) {
            PublishMessage.PublishState.START -> {
                notifyOnStart(publishTask, listener)
            }
            PublishMessage.PublishState.RUNNING -> {
                notifyOnProgress(publishTask, publishMessage.progress ?: 0, listener)
            }
            PublishMessage.PublishState.SUCCESS -> {
                notifyOnCompleted(publishTask, publishMessage, listener)
            }
            PublishMessage.PublishState.ERROR -> {
                notifyOnError(publishTask, publishMessage, listener)
            }
            PublishMessage.PublishState.CANCELED -> {
                notifyOnCanceled(publishTask, publishMessage, listener)
            }
        }
    }

    private fun notifyOnCompleted(task: IPublishTask, publishMessage: PublishMessage,
                                  listener: IPublishCallback) {
        d(TAG, "notifyCompleted")

        synchronized(waitingQueue) {
            currentTask = null
            next()
        }

        listener.onPublishCompleted(task, publishMessage.state)
        HalfLoginManager.updateClickCount(RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.PUBLISH))
    }

    private fun notifyOnCanceled(task: IPublishTask, publishMessage: PublishMessage,
                                 listener: IPublishCallback) {
        d(TAG, "notifyCancel")

        synchronized(waitingQueue) {
            currentTask = null
            next()
        }
        listener.onPublishCompleted(task, publishMessage.state)
    }

    private fun notifyOnError(task: IPublishTask, message: PublishMessage,
                              listener: IPublishCallback) {
        e(TAG, "notifyError")
        PublishAnalyticsManager.getInstance().publishErrorAnalytics(
                task.getPublishDraft().getDraftId(), message.errorCode.ordinal, message.errorMsg)

        synchronized(waitingQueue) {
            currentTask = null
            next()
        }

        listener.onPublishCompleted(task, message.state)
    }

    private fun notifyOnProgress(task: IPublishTask, progress: Int, listener: IPublishCallback) {
        listener.onPublishProgress(task, progress)
    }

    private fun notifyOnStart(task: IPublishTask, listener: IPublishCallback) {
        d(TAG, "notifyStart")
        listener.onPublishStart(task)
    }

    /**
     * Publish a post,comment,reply and reply back
     * When a new task is published, a new Task object is add int the task queue and to invoke next.
     * If there is no task being executed in the current task queue, return.
     * When each task Completed calls next again to check if there are still unpublished tasks in the queue.
     */
    private fun next() {
        synchronized(waitingQueue) {
            if (currentTask != null) {
                w(TAG, "Try to publish ,but the ongoing mission is not over yet！")
                return
            }

            if (waitingQueue.isEmpty()) {
                w(TAG, "Try to publish , all publish task already end!")
                return
            }

            currentTask = waitingQueue.removeAt(waitingQueue.size - 1)

            if (currentTask == null) {
                w(TAG, "Try to publish fail,because the current task is null!")
                return
            }

            val task = currentTask
            BgDispatcher.dispatch {
                if (task == null) {
                    w(TAG, "Try to publish fail,because the task is null!")
                    return@dispatch
                }
                task.start()
            }
        }
    }

    private fun addOrReplace(task: IPublishTask) {
        synchronized(waitingQueue) {
            var existTask: IPublishTask? = null
            waitingQueue.forEach {
                if (it.getTaskId() == task.getTaskId()) {
                    existTask = it
                    return@forEach
                }
            }

            if (currentTask != null && currentTask!!.getTaskId() == task.getTaskId()) {
                return
            }

            if (existTask == null) {
                waitingQueue.add(task)
            } else {
                waitingQueue.remove(existTask!!)
                waitingQueue.add(task)
            }
        }
    }


    interface IPublishCallback {
        /**
         * This callback method is called when the task is added to the thread pool and starts execution.
         */
        fun onPublishStart(task: IPublishTask)

        /**
         *
         */
        fun onPublishProgress(task: IPublishTask, progress: Int)

        /**
         * There are several cases of calling this method.
         * 1. Publish successful
         * 2. Publish Error
         * 3. Publish Cancel
         */
        fun onPublishCompleted(task: IPublishTask, publishState: PublishMessage.PublishState?)
    }
}

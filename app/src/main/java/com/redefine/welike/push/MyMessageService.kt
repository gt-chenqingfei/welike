package com.redefine.welike.push

import android.os.Bundle
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.redefine.im.w


class MyMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String?) {
        w("MyMessageService.onNewToken $token")
        token?.let { scheduleJob(it) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.data?.let { d ->
            WeLikeNotificationManager.getNotificationManager().apply {
                setData(d)
                checkEnv()
            }
        }
    }

    private fun scheduleJob(token: String) {
        // [START dispatch_job]
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val myJob = dispatcher.newJobBuilder()
                .setService(MyJobService::class.java)
                .setTag("my-job-tag")
                .setExtras(Bundle().also { it.putString("KEY", token) })
                .build()
        dispatcher.schedule(myJob)
        // [END dispatch_job]
    }

}
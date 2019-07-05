package com.redefine.welike.push

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.redefine.im.threadTry
import com.redefine.im.w

class MyJobService : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        w("Performing long running task in scheduled job")
        jobParameters.extras?.let { ex ->
            ex.keySet()?.forEach {
                w("jobParameters $it = ${ex[it]}")
            }
        }
        val token = jobParameters.extras?.getString("KEY", "") ?: ""
        threadTry {
            PushService.apply {
                saveToken(token)
                subTopic()
                checkToken {
                    jobFinished(jobParameters, false)
                }
            }
        }
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }
}
package com.redefine.welike.statistical.task

import android.content.Context
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.appsflyer.AppsFlyerLib
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.MyApplication
import com.redefine.welike.Switcher
import com.redefine.welike.base.URLCenter
import com.redefine.welike.base.dao.eventlog.EventStore
import com.redefine.welike.statistical.Config
import com.redefine.welike.statistical.http.SyncEventRequest
import com.redefine.welike.statistical.utils.ConditionUtil

class AmountUploadRunnable(context: Context) : BaseStrategyRunnable() {

    //    private val request: SyncEventRequest
//    private val jsonArray: JSONArray = JSONArray()
    private var latestAFReport: Long = 0


    override fun run() {
        mStop = false
        while (!mStop) {
            upload(0, URLCenter.getHostLog()) //upload to ali
            if (Switcher.EVENT_REPORT_AWS) {
                upload(1, URLCenter.getHostAWSLog())//upload to AWS
            }
            //            }
            //try to report AF event.
            val c = System.currentTimeMillis()
            if (c - latestAFReport > 5 * 60 * 1000) {
                latestAFReport = c
                AppsFlyerLib.getInstance().reportTrackSession(MyApplication.getAppContext())

            }
            try {
                Thread.sleep(ConditionUtil.getAmountSleepTime().toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    fun upload(server: Int, host: String) {
        val entities = EventStore.queryAmountEvent(server, Config.UPLOAD_COUNT_AMOUNT)
        if (!CollectionUtil.isEmpty(entities)) {
            val jsonArray = JSONArray()
            for (entity in entities) {
                val eventInfo = entity.eventInfo
                val parse = JSONObject.parse(eventInfo) as JSONObject
                filter(parse)
                jsonArray.add(parse)
            }

            try {
                val success = SyncEventRequest().sendEvent(jsonArray)
                if (success) {
                    EventStore.deleteAll(*entities.toTypedArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}

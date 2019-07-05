package com.redefine.welike.common.vip

import android.content.Context
import android.content.SharedPreferences
import android.util.SparseArray
import com.redefine.im.threadTry
import com.redefine.welike.MyApplication
import com.redefine.welike.base.URLCenter
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.init.InitCheck
import com.redefine.welike.business.init.InitSp
import com.redefine.welike.common.VipUtil
import com.redefine.welike.common.VipVerifyRequest
import com.redefine.welike.kext.readAssets
import com.redefine.welike.kext.save
import com.redefine.welike.net.OkHttpFactory
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

/**
 * Created by daining on 2018/4/19.
 */

object VipManager {

    private val VERIFY_URL_RELEASE = "{\"userGrade\":\"http://inapp.welike.in/#/userGrade\",\"profile\":\"http://event.welike.in/#/v2/interestProfile\"," +
            "\"verify\":\"http://event.welike.in/#/v2/interestVerify\"" +
            ",\"topicAdmApply\":\"http://inapp.welike.in/#/topicAdmApply\"}"
    private val VERIFY_URL_DEBUG = "{\"userGrade\":\"http://pre-inapp.welike.in/#/userGrade\",\"profile\":\"http://pre-event.welike.in/#/v2/interestProfile\",\"verify\":\"http://pre-event.welike.in/#/v2/interestVerify\"" + ",\"topicAdmApply\":\"http://pre-inapp.welike.in/#/topicAdmApply\"}"

    private val KEY_CONFIG = "KEY_CONFIG"
    private val KEY_VERIFY_URL = "KEY_VERIFY_URL"
    private val KEY_TOPIC_ADM_APPLY = "KEY_TOPIC_ADM_APPLY"

    val sp: SharedPreferences by lazy { MyApplication.getAppContext().getSharedPreferences("VIP", Context.MODE_PRIVATE) }

    private val api by lazy { Retrofit.Builder().baseUrl(URLCenter.getHost()).client(OkHttpFactory.getInstance().okHttpClient).build().create(VipAPI::class.java) }

    fun init() {
        threadTry {
            val tagVersion = initTag()
            initVerifyUrl()
            InitCheck.VipCheck.check({
                try {
                    Thread.sleep((Random(10).nextInt(10) + 5) * 1000L)
                } catch (e: InterruptedException) {
                }
                updateVip(tagVersion)
                updateVerifyUrl()
            }, 3)
        }
    }

    /**
     * 初始化Tag信息
     */
    private fun initTag(): Long {
        var content = sp.getString(KEY_CONFIG, "")
        if (content.isNullOrEmpty()) {
            //如果SP中没有值，从ASSET中取默认
            content = initDefault()
        }
        var version: Long = 0
        try {
            val jo = JSONObject(content)
            version = jo.optLong("version", 0)
            VipUtil.init(parser(jo.optJSONArray("tags")))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return version
    }

    private fun initVerifyUrl() {
        var profile: String? = null
        var verify: String? = null
        var userGrade: String? = null
        var content = sp.getString(KEY_VERIFY_URL, "")
        if (!content.isNullOrEmpty()) {
            try {
                val jo = JSONObject(content)
                profile = jo.getString("profile")
                verify = jo.getString("verify")
                userGrade = jo.getString("userGrade")
            } catch (e: Exception) {
            }
        }
        //如果取值有问题，取默认
        if (profile.isNullOrEmpty() || verify.isNullOrEmpty() || userGrade.isNullOrEmpty()) {
            content = if (com.redefine.welike.BuildConfig.DEBUG) VERIFY_URL_DEBUG else VERIFY_URL_RELEASE
            sp.save(KEY_VERIFY_URL, content)
        }
        updateURL(content)
    }

    private fun updateURL(content: String): Boolean {
        try {
            val jo = JSONObject(content)
            VipUtil.setProfileAddress(jo.getString("profile"))
            VipUtil.setCertifyAddress(jo.getString("verify"))
            VipUtil.setUserGradeAddress(jo.getString("userGrade"))
            return true
        } catch (e: Exception) {
        }
        return false
    }

    private fun initDefault(): String {
        val record = MyApplication.getApp().readAssets("vip_config.json")
        sp.save(KEY_CONFIG, record)
        return record
    }

    private fun updateVip(version: Long) {
        api.vipConfig(version.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.string()?.let { result ->
                    try {
                        val jo = JSONObject(result).optJSONObject("result")
                        val v = jo.getLong("version")
                        if (v > version) {
                            jo.optJSONArray("tags")?.let { tags ->
                                VipUtil.init(parser(tags))
                                sp.edit().putString(KEY_CONFIG, jo.toString()).apply()
                                InitCheck.VipCheck.done()
                            }
                        }
                    } catch (e: Exception) {

                    }
                }

            }
        })
    }

    private fun updateVerifyUrl() {

        val request = VipVerifyRequest(MyApplication.getAppContext())
        try {
            request.req(object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {}

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: com.alibaba.fastjson.JSONObject) {
                    InitCheck.VipCheck.done()
                    sp.save(KEY_VERIFY_URL, result.toString())
                    VipUtil.setProfileAddress(result.getString("profile"))
                    VipUtil.setCertifyAddress(result.getString("verify"))
                    VipUtil.setUserGradeAddress(result.getString("userGrade"))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun parser(ja: JSONArray): SparseArray<HashMap<String, String>> {
        val res = SparseArray<HashMap<String, String>>()
        val l = ja.length()
        var jo: JSONObject
        var key = 0
        var en: String
        var hi: String
        for (i in 0 until l) {
            jo = ja.optJSONObject(i)
            key = jo.optInt("vip", -1)
            en = jo.optString("en")
            hi = jo.optString("in")
            if (key > 0) {
                val map = HashMap<String, String>()
                map["en"] = en
                map["hi"] = hi
                res.put(key, map)
            }
        }
        return res
    }

}

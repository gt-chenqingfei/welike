package com.redefine.welike.common.abtest

import android.content.Context
import android.content.SharedPreferences
import com.redefine.im.threadTry
import com.redefine.im.w
import com.redefine.welike.MyApplication
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.init.InitCheck
import com.redefine.welike.kext.save
import org.json.JSONObject

object ABTest {
    //    val map = HashMap<ABKeys, ABConfig>()
    val setting = ArrayList<ABKeys>()

    val sp: SharedPreferences by lazy { MyApplication.getApp().getSharedPreferences("ABTest", Context.MODE_PRIVATE) }
    fun init() {
        restore()
    }

    fun update() {
        threadTry {
            InitCheck.ABTest.check(after = {
                ABTestRequest(ABKeys.values().map { it.name }).req(object : RequestCallback {
                    override fun onError(request: BaseRequest?, errCode: Int) {

                    }

                    override fun onSuccess(request: BaseRequest?, result: com.alibaba.fastjson.JSONObject?) {
                        threadTry {
                            /**
                             * {"TEST_USER_PHOTO_LIST":{},"TEST_LOGIN":{},"TEST_LOGIN_HOME":{},"buckets":{"testabc":"A","foryou_ads":"A"}}
                             */
                            result?.let {
                                it.toJSONString().let { json ->
                                    JSONObject(json).let { data ->
                                        setting.forEach { key ->
                                            data.optJSONObject(key.name)?.toString()?.let { config ->
                                                key.json = config
                                                sp.save(key.name, config)
                                            }
                                        }
                                        InitCheck.ABTest.done()
                                        // update event manager
                                        callback?.invoke(result)
                                    }
                                }
                            }
                        }
                    }
                })
            }, day = 1)
        }

    }

    /**
     * 从 SharedPreference 恢复配置
     */
    private fun restore() {
        ABKeys.values().forEach { key ->
            setting.add(key.apply { json = sp.getString(key.name, "") })
        }
    }

    //    fun getConfigs(): String = Gson().toJson(map).toString()
    fun getConfigs(): String {
        val map = JSONObject()
        setting.forEach {
            var jo = JSONObject()
            try {
                jo = JSONObject(it.json)
            } catch (e: Exception) {
            }
            map.put(it.name, jo)
        }
        return map.toString()
    }

    var callback: ((com.alibaba.fastjson.JSONObject) -> Unit)? = null

    fun registerListener(callback: (com.alibaba.fastjson.JSONObject) -> Unit) {
        this.callback = callback
        callback(com.alibaba.fastjson.JSONObject.parse(getConfigs()) as com.alibaba.fastjson.JSONObject)
    }

    fun check(key: ABKeys): Int {
        return setting.firstOrNull { it == key }?.getConfigValue() ?: 0
    }
}

enum class ABKeys(var json: String = "") {

    buckets,//服务端abtest 数据

    TEST_LOGIN,//登陆交互 (AB)；
    TEST_LOGIN_HOME,//未登录首页结构 (AB)  0=没有底部；1=底部只有2个TAB；
    TEST_USER_PHOTO_LIST,//客人态相册流 (AB)
    TEST_UPLOAD,//阿里云oss开关
    TEST_RECOMMEND_CONTACT,//横插推荐加联系人
    TEST_RECOMMEND,//推荐关注人样式 (AB)
    TEST_IMAGE_DISPLAY;//图片展示策略(0 :default 1:view full)

    fun getConfigValue(): Int {
        try {
            return JSONObject(json).optInt("value", 0)
        } catch (e: Exception) {
        }
        return 0
    }
}
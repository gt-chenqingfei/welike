package com.redefine.welike.business.startup.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.h5.WebViewActivity
import com.redefine.foundation.framework.Event
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import kotlinx.android.synthetic.main.activity_sign_login_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author redefine honlin
 * @Date on 2018/11/20
 * @Description 注册登录页
 */
@Route(path = RouteConstant.LAUNCH_SIGN_LOGIN_ACTIVITY)
class SignOrLoginActivity : BaseActivity() {

    private var eventModel: RegisterAndLoginModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.dialog_bottom_enter,0)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_login_layout)

        EventBus.getDefault().register(this)

        setEvent()

        HalfLoginManager.getInstancce().checkIsExistAccount()
    }

    fun setEvent() {

        iv_close.setOnClickListener {
            finish()
        }
        user_terms.setMovementMethod(LinkMovementMethod.getInstance())
        user_terms.text = (getTermsSpannable())


        var event = intent.extras.getSerializable(RegisteredConstant.KEY_EVENT_MODEL)
        event?.let {
            eventModel = event as RegisterAndLoginModel
            eventModel!!.accountStatus = EventLog.RegisterAndLogin.AccountStatus.NOT_LOGIN
            EventLog.RegisterAndLogin.report23(eventModel!!.pageSource, eventModel!!.accountStatus)
        }
        tv_sign_up.setOnClickListener {

            if (HalfLoginManager.getInstancce().isExistAccount) {
                val bundle = Bundle()
                bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER))
                EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_LOGIN_PAGE, bundle))
            } else
                RegisterActivity.show(it.context, 0, eventModel)
        }
        tv_log_in.setOnClickListener {

            RegistActivity.show(it.context, RegisteredConstant.FRAGMENT_THIRD_LOGIN, eventModel)

        }

    }

    private fun getTermsSpannable(): Spannable {
        val termsEx = ResourceTool.getString("regist_terms_service_ex")
        val terms = ResourceTool.getString("regist_terms_service")
        val termsSpannable = SpannableString(termsEx + terms)
        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#AFB0B1")
                ds.isUnderlineText = true
            }

            override fun onClick(widget: View) {
                WebViewActivity.luanch(widget.context, GlobalConfig.USER_SERVICE, R.color.white)
            }
        }
        termsSpannable.setSpan(clickableSpan, termsEx.length, termsEx.length + terms.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return termsSpannable
    }


    override fun onStart() {
        super.onStart()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.dialog_bottom_exit)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish()
        }
    }
}
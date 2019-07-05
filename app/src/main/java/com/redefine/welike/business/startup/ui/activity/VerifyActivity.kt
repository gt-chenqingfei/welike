package com.redefine.welike.business.startup.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.h5.WebViewActivity
import com.redefine.commonui.widget.LoadingTranslateBgDlg1
import com.redefine.foundation.framework.Event
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.business.startup.ui.viewmodel.VerifyViewModel
import com.redefine.welike.commonui.activity.MainActivityEx
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import kotlinx.android.synthetic.main.activity_verify_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author redefine honlin
 * @Date on 2018/10/29
 * @Description
 */

@Route(path = RouteConstant.LAUNCH_VERIFY_ACTIVITY)
class VerifyActivity : BaseActivity() {

    private var loadingDlg: LoadingTranslateBgDlg1? = null

    private var needOpenMain = false

    private lateinit var verifyViewModel: VerifyViewModel
    private  var eventModel: RegisterAndLoginModel?=null

    companion object {
        fun show(context: Context) {
            val intent = Intent()
            intent.setClass(context, VerifyActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_layout)
        EventBus.getDefault().register(this)

        verifyViewModel = ViewModelProviders.of(this).get(VerifyViewModel::class.java)
        needOpenMain = intent.extras.getBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, false)
        var event = intent.extras.getSerializable(RegisteredConstant.KEY_EVENT_MODEL)
        event?.let {
            eventModel = event as RegisterAndLoginModel
        }

        initView()

        setThirdLoginEvent()

        setEvent()

        verifyViewModel.getPageStatus().observe(this, Observer<PageStatusEnum> { pageStatusEnum ->
            if (pageStatusEnum == null) return@Observer

            when (pageStatusEnum) {
                PageStatusEnum.LOADING -> showLoadingDialog()
                PageStatusEnum.CONTENT, PageStatusEnum.ERROR -> dissmissLoadingDialog()
            }
        })

        verifyViewModel.getCodeStatus().observe(this, Observer<Int> { errorCode ->
            if (errorCode == null) return@Observer

            if (errorCode == ErrorCode.ERROR_SUCCESS) {
                jumpMain()
                eventModel?.let { EventLog.RegisterAndLogin.report25(it.accountStatus, it.verifySource, it.pageSource, it.loginSource) }
                return@Observer
            }

            val showText = ErrorCode.showErrCodeText(errorCode)
            if (!TextUtils.isEmpty(showText)) {
                ToastUtils.showShort(showText)
            }
        })

    }


    private fun initView() {

        user_terms.movementMethod = (LinkMovementMethod.getInstance())
        user_terms.text = getTermsSpannable()

        if (!third_login_layout.truecallerUsable()) {
            third_login_layout.truecallerHideOnUnusable()
        }

        eventModel?.let {
            it.tcInstalled = if (third_login_layout.truecallerUsable()) EventLog.RegisterAndLogin.TcInstalled.YES else EventLog.RegisterAndLogin.TcInstalled.NO
            it.loginVerifyType = EventLog.RegisterAndLogin.LoginVerifyType.LINK_ACCOUNT
            it.accountStatus = EventLog.RegisterAndLogin.AccountStatus.LOGIN_UNVERIFY
            it.verifySource = EventLog.RegisterAndLogin.VerifySource.INVALID
            EventLog.RegisterAndLogin.report24(eventModel!!.pageSource, eventModel!!.accountStatus, eventModel!!.verifySource)
        }
    }

    private fun setEvent() {

        tv_login_phone.setOnClickListener {

            //            todo
            val bundle = Bundle()
            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_VERIFY_PHONE_PAGE, bundle))
            reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.PHONE_NUMBER)
        }

        iv_close.setOnClickListener {

            jumpMain()
        }
    }

    private fun jumpMain() {
        if (needOpenMain) {
            MainActivityEx.show(this)
            this.overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out)
            EventBus.getDefault().post(Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null))
        }

        finish()
    }

    fun setThirdLoginEvent() {
        third_login_layout.registerCallback(object : ThirdLoginCallback {
            override fun onLoginBtnClick(thirdLoginType: ThirdLoginType) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.FACEBOOK)
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    showLoadingDialog()
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.GOOGLE)
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER)
                }
            }

            override fun onLoginSuccess(thirdLoginType: ThirdLoginType, profile: ThirdLoginProfile) {
                dissmissLoadingDialog()
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    val facebookProfile = profile.facebookProfile
                    if (facebookProfile != null) {
                        verifyViewModel.tryBindFacebook(facebookProfile.accessToken.token)
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.SUCCESS)
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    val googleProfile = profile.googleProfile
                    if (googleProfile != null) {
                        verifyViewModel.tryBindGoogle(googleProfile.idToken)
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.SUCCESS)
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    val trueCallerProfile = profile.mTrueCallerProfile
                    if (trueCallerProfile != null) {
                        verifyViewModel.tryBindTrueCaller(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm)
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER, EventLog.RegisterAndLogin.ReturnResult.SUCCESS)
                }
            }

            override fun onLoginFail(thirdLoginType: ThirdLoginType, errorCode: Int) {
                dissmissLoadingDialog()
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    ToastUtils.showShort("Login failed")
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.FAIL)
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    ToastUtils.showShort("Login failed")
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.FAIL)
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER, EventLog.RegisterAndLogin.ReturnResult.FAIL)
                }
            }

            override fun onLoginCancel(thirdLoginType: ThirdLoginType) {
                dissmissLoadingDialog()
                val cancelText = ResourceTool.getString("canceled")
                ToastUtils.showShort(cancelText)
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.CANCEL)
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.CANCEL)
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER, EventLog.RegisterAndLogin.ReturnResult.CANCEL)
                }
            }
        })

    }

    override fun onBackPressed() {
        jumpMain()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        third_login_layout.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        dissmissLoadingDialog()
        EventBus.getDefault().unregister(this)
    }

    private fun showLoadingDialog() {
        if (loadingDlg != null) {
            loadingDlg?.dismiss()
            loadingDlg = null
        }
        loadingDlg = LoadingTranslateBgDlg1(this)
        loadingDlg?.show()
    }

    private fun dissmissLoadingDialog() {
        if (loadingDlg != null) {
            loadingDlg?.dismiss()
            loadingDlg = null
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish()
        }
    }

    private fun reportClickThirdLoginClick(loginSource: EventLog.RegisterAndLogin.LoginSource) {
        eventModel?.let {
            it.loginSource = loginSource
            it.verifySource = EventLog.RegisterAndLogin.VerifySource.INVALID
            EventLog.RegisterAndLogin.report2(it.loginSource, it.loginVerifyType, it.pageType, it.verifySource)
        }
    }

    private fun reportClickThirdLoginCallback(loginSource: EventLog.RegisterAndLogin.LoginSource, returnResult: EventLog.RegisterAndLogin.ReturnResult) {
        eventModel?.let {
            it.loginSource = loginSource
            it.returnResult = returnResult
            EventLog.RegisterAndLogin.report3(it.loginSource, it.newUser, it.returnResult)
        }
    }

}
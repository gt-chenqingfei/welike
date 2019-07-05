package com.redefine.welike.business.startup.ui.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.facebook.drawee.backends.pipeline.Fresco
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.widget.LoadingTranslateBgDlg1
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.profile.bean.Account
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.business.startup.ui.activity.RegistActivity
import com.redefine.welike.business.startup.ui.viewmodel.LoginViewModel
import com.redefine.welike.common.CompatUtil
import com.redefine.welike.commonui.activity.MainActivityEx
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.event.RouteDispatcher
import com.redefine.welike.hive.AppsFlyerManager
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import kotlinx.android.synthetic.main.dialog_login_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author redefine honlin
 * @Date on 2018/10/29
 * @Description
 */
class LoginDialog : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    var loadingDlg: LoadingTranslateBgDlg1? = null
    private lateinit var eventModel: RegisterAndLoginModel


    companion object {
        fun show(context: Context, eventModel: RegisterAndLoginModel) {
            val intent = Intent()
            intent.setClass(context, LoginDialog::class.java)
            intent.putExtra(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.dialog_bottom_enter,0)
        super.onCreate(savedInstanceState)

        CompatUtil.disableAutoFill(this)

        EventBus.getDefault().register(this)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        setContentView(R.layout.dialog_login_layout)

        var d = windowManager.defaultDisplay
        val p = window.attributes
        p.height = ScreenUtils.dip2Px(this, 428f)
        p.width = d.width
        p.gravity = Gravity.BOTTOM
        window.attributes = (p)

//        window.setWindowAnimations(R.style.dialog_anim_pan)


        initData()

        setEvent()

        setViewModel()

    }


    private fun initData() {

        if (HalfLoginManager.getInstancce().userInfoBean == null) {
            sdv_user_avatar.visibility = View.GONE
            tv_user_name.visibility = View.GONE
            cl_login_with_this_account.visibility = View.GONE
        } else {

            val controller = Fresco.newDraweeControllerBuilder()
                    .setUri(HalfLoginManager.getInstancce().userInfoBean.avatarUrl)
                    .setOldController(sdv_user_avatar.controller)
                    .build()

            sdv_user_avatar.controller = (controller)

            tv_user_name.text = HalfLoginManager.getInstancce().userInfoBean.nickName

            if (HalfLoginManager.getInstancce().userInfoBean.source == 2) {
                iv_vidmate.visibility = View.VISIBLE
            }
        }

    }

    private fun setEvent() {

        cl_login_with_this_account.setOnClickListener {

            if (HalfLoginManager.getInstancce().userInfoBean.status == Account.ACCOUNT_HALF) {
                loginViewModel.tryLogin(HalfLoginManager.getInstancce().userInfoBean.nickName)
            } else {

                third_login_layout.setClickButton(HalfLoginManager.getInstancce().userInfoBean.registerWay)
            }

        }


        third_login_layout.phoneInsteadTruecallerOnUnusable()
        if (third_login_layout.truecallerUsable()) {
            login_with_mobile.visibility = (View.VISIBLE)
        } else {
            login_with_mobile.visibility = (View.INVISIBLE)
        }

        login_with_mobile.setOnClickListener {

            RegistActivity.show(it.context, RegisteredConstant.FRAGMENT_PHONENUM, null)

        }

        third_login_layout.registerCallback(object : ThirdLoginCallback {
            override fun onLoginBtnClick(thirdLoginType: ThirdLoginType) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.FACEBOOK)
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    showLoadingDialog()
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.GOOGLE)
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER)
                } else if (thirdLoginType == ThirdLoginType.PHONE) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.PHONE_NUMBER)
                    RegistActivity.show(this@LoginDialog, RegisteredConstant.FRAGMENT_PHONENUM, null)
                }
            }

            override fun onLoginSuccess(thirdLoginType: ThirdLoginType, profile: ThirdLoginProfile) {
                dissmissLoadingDialog()
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    val facebookProfile = profile.facebookProfile
                    if (facebookProfile != null) {
                        loginViewModel.loginFacebook(facebookProfile.accessToken.token)
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.SUCCESS)
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    val googleProfile = profile.googleProfile
                    if (googleProfile != null) {
                        loginViewModel.loginGoogle(googleProfile.idToken)
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.SUCCESS)
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    val trueCallerProfile = profile.mTrueCallerProfile
                    if (trueCallerProfile != null) {
                        loginViewModel.loginTrueCaller(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm)
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
                    ToastUtils.showShort("Login failed")
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

        val event = intent.extras.getSerializable(RegisteredConstant.KEY_EVENT_MODEL)
        event?.let {
            eventModel = event as RegisterAndLoginModel
            eventModel.tcInstalled = if (third_login_layout.truecallerUsable()) EventLog.RegisterAndLogin.TcInstalled.YES else EventLog.RegisterAndLogin.TcInstalled.NO
            eventModel.loginVerifyType = EventLog.RegisterAndLogin.LoginVerifyType.LINK_ACCOUNT
            eventModel.accountStatus = EventLog.RegisterAndLogin.AccountStatus.NOT_LOGIN
            eventModel.pageType = EventLog.RegisterAndLogin.PageType.DIALOG
            eventModel.loginVerifyType = EventLog.RegisterAndLogin.LoginVerifyType.LINK_ACCOUNT_ME
            EventLog.RegisterAndLogin.report1(eventModel.pageSource, eventModel.language, eventModel.tcInstalled, eventModel.loginVerifyType, eventModel.accountStatus, eventModel.pageType)
        }
    }

    private fun setViewModel() {


        loginViewModel.codeStatus.observe(this, Observer<Int> {

            if (it == null) return@Observer

            when (it) {
                ErrorCode.ERROR_SUCCESS -> {
                    eventModel?.let {
                        sendEvent()
                    }
                }

                else -> {
                    ToastUtils.showShort(ErrorCode.showErrCodeText(it))
                }
            }

        })

        loginViewModel.loginStatus.observe(this, Observer {

            if (it == null) return@Observer

            when (it) {
                LoginViewModel.LoginStatus.LOGIN_ING -> showLoadingDialog()
                LoginViewModel.LoginStatus.LOGIN_SUCCESS -> {
                    dissmissLoadingDialog()
                    jump2Main()
                }
                LoginViewModel.LoginStatus.LOGIN_FAIL -> dissmissLoadingDialog()
            }


        })

    }

    private fun sendEvent() {
        eventModel?.let {
            EventLog.RegisterAndLogin.report18(eventModel.nickName, eventModel.nickNameCheck, eventModel.phoneNumber, eventModel.verifyType,
                    eventModel.smsSend, eventModel.stayTime, eventModel.loginSource, eventModel.returnResult, eventModel.newUser, eventModel.language,
                    eventModel.smsCheck, eventModel.pageSource, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay, eventModel.requestWay,
                    eventModel.fromPage, eventModel.loginVerifyType, eventModel.accountStatus, eventModel.pageType, eventModel.verifySource)
        }
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

    private fun jump2Main() {

        val uri = Uri.parse(AppsFlyerManager.getInstance().referrerUri)
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation()
        } else
            MainActivityEx.show(this)
        EventBus.getDefault().post(Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null))

        finish()
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


    override fun finish() {
        super.finish()
        window.setDimAmount(0f)
        overridePendingTransition(0,R.anim.dialog_bottom_exit)
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
            it.verifySource = EventLog.RegisterAndLogin.VerifySource.DIALOG_LINK
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
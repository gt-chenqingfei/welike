package com.redefine.welike.business.startup.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.facebook.drawee.backends.pipeline.Fresco
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.widget.LoadingTranslateBgDlg1
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.profile.bean.Account
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
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
import kotlinx.android.synthetic.main.activity_login_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author redefine honlin
 * @Date on 2018/10/29
 * @Description
 */
@Route(path = RouteConstant.LAUNCH_LOGIN_ACTIVITY)
class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    var loadingDlg: LoadingTranslateBgDlg1? = null
    private lateinit var eventModel: RegisterAndLoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.dialog_bottom_enter,R.anim.activity_normal_exit)
        super.onCreate(savedInstanceState)

        CompatUtil.disableAutoFill(this)

        EventBus.getDefault().register(this)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        setContentView(R.layout.activity_login_activity)

        initData()

        setEvent()

        setViewModel()

        var event = intent.extras.getSerializable(RegisteredConstant.KEY_EVENT_MODEL)
        event?.let {
            eventModel = event as RegisterAndLoginModel
            eventModel.verifySource = EventLog.RegisterAndLogin.VerifySource.INVALID
            eventModel.accountStatus = EventLog.RegisterAndLogin.AccountStatus.NOT_LOGIN
            eventModel.tcInstalled = if (third_login_layout.truecallerUsable()) EventLog.RegisterAndLogin.TcInstalled.YES else EventLog.RegisterAndLogin.TcInstalled.NO
            EventLog.RegisterAndLogin.report1(eventModel.pageSource, eventModel.language, eventModel.tcInstalled, eventModel.loginVerifyType,
                    eventModel.accountStatus, eventModel.pageType)
        }
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

        iv_close.setOnClickListener {
            finish()
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
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    showLoadingDialog()
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                } else if (thirdLoginType == ThirdLoginType.PHONE) {
                    RegistActivity.show(this@LoginActivity, RegisteredConstant.FRAGMENT_PHONENUM, null)
                }
            }

            override fun onLoginSuccess(thirdLoginType: ThirdLoginType, profile: ThirdLoginProfile) {
                dissmissLoadingDialog()
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    val facebookProfile = profile.facebookProfile
                    if (facebookProfile != null) {
                        loginViewModel.loginFacebook(facebookProfile.accessToken.token)
                    }
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    val googleProfile = profile.googleProfile
                    if (googleProfile != null) {
                        loginViewModel.loginGoogle(googleProfile.idToken)
                    }
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    val trueCallerProfile = profile.mTrueCallerProfile
                    if (trueCallerProfile != null) {
                        loginViewModel.loginTrueCaller(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm)
                    }
                }
            }

            override fun onLoginFail(thirdLoginType: ThirdLoginType, errorCode: Int) {
                dissmissLoadingDialog()
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    ToastUtils.showShort("Login failed")
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    ToastUtils.showShort("Login failed")
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    ToastUtils.showShort("Login failed")
                }
            }

            override fun onLoginCancel(thirdLoginType: ThirdLoginType) {
                dissmissLoadingDialog()
                val cancelText = ResourceTool.getString("canceled")
                ToastUtils.showShort(cancelText)
            }
        })

    }

    private fun setViewModel() {


        loginViewModel.codeStatus.observe(this, Observer<Int> {

            if (it == null) return@Observer

            when (it) {
                ErrorCode.ERROR_SUCCESS -> {
                    eventModel?.let {
                        EventLog.RegisterAndLogin.report18(it.nickName, it.nickNameCheck, it.phoneNumber, it.verifyType,
                                it.smsSend, it.stayTime, it.loginSource, it.returnResult, it.newUser, it.language,
                                it.smsCheck, it.pageSource, it.pageStatus, it.tcInstalled, it.inputWay, it.requestWay,
                                it.fromPage, it.loginVerifyType, it.accountStatus, it.pageType, it.verifySource)
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

//        if (AccountManager.getInstance().account?.status == Account.ACCOUNT_HALF) {
//            val bundle = Bundle()
//            bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, true)
//            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle))
//        } else {

        val uri = Uri.parse(AppsFlyerManager.getInstance().referrerUri)
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation()
        } else
            MainActivityEx.show(this)

//        }
        this.overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out)

        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        third_login_layout.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.dialog_bottom_exit)
    }

    override fun onDestroy() {
        super.onDestroy()

        dissmissLoadingDialog()

        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish()
        }
    }
}
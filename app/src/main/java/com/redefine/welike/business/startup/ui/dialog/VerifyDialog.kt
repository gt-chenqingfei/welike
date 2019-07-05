package com.redefine.welike.business.startup.ui.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.widget.LoadingTranslateBgDlg1
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
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
import kotlinx.android.synthetic.main.dialog_verify_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author redefine honlin
 * @Date on 2018/10/29
 * @Description
 */
@Route(path = RouteConstant.LAUNCH_VERIFY_DIALOG)
class VerifyDialog : BaseActivity() {

    private var loadingDlg: LoadingTranslateBgDlg1? = null

    private lateinit var verifyViewModel: VerifyViewModel
    private var eventModel: RegisterAndLoginModel? = null

    companion object {
        fun show(context: Context, eventModel: RegisterAndLoginModel) {
            val intent = Intent()
            intent.setClass(context, VerifyDialog::class.java)
            intent.putExtra(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.dialog_bottom_enter, 0)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_verify_layout)

        var d = windowManager.defaultDisplay
        val p = window.attributes
        p.height = ScreenUtils.dip2Px(this, 247f)
        p.width = d.width
        p.gravity = Gravity.BOTTOM
        window.attributes = (p)



        EventBus.getDefault().register(this)

        verifyViewModel = ViewModelProviders.of(this).get(VerifyViewModel::class.java)
        var event = intent.extras.getSerializable(RegisteredConstant.KEY_EVENT_MODEL)
        event?.let {
            eventModel = event as RegisterAndLoginModel
            eventModel!!.tcInstalled = if (third_login_layout.truecallerUsable()) EventLog.RegisterAndLogin.TcInstalled.YES else EventLog.RegisterAndLogin.TcInstalled.NO
            eventModel!!.verifySource = EventLog.RegisterAndLogin.VerifySource.DIALOG_LINK
            eventModel!!.accountStatus = EventLog.RegisterAndLogin.AccountStatus.LOGIN_UNVERIFY
            eventModel!!.pageType = EventLog.RegisterAndLogin.PageType.DIALOG
            EventLog.RegisterAndLogin.report24(eventModel!!.pageSource, eventModel!!.accountStatus, eventModel!!.verifySource)
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

        third_login_layout.phoneInsteadTruecallerOnUnusable()
        if (third_login_layout.truecallerUsable()) {
            login_with_mobile.visibility = (View.VISIBLE)
        } else {
            login_with_mobile.visibility = (View.INVISIBLE)
        }

    }

    private fun setEvent() {

        login_with_mobile.setOnClickListener {

            //            todo
            val bundle = Bundle()
            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_VERIFY_PHONE_PAGE, bundle))
            reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.PHONE_NUMBER)
        }

    }

    private fun jumpMain() {
        MainActivityEx.show(this)
        EventBus.getDefault().post(Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null))

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
                } else if (thirdLoginType == ThirdLoginType.PHONE) {
                    val bundle = Bundle()
                    bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
                    EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_VERIFY_PHONE_PAGE, bundle))
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.PHONE_NUMBER)
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
        overridePendingTransition(0, R.anim.dialog_bottom_exit)
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


    override fun finish() {
        super.finish()
        window.setDimAmount(0f)
        overridePendingTransition(0, R.anim.dialog_bottom_exit)
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
package com.redefine.welike.business.startup.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.transition.TransitionManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.widget.LoadingDlg
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.MyApplication
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.business.startup.management.bean.NickNameCheckerBean
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.business.startup.ui.viewmodel.RegisterViewModel
import com.redefine.welike.common.CompatUtil
import com.redefine.welike.commonui.activity.MainActivityEx
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.event.RouteDispatcher
import com.redefine.welike.hive.AppsFlyerManager
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import kotlinx.android.synthetic.main.activity_register_create_account.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author redefine honlin
 * @Date on 2018/10/29
 * @Description
 */
@Route(path = RouteConstant.LAUNCH_REGISTER_ACTIVITY)
class RegisterActivity : BaseActivity() {

    private var mInputShown = false

    private lateinit var registerModel: RegisterViewModel

    private var nickname: String = ""

    private var canNext = false

    private var titleType = 0

    private lateinit var loadingDlg: LoadingDlg
    private var eventModel: RegisterAndLoginModel? = null

    companion object {
        fun show(context: Context, type: Int, eventModel: RegisterAndLoginModel?) {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putInt(RegisteredConstant.LAUNCH_REGISTER_TYPE, type)
            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
            intent.putExtras(bundle)
            intent.setClass(MyApplication.getAppContext(), RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.dialog_bottom_enter,0)
        super.onCreate(savedInstanceState)

        CompatUtil.disableAutoFill(this)


        EventBus.getDefault().register(this)


        registerModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        setContentView(R.layout.activity_register_create_account)

        loadingDlg = LoadingDlg(this@RegisterActivity)

        initData()

        setEvent()

        setViewModel()

        reportEvent()
    }

    private fun reportEvent() {
        eventModel?.let {
            it.accountStatus = EventLog.RegisterAndLogin.AccountStatus.NOT_LOGIN
            EventLog.RegisterAndLogin.report20(it.accountStatus, it.pageSource)
        }
    }

    private fun initData() {
        titleType = intent.extras.getInt(RegisteredConstant.LAUNCH_REGISTER_TYPE, 0)
        val event = intent.extras.getSerializable(RegisteredConstant.KEY_EVENT_MODEL)
        event?.let { eventModel = event as RegisterAndLoginModel }
    }

    private fun setEvent() {


        when (titleType) {
            0 -> {
                tv_regist_name_title.text = resources.getString(R.string.register_nickname_title)
            }
            1 -> {
                tv_regist_name_title.text = resources.getString(R.string.register_nickname_title1)

            }
            2 -> {
                tv_regist_name_title.text = resources.getString(R.string.register_nickname_title2)

            }
        }
        et_register_name.requestFocus()
        InputMethodUtil.showInputMethod(et_register_name)


        //监听软键盘状态
        cl_root_view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            cl_root_view.getWindowVisibleDisplayFrame(rect)
            val height = cl_root_view.rootView.height
            val heightDefere = height - rect.bottom

            if (heightDefere < 200) {
                hideInputMode()
            } else {
                showInputMode()
//                if (canCkecked) {
//                    canCkecked = false
//                    registerModel.checkLoginState()
//                    eventModel?.let {
//                        EventLog.RegisterAndLogin.report21(EventLog.RegisterAndLogin.AccountStatus.NOT_LOGIN, it.pageSource)
//                    }
//                }
            }

            mInputShown = heightDefere > 200


        }

        et_register_name.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {

                canNext = false

                if (TextUtils.isEmpty(s.toString())) {
                    checkEmpty()

                } else {

                    registerModel.checkNickName(s.toString())

                    nickname = s.toString()
                    eventModel?.let { it.nickNameCheck++ }
                }

            }

        })

        btn_regist_pickPhoto_next.setOnClickListener {

            if (!TextUtils.isEmpty(nickname) && canNext) {

                registerModel.tryLogin(nickname)

                eventModel?.let {
                    it.nickName = nickname
                    it.newUser = EventLog.RegisterAndLogin.NewUser.NEW_USER
                    EventLog.RegisterAndLogin.report22(EventLog.RegisterAndLogin.AccountStatus.NOT_LOGIN, eventModel?.pageSource, nickname, EventLog.RegisterAndLogin.NewUser.NEW_USER)
                }
            }

        }

        iv_register_nick_delete.setOnClickListener {

            canNext = false
            nickname = ""
            et_register_name.setText(nickname)
            btn_regist_pickPhoto_next.isEnabled = (false)
        }

//        tv_login_1.setOnClickListener {
//            RegistActivity.show(it.context, eventModel)
//        }

        iv_close.setOnClickListener {
            finish()
        }

    }

    private fun showInputMode() {
        iv_logo.visibility = (View.GONE)
        btn_regist_pickPhoto_next.visibility = View.VISIBLE
        (rl_content.layoutParams as ConstraintLayout.LayoutParams).topMargin = ScreenUtils.dip2Px(this, 80f)
        rl_content.layoutParams = rl_content.layoutParams

        (rl_regist_name.layoutParams as RelativeLayout.LayoutParams).topMargin = ScreenUtils.dip2Px(this, 64f)
        rl_regist_name.layoutParams = rl_regist_name.layoutParams
        TransitionManager.beginDelayedTransition(cl_root_view)
        view_edit_line.setBackgroundColor(resources.getColor(R.color.main_orange_dark))

    }

    private fun hideInputMode() {
        iv_logo.visibility = (View.VISIBLE)
        if (TextUtils.isEmpty(nickname))
            btn_regist_pickPhoto_next.visibility = View.GONE
        (rl_content.layoutParams as ConstraintLayout.LayoutParams).topMargin = ScreenUtils.dip2Px(this, 150f)
        rl_content.layoutParams = rl_content.layoutParams

        (rl_regist_name.layoutParams as RelativeLayout.LayoutParams).topMargin = ScreenUtils.dip2Px(this, 59f)
        rl_regist_name.layoutParams = rl_regist_name.layoutParams
        TransitionManager.beginDelayedTransition(cl_root_view)
        view_edit_line.setBackgroundColor(resources.getColor(R.color.main_grey))

    }

    private fun setViewModel() {

        registerModel.nickNameBeanData.observe(this, Observer<NickNameCheckerBean> {
            if (it == null) return@Observer

            if (it.errCode == ErrorCode.ERROR_SUCCESS) {

                if (it.repeat) {

                    if (it.gt) {
                        tv_register_username_intro.text = String.format(resources.getString(R.string.register_nickname_use_more), it.repeatNum)
                    } else {
                        tv_register_username_intro.text = String.format(resources.getString(R.string.register_nickname_use_less), it.repeatNum)
                    }

                } else {

                    tv_register_username_intro.text = resources.getString(R.string.register_nickname_unique)

                }

                canNext = true

            } else {

                tv_register_username_intro.text = (ErrorCode.showErrCodeText(it.errCode))
                canNext = false
            }


        })

        registerModel.checkStatus.observe(this, Observer {
            if (it == null) return@Observer

            when (it) {

                PageStatusEnum.LOADING -> {
                    checkLoading()

                }

                PageStatusEnum.CONTENT -> {
                    checkContent()
                }

            }

        })


        registerModel.codeStatus.observe(this, Observer {
            if (it == null) return@Observer


            when (it) {

                ErrorCode.ERROR_SUCCESS -> {

                    jump2Main()
                    eventModel?.let {
                        EventLog.RegisterAndLogin.report18(it.nickName, it.nickNameCheck, it.phoneNumber, it.verifyType,
                                it.smsSend, it.stayTime, it.loginSource, it.returnResult, it.newUser, it.language,
                                it.smsCheck, it.pageSource, it.pageStatus, it.tcInstalled, it.inputWay, it.requestWay,
                                it.fromPage, it.loginVerifyType, it.accountStatus, it.pageType, it.verifySource)
                    }

                }
                else -> {

                    if (it == ErrorCode.ERROR_NETWORK_USER_WAS_FREQUENTLY) {
                        ToastUtils.showShort(ErrorCode.showErrCodeText(it))
                    } else
                        ToastUtils.showShort(resources.getString(R.string.user_half_error_not_found))

                }

            }


        })


        registerModel.loginStatus.observe(this, Observer {

            if (it == null) return@Observer

            when (it) {
                PageStatusEnum.LOADING -> {
                    loadingDlg.show()
                }

                PageStatusEnum.CONTENT -> {
                    loadingDlg.dismiss()
                }
                PageStatusEnum.ERROR -> {
                    loadingDlg.dismiss()
                }
            }

        })

    }

    private fun jump2Main() {

//        if (AccountManager.getInstance().account?.status == Account.ACCOUNT_HALF) {
//            val bundle = Bundle()
//            bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, true)
//            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, eventModel)
//            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_VERIFY_PAGE, bundle))
//        } else

        val uri = Uri.parse(AppsFlyerManager.getInstance().referrerUri)
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation()
        } else
            MainActivityEx.show(this)
        EventBus.getDefault().post(Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null))

        this.overridePendingTransition(R.anim.sliding_right_in, R.anim.sliding_to_left_out)

        finish()
    }

    private fun checkLoading() {
        iv_register_check_anim.visibility = View.VISIBLE

        iv_register_nick_delete.visibility = View.GONE

        tv_register_username_intro.visibility = View.GONE

    }

    fun checkContent() {
        iv_register_check_anim.visibility = View.GONE

        iv_register_nick_delete.visibility = View.VISIBLE

        tv_register_username_intro.visibility = View.VISIBLE

        btn_regist_pickPhoto_next.isEnabled = (true)
    }

    fun checkEmpty() {
        iv_register_check_anim.visibility = View.GONE

        iv_register_nick_delete.visibility = View.GONE

        tv_register_username_intro.visibility = View.GONE


        btn_regist_pickPhoto_next.isEnabled = (false)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.dialog_bottom_exit)
    }

    override fun onDestroy() {
        super.onDestroy()

        loadingDlg.dismiss()

        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish()
        }


    }
}
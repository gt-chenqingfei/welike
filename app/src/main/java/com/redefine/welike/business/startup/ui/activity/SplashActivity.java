package com.redefine.welike.business.startup.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.welike.R;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.util.DateTimeUtil;
import com.redefine.welike.base.util.MemoryExt;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.browse.ui.activity.AvoidTokenDiscoverActivity;
import com.redefine.welike.business.startup.management.bean.SplashEntity;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.viewmodel.SplashViewModel;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.common.util.IntentUtil;
import com.redefine.welike.common.util.SignatureUtil;
import com.redefine.welike.commonui.activity.MainActivityEx;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.DeviceInfoManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author by liubin on 2018/1/5.
 * @author by honlin on 2018/6/12.
 */

@Route(path = RouteConstant.SPLASH_ROUTE_PATH)
public class SplashActivity extends BaseActivity {


    private int time = 1;

    private long interval = 1000;


    private View mRootView;
    private SimpleDraweeView ivSplash;
//    private LottieAnimationView mSplashLottie;

    private TextView tvJumpSplash;

//    private RelativeLayout rlSlogan0;

    private LoadingDlg loadingDlg;

    private Uri uri;

    private SplashViewModel splashViewModel;

    private SplashEntity mSplashEntity;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String pushParam = "";

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            time--;

            if (time <= 0) {
                handler.removeCallbacks(runnable);
                splashViewModel.getCurrentAccountStatus();

            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        tvJumpSplash.setText(String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "splash_jump"), String.valueOf(time)));
                    }
                }, 0, TimeUnit.MILLISECONDS);
                handler.postDelayed(this, interval);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if (!new SignatureUtil().check(this)) {
            finish();
            System.exit(0);
            return;
        }
        new IntentUtil().showIntent("onCreate", getIntent());
        new IntentUtil().checkDynamicLink(this);
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        Intent mainIntent = getIntent();

        pushParam = new IntentUtil().checkPush(mainIntent);
        if (!TextUtils.isEmpty(pushParam)) {
//            if (AccountManager.getInstance().getAccount() != null) {
//                jump2Main();
//            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pushParam)));
            EventLog1.Push.report3(1000, "", EventLog1.Push.Result.POP, EventLog1.Push.PushChannel.FIREBASE, "1000",pushParam);
            EventLog1.LaunchApp.report1(EventLog1.LaunchApp.FromPush.FROM_PUSH);
            finish();
        } else {
            if (!isTaskRoot()) {
                String action = mainIntent.getAction();
                if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER)
                        && action != null
                        && action.equals(Intent.ACTION_MAIN)) {
                    this.finish();
                    return;
                }
            }
        }


        setContentView(R.layout.activity_splash);
        initViews();
        initEvents();
//        //fcm
//        if (getIntent() != null && getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                if (key.equalsIgnoreCase("clickAction")) {
//                    pushParam = String.valueOf(value);
//                }
//            }
//        }
        if (getIntent() != null) {
            this.uri = getIntent().getParcelableExtra(RouteConstant.ROUTE_KEY_STARTUP_URI);
        }

        EventLog.LaunchApp.report1(1);
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_OPEN_APP);
        ScoreManager.resetCountIfSameDay2ShowScore(this);
        DeviceInfoManager.INSTANCE.log(this);

    }

    private void initViews() {
        mRootView = findViewById(R.id.splash_root_view);
        tvJumpSplash = findViewById(R.id.tv_splash_jump);
        ivSplash = findViewById(R.id.iv_splash);
//        mSplashLottie = findViewById(R.id.splash_lottie_view);
    }

    private void resetViews() {
        ivSplash.setVisibility(View.VISIBLE);
//        mSplashLottie.setVisibility(View.GONE);
        EventLog.Splash.report1(mSplashEntity.getId(), AccountManager.getInstance().isLoginComplete());
        EventLog1.Splash.report1(mSplashEntity.getId(), AccountManager.getInstance().isLoginComplete());
        time = mSplashEntity.getPlayTime();
        tvJumpSplash.setVisibility(View.VISIBLE);
        tvJumpSplash.setText(String.format(getResources().getString(R.string.splash_jump), String.valueOf(time)));
        SpManager.Setting.setSplashShowTime(SplashActivity.this);

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP)
                .setActualImageFocusPoint(new PointF(.5f, 0f))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(mSplashEntity.getImgUrl())
                .setOldController(ivSplash.getController())
                .build();

        ivSplash.setHierarchy(hierarchy);
        ivSplash.setController(controller);
    }

    private void initEvents() {
//        mRootView.setBackgroundResource(R.drawable.shape_splash_gradient);
//        mSplashLottie.setAnimation("welike_splash.json");
//        mSplashLottie.playAnimation();
        tvJumpSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                splashViewModel.getCurrentAccountStatus();
            }
        });

        ivSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/5/10  走scheme filter activity的逻辑


                if (mSplashEntity == null) return;
//
                if ((mSplashEntity.getNeedLogin() || mSplashEntity.getNeedUserInfo()) && !AccountManager.getInstance().isLoginComplete()) {
                    handler.removeCallbacks(runnable);

                    splashViewModel.getCurrentAccountStatus2Login();

                    return;
                }

//                if (AccountManager.getInstance().isLoginComplete() && !TextUtils.isEmpty(mSplashEntity.getRouting())) {
                if (!TextUtils.isEmpty(mSplashEntity.getRouting())) {

                    uri = Uri.parse(mSplashEntity.getRouting());

                    boolean validUri = RouteDispatcher.validUri(uri);
                    if (validUri) {
                        EventLog.Splash.report2(mSplashEntity.getId(), AccountManager.getInstance().isLoginComplete());
                        EventLog1.Splash.report2(mSplashEntity.getId(), AccountManager.getInstance().isLoginComplete());
                        handler.removeCallbacks(runnable);

                        splashViewModel.getCurrentAccountStatus();

                    }
                }
            }
        });

        setVmEvent();
    }

    private void setVmEvent() {
        splashViewModel.getmSplashEntity().observe(this, new Observer<SplashEntity>() {
            @Override
            public void onChanged(SplashEntity splashEntity) {
                mSplashEntity = splashEntity;
//                if (mSplashEntity.getNeedLogin()) {
//                    if (AccountManager.getInstance().isLoginComplete()) resetViews();
//                } else
                resetViews();


                handler.postDelayed(runnable, interval);
            }
        });

        splashViewModel.getmPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) return;
                switch (pageStatusEnum) {
                    case EMPTY:
                        handler.postDelayed(runnable, interval);
                        break;
                }
            }
        });

        splashViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (loadingDlg != null) loadingDlg.dismiss();
                if (integer == null) return;
                switch (integer) {
                    case StartConstant.START_STATE_VIDMATE:
                        jump2Browse();
                        break;
                    case StartConstant.START_STATE_MAIN:
                        jump2Main();
                        break;
                    case StartConstant.START_STATE_LANG:
                        jump2Registered(RegisteredConstant.FRAGMENT_LANGUAGE);
                        break;
                    case StartConstant.START_STATE_THIRD_LOGIN:
                        jump2Registered(RegisteredConstant.FRAGMENT_THIRD_LOGIN);
                        break;
                    case StartConstant.START_STATE_REGISTER_USERINFO:
                        jump2Registered(RegisteredConstant.FRAGMENT_USERINFO);
                        break;
                    case StartConstant.START_STATE_REGISTER_INTERESTS:
                        jump2Registered(RegisteredConstant.FRAGMENT_INTERET);
                        break;
                    case StartConstant.START_STATE_REGISTER_SUG_USERS:
                        jump2Registered(RegisteredConstant.FRAGMENT_RECOMMOND);
                        break;
                    default:
                        jump2Registered(RegisteredConstant.FRAGMENT_PHONENUM);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new IntentUtil().showIntent("onStart", getIntent());
        long lastTime = SpManager.Setting.getSplashShowTime(this);
        if ((System.currentTimeMillis() - lastTime) > TimeUtil.HOUR_1 / 2) {
            splashViewModel.getSplashData();
        } else {
            splashViewModel.getCurrentAccountStatus();
        }

        splashViewModel.getTopicData();
        AppsFlyerManager.getInstance().checkOpen(getApplication());
    }


    @Override
    protected void onDestroy() {

        if (loadingDlg != null) loadingDlg.dismiss();
        super.onDestroy();
        handler.removeCallbacks(runnable);
//        if (mSplashLottie != null) {
//            mSplashLottie.cancelAnimation();
//        }
    }

    private void jump2Main() {
        if (!TextUtils.isEmpty(pushParam)) {
            uri = Uri.parse(pushParam);
        }
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
        } else {
            MainActivityEx.show(this);
//            MainPageActivity.launcher(this);
        }
        this.overridePendingTransition(0, 0);
        finish();
    }


    private void jump2Browse() {
        if (RouteDispatcher.validUri(uri)) {
            uri = Uri.parse(uri.toString().replace(RouteConstant.MAIN_ROUTE_PATH, RouteConstant.AVOID_ROUTE_PATH));
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
        } else {
            AvoidTokenDiscoverActivity.Companion.launch(this);
        }
        this.overridePendingTransition(0, 0);

        finish();
    }

    private void jump2Registered(int state) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        RegistActivity.show(this, state, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
        this.overridePendingTransition(0, 0);
        finish();
    }

}

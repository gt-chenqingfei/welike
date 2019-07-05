package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.commonui.widget.LoadingTranslateBgDlg;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.adapter.IntroductionViewPagerAdapter;
import com.redefine.welike.business.startup.ui.viewmodel.RegisteredViewModel;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginLayout;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;
import com.truecaller.android.sdk.TrueProfile;

import java.util.ArrayList;

/**
 * Created by nianguowang on 2018/7/17
 */
public class RegistThirdLoginFragment extends Fragment {
    public static final String THIRD_LOGIN_FRAGMENT_TAG = "third_login_fragment";

    private static final int PAGE_MAX_SIZE = 5;
    private static final int LOOP_DELAY = 3000;
    private static final int MESSAGE_WHAT_LOOP = 1;

    private ViewPager mPager;
    private LinearLayout mDotsLayout;
    private ThirdLoginLayout mThirdLoginLayout;
    private TextView mLoginMobile;
    private TextView mUserTerms;
    private LoadingTranslateBgDlg loadingDlg;

    private int mLoopIndex;
    private ArrayList<View> viewList;
    private RegisteredViewModel registeredViewModel;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_WHAT_LOOP) {
                mLoopIndex++;
                mPager.setCurrentItem(mLoopIndex % PAGE_MAX_SIZE);
                mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_LOOP, LOOP_DELAY);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_third_login, container, false);
        registeredViewModel = ViewModelProviders.of(getActivity()).get(RegisteredViewModel.class);
        initView(rootView);
        initEvent();
        setEvent();
        return rootView;
    }

    public ThirdLoginLayout getThirdLoginLayout() {
        return mThirdLoginLayout;
    }

    private void initView(View rootView) {
        mDotsLayout = rootView.findViewById(R.id.guide_dots);
        mPager = rootView.findViewById(R.id.guide_viewpager);
        mThirdLoginLayout = rootView.findViewById(R.id.third_login_layout);
        mLoginMobile = rootView.findViewById(R.id.login_with_mobile);
        mUserTerms = rootView.findViewById(R.id.user_terms);
        rootView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeredViewModel.setStatus(StartConstant.REGISTER_STATE_FINISH);
//                UnLoginEventManager.INSTANCE.setPageSource(EventConstants.UNLOGIN_PAGE_SOURCE_LOGIN);
            }
        });
        mThirdLoginLayout.phoneInsteadTruecallerOnUnusable();
        if (mThirdLoginLayout.truecallerUsable()) {
            mLoginMobile.setVisibility(View.VISIBLE);
        } else {
            mLoginMobile.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mThirdLoginLayout.getLayoutParams();
            layoutParams.bottomMargin = ScreenUtils.dip2Px(40);
        }
    }

    private void initEvent() {
        viewList = new ArrayList<>();
        for (int i = 0; i < PAGE_MAX_SIZE; i++) {
            viewList.add(initIntroView(i));
        }
        initDots(viewList.size());
        mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_LOOP, LOOP_DELAY);

        mPager.setAdapter(new IntroductionViewPagerAdapter(viewList));

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mLoopIndex = arg0;
                for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                    if (i == arg0) {
                        mDotsLayout.getChildAt(i).setSelected(true);
                    } else {
                        mDotsLayout.getChildAt(i).setSelected(false);
                    }
                }
                LottieAnimationView lottieAnimationView = viewList.get(arg0).findViewById(R.id.intro_view);
                lottieAnimationView.cancelAnimation();
                lottieAnimationView.playAnimation();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    mHandler.removeCallbacksAndMessages(null);
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_LOOP, LOOP_DELAY);
                }
                return false;
            }
        });

        mLoginMobile.setText(ResourceTool.getString("login_with_mobile"));
        mLoginMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_MOBILE);
            }
        });

        mUserTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mUserTerms.setText(getTermsSpannable());
        mThirdLoginLayout.registerCallback(new ThirdLoginCallback() {
            @Override
            public void onLoginBtnClick(ThirdLoginType thirdLoginType) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    EventLog.Login.report23(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    showLoadingDialog();
                    EventLog.Login.report25(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    EventLog.Login.report32(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                } else if (thirdLoginType == ThirdLoginType.PHONE) {
                    registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_MOBILE);
                }
            }

            @Override
            public void onLoginSuccess(ThirdLoginType thirdLoginType, ThirdLoginProfile profile) {
                dissmissLoadingDialog();
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    LoginResult facebookProfile = profile.facebookProfile;
                    if (facebookProfile != null) {
                        registeredViewModel.loginFacebook(facebookProfile.getAccessToken().getToken());
                    }
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    GoogleSignInAccount googleProfile = profile.googleProfile;
                    if (googleProfile != null) {
                        registeredViewModel.loginGoogle(googleProfile.getIdToken());
                    }
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    TrueProfile trueCallerProfile = profile.mTrueCallerProfile;
                    if (trueCallerProfile != null) {
                        LogUtil.d("wng", "True caller get payload success : " + trueCallerProfile.payload);
                        registeredViewModel.loginTrueCaller(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm);
                    }
                }
            }

            @Override
            public void onLoginFail(ThirdLoginType thirdLoginType, int errorCode) {
                dissmissLoadingDialog();
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    EventLog.Login.report24(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                    ToastUtils.showShort("Login failed");
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    EventLog.Login.report26(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage(), errorCode);
                    ToastUtils.showShort("Login failed");
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    EventLog.Login.report33(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                }
            }

            @Override
            public void onLoginCancel(ThirdLoginType thirdLoginType) {
                dissmissLoadingDialog();
                String cancelText = ResourceTool.getString("canceled");
                ToastUtils.showShort(cancelText);
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    EventLog.Login.report30(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    EventLog.Login.report31(StartEventManager.getInstance().getPage_source(), EventLog.Login.FromPage.PAGE_THIRD_LOGIN.getFromPage());
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                }
            }
        });
    }

    private Spannable getTermsSpannable() {
        String termsEx = ResourceTool.getString("regist_terms_service_ex");
        String terms = ResourceTool.getString("regist_terms_service");
        SpannableString termsSpannable = new SpannableString(termsEx + terms);
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#AFB0B1"));
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                WebViewActivity.luanch(widget.getContext(), GlobalConfig.USER_SERVICE, R.color.white);
            }
        };
        termsSpannable.setSpan(clickableSpan, termsEx.length(), termsEx.length() + terms.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return termsSpannable;
    }

    private void setEvent() {

        registeredViewModel.getThirdLoginType().observe(this, new Observer<ThirdLoginType>() {
            @Override
            public void onChanged(@Nullable ThirdLoginType thirdLoginType) {

                if (thirdLoginType != null && mThirdLoginLayout != null) {
                    mThirdLoginLayout.setThirdClick(thirdLoginType);
                }
            }
        });


    }

    private View initIntroView(int position) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.intro_fragment_view, null);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.intro_view);
        TextView tvTitle = view.findViewById(R.id.tv_intro_title);
        TextView tvIntroContent = view.findViewById(R.id.tv_intro_content);
        switch (position) {
            case 0:
                String guide1 = ResourceTool.getString("regist_guide1");
                tvIntroContent.setText(guide1);
                lottieAnimationView.setImageResource(R.drawable.common_intro_0);
                String guide2 = ResourceTool.getString("regist_guide2");
                tvTitle.setText(guide2);
                break;
            case 1:
                String guide3 = ResourceTool.getString("regist_guide3");
                lottieAnimationView.setAnimation("intro_interest.json");
                lottieAnimationView.playAnimation();
                tvTitle.setText(guide3);
                break;
            case 2:
                String guide4 = ResourceTool.getString("regist_guide4");
                tvTitle.setText(guide4);
                lottieAnimationView.setAnimation("intro_discover.json");
                lottieAnimationView.playAnimation();
                break;
            case 3:
                String guide5 = ResourceTool.getString("regist_guide5");
                tvTitle.setText(guide5);
                lottieAnimationView.setAnimation("intro_whatsapp.json");
                lottieAnimationView.playAnimation();
                break;
            case 4:
                String guide6 = ResourceTool.getString("regist_guide6");
                tvTitle.setText(guide6);
                lottieAnimationView.setAnimation("intro_publish.json");
                lottieAnimationView.playAnimation();
                break;
        }

        return view;
    }

    private void initDots(int count) {
        for (int j = 0; j < count; j++) {
            mDotsLayout.addView(initDot());
        }
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    private View initDot() {
        return LayoutInflater.from(getContext()).inflate(R.layout.intro_layout_dot, null);
    }

    private void showLoadingDialog() {
        if (getActivity() == null) {
            return;
        }
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
        loadingDlg = new LoadingTranslateBgDlg(getActivity());
        loadingDlg.show();
    }

    private void dissmissLoadingDialog() {
        if (getActivity() == null) {
            return;
        }
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}

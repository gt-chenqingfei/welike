package com.redefine.welike.business.feeds.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.redefine.commonui.widget.CircleBgTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.contract.IMainMineContract;
import com.redefine.welike.business.publisher.management.DraftManager;
import com.redefine.welike.business.publisher.management.cache.IDraftCountCallback;
import com.redefine.welike.business.publisher.ui.activity.DraftActivity;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginLayout1;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.truecaller.android.sdk.TrueProfile;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by gongguan on 2018/1/13.
 */

public class MainMineView implements IMainMineContract.IMainMineView {

    private IMainMineContract.IMainMinePresenter mPresenter;
    private TextView mDraftBoxTab;
    private CircleBgTextView mDraftBoxCount;

    private TextView mVerifyText;
    private View mVerifyView;
    private TextView mViewProfile;

    //    private View mTaskSystemContainer;
    private ImageView mTaskIcon;
    private TextView mTaskTitle;
    private TextView mTaskLevel;
    private SimpleDraweeView mTaskMetal;
    private LinearLayout mSubTaskContainer;
//    private ArrowTextView guide;


    //gender

    private TextView tvGenderTitle, tvMale, tvFemale;

    private LinearLayout llMale, llFemale, llGender;

    private LinearLayout llVerify;

    private ThirdLoginLayout1 thirdLoginLayout1;

    @Override
    public void setPresenter(IMainMineContract.IMainMinePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refreshDraftBoxCount() {
        DraftManager.getInstance().getDraftCount(new IDraftCountCallback() {
            @Override
            public void onDraftCountCallback(final int count) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if (count > 0) {
                            mDraftBoxCount.setVisibility(View.VISIBLE);
                            mDraftBoxCount.setText(String.valueOf(count));
                        } else {
                            mDraftBoxCount.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void setVerifyView(boolean show) {
        mVerifyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setVerifyAccountView(boolean show) {
        llVerify.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            EventLog.RegisterAndLogin.report24(EventLog.RegisterAndLogin.PageSource.OTHER, EventLog.RegisterAndLogin.AccountStatus.LOGIN_UNVERIFY, EventLog.RegisterAndLogin.VerifySource.MAIN_ME_LINK);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        thirdLoginLayout1.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showGenderView(boolean show) {
        llGender.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_mine_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View rootView) {
        mDraftBoxTab = rootView.findViewById(R.id.main_mine_draft_view);
        mDraftBoxCount = rootView.findViewById(R.id.main_mine_draft_count);

        mVerifyText = rootView.findViewById(R.id.user_main_verify_text);
        mVerifyView = rootView.findViewById(R.id.user_main_verify_layout);
        mViewProfile = rootView.findViewById(R.id.main_mine_view_profile);

//        mTaskSystemContainer = rootView.findViewById(R.id.task_system_container);
        mTaskIcon = rootView.findViewById(R.id.task_icon);
        mTaskTitle = rootView.findViewById(R.id.task_system_title);
        mTaskLevel = rootView.findViewById(R.id.task_level);
        mTaskMetal = rootView.findViewById(R.id.task_medal);
        mSubTaskContainer = rootView.findViewById(R.id.sub_task_container);
//        guide = rootView.findViewById(R.id.guide);


        //gender
        tvGenderTitle = rootView.findViewById(R.id.tv_gender_title);
        tvMale = rootView.findViewById(R.id.tv_male);
        tvFemale = rootView.findViewById(R.id.tv_female);


        llGender = rootView.findViewById(R.id.ll_gender);
        llMale = rootView.findViewById(R.id.ll_male);
        llFemale = rootView.findViewById(R.id.ll_female);


        //verify
        llVerify = rootView.findViewById(R.id.ll_verify);
        thirdLoginLayout1 = rootView.findViewById(R.id.third_login_layout);
        if (!thirdLoginLayout1.truecallerUsable()) {
            thirdLoginLayout1.truecallerHideOnUnusable();
        }

        rootView.findViewById(R.id.ll_draft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DraftActivity.launch(v.getContext());
                EventLog.MainMe.report12();
                EventLog1.MainMe.report12();
            }
        });
        mVerifyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onVerifyClick();
                EventLog.MainMe.report6();
                EventLog1.MainMe.report6();
            }
        });

        mDraftBoxTab.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_draft_text"));
        mVerifyText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_growth_verification"));
        mViewProfile.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "view_my_profile"));

        tvGenderTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_gender_title"));
        tvMale.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "user_sex_boy"));
        tvFemale.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "user_sex_girl"));


        llMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte sex = 0;
                mPresenter.setAccountGender(sex);
                InterestAndRecommendCardEventManager.INSTANCE.setSex(EventConstants.INTEREST_CARD_SEX_MALE);
                InterestAndRecommendCardEventManager.INSTANCE.report8();
            }
        });

        llFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte sex = 1;
                mPresenter.setAccountGender(sex);
                InterestAndRecommendCardEventManager.INSTANCE.setSex(EventConstants.INTEREST_CARD_SEX_FEMALE);
                InterestAndRecommendCardEventManager.INSTANCE.report8();
            }
        });

        thirdLoginLayout1.registerCallback(new ThirdLoginCallback() {
            @Override
            public void onLoginBtnClick(ThirdLoginType thirdLoginType) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                }
            }

            @Override
            public void onLoginSuccess(ThirdLoginType thirdLoginType, ThirdLoginProfile profile) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    LoginResult facebookProfile = profile.facebookProfile;
                    if (facebookProfile != null) {
                        mPresenter.tryBindFacebook(facebookProfile.getAccessToken().getToken());
                    }
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    GoogleSignInAccount googleProfile = profile.googleProfile;
                    if (googleProfile != null) {
                        mPresenter.tryBindGoogle(googleProfile.getIdToken());
                    }
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    TrueProfile trueCallerProfile = profile.mTrueCallerProfile;
                    if (trueCallerProfile != null) {
                        mPresenter.tryBindTrueCaller(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm);
                    }
                }
            }

            @Override
            public void onLoginFail(ThirdLoginType thirdLoginType, int errorCode) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    ToastUtils.showShort("Login failed");
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    ToastUtils.showShort("Login failed");
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                }
            }

            @Override
            public void onLoginCancel(ThirdLoginType thirdLoginType) {
                String cancelText = ResourceTool.getString("canceled");
                ToastUtils.showShort(cancelText);
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    //TODO
                }
            }
        });

    }
}

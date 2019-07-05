package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.progress.CircularProgressBar;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.bean.NickNameCheckerBean;
import com.redefine.welike.business.startup.ui.activity.RegistActivity;
import com.redefine.welike.business.startup.ui.viewmodel.RegisteredViewModel;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

/**
 * @author gongguan
 * @time 2018/1/8 下午4:13
 */
public class RegistUserInfoFragment extends Fragment {
    public static final String FILL_USERINFO_TAG = "fillUserInfoFragment";

    private View mView;


    private TextView tvSkip;
    private TextView btn_next;

    private Animation mCheckNameAnim, checkSexAnim;

    //nick
    private EditText et_name;
    private TextView tv_register_username_intro;
    private View view_edit_line;
    private ImageView ivDelete;
    private CircularProgressBar iv_check_name;
    private RelativeLayout rlNickName;

    private boolean canNext = false;

    ///sex
    private LinearLayout ll_sex;
    private ConstraintLayout clMale, clFemale;
    private TextView tvMale, tvFemale;
    private int cureentSex = -1;


    private RegisteredViewModel registeredViewModel;
    private RegisterAndLoginModel eventModel;

    private Account account;

    @Override
    public void onResume() {
        super.onResume();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_USER_INFORMATION);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist_userinfo, container, false);
        FragmentActivity activity = getActivity();
        registeredViewModel = ViewModelProviders.of(activity).get(RegisteredViewModel.class);

        account = AccountManager.getInstance().getAccount();

        initViews();

        setCurrentSex(cureentSex);

        initEvents();

        setViewModel();

        setOnclick();

        if (activity instanceof RegistActivity) {
            eventModel = ((RegistActivity) activity).getEventModel();
        }
        if (eventModel != null) {
            EventLog.RegisterAndLogin.report15(eventModel.phoneNumber, eventModel.smsSend, eventModel.verifyType, eventModel.pageSource, eventModel.stayTime);
        }

        new Thread() {
            @Override
            public void run() {
                AppsFlyerManager.getInstance().report();
            }
        }.start();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mView != null)
            mView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);

    }


    private void initViews() {
        tvSkip = mView.findViewById(R.id.tv_skip_userinfo);
        btn_next = mView.findViewById(R.id.btn_regist_pickPhoto_next);
        ivDelete = mView.findViewById(R.id.iv_register_nick_delete);
        view_edit_line = mView.findViewById(R.id.view_edit_line);
        clMale = mView.findViewById(R.id.ll_male);
        tvMale = mView.findViewById(R.id.tv_male);
        clFemale = mView.findViewById(R.id.ll_female);
        tvFemale = mView.findViewById(R.id.tv_female);
        rlNickName = mView.findViewById(R.id.rl_regist_name);

        iv_check_name = mView.findViewById(R.id.iv_register_check_anim);
        et_name = mView.findViewById(R.id.et_register_name);
        ll_sex = mView.findViewById(R.id.ll_choose_sex);
        tv_register_username_intro = mView.findViewById(R.id.tv_register_username_intro);
    }

    private void initEvents() {

        iv_check_name.setVisibility(View.INVISIBLE);

        et_name.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (!inputMethodManager.isActive()) {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        //监听软键盘状态
        mView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

    }

//    private void startCheckNameAnim() {
//        stopCheckNameAnim();
//        mCheckNameAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mCheckNameAnim.setDuration(800);
//        mCheckNameAnim.setRepeatCount(Animation.INFINITE);
//        mCheckNameAnim.setInterpolator(new LinearInterpolator());
//        iv_check_name.setVisibility(View.VISIBLE);
//        iv_check_name.startAnimation(mCheckNameAnim);
//    }

    private void startSexAnim() {
        if (ll_sex.getAnimation() == null) {
            checkSexAnim = new TranslateAnimation(20, -20, 0, 0);
            checkSexAnim.setDuration(20);
            checkSexAnim.setRepeatCount(2);
            ll_sex.startAnimation(checkSexAnim);
        }
    }

    private void setOnclick() {

        et_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s.toString())) {
                    checkEmpty();
                    return;
                }

                registeredViewModel.checkNickName(s.toString());
                StartEventManager.getInstance().setNickname(s.toString());
                if (eventModel != null) {
                    eventModel.nickNameCheck++;
                    eventModel.nickName = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });


        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setText("");
                StartEventManager.getInstance().setNickname("");
            }
        });


        clFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentSex(1);
            }
        });

        clMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentSex(0);
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    ToastUtils.showShort(ResourceTool.getString("error_nickname_cannot_empty"));
                    return;
                }
                if (canNext) {
                    if (cureentSex == -1) {
                        hideSoftInput();
                        startSexAnim();
                    } else {
                        registeredViewModel.tryUpdateUserInfo();
                    }

                    if (eventModel != null) {
                        EventLog.RegisterAndLogin.report17(eventModel.nickName, eventModel.nickNameCheck, eventModel.phoneNumber, eventModel.verifyType, eventModel.smsSend, eventModel.stayTime);
                    }
                }
            }

        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/7/25
                if (account.isFirst()) {
                    registeredViewModel.tryFakeSkip();
                } else
                    registeredViewModel.trySkip();
                hideSoftInput();

                if (eventModel != null) {
                    EventLog.RegisterAndLogin.report16(eventModel.phoneNumber, eventModel.smsSend, eventModel.verifyType, eventModel.accountStatus);
                }
            }
        });
    }


    private void setCurrentSex(int clickSex) {

        if (clickSex == cureentSex) {
//            cureentSex = -1;
//            return;
        } else {
            cureentSex = clickSex;
        }
        StartEventManager.getInstance().setSex((byte) cureentSex);

        switch (cureentSex) {
            case 0:
                selectMale();
                unselectFemale();
                break;

            case 1:
                selectFemale();
                unSelectMale();
                break;
            default:
                unSelectMale();
                unselectFemale();
        }


    }

    private void selectMale() {

        clMale.setBackgroundResource(R.drawable.bg_stroke_male);
        tvMale.setTextColor(ContextCompat.getColor(tvMale.getContext(), R.color.color_53D0FF));
//        ivMale.setImageResource(R.drawable.ic_main_mine_male);

    }

    private void unSelectMale() {
        clMale.setBackgroundResource(R.drawable.bg_stroke_main_ededed);
        tvMale.setTextColor(ContextCompat.getColor(tvMale.getContext(), R.color.color_62));
//        ivMale.setImageResource(R.drawable.ic_main_blue_male);
    }

    private void selectFemale() {
        clFemale.setBackgroundResource(R.drawable.bg_stroke_female);
        tvFemale.setTextColor(ContextCompat.getColor(tvFemale.getContext(), R.color.color_ff9e9e));
//        ivFemale.setImageResource(R.drawable.ic_main_mine_female);

    }

    private void unselectFemale() {

        clFemale.setBackgroundResource(R.drawable.bg_stroke_main_ededed);
        tvFemale.setTextColor(ContextCompat.getColor(tvFemale.getContext(), R.color.color_62));
//        ivFemale.setImageResource(R.drawable.ic_main_pink_female);
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null)
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void stopCheckNameAnim() {
        if (mCheckNameAnim != null) {
            mCheckNameAnim.cancel();
            mCheckNameAnim = null;
            iv_check_name.clearAnimation();
            iv_check_name.setVisibility(View.INVISIBLE);
        }
    }

    private void setViewModel() {

        registeredViewModel.getNickNameBeanData().observe(this, new Observer<NickNameCheckerBean>() {
            @Override
            public void onChanged(@Nullable NickNameCheckerBean nickNameCheckerBean) {

                if (nickNameCheckerBean == null) return;

                if (nickNameCheckerBean.getErrCode() == ErrorCode.ERROR_SUCCESS) {
                    if (nickNameCheckerBean.getRepeat()) {

                        if (nickNameCheckerBean.getGt()) {
                            tv_register_username_intro.setText(String.format(getResources().getString(R.string.register_nickname_use_less),
                                    nickNameCheckerBean.getRepeatNum()));
                        } else {
                            tv_register_username_intro.setText(String.format(getResources().getString(R.string.register_nickname_use_more),
                                    nickNameCheckerBean.getRepeatNum()));
                        }

                    } else {

                        tv_register_username_intro.setText(getResources().getString(R.string.register_nickname_unique));

                    }


                    canNext = true;

                } else {
                    tv_register_username_intro.setText(ErrorCode.showErrCodeText(nickNameCheckerBean.getErrCode()));
                    canNext = false;
                }

            }
        });

        registeredViewModel.getCheckStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {

                if (pageStatusEnum == null) return;

                switch (pageStatusEnum) {

                    case LOADING: {
                        checkLoading();

                    }

                    case CONTENT: {
                        checkContent();
                    }

                }
            }
        });

    }


    private void checkLoading() {
        iv_check_name.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.GONE);
        tv_register_username_intro.setVisibility(View.INVISIBLE);


    }

    void checkContent() {
        iv_check_name.setVisibility(View.GONE);
        ivDelete.setVisibility(View.VISIBLE);
        tv_register_username_intro.setVisibility(View.VISIBLE);
    }

    void checkEmpty() {
        iv_check_name.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
        tv_register_username_intro.setVisibility(View.INVISIBLE);
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            Rect rect = new Rect();
            mView.getWindowVisibleDisplayFrame(rect);
            int height = mView.getRootView().getHeight();
            int heightDefere = height - rect.bottom;
            if (heightDefere > 200) {

                view_edit_line.setBackgroundColor(view_edit_line.getContext().getResources().getColor(R.color.main_orange_dark));
                ((RelativeLayout.LayoutParams) rlNickName.getLayoutParams()).topMargin = ScreenUtils.dip2Px(rlNickName.getContext(), 20);
                rlNickName.setLayoutParams(rlNickName.getLayoutParams());
            } else {
                view_edit_line.setBackgroundColor(view_edit_line.getContext().getResources().getColor(R.color.main_grey));
                ((RelativeLayout.LayoutParams) rlNickName.getLayoutParams()).topMargin = ScreenUtils.dip2Px(rlNickName.getContext(), 47);
                rlNickName.setLayoutParams(rlNickName.getLayoutParams());
            }
        }
    };


}
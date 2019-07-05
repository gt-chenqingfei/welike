package com.redefine.welike.business.user.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.vm.UserSocialViewModel;
import com.redefine.welike.commonui.event.commonenums.ResultEnum;
import com.redefine.welike.commonui.event.commonenums.SocialMedia;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by nianguowang on 2018/8/16
 */
@Route(path = RouteConstant.SOCIAL_HOST_ROUTE_PATH)
public class UserSocialHostPage extends BaseActivity {

    private View mBackBtn;
    private TextView mTitleText;

    private TextView mSocialTitle;
    private TextView mErrorText;
    private EditText mSocialInput;
    private View mClearInput;
    private TextView mSubmit;
    private View mYoutubeExample;
    private TextView mExampleLink;
    private UserSocialViewModel mViewModel;
    private int mHostType;
    private UserBase.Link mHostUrl;
    private SocialMedia mSocialHost;

    private LoadingDlg mLoadingDlg;

    public static void show(int type, UserBase.Link link) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserConstant.BUNDLE_KEY_HOST_TYPE, type);
        bundle.putSerializable(UserConstant.BUNDLE_KEY_HOST_URL, link);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PROFILE_BIND_SOCIAL, bundle));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            finish();
            return;
        }
        parseBundle(extras);
        setContentView(R.layout.user_social_host_page);
        mViewModel = ViewModelProviders.of(this).get(UserSocialViewModel.class);
        initView();
        EventLog.EditProfile.report10(mHostType);
        EventLog1.EditProfile.report10(mSocialHost);
    }

    private void parseBundle(Bundle pageBundle) {
        mHostType = pageBundle.getInt(UserConstant.BUNDLE_KEY_HOST_TYPE);
        mHostUrl = (UserBase.Link) pageBundle.getSerializable(UserConstant.BUNDLE_KEY_HOST_URL);
    }

    protected void initView() {
        mBackBtn = findViewById(R.id.iv_common_back);
        mTitleText = findViewById(R.id.tv_common_title);

        mSocialTitle = findViewById(R.id.tv_social_title);
        mSocialInput = findViewById(R.id.social_host_address);
        mErrorText = findViewById(R.id.social_error_hint);
        mClearInput = findViewById(R.id.social_clear_text);
        mSubmit = findViewById(R.id.tv_common_social_submit);
        mYoutubeExample = findViewById(R.id.social_example_container);
        mExampleLink =findViewById(R.id.social_example_link);

        mBackBtn.setOnClickListener(mClickListener);
        mClearInput.setOnClickListener(mClickListener);
        mSubmit.setOnClickListener(mClickListener);
        mSocialInput.addTextChangedListener(mTextWatcher);

        if (mHostUrl != null) {
            mSocialInput.setText(mHostUrl.getLink());
        }
        mTitleText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_link"));
        mErrorText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_invalid_url"));
        mSubmit.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_link_submit"));
        mExampleLink.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_social_example_link"));

        String socialTitle = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_input_social_host");
        String type = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_social_facebook");
        if (mHostType == UserConstant.USER_SOCIAL_LINK_FACEBOOK) {
            type = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_social_facebook");
            mSocialInput.setHint(R.string.profile_link_facebook);
            mYoutubeExample.setVisibility(View.INVISIBLE);
            mSocialHost = SocialMedia.FACEBOOK;
        } else if (mHostType == UserConstant.USER_SOCIAL_LINK_INSTAGRAM) {
            type = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_social_instagram");
            mSocialInput.setHint(R.string.profile_link_instagram);
            mYoutubeExample.setVisibility(View.INVISIBLE);
            mSocialHost = SocialMedia.INSTAGRAM;
        } else if (mHostType == UserConstant.USER_SOCIAL_LINK_YOUTUBE) {
            type = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_social_youtube");
            mSocialInput.setHint("");
            mYoutubeExample.setVisibility(View.VISIBLE);
            mSocialHost = SocialMedia.YOUTUBE;
        }
        mSocialTitle.setText(String.format(socialTitle, type));

        mViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
                }
                switch (pageStatusEnum) {
                    case LOADING:
                        showLoading();
                        break;
                    case CONTENT:
                        dismissLoading();
                        ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "successful"));
                        EventLog1.EditProfile.report12(mSocialHost, ResultEnum.SUCCESS);
                        EventLog.EditProfile.report12(mHostType, EventConstants.COMMON_RESULT_SUCCESS);
                        finish();
                        break;
                    case ERROR:
                        ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "failed"));
                        EventLog1.EditProfile.report12(mSocialHost, ResultEnum.FAIL);
                        EventLog.EditProfile.report12(mHostType, EventConstants.COMMON_RESULT_FAIL);
                        dismissLoading();
                        break;
                }
            }
        });
    }

    private void showLoading() {
        if (mLoadingDlg == null) {
            mLoadingDlg = new LoadingDlg(this);
        }
        mLoadingDlg.show();
    }

    private void dismissLoading() {
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        InputMethodUtil.hideInputMethod(mSocialInput);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mClearInput.setVisibility(View.VISIBLE);
            } else {
                mClearInput.setVisibility(View.INVISIBLE);
            }
            mErrorText.setVisibility(View.INVISIBLE);
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBackBtn) {
                finish();
            } else if (v == mClearInput) {
                mSocialInput.setText("");
            } else if (v == mSubmit) {
                onSubmit();
            }
        }
    };

    private void onSubmit() {
        EventLog.EditProfile.report11(mHostType);
        EventLog1.EditProfile.report11(mSocialHost);
        String socialHost = mSocialInput.getText().toString().trim();
        if (mViewModel.validUrl(socialHost)) {
            if (!socialHost.startsWith("https://") && !socialHost.startsWith("http://")) {
                socialHost = "https://" + socialHost;
            }
        }
        if (!mViewModel.matchSocialHost(socialHost, mHostType)) {
            mErrorText.setVisibility(View.VISIBLE);
        } else {
            if (mHostUrl == null) {
                mViewModel.addLink(mHostType, socialHost);
            } else {
                mHostUrl.setLink(socialHost);
                mViewModel.updateLink(mHostUrl);
            }
        }
    }
}

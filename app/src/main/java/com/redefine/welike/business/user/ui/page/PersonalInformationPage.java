package com.redefine.welike.business.user.ui.page;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonSingleConfirmDialog;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.UploadListener;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.user.management.util.UserGradeRedirectHandler;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.vm.EditInterestViewModel;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.event.commonenums.SocialMedia;
import com.redefine.welike.commonui.photoselector.PhotoSelector;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.view.ActionSnackBar;
import com.redefine.welike.commonui.widget.ArrowTextView;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.WrapSizeFile;

/**
 * Created by gongguan on 2018/2/23.
 */
@PageName("PersonalInformationPage")
@Route(path = RouteConstant.EDIT_PROFILE_ROUTE_PATH)
public class PersonalInformationPage extends BaseActivity implements AccountManager.AccountCallback {
    private TextView page_title, tv_name_title, tv_gender_title, tv_brief_title, tv_update_headview_text,
            tv_nickname, tv_gender, tv_brief;
    private ImageView iv_back, mIvGenderNext;
    private VipAvatar headView;
    private LoadingDlg loadingDlg;
    private CommonSingleConfirmDialog modifyDialog;
    private LinearLayout mLlGender, mllNick, mLlIntroduction;
    private long modifyNextDate;
    private boolean isAllowEditName, isAllowEditSex;
    private TextView tvInterestTitle;
    private ArrowTextView guideAvatar, guideIntro;
    private int mVip;
    private View mCertificationView;
    private TextView mIdentityText, mIdentityDetailText;

    private TextView mSocialTitle;
    private AppCompatImageView mFacebookHost;
    private AppCompatImageView mInstgramHost;
    private AppCompatImageView mYoutubeHost;

    private EditInterestViewModel editInterestViewModel;

    private boolean isInterestChange = false;
    private final Map<String, UserBase.Intrest> mSelectInterests = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_personal_information);

        parseBundle();
        initViews();
        bindViews();

        AccountManager.getInstance().register(this);
        EventBus.getDefault().register(this);

//        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//            @Override
//            public void run() {
//                if (MissionManager.INSTANCE.checkGuide(MissionType.AVATAR)) {
//                    MissionManager.INSTANCE.showGuide(MissionType.AVATAR, guideAvatar, headView);
//                }
//                if (MissionManager.INSTANCE.checkGuide(MissionType.INTRODUCTION)) {
//                    MissionManager.INSTANCE.showGuide(MissionType.INTRODUCTION, guideIntro, mLlIntroduction);
//                }
//            }
//        }, 1, TimeUnit.SECONDS);
        EventLog1.EditProfile.report1();
        EventLog.EditProfile.report1();
    }

    private void parseBundle() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        if (extras.containsKey(UserConstant.USER_HOST_USER_MODIFY_NEXT_DATE)) {
            modifyNextDate = extras.getLong(UserConstant.USER_HOST_USER_MODIFY_NEXT_DATE);
        } else {
            modifyNextDate = AccountManager.getInstance().getAccount().getNextUpdateNickNameDate();
        }
        if (extras.containsKey(UserConstant.USER_HOST_USER_EDIT_NAME)) {
            isAllowEditName = extras.getBoolean(UserConstant.USER_HOST_USER_EDIT_NAME);
        } else {
            isAllowEditName = AccountManager.getInstance().getAccount().isAllowUpdateNickName();
        }
        if (extras.containsKey(UserConstant.USER_HOST_USER_EDIT_SEX)) {
            isAllowEditSex = extras.getBoolean(UserConstant.USER_HOST_USER_EDIT_SEX);
        } else {
            isAllowEditSex = AccountManager.getInstance().getAccount().isAllowUpdateSex();
        }
        if (extras.containsKey(UserConstant.USER_HOST_USER_VIP)) {
            mVip = extras.getInt(UserConstant.USER_HOST_USER_VIP);
        } else {
            mVip = AccountManager.getInstance().getAccount().getVip();
        }
    }

    private void bindViews() {
        Account account = AccountManager.getInstance().getAccount();
        bindHeadView(account);
        bindCertification();
        bindNickName(account);
        bindSexView(account);
        bindIntroduction(account);
    }

    private void bindCertification() {
        String description = VipUtil.getDescription(mVip);
        if (TextUtils.isEmpty(description)) {
            mCertificationView.setVisibility(View.GONE);
        } else {
            mCertificationView.setVisibility(View.VISIBLE);
            mIdentityText.setText(ResourceTool.getString("user_main_verify"));
            mIdentityDetailText.setText(description);
        }
    }

    private void bindIntroduction(Account account) {
        tv_brief.setText(account.getIntroduction());
    }

    private void bindSexView(Account account) {
        if (account.getSex() == UserBase.MALE) {
            tv_gender.setText(ResourceTool.getString("user_sex_boy"));
        } else {
            tv_gender.setText(ResourceTool.getString("user_sex_girl"));
        }
        if (!isAllowEditSex) {
            mIvGenderNext.setVisibility(View.INVISIBLE);
        } else {
            mIvGenderNext.setVisibility(View.VISIBLE);
        }
    }

    private void bindNickName(Account account) {
//        tv_nickname.setText(account.getNickName());
        VipUtil.setNickName(tv_nickname, account.getCurLevel(), account.getNickName());
    }

    private void bindHeadView(Account account) {
//        HeadUrlLoader.getInstance().loadHeaderUrl(headView, account.getHeadUrl());
        VipUtil.set(headView, account.getHeadUrl(), account.getVip());
    }

    private void bindLink(final Account account) {
        List<UserBase.Link> links = account.getLinks();
        boolean facebookBind = false, instagramBind = false, youtubeBind = false;
        UserBase.Link facebookUrl = null, instagramUrl = null, youtubeUrl = null;
        if (!CollectionUtil.isEmpty(links)) {
            for (UserBase.Link link : links) {
                if (link.getLinkType() == UserConstant.USER_SOCIAL_LINK_FACEBOOK) {
                    facebookBind = true;
                    facebookUrl = link;
                }
                if (link.getLinkType() == UserConstant.USER_SOCIAL_LINK_INSTAGRAM) {
                    instagramBind = true;
                    instagramUrl = link;
                }
                if (link.getLinkType() == UserConstant.USER_SOCIAL_LINK_YOUTUBE) {
                    youtubeBind = true;
                    youtubeUrl = link;
                }
            }
        }
        mFacebookHost.setImageResource(account.getCurLevel() > 1 ? (facebookBind ? R.drawable.profile_facebook_host_yes : R.drawable.profile_facebook_host_no) : R.drawable.profile_facebook_host_no);
        mInstgramHost.setImageResource(account.getCurLevel() > 1 ? (instagramBind ? R.drawable.profile_instgram_host_yes : R.drawable.profile_instgram_host_no) : R.drawable.profile_instgram_host_no);
        mYoutubeHost.setImageResource(account.getCurLevel() > 1 ? (youtubeBind ? R.drawable.profile_youtube_host_yes : R.drawable.profile_youtube_host_no) : R.drawable.profile_youtube_host_no);

        final boolean finalFacebookBind = facebookBind;
        final UserBase.Link finalFacebookUrl = facebookUrl;
        mFacebookHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.EditProfile.report7(SocialMedia.FACEBOOK);
                EventLog.EditProfile.report7(EventConstants.PROFILE_ICON_TYPE_FACEBOOK);
                if (account.getCurLevel() > 1) {
                    UserSocialHostPage.show(UserConstant.USER_SOCIAL_LINK_FACEBOOK, finalFacebookBind ? finalFacebookUrl : null);
                } else {
                    EventLog1.EditProfile.report8(SocialMedia.FACEBOOK);
                    EventLog.EditProfile.report8(EventConstants.PROFILE_ICON_TYPE_FACEBOOK);
                    if (account.getStatus() == Account.ACCOUNT_HALF) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, true);
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));
                        return;
                    }
                    ActionSnackBar.getInstance().showLoginSnackBar(headView,
                            ResourceTool.getString("profile_guru_permission"),
                            ResourceTool.getString("profile_apply_now"), 3000, new ActionSnackBar.ActionBtnClickListener() {
                                @Override
                                public void onActionClick() {
                                    new UserGradeRedirectHandler().onRedirect();
                                    EventLog1.EditProfile.report9(SocialMedia.FACEBOOK);
                                    EventLog.EditProfile.report9(EventConstants.PROFILE_ICON_TYPE_FACEBOOK);

                                }
                            });
                }
            }
        });
        final boolean finalInstagramBind = instagramBind;
        final UserBase.Link finalInstagramUrl = instagramUrl;
        mInstgramHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.EditProfile.report7(SocialMedia.INSTAGRAM);
                EventLog.EditProfile.report7(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM);
                if (account.getCurLevel() > 1) {
                    UserSocialHostPage.show(UserConstant.USER_SOCIAL_LINK_INSTAGRAM, finalInstagramBind ? finalInstagramUrl : null);
                } else {
                    EventLog1.EditProfile.report8(SocialMedia.INSTAGRAM);
                    EventLog.EditProfile.report8(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM);
                    if (account.getStatus() == Account.ACCOUNT_HALF) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, true);
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));
                        return;
                    }
                    ActionSnackBar.getInstance().showLoginSnackBar(headView,
                            ResourceTool.getString("profile_guru_permission"),
                            ResourceTool.getString("profile_apply_now"), 3000, new ActionSnackBar.ActionBtnClickListener() {
                                @Override
                                public void onActionClick() {
                                    new UserGradeRedirectHandler().onRedirect();
                                    EventLog1.EditProfile.report9(SocialMedia.INSTAGRAM);
                                    EventLog.EditProfile.report9(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM);
                                }
                            });
                }
            }
        });
        final boolean finalYoutubeBind = youtubeBind;
        final UserBase.Link finalYoutubeUrl = youtubeUrl;
        mYoutubeHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.EditProfile.report7(SocialMedia.YOUTUBE);
                EventLog.EditProfile.report7(EventConstants.PROFILE_ICON_TYPE_YOUTUBE);
                if (account.getCurLevel() > 1) {
                    UserSocialHostPage.show(UserConstant.USER_SOCIAL_LINK_YOUTUBE, finalYoutubeBind ? finalYoutubeUrl : null);
                } else {
                    EventLog1.EditProfile.report8(SocialMedia.YOUTUBE);
                    EventLog.EditProfile.report8(EventConstants.PROFILE_ICON_TYPE_YOUTUBE);

                    if (account.getStatus() == Account.ACCOUNT_HALF) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, true);
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));

                        return;
                    }
                    EventLog.EditProfile.report8(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM);
                    ActionSnackBar.getInstance().showLoginSnackBar(headView,
                            ResourceTool.getString("profile_guru_permission"),
                            ResourceTool.getString("profile_apply_now"), 3000, new ActionSnackBar.ActionBtnClickListener() {
                                @Override
                                public void onActionClick() {
                                    new UserGradeRedirectHandler().onRedirect();
                                    EventLog.EditProfile.report9(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM);
                                    EventLog1.EditProfile.report9(SocialMedia.YOUTUBE);
                                }
                            });
                }
            }
        });
    }

    private void initViews() {
        page_title = findViewById(R.id.tv_common_title);
        iv_back = findViewById(R.id.iv_common_back);
        tv_name_title = findViewById(R.id.tv_user_host_personInfo_nickname_title);
        tv_gender_title = findViewById(R.id.tv_user_host_personInfo_gender_title);
        tv_brief_title = findViewById(R.id.tv_user_host_personInfo_brief_title);
        tv_update_headview_text = findViewById(R.id.tv_user_host_personInfo_update_headview);
        mIvGenderNext = findViewById(R.id.iv_user_host_personInfo_gender_next);
        mLlIntroduction = findViewById(R.id.ll_user_host_personInfo_brief);
        mllNick = findViewById(R.id.ll_user_host_personInfo_nickname);
        mLlGender = findViewById(R.id.ll_user_host_personInfo_gender);
        headView = findViewById(R.id.simpleDraw_user_host_personInfo_headview);
        tv_nickname = findViewById(R.id.tv_user_host_personInfo_nickname);
        tv_gender = findViewById(R.id.tv_user_host_personInfo_gender);
        tv_brief = findViewById(R.id.tv_user_host_personInfo_brief);
        guideAvatar = findViewById(R.id.guide_avatar);
        guideIntro = findViewById(R.id.guide_intro);

        mCertificationView = findViewById(R.id.user_certification_view);
        mIdentityText = findViewById(R.id.user_identity_text);
        mIdentityDetailText = findViewById(R.id.user_identity_detail_text);

        tvInterestTitle = findViewById(R.id.tv_interest_title);

        mSocialTitle = findViewById(R.id.tv_social_title);
        mFacebookHost = findViewById(R.id.profile_facebook_host);
        mInstgramHost = findViewById(R.id.profile_instgram_host);
        mYoutubeHost = findViewById(R.id.profile_youtube_host);

        page_title.setText(ResourceTool.getString("mine_user_host_personal_info_page_title"));
        tv_update_headview_text.setText(ResourceTool.getString("mine_user_host_update_photo_title"));
        tv_name_title.setText(ResourceTool.getString("regist_user_info_input_nickname"));
        tv_gender_title.setText(ResourceTool.getString("mine_user_host_gender_title"));
        tv_brief_title.setText(ResourceTool.getString("mine_user_host_brief_title"));

        tvInterestTitle.setText(ResourceTool.getString("user_interest_title"));
        mSocialTitle.setText(ResourceTool.getString("profile_link_account"));

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guideAvatar != null && guideAvatar.getVisibility() == View.VISIBLE) {
                    guideAvatar.setVisibility(View.GONE);
//                    MissionManager.INSTANCE.hideGuide(MissionType.AVATAR);
                }
                PhotoSelector.launchChooseUserHeaderSelect(PersonalInformationPage.this);
                EventLog1.EditProfile.report2();
                EventLog.EditProfile.report2();
            }
        });

        mllNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllowEditName) {
                    modifyDialog = CommonSingleConfirmDialog.showConfirmDialog(v.getContext(),
                            String.format(ResourceTool.getString("mine_user_host_personal_edit_name_canot_modify_slogen"),
                                    DateTimeUtil.INSTANCE.formatModifyDate(modifyNextDate)),
                            ResourceTool.getString("common_cancel"), null);
                } else {
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MINE_USER_PERSONAL_EDIT_NAME, null));
                }
                EventLog.EditProfile.report3();
                EventLog1.EditProfile.report3();
            }
        });

        mLlIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guideIntro != null && guideIntro.getVisibility() == View.VISIBLE) {
                    guideIntro.setVisibility(View.GONE);
//                    MissionManager.INSTANCE.hideGuide(MissionType.INTRODUCTION);
                }
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MINE_USER_PERSONAL_EDIT_BRIEF, null));
                EventLog.EditProfile.report5();
                EventLog1.EditProfile.report5();
            }
        });
        mLlGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllowEditSex) {
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MINE_USER_PERSONAL_EDIT_SEX, null));
                }
                EventLog.EditProfile.report4();
                EventLog1.EditProfile.report4();
            }
        });

        tvInterestTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInterestSelectPage.launch(2, UserConstant.USER_INTEREST_TYPE_PROFILE);
                EventLog.EditProfile.report6();
                EventLog1.EditProfile.report6();
            }
        });

        editInterestViewModel = ViewModelProviders.of(this).get(EditInterestViewModel.class);
        editInterestViewModel.refresh();
        editInterestViewModel.getmAccount().observe(this, new Observer<Account>() {
            @Override
            public void onChanged(@Nullable Account account) {

                bindHeadView(account);
                bindCertification();
                isAllowEditName = account.isAllowUpdateNickName();
                modifyNextDate = account.getNextUpdateNickNameDate();
                bindNickName(account);
                bindSexView(account);
                bindIntroduction(account);
                bindLink(account);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        updateUserInterest();
        EventBus.getDefault().unregister(this);
        AccountManager.getInstance().unregister(this);
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }

    /**
     * 更新用户信息
     */
    private void updateUserInterest() {
        if (isInterestChange) {
            Set<String> keySet = mSelectInterests.keySet();
            List<UserBase.Intrest> list = new ArrayList<>();
            for (String key : keySet) {
                list.add(mSelectInterests.get(key));
            }
            Account account = AccountManager.getInstance().getAccount().copy();
            account.setIntrests(list);
            AccountManager.getInstance().modifyAccount(account);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Message message) {
        if (message.what == MessageIdConstant.MESSAGE_EDIT_NICK_NAME_SUCCESS) {
            Account account = AccountManager.getInstance().getAccount();
            isAllowEditName = account.isAllowUpdateNickName();
            isAllowEditSex = account.isAllowUpdateSex();
            modifyNextDate = account.getNextUpdateNickNameDate();
            bindNickName(account);
        } else if (message.what == MessageIdConstant.MESSAGE_EDIT_SEX_SUCCESS) {
            Account account = AccountManager.getInstance().getAccount();
            isAllowEditName = account.isAllowUpdateNickName();
            isAllowEditSex = account.isAllowUpdateSex();
            modifyNextDate = account.getNextUpdateNickNameDate();
            bindSexView(account);
        } else if (message.what == MessageIdConstant.MESSAGE_EDIT_INTRODUCTION_SUCCESS) {
            Account account = AccountManager.getInstance().getAccount();
            isAllowEditName = account.isAllowUpdateNickName();
            isAllowEditSex = account.isAllowUpdateSex();
            modifyNextDate = account.getNextUpdateNickNameDate();
            bindIntroduction(account);
            MissionManager.INSTANCE.notifyEvent(MissionType.INTRODUCTION);
        }
    }

    @Override
    public void onModified() {
        if (loadingDlg != null) {
            loadingDlg.dismiss();
        }
        Account account = AccountManager.getInstance().getAccount();
        VipUtil.set(headView, account.getHeadUrl(), account.getVip());
    }

    @Override
    public void onModifyFailed(int errCode) {
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CommonRequestCode.USER_HOST_CHOOSE_PIC_CODE) {

                final ArrayList<Item> items = data.getParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS);
                if (!CollectionUtil.isEmpty(items)) {
                    Schedulers.newThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            final WrapSizeFile wrapFile;
                            try {
                                wrapFile = Luban.with(PersonalInformationPage.this)
                                        .setTargetDir(WeLikeFileManager.getTempCacheDir().getAbsolutePath())
                                        .setCompressQuality(80)
                                        .ignoreBy(0).get(items.get(0).filePath);
                                AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_UPLOAD_AVATAR);

                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!wrapFile.file.exists()) {
                                            ToastUtils.showShort(ResourceTool.getString("error_regist_user_head_too_large_reupload_avatar"));
                                            return;
                                        }
                                        AccountManager.getInstance().modifyAccountHeadUrl(wrapFile.getAbsolutePath(), new UploadListener() {
                                            @Override
                                            public void onFinish(String url) {
                                                MissionManager.INSTANCE.notifyEvent(MissionType.AVATAR);
                                                loadingDlg = new LoadingDlg(PersonalInformationPage.this);
                                                loadingDlg.show();
                                            }

                                            @Override
                                            public void onFail() {

                                            }
                                        });
                                    }
                                });


                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    public static void launch(String uId, long nextDate, boolean isAllowEditName, boolean isAllowEditSex, int vip) {
        Bundle bundle = new Bundle();
        bundle.putString(UserConstant.USER_HOST_USER_INFO_UID, uId);
        bundle.putLong(UserConstant.USER_HOST_USER_MODIFY_NEXT_DATE, nextDate);
        bundle.putBoolean(UserConstant.USER_HOST_USER_EDIT_NAME, isAllowEditName);
        bundle.putBoolean(UserConstant.USER_HOST_USER_EDIT_SEX, isAllowEditSex);
        bundle.putInt(UserConstant.USER_HOST_USER_VIP, vip);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MINE_USER_PERSONAL_INFO, bundle));
    }

}

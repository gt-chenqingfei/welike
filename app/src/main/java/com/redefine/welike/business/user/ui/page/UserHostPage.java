package com.redefine.welike.business.user.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.androidkun.xtablayout.XTabLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.constant.UserConstants;
import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.enums.BlockStatusEnum;
import com.redefine.commonui.enums.FollowStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.DefaultShareMenuManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUserCallBack;
import com.redefine.welike.business.browse.management.dao.FollowUserCountCallBack;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.constant.ImConstant;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.ui.dialog.VerifyDialog;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.util.StatusBarHeightCalculator;
import com.redefine.welike.business.user.ui.adapter.ProfilePagerAdapter;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.interceptor.BrowseInterceptor;
import com.redefine.welike.business.user.ui.view.state.IProfileState;
import com.redefine.welike.business.user.ui.view.state.ProfileOwnerState;
import com.redefine.welike.business.user.ui.view.state.ProfileVisitState;
import com.redefine.welike.business.user.ui.vm.ProfileViewModel;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.WindowUtil;
import com.redefine.welike.commonui.event.commonenums.UserType;
import com.redefine.welike.commonui.event.helper.LoginEventHelper;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.photoselector.PhotoSelector;
import com.redefine.welike.commonui.share.CustomShareMenuFactory;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareManagerWrapper;
import com.redefine.welike.commonui.share.ShareMenu;
import com.redefine.welike.commonui.share.model.ShareTitleModel;
import com.redefine.welike.commonui.share.model.ShortLinkModel;
import com.redefine.welike.commonui.share.model.WaterMarkModel;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.redefine.welike.business.user.ui.constant.UserConstant.FOLLOWER;
import static com.redefine.welike.business.user.ui.constant.UserConstant.FOLLOWING;

/**
 * 个人信息页，（主人态，客人态）
 * Created by gongguan on 2018/2/5.
 */
@Route(path = RouteConstant.PROFILE_ROUTE_PATH)
public class UserHostPage extends BaseActivity {

    private ViewPager mVp;
    private CollapsingToolbarLayout mCollapsingLayout;
    private AppBarLayout mAppBarLayout;
    private LinearLayout mChatFollow;
    private XTabLayout pageTab;
    private TextView tv_introduction, mTvNickName, mTvChat, mTvFollow, mTitle, mEditProfile, mFollowingNum, mFollowerNum;
    private VipAvatar simple_headView;

    private User user;
    private int postCount;
    private long likeCount;
    private ImageView mHeaderBg;
    private ProfilePagerAdapter mVpAdapter;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private CommonConfirmDialog unfollowDialog;
    private ImageView ivGender;
    private LinearLayout mTitleBar;
    private SimpleDraweeView mTitleHeader;
    private View mTitleNickContainer;
    private View mVipContainer;
    private TextView mExpandIntroduce;
    private View mInterestContainer;
    private View mInFluenceContainer;
    private AppCompatImageView mTitleEditProfile;
    private AppCompatImageView mMoreBtn;
    private AppCompatImageView mBackBtn;

    private AppCompatImageView mFacebookHost;
    private AppCompatImageView mInstgramHost;
    private AppCompatImageView mYoutubeHost;

    private SimpleDraweeView mTaskMedal;
    private SimpleDraweeView mWorldCupMedal;
    private boolean mCollapsingState;

    private LoadingDlg mLoadingDlg;
    private IProfileState mProfileState;
    private ProfileViewModel mProfileViewModel;

    private String mUid;
    private int mFromPage;
    private String mEntryType;
    private boolean isEpanded;
    private boolean mIsDestroyed;
    private int softInputMode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_host);
        parseBundle();

        initView();
        initObservers();
        initData();

        EventLog.Profile.report1(mFromPage == EventConstants.PROFILE_FROM_PAGE_ME ? mFromPage : EventConstants.PROFILE_FROM_PAGE_OTHER,
                AccountManager.getInstance().isSelf(mUid) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);

        PostEventManager.INSTANCE.reset();
        EventLog1.Profile.report1(mFromPage == EventConstants.PROFILE_FROM_PAGE_ME ? EventLog1.Profile.FromPage.ME : EventLog1.Profile.FromPage.OTHER,
                AccountManager.getInstance().isSelf(mUid) ? UserType.OWNER : UserType.VISIT, mUid);

        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_USER_PROFILE);
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_USER_DETAIL);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        EventBus.getDefault().register(this);
    }

    private void parseBundle() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        mUid = bundle.getString(UserConstant.UID);
        isEpanded = bundle.getBoolean(UserConstant.IS_EXPANDED, false);
        mFromPage = bundle.getInt(UserConstant.FROM_PAGE);
        mEntryType = bundle.getString(RouteConstant.ROUTE_KEY_ENTRY_TYPE, "");
        mCollapsingState = !isEpanded;

        if (bundle.containsKey(RouteConstant.ROUTE_KEY_TOAST)) {
            final String mToastStr = bundle.getString(RouteConstant.ROUTE_KEY_TOAST);
            // 防止恢复，造成再次的toast
            bundle.remove(RouteConstant.ROUTE_KEY_TOAST);
            if (!TextUtils.isEmpty(mToastStr)) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsDestroyed) {
                            ToastUtils.showLong(mToastStr);
                        }
                    }
                }, 3500, TimeUnit.MILLISECONDS);
            }
        }
    }

    private void initObservers() {
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mProfileViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
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
                        showContent();
                        break;
                    case ERROR:
                        showErrorView();
                        break;
                    case EMPTY:
                        showEmptyView();
                        break;
                }
            }
        });
        mProfileViewModel.getBlockStatus().observe(this, new Observer<BlockStatusEnum>() {
            @Override
            public void onChanged(@Nullable BlockStatusEnum blockStatusEnum) {
                if (blockStatusEnum == null || user == null) {
                    return;
                }
                switch (blockStatusEnum) {
                    case LOADING:
                        showLoadingDlg();
                        break;
                    case BLOCK_FAIL:
                        dismissLoadingDlg();
                        break;
                    case UNBLOCK_FAIL:
                        dismissLoadingDlg();
                        break;
                    case BLOCK_SUCCESS:
                        dismissLoadingDlg();
                        user.setBlock(true);
                        break;
                    case UNBLOCK_SUCCESS:
                        dismissLoadingDlg();
                        user.setBlock(false);
                        break;
                }
            }
        });
        mProfileViewModel.getFollowStatus().observe(this, new Observer<FollowStatusEnum>() {
            @Override
            public void onChanged(@Nullable FollowStatusEnum followStatusEnum) {
                if (followStatusEnum == null || user == null) {
                    return;
                }
                switch (followStatusEnum) {
                    case LOADING:
                        showLoadingDlg();
                        break;
                    case FOLLOW_FAIL:
                        dismissLoadingDlg();
                        break;
                    case FOLLOW_SUCCESS:
                        dismissLoadingDlg();
                        user.setFollowing(true);
                        break;
                    case UNFOLLOW_FAIL:
                        dismissLoadingDlg();
                        break;
                    case UNFOLLOW_SUCCESS:
                        dismissLoadingDlg();
                        user.setFollowing(false);
                        break;
                }
                setFollowBtnStatus(user);
            }
        });
        mProfileViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    UserHostPage.this.user = user;
                    bindUser(user);
                }
            }
        });
    }

    private void initView() {
        mTitle = findViewById(R.id.tv_common_title);
        mVp = findViewById(R.id.user_host_view_pager);
        mHeaderBg = findViewById(R.id.profile_header_bg);
        mCollapsingLayout = findViewById(R.id.profile_collapsing);
        mAppBarLayout = findViewById(R.id.user_host_app_bar);
        pageTab = findViewById(R.id.slidingTab_user_host);
        tv_introduction = findViewById(R.id.tv_user_host_introduce);
        mTvNickName = findViewById(R.id.tv_user_host_name);
        ivGender = findViewById(R.id.iv_user_host_name);
        simple_headView = findViewById(R.id.simpleView_user_host_headview);
        mEditProfile = findViewById(R.id.tv_user_host_edit_profile);
        mTvChat = findViewById(R.id.tv_user_host_chat);
        mTvFollow = findViewById(R.id.tv_user_host_follow);
        mChatFollow = findViewById(R.id.user_host_bottom);
        mFollowingNum = findViewById(R.id.user_host_following);
        mFollowerNum = findViewById(R.id.user_host_follower);
        mEmptyView = findViewById(R.id.common_empty_view);
        mErrorView = findViewById(R.id.common_error_view);
        mLoadingView = findViewById(R.id.common_loading_view);
        mVipContainer = findViewById(R.id.user_vip_container);
        mExpandIntroduce = findViewById(R.id.user_host_expand_introduce);
        mInterestContainer = findViewById(R.id.user_interest_container);
        mInFluenceContainer = findViewById(R.id.user_influence_container);
        mFacebookHost = findViewById(R.id.profile_facebook_host);
        mInstgramHost = findViewById(R.id.profile_instgram_host);
        mYoutubeHost = findViewById(R.id.profile_youtube_host);
        mTitleBar = findViewById(R.id.user_host_top);
        mMoreBtn = findViewById(R.id.common_more_btn);
        mBackBtn = findViewById(R.id.iv_common_back);
        mTitleHeader = findViewById(R.id.profile_title_user_header);
        mTitleNickContainer = findViewById(R.id.profile_user_title);
        mTitleEditProfile = findViewById(R.id.common_edit_btn);
        mTaskMedal = findViewById(R.id.user_task_medal);
        mWorldCupMedal = findViewById(R.id.user_world_cup_medal);

        mTvChat.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_bottom_message"));
        mEditProfile.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_edit_profile"));
        mExpandIntroduce.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "more"));

        int statusBarHeight = StatusBarHeightCalculator.getInternalDimensionSize(getResources());
        mCollapsingLayout.setMinimumHeight(ScreenUtils.dip2Px(48) + statusBarHeight);
        mTitleBar.setPadding(0, statusBarHeight, 0, 0);
        ViewGroup.LayoutParams layoutParams = mTitleBar.getLayoutParams();
        layoutParams.height = ScreenUtils.dip2Px(48) + statusBarHeight;
        mTitleBar.requestLayout();

        mAppBarLayout.setExpanded(isEpanded);

        mErrorView.setOnErrorViewClickListener(new ErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                showLoading();
                mProfileViewModel.loadUser(mUid);
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                boolean mCollapsingStateTmp = Math.abs(verticalOffset) >= ScreenUtils.dip2Px(75);
                if (mCollapsingStateTmp != mCollapsingState) {
                    mCollapsingState = mCollapsingStateTmp;
                    setTitleStatus();
                }
            }
        });
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PostEventManager.INSTANCE.reset();
                if (position != 0) {
                    UserType userType = (!AccountManager.getInstance().isSelf(mUid)) ? UserType.VISIT : UserType.OWNER;
                    EventLog1.Profile.report14(userType, mUid);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBackBtn.setOnClickListener(mOnClickListener);
        mFollowingNum.setOnClickListener(mOnClickListener);
        mFollowerNum.setOnClickListener(mOnClickListener);
        mEditProfile.setOnClickListener(mOnClickListener);
        mExpandIntroduce.setOnClickListener(mOnClickListener);
        mTitleEditProfile.setOnClickListener(mOnClickListener);
        simple_headView.setOnClickListener(mOnClickListener);
        mTvFollow.setOnClickListener(mOnClickListener);
        mWorldCupMedal.setOnClickListener(mOnClickListener);
        mTvChat.setOnClickListener(mOnClickListener);
        mMoreBtn.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        mVpAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mUid);
        mVp.setAdapter(mVpAdapter);
        pageTab.setupWithViewPager(mVp);
        mProfileViewModel.loadUser(mUid);
    }

    private void bindUser(final User user) {
        if (user == null) {
            return;
        }
        if (!AccountManager.getInstance().isSelf(user.getUid())) {
            mProfileState = new ProfileVisitState(user);
        } else {
            mProfileState = new ProfileOwnerState(user);
            mTaskMedal.setOnClickListener(mOnClickListener);
            mVipContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVerifyClick(user.getUid());
                    EventLog.Profile.report3();
                    EventLog1.Profile.report3(user.getUid());
                }
            });
        }
        setTitleStatus();
        mProfileState.setChatAndFollow(mEditProfile, mChatFollow);
        mProfileState.setInfluence(mInFluenceContainer);
        mProfileState.setInterests(mInterestContainer);
        mProfileState.setVerifyInfo(mVipContainer);
        mProfileState.setSocialHost(mFacebookHost, mInstgramHost, mYoutubeHost);

        int curLevel = user.getCurLevel();
        if (curLevel == UserConstants.USER_COMMON_USER) {
            mHeaderBg.setBackgroundResource(R.drawable.profile_header_bg);
        } else if (curLevel == UserConstants.USER_PRO_USER) {
            mHeaderBg.setBackgroundResource(R.drawable.profile_header_pro_bg);
        } else if (curLevel == UserConstants.USER_TOP_USER) {
            mHeaderBg.setBackgroundResource(R.drawable.profile_header_top_bg);
        }
        VipUtil.set(simple_headView, user.getHeadUrl(), user.getVip());
        VipUtil.setNickName(mTvNickName, user.getCurLevel(), user.getNickName());
        HeadUrlLoader.getInstance().loadHeaderUrl2(mTitleHeader, user.getHeadUrl());

        pageTab.setAllCaps(false);

        UserHonor taskHonor = null, wcHonor = null;

        List<UserHonor> userHonors = user.getUserHonors();
        if (!CollectionUtil.isEmpty(userHonors)) {
            for (UserHonor userHonor : userHonors) {
                if (userHonor.type == UserHonor.TYPE_TASK) {
                    taskHonor = userHonor;
                }
                if (userHonor.type == UserHonor.TYPE_EVENT) {
                    wcHonor = userHonor;
                }
            }
        }
        if (wcHonor != null) {
            mWorldCupMedal.setVisibility(View.VISIBLE);
            mWorldCupMedal.setImageURI(wcHonor.honorPic);
            mWorldCupMedal.setTag(wcHonor.forwardUrl);
        } else {
            mWorldCupMedal.setVisibility(View.GONE);
        }

        if (taskHonor != null) {
            mTaskMedal.setVisibility(View.VISIBLE);
            mTaskMedal.setImageURI(taskHonor.honorPic);
        } else {
            mTaskMedal.setVisibility(View.GONE);
        }

        //-1未知，0男，1女
        int mSexMale = user.getSex();
        if (mSexMale == UserConstant.SEX_UNKUOW) {
            ivGender.setVisibility(View.GONE);
        } else {
            ivGender.setImageResource(mSexMale == UserConstant.SEX_MALE ? R.drawable.common_sex_male : R.drawable.common_sex_female);
        }

        postCount = user.getPostsCount();
        likeCount = user.getMyLikedPostsCount();
        mVpAdapter.setPostAndLikeCount(postCount, likeCount);
        mVpAdapter.notifyDataSetChanged();

        String followerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text");
        String followingText = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_following_num_text");
        setFollowingAndFollower(followerText + " ", user.getFollowedUsersCount(), mFollowerNum);
        setFollowingAndFollower(followingText + " ", user.getFollowUsersCount(), mFollowingNum);

        mProfileState.setIntroduction(tv_introduction, mExpandIntroduce);
        setFollowBtnStatus(user);
        if (!AccountManager.getInstance().isLogin()) {
            EventLog.UnLogin.report12(user.getNickName(), user.getUid());
        }

    }

    private void setFollowingAndFollower(String extra, int num, TextView target) {
        String text = extra + num;
        SpannableString spannableString = new SpannableString(text);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(styleSpan, extra.length(), text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        target.setText(spannableString);
    }

    private void setFollowBtnStatus(User user) {
        if (user == null) {
            return;
        }
        if (!AccountManager.getInstance().isLogin()) {
            doFollowStatus();
            BrowseEventStore.INSTANCE.getFollowUser(user.getUid(), new FollowUserCallBack() {
                @Override
                public void onLoadEntity(final FollowUser user) {

                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (user == null) doFollowStatus();

                            else doFollowingStatus();
                        }
                    });

                }
            });

        } else {
            if (user.isFollowing()) {
                if (user.isFollower()) {
                    doFriendStatus();
                } else {
                    doFollowingStatus();
                }
            } else {
                doFollowStatus();
            }
        }
    }

    private void setTitleStatus() {
        if (user == null) {
            return;
        }

        if (mCollapsingState) {
            mMoreBtn.setImageResource(R.drawable.profile_share_icon_dark);
            mBackBtn.setImageResource(R.drawable.common_back_btn);
            mTitleBar.setBackgroundResource(R.color.white);
            mTitleHeader.setVisibility(View.VISIBLE);
            mTitle.setText(user.getNickName());
            mTitle.setTextColor(Color.parseColor("#313131"));
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            mTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            mMoreBtn.setImageResource(R.drawable.profile_share_icon);
            mBackBtn.setImageResource(R.drawable.common_back_btn_light);
            mTitleBar.setBackgroundResource(android.R.color.transparent);
            mTitleHeader.setVisibility(View.GONE);
            mTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "view_my_profile"));
            mTitle.setTextColor(Color.parseColor("#FFFFFF"));
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            mTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        if (mProfileState != null) {
            mProfileState.setTitle(mCollapsingState, mTitleEditProfile);
        }
    }

    private BrowseInterceptor mBrowseInterceptor = new BrowseInterceptor() {
        @Override
        public void onChatClick() {
            super.onChatClick();
            StartEventManager.getInstance().setActionType(BrowseConstant.TYPE_MESSAGE);
            StartEventManager.getInstance().setFrom_page(2);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            showLoginDialog(BrowseConstant.TYPE_MESSAGE);
        }

        @Override
        public void onFollowClick() {
            super.onFollowClick();
            StartEventManager.getInstance().setActionType(BrowseConstant.TYPE_FOLLOW);
            StartEventManager.getInstance().setFrom_page(2);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());


            BrowseEventStore.INSTANCE.getFollowUserCount(new FollowUser(user.getUid(), user.getNickName()), new FollowUserCountCallBack() {
                @Override
                public void onLoadEntity(boolean inserted, final int count) {
                    if (inserted) {
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                doFollowingStatus();
                                if (count % 3 == 1) {
//                                    RegisterActivity.Companion.show(UserHostPage.this, 1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                                    HalfLoginManager.getInstancce().showLoginDialog(UserHostPage.this, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                                }
                            }
                        });

                    } else {
//                        RegisterActivity.Companion.show(UserHostPage.this, 2, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                        HalfLoginManager.getInstancce().showLoginDialog(UserHostPage.this, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(R.string.common_continue_follow_by_login1);
                            }
                        });
                    }
                }
            });

//            showLoginDialog();
        }

        @Override
        public void onFollowingClick() {
            super.onFollowingClick();
            StartEventManager.getInstance().setActionType(BrowseConstant.TYPE_UNKOWN);
            StartEventManager.getInstance().setFrom_page(2);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            showLoginDialog(BrowseConstant.TYPE_FOLLOW);
        }

        @Override
        public void onFollowerClick() {
            super.onFollowerClick();
            StartEventManager.getInstance().setActionType(BrowseConstant.TYPE_UNKOWN);
            StartEventManager.getInstance().setFrom_page(2);
            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
            showLoginDialog(BrowseConstant.TYPE_FOLLOW);
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_common_back:
                    finish();
                    break;
                case R.id.user_host_follower:
                    EventLog.Profile.report6();
                    EventLog1.Profile.report6(mUid);
                    if (!AccountManager.getInstance().isLogin()) {
                        mBrowseInterceptor.onFollowerClick();
                        return;
                    }
                    if (user != null) {
                        UserFollowActivity.launch(FOLLOWER, user.getUid(), user.getNickName());
                    }
                    break;
                case R.id.user_host_following:
                    EventLog.Profile.report5();
                    EventLog1.Profile.report5(mUid);
                    if (!AccountManager.getInstance().isLogin()) {
                        mBrowseInterceptor.onFollowingClick();
                        return;
                    }
                    if (user != null) {
                        UserFollowActivity.launch(FOLLOWING, user.getUid(), user.getNickName());
                    }
                    break;
                case R.id.simpleView_user_host_headview:
                    if (user != null && !TextUtils.isEmpty(user.getHeadUrl())) {
                        PhotoSelector.previewSinglePic(UserHostPage.this, user.getNickName(), user.getHeadUrl());
                    }
                    break;
                case R.id.user_host_expand_introduce:
                    expandIntroduce();
                    break;
                case R.id.tv_user_host_edit_profile:
                case R.id.common_edit_btn:
                    if (user == null) {
                        return;
                    }
                    EventLog.Profile.report7();
                    EventLog1.Profile.report7(mUid);
                    if (AccountManager.getInstance().isSelf(user.getUid())) {
                        PersonalInformationPage.launch(user.getUid(), user.getNextUpdateNickNameDate(),
                                user.isAllowUpdateNickName(),
                                user.isAllowUpdateSex(),
                                user.getVip());
                    }
                    break;
                case R.id.tv_user_host_follow:
                    onFollowClick();
                    break;
                case R.id.user_task_medal:
                    new DefaultUrlRedirectHandler(mTaskMedal.getContext(), DefaultUrlRedirectHandler.FROM_TASK).onUrlRedirect(MissionManager.INSTANCE.getForwardUrl());
                    MissionManager.INSTANCE.notifyEvent(MissionType.TASK_LIST);
                    break;
                case R.id.user_world_cup_medal:
                    String forwardUrl = (String) v.getTag();
                    if (!TextUtils.isEmpty(forwardUrl)) {
                        new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TASK).onUrlRedirect(forwardUrl);
                    }
                    break;
                case R.id.tv_user_host_chat:
                    onChatClick();
                    break;
                case R.id.common_more_btn:
                    onMoreClick();
                default:
                    break;
            }
        }
    };

    private void onMoreClick() {
        if (user == null) {
            return;
        }
        if (!AccountManager.getInstance().isLogin()) {
            showLoginDialog(BrowseConstant.TYPE_SHARE);
            return;
        }

        SharePackageModel block = CustomShareMenuFactory.Companion.createMenu(ShareMenu.BLOCK, new Function1<ShareMenu, Unit>() {
            @Override
            public Unit invoke(ShareMenu shareMenu) {
                mProfileViewModel.blockUser();
                EventLog.Profile.report8(EventConstants.PROFILE_MORE_TYPE_OTHER);
                EventLog1.Profile.report8(EventLog1.Profile.MoreType.OTHER, mUid);
                EventLog1.BlockUser.report1(user.getUid(), EventConstants.FEED_PAGE_PROFILE, null, null, null, null);
                return null;
            }
        });
        SharePackageModel unblock = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UNBLOCK, new Function1<ShareMenu, Unit>() {
            @Override
            public Unit invoke(ShareMenu shareMenu) {
                mProfileViewModel.unblockUser();
                EventLog.Profile.report8(EventConstants.PROFILE_MORE_TYPE_OTHER);
                EventLog1.Profile.report8(EventLog1.Profile.MoreType.OTHER, mUid);
                EventLog1.BlockUser.report2(user.getUid(), EventConstants.FEED_PAGE_PROFILE, null, null, null, null);
                return null;
            }
        });

        ShareModel shareModel = new ShareModel();
        shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_PROFILE);
        shareModel.setImageUrl(ShareHelper.getShareProfileImage(user));
        ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
        builder.with(mMoreBtn.getContext())
                .login(AccountManager.getInstance().isLoginComplete())
                .shareModel(shareModel)
                .eventModel(new EventModel(EventLog1.Share.ContentType.PROFILE, null, null, EventLog1.Share.ShareFrom.OTHER, EventLog1.Share.PopPage.PROFILE, null, null, null, null,
                        null, null, null, null, null, null, null, null))
                .shortLinkModel(new ShortLinkModel(ShareModel.SHARE_MODEL_TYPE_PROFILE, user.getUid()))
                .shareTitleModel(new ShareTitleModel(ShareModel.SHARE_MODEL_TYPE_PROFILE, user.getNickName()))
                .waterMarkModel(new WaterMarkModel(user.getHeadUrl(), user.getNickName(), ""));
        if (user != null && !AccountManager.getInstance().isSelf(user.getUid())) {
            ArrayList<SharePackageModel> menus = new ArrayList<>();
            if (user.isBlock()) {
                menus.add(unblock);
            } else {
                menus.add(block);
            }
            builder.addMenuList(menus);
        }
        builder.build().share();
        NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_PROFILE);
        EventLog.Profile.report8(EventConstants.PROFILE_MORE_TYPE_SHARE);
        EventLog1.Profile.report8(EventLog1.Profile.MoreType.SHARE, mUid);
    }

    private void onChatClick() {
        EventLog.Profile.report10();
        EventLog1.Profile.report10(mUid);
        if (user == null) {
            return;
        }
        if (!AccountManager.getInstance().isLogin()) {
//            mBrowseInterceptor.onChatClick();
            HalfLoginManager.getInstancce().showLoginDialog(this, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.PROFILE_MESSAGE));
//            RegisterActivity.Companion.show(this, 2, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.PROFILE_MESSAGE));
            return;
        }

        Account account = AccountManager.getInstance().getAccount();

        if (account == null || account.getStatus() == Account.ACCOUNT_HALF) {

            VerifyDialog.Companion.show(this, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.CHAT));
        } else if (user.getStatus() == Account.ACCOUNT_DEACTIVATE) {
            ToastUtils.showShort(ResourceTool.getString("user_deactivate_message_info"));
        } else {
            IMHelper.INSTANCE.getSession(user.getUid(),
                    user.getNickName(), user.getHeadUrl(), new Function1<SESSION, Unit>() {
                        @Override
                        public Unit invoke(SESSION session) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ImConstant.IM_SESSION_KRY, session);
                            if (!TextUtils.isEmpty(mEntryType)) {
                                bundle.putString(RouteConstant.ROUTE_KEY_ENTRY_TYPE, mEntryType);
                            }
                            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
                            return null;
                        }
                    });
        }
    }

    private void onFollowClick() {
        EventLog.Profile.report9();
        EventLog1.Profile.report9(mUid);
        EventLog.UnLogin.report24(EventConstants.UNLOGIN_FROM_PAGE_PROFILE, mUid);
        if (!AccountManager.getInstance().isLogin()) {
            mBrowseInterceptor.onFollowClick();
            return;
        }
        if (user != null && !user.isFollowing()) {
            if (user.getStatus() == Account.ACCOUNT_DEACTIVATE) {
                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_message_info"));
            } else {
                mProfileViewModel.followUser();
            }
        } else if (user != null) {

            unfollowDialog = CommonConfirmDialog.showConfirmDialog(UserHostPage.this,
                    ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follow_dialog_title"), new CommonConfirmDialog.IConfirmDialogListener() {
                        @Override
                        public void onClickCancel() {

                        }

                        @Override
                        public void onClickConfirm() {
                            mProfileViewModel.unfollowUser();
                        }
                    });
        }
    }

    private void onVerifyClick(String uid) {
        String certifyAddress = VipUtil.getProfileAddress();
        Account account = AccountManager.getInstance().getAccount();
        if (!TextUtils.isEmpty(certifyAddress) && account != null) {
            String language = LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
            StringBuilder urlBuilder = new StringBuilder(certifyAddress);
            urlBuilder.append("?")
                    .append("profileId=").append(uid).append("&")
                    .append("userId=").append(account.getUid()).append("&")
                    .append("token=").append(account.getAccessToken()).append("&")
                    .append("la=").append(language);

            Bundle bundle = new Bundle();
            bundle.putString("url", urlBuilder.toString());
            bundle.putString(WebViewConstant.KEY_FROM, DefaultUrlRedirectHandler.FROM_PROFILE);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));
        }
    }

    private void expandIntroduce() {
        mExpandIntroduce.setVisibility(View.GONE);
        tv_introduction.setMaxLines(Integer.MAX_VALUE);
    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    private void showLoadingDlg() {
        if (mLoadingDlg == null) {
            mLoadingDlg = new LoadingDlg(this);
        }
        mLoadingDlg.show();
    }

    private void dismissLoadingDlg() {
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }

    private void doFriendStatus() {
        mTvFollow.setTextColor(getResources().getColor(R.color.common_text_color_afb0b1));
        mTvFollow.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "friends_btn_text"));
    }

    private void doFollowingStatus() {
        mTvFollow.setTextColor(getResources().getColor(R.color.common_text_color_afb0b1));
        mTvFollow.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "following_btn_text"));
    }

    private void doFollowStatus() {
        mTvFollow.setTextColor(getResources().getColor(R.color.main_orange_dark));
        mTvFollow.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "follow_btn_text"));
        EventLog1.Follow.report3(EventConstants.FEED_PAGE_PROFILE, mUid, null, null, null, null, null);
    }

    private void showLoginDialog(int type) {
        HalfLoginManager.getInstancce().showLoginDialog(this, new RegisterAndLoginModel(LoginEventHelper.convertTypeToPageSource(type)));
//        RegisterActivity.Companion.show(this, 0, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
//        if (BrowseManager.getLoginB()) {
//            RegisterLoginMethodDialog.show(UserHostPage.this);
//            return;
//        }
//        String info;
//        switch (type) {
//
//            case BrowseConstant.TYPE_SHOW_CONTENT_FOLLOW0:
//                info = ResourceTool.getString("common_continue_follow_by_login0");
//                break;
//            case BrowseConstant.TYPE_SHOW_CONTENT_FOLLOEW1:
//                info = ResourceTool.getString("common_continue_by_login");
//                break;
//            case BrowseConstant.TYPE_SHOW_CONTENT_LIKE:
//                info = ResourceTool.getString("common_continue_like_by_login");
//                break;
//
//            case BrowseConstant.TYPE_SHOW_CONTENT_DEFAULT:
//            default:
//                info = ResourceTool.getString("common_continue_by_login");
//        }
//
//
//        BrowseSchemeManager.getInstance().setUserProfile(user.getUid());
//        StartEventManager.getInstance().setFrom_page(2);
//        StartEventManager.getInstance().setActionType(BrowseConstant.TYPE_SHARE);
//        ActionSnackBar.getInstance().showLoginSnackBar(mAppBarLayout,
//                info,
//                ResourceTool.getString("common_login"), 3000, new ActionSnackBar.ActionBtnClickListener() {
//                    @Override
//                    public void onActionClick(View view) {
//                        // TODO: 2018/7/11
//                        if (BrowseManager.getLoginA())
//                            RegisterLoginMethodDialog.show(view.getContext());
//                        else
//                            RegistActivity.show(view.getContext());
//                    }
//                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowUtil.INSTANCE.resumeSoftInputMode(getWindow(), softInputMode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        softInputMode = WindowUtil.INSTANCE.pauseSoftInputMode(getWindow());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id || event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mIsDestroyed = true;
        user = null;
        if (unfollowDialog != null) {
            unfollowDialog.dismiss();
            unfollowDialog = null;
        }

        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    public static void launch(boolean isExpended, String uId) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(UserConstant.IS_EXPANDED, isExpended);
        bundle.putString(UserConstant.UID, uId);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));
    }

//    public static void launch(boolean isExpended, boolean isBrowse, String uId) {
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(UserConstant.IS_EXPANDED, isExpended);
//        bundle.putBoolean(UserConstant.IS_BROWSE, isBrowse);
//        bundle.putString(UserConstant.UID, uId);
//
//        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));
//    }

    public static void launch(boolean isExpended, String uId, int fromPage) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(UserConstant.IS_EXPANDED, isExpended);
        bundle.putInt(UserConstant.FROM_PAGE, fromPage);
        bundle.putString(UserConstant.UID, uId);

        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_HOST_EVENT, bundle));
    }


}

package com.redefine.welike.business.topic.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.topic.ui.adapter.TopicUserAdapter;
import com.redefine.welike.business.topic.ui.contract.ITopicUserContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class TopicUserView implements ITopicUserContract.ITopicUserView, View.OnClickListener
        , BaseErrorView.IErrorViewClickListener, ILoadMoreDelegate {
    private RecyclerView mRecycler;
    private View mBackBtn;
    private TextView mTitleView;
    private ITopicUserContract.ITopicUserPresenter mPresenter;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private EmptyView mEmptyView;

    private VipAvatar mUserHeader;
    private TextView mUserName;
    private TextView mPasserByTime;
    private TextView mUserIntroduction;
    private String mPublishNow;
    private String mPublishInMinute;
    private String mPublishInHour;
    private String mPublishInTime;
    private ViewGroup mTopicHeader;
    private ViewGroup mTopicUsersHeaderLayout;
    private TextView mTopicUsersHeaderText;


    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_user_layout, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecycler = view.findViewById(R.id.topic_user_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mErrorView = view.findViewById(R.id.common_error_view);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        mBackBtn = view.findViewById(R.id.iv_common_back);
        mTitleView = view.findViewById(R.id.tv_common_title);

        mEmptyView.showEmptyImg(R.drawable.ic_common_empty);
        mTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_title"));
        mBackBtn.setOnClickListener(this);

        mErrorView.setOnErrorViewClickListener(this);
        initHeader(view);
    }

    private void initHeader(View itemView) {

        mUserHeader = itemView.findViewById(R.id.topic_user_head);
        mUserName = itemView.findViewById(R.id.topic_user_name);
        mPasserByTime = itemView.findViewById(R.id.topic_user_time);
        mUserIntroduction = itemView.findViewById(R.id.topic_user_introduction);
        mTopicUsersHeaderLayout = itemView.findViewById(R.id.topic_host_layout);
        mTopicUsersHeaderText = itemView.findViewById(R.id.topic_host_text);
        mTopicHeader = itemView.findViewById(R.id.topic_head);

        mPublishNow = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_just_now");
        mPublishInMinute = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_minutes");
        mPublishInHour = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_hours");
        mPublishInTime = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_day");


    }

    private String getUrlFromSp() {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences("VIP", Context.MODE_PRIVATE);
        String content = sp.getString("KEY_VERIFY_URL", "");
        try {
            JSONObject jo = new JSONObject(content);
            String url = jo.getString("topicAdmApply");
            return url;

        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public void setData(final TopicUser user) {
        mTopicHeader.setVisibility(View.VISIBLE);
        mTopicUsersHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WebViewPage.
                Account account = AccountManager.getInstance().getAccount();
                String url = getUrlFromSp();
                if (!TextUtils.isEmpty(url) && account != null) {
                    String language = LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
                    StringBuilder urlBuilder = new StringBuilder(url);
                    urlBuilder.append("?")
                            .append("userId=").append(account.getUid()).append("&")
                            .append("token=").append(account.getAccessToken()).append("&")
                            .append("la=").append(language).append("&")
                            .append("topicId=").append(user.topicId);

                    Bundle bundle = new Bundle();
                    bundle.putString("url", urlBuilder.toString());
                    bundle.putString(WebViewConstant.KEY_FROM, DefaultUrlRedirectHandler.FROM_TOP_USER);
                  //  String temp=urlBuilder.toString();
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));

                }


            }
        });
        mUserHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHostPage.launch(true, user.user.getUid());
            }
        });


        VipUtil.set(mUserHeader, user.user.getHeadUrl(), user.user.getVip());
        VipUtil.setNickName(mUserName, user.user.getCurLevel(), user.user.getNickName());
        mTopicUsersHeaderText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_host_header"));

        if (user.user.getSex() == UserBase.MALE) {
            mUserName.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mUserName.getResources(), R.drawable.common_sex_male), null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(5));
        } else if (user.user.getSex() == UserBase.FEMALE) {
            mUserName.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mUserName.getResources(), R.drawable.common_sex_female), null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(5));
        } else {
            mUserName.setCompoundDrawables(null, null, null, null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(0));
        }

        mPasserByTime.setText(getPasserByTime(user.time));
        mUserIntroduction.setText(user.user.getIntroduction());

    }

    private String getPasserByTime(long passTime) {
        long offset = System.currentTimeMillis() - passTime;
        if (offset < DateTimeUtil.SECOND_60) {
            return mPublishNow;
        } else if (offset < DateTimeUtil.HOUR_1) {
            return mPublishInMinute;
        } else if (offset < DateTimeUtil.DAY_1) {
            return mPublishInHour;
        } else {
            return String.format(mPublishInTime, DateTimeUtil.INSTANCE.formatDateTime(passTime));
        }
    }


    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            mPresenter.onBackPressed();
        }
    }

    @Override
    public void setPresenter(ITopicUserContract.ITopicUserPresenter locationPasserByPresenter) {
        mPresenter = locationPasserByPresenter;
    }

    @Override
    public void setAdapter(TopicUserAdapter mAdapter) {
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
    }

}

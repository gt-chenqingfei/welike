package com.redefine.welike.business.supertopic.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.androidkun.xtablayout.XTabLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.fresco.loader.BannerUrlLoader;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.richtext.RichItem;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.business.supertopic.management.SuperTopicDetailManager;
import com.redefine.welike.business.supertopic.management.bean.SuperTopicDetailInfo;
import com.redefine.welike.business.supertopic.ui.adapter.SuperTopicViewPagerAdapter;
import com.redefine.welike.commonui.framework.PageStackManager;

@Route(path = RouteConstant.LAUNCH_SUPER_TOPIC_PAGE)
public class SuperTopicLandingActivity extends BaseActivity implements SuperTopicDetailManager.SuperTopicDetailInfoCallback, AppBarLayout.OnOffsetChangedListener {
    private static final int DEFAULT_SELECT_POSITION = 1;
    private SimpleDraweeView banner;
    private TextView bannerTitle;
    private TextView bannerPostNum;
    private TextView titleView;
    private LoadingView loadingView;
    private ErrorView errorView;
    private SuperTopicDetailManager detailManager = new SuperTopicDetailManager();
    private SuperTopicViewPagerAdapter pagerAdapter;
    private PageStackManager stackManager;
    private SuperTopicDetailInfo mSuperTopic;
    private AppBarLayout mAppBarLayout;
    private View mTablayoutLineView;
    private View mTablayoutShadowView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_topic_landing_page);

        View backBtn = findViewById(R.id.common_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        if (bundle == null) return;

        stackManager = new PageStackManager(this);

        String topicId = bundle.getString("topicId");
        boolean isBrowse = !AccountManager.getInstance().isLoginComplete();
        pagerAdapter = new SuperTopicViewPagerAdapter(stackManager, topicId, isBrowse);

        banner = findViewById(R.id.super_topic_page_banner);
        mTablayoutLineView = findViewById(R.id.topic_landing_view_line);
        mTablayoutShadowView = findViewById(R.id.topic_landing_view_shadow);
        mAppBarLayout = findViewById(R.id.super_topic_page_app_bar);
        bannerTitle = findViewById(R.id.super_topic_page_banner_title);
        bannerPostNum = findViewById(R.id.super_topic_page_banner_post);
        titleView = findViewById(R.id.super_topic_page_title_text);
        loadingView = findViewById(R.id.common_loading_view);
        errorView = findViewById(R.id.common_error_view);
        View publishBtn = findViewById(R.id.super_topic_publish_btn);
        ViewPager viewPager = findViewById(R.id.super_topic_view_pager);
        XTabLayout pageTab = findViewById(R.id.super_topic_page_tabs);
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSuperTopic != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FeedConstant.KEY_HASH_TYPE, RichItem.RICH_TYPE_SUPER_TOPIC);
                    bundle.putString(FeedConstant.KEY_HASH_ID, mSuperTopic.tid);
                    bundle.putString(FeedConstant.KEY_HASH_TAG, mSuperTopic.topicName);

                    PublishPostStarter.INSTANCE.startActivityFromTopic(SuperTopicLandingActivity.this, bundle);
                }

            }
        });
        if (isBrowse) {
            publishBtn.setVisibility(View.GONE);
        }
        pageTab.setAllCaps(false);
        viewPager.setAdapter(pagerAdapter);
        pageTab.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(DEFAULT_SELECT_POSITION);
        pagerAdapter.setUserVisibleHint(DEFAULT_SELECT_POSITION);

        if (!TextUtils.isEmpty(topicId)) {
            detailManager.superTopicInfo(topicId);
            detailManager.setListener(this);
            showLoading();
        } else {
            showErrorView();
        }

        mAppBarLayout.addOnOffsetChangedListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (pagerAdapter != null) {
                    pagerAdapter.setUserVisibleHint(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onSuperTopicDetailInfoSuccessed(SuperTopicDetailInfo info) {
        showContentView();
        mSuperTopic = info;
        titleView.setText(info.topicName);
        bannerTitle.setText(info.topicName);
        bannerPostNum.setText(String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "post_nums_on_this_topic"), String.valueOf(info.postsCount)));
        BannerUrlLoader.getInstance().loadBannerUrl(banner, info.bannerUrl);

        pagerAdapter.getInfoPage().showTopPosts(info.topicInfo, info.topPosts);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pagerAdapter != null) {
            pagerAdapter.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pagerAdapter != null) {
            pagerAdapter.onActivityPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onSuperTopicDetailInfoError(int errorCode) {
        showErrorView();
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    private void showErrorView() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void showContentView() {
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            mTablayoutLineView.setVisibility(View.GONE);
            mTablayoutShadowView.setVisibility(View.VISIBLE);
        } else {
            mTablayoutLineView.setVisibility(View.VISIBLE);
            mTablayoutShadowView.setVisibility(View.GONE);
        }
    }
}

package com.redefine.welike.business.feeds.ui.viewholder;

/**
 * Created by MR on 2018/1/14.
 */

//public class DiscoverHeaderViewHolder extends BaseRecyclerViewHolder<HotFeedHeaderBean> implements View.OnClickListener, InterestCategoryViewHolder.onClickInterestListener {
public class DiscoverHeaderViewHolder {

    public static final int TAB_HOT = 0;
    public static final int TAB_LATEST = 1;
    public static final String INTEREST_ALL = "interest_all";

//    private final com.youth.banner.Banner mBannerView;
//    private final View mBannerLayout;
//
////    private final View mInterestCategoryLayout;
//
////    private final RelativeLayout mTabHot, mTabLatest;
////    private final TextView mHotText, mLatestText;
////    private final View mHotIndicator, mLatestIndicator;
//    private final ErrorView mErrorView;
//    private final EmptyView mEmptyView;
////    private final View mLoadingView, mViewContainer;
//
//    private final TopUserViewHolderDelegate mTopUserDelegate;
//
//    private final TopicViewDelegate mTopicViewDelegate;
//
//    private final ViewGroup mTopUserLayout;
//
//    private final InterestCategoryViewHolder mInterestCategoryViewHolder;
//
//    private final OnTabChangeListener mListener;
//    private onClickInterestListener mInterestListener;
////    private final View mTopicLayout;
//    private final View mTopicDivider;
//    private ArrowTextView guideBanner, guideRank;
//
//    public DiscoverHeaderViewHolder(View itemView, OnTabChangeListener listener, BaseErrorView.IErrorViewClickListener errorListener) {
//        super(itemView);
//        mListener = listener;
////        mInterestListener = onClickInterestListener;
//        mBannerView = itemView.findViewById(R.id.hot_banner_view);
//        mBannerLayout = itemView.findViewById(R.id.hot_banner_layout);
//        guideBanner = itemView.findViewById(R.id.guide_banner);
//        guideRank = itemView.findViewById(R.id.guide_rank);
////        mInterestCategoryLayout = itemView.findViewById(R.id.discover_interest_category_layout);
////
////        mTabHot = itemView.findViewById(R.id.discover_main_hot_tab);
////        mTabLatest = itemView.findViewById(R.id.discover_main_latest_tab);
////        mHotText = itemView.findViewById(R.id.discover_main_hot_text);
////        mLatestText = itemView.findViewById(R.id.discover_main_latest_text);
////        mHotIndicator = itemView.findViewById(R.id.discover_main_hot_indicator);
////        mLatestIndicator = itemView.findViewById(R.id.discover_main_latest_indicator);
//
//        mErrorView = itemView.findViewById(R.id.discover_error_view);
////        mLoadingView = itemView.findViewById(R.id.discover_loading_view);
//        mEmptyView = itemView.findViewById(R.id.discover_empty_view);
////        mViewContainer = itemView.findViewById(R.id.discover_view_container);
//        mTopUserLayout = itemView.findViewById(R.id.discover_top_user_layout);
////        mTopicLayout = itemView.findViewById(R.id.discover_topic_layout);
//        mTopicDivider = itemView.findViewById(R.id.top_user_interest_tag_divider);
//
////        mTopicViewDelegate = new TopicViewDelegate(mTopicLayout);
//
//        mTopUserDelegate = new TopUserViewHolderDelegate();
//
//        mTopUserDelegate.createViewHolder(mTopUserLayout);
//
//        mInterestCategoryViewHolder = new InterestCategoryViewHolder(itemView.findViewById(R.id.discover_interest_category));
//        mHotText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_hot"));
//        mLatestText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_latest"));
//
//        mTabHot.setOnClickListener(this);
//        mTabLatest.setOnClickListener(this);
//        mInterestCategoryViewHolder.setListener(this);
//        mErrorView.setOnErrorViewClickListener(errorListener);
//        mEmptyView.showEmptyText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_empty_text"));
//    }
//
//    private ArrayList<Banner> bannerMap = new ArrayList<>();
//
//    @Override
//    public void bindViews(RecyclerView.Adapter adapter, final HotFeedHeaderBean data) {
//        super.bindViews(adapter, data);
//        if (CollectionUtil.isEmpty(data.getBanner())) {
//            mBannerLayout.setVisibility(View.GONE);
//        } else {
//            mBannerLayout.setVisibility(View.VISIBLE);
//            if (bannerListener != null) {
//                bannerListener.onShow(mBannerLayout, guideBanner);
//            }
//            bannerMap.clear();
//            ArrayList<String> bannerUrls = new ArrayList<>();
//            for (Banner banner : data.getBanner()) {
//                bannerUrls.add(banner.getPhoto());
//                bannerMap.add(banner);
//            }
//
//            mBannerView.setImages(bannerUrls)
//                    .setImageLoader(new MyImageLoader())
//                    .setOnBannerListener(new OnBannerListener() {
//                        @Override
//                        public void OnBannerClick(int position) {
//                            if (bannerListener != null) {
//                                bannerListener.onClick(mBannerLayout);
//                            }
//                            final Banner bean = bannerMap.get(position);
//                            if (!TextUtils.isEmpty(bean.getAction())) {
//                                MissionManager.INSTANCE.notifyEvent(MissionType.BROWSE_OFFICIAL_ACTIVE);
//                                new DefaultUrlRedirectHandler(mBannerView.getContext()).onUrlRedirect(bean.getAction());
//                            }
//                        }
//                    })
//                    .start();
//        }
//
////        mTopUserDelegate.guideListener = new GuideListener<View>() {
////            @Override
////            public void onShow(View view, ArrowTextView guide) {
////                Log.e("DDAI", "DiscoverHeaderViewHolder");
////                rankListener.onShow(view, guideRank);
////            }
////
////            @Override
////            public void onClick(View view) {
////                rankListener.onClick(view);
////            }
////        };
//        mTopUserDelegate.bindView(mTopUserLayout, data.getTopUser());
//        mTopicViewDelegate.bindView(data.getTopics());
//        if (CollectionUtil.isEmpty(data.getInterests())) {
//            mInterestCategoryLayout.setVisibility(View.GONE);
//        } else {
//            mInterestCategoryLayout.setVisibility(View.VISIBLE);
//            mInterestCategoryViewHolder.bindViews(data.getInterests());
//        }
//
//        if (mInterestCategoryLayout.getVisibility() == View.GONE && mTopUserLayout.getVisibility() == View.GONE && mTopicLayout.getVisibility() == View.GONE) {
//            mTopicDivider.setVisibility(View.GONE);
//        } else {
//            mTopicDivider.setVisibility(View.VISIBLE);
//        }
//
//        int loadingState = data.getLoadingState();
//        if (loadingState == HotFeedHeaderBean.STATE_LOADING) {
//            showLoadingView();
//        } else if (loadingState == HotFeedHeaderBean.STATE_LOAD_EMPTY) {
//            showEmptyView();
//        } else if (loadingState == HotFeedHeaderBean.STATE_LOAD_ERROR) {
//            showErrorView();
//        } else {
//            showContent();
//        }
//
//        if (data.getCurrentTab() == TAB_HOT) {
//            selectHotTab();
//        } else {
//            selectLatestTab();
//        }
//    }
//
//    public void setCurrentActivityState(HotFeedHeaderBean data, boolean isResume) {
//        if (!CollectionUtil.isEmpty(data.getBanner())) {
//            if (isResume) {
//                mBannerView.startAutoPlay();
//            } else {
//                mBannerView.stopAutoPlay();
//            }
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.discover_main_hot_tab:
//                if (mListener != null) {
//                    mListener.onTabChange(TAB_HOT);
//                }
//                selectHotTab();
//                break;
//            case R.id.discover_main_latest_tab:
//                if (mListener != null) {
//                    mListener.onTabChange(TAB_LATEST);
//                }
//                selectLatestTab();
//                break;
//        }
//    }
//
//    private void showContent() {
//        mViewContainer.setVisibility(View.GONE);
//    }
//
//    private void showEmptyView() {
//        mViewContainer.setVisibility(View.VISIBLE);
//        mEmptyView.setVisibility(View.VISIBLE);
//        mLoadingView.setVisibility(View.GONE);
//        mErrorView.setVisibility(View.GONE);
//    }
//
//    private void showErrorView() {
//        mViewContainer.setVisibility(View.VISIBLE);
//        mErrorView.setVisibility(View.VISIBLE);
//        mEmptyView.setVisibility(View.GONE);
//        mLoadingView.setVisibility(View.GONE);
//    }
//
//    private void showLoadingView() {
//        mViewContainer.setVisibility(View.VISIBLE);
//        mLoadingView.setVisibility(View.VISIBLE);
//        mEmptyView.setVisibility(View.GONE);
//        mErrorView.setVisibility(View.GONE);
//    }
//
//    private void selectHotTab() {
//        mHotText.setTextColor(Color.parseColor("#464646"));
//        mLatestText.setTextColor(Color.parseColor("#AFB0B1"));
//        mHotIndicator.setSelected(true);
//        mLatestIndicator.setSelected(false);
//    }
//
//    private void selectLatestTab() {
//        mLatestText.setTextColor(Color.parseColor("#464646"));
//        mHotText.setTextColor(Color.parseColor("#AFB0B1"));
//        mLatestIndicator.setSelected(true);
//        mHotIndicator.setSelected(false);
//    }
//
//    @Override
//    public void onclickInterestItem(UserBase.Intrest intrest) {
//        if (mInterestListener != null)
//            mInterestListener.onclickInterestItem(intrest);
//    }
//
//
//    public class MyImageLoader extends ImageLoader {
//
//        @Override
//        public void displayImage(Context context, Object path, ImageView imageView) {
//            if (path != null) {
//                Uri uri = Uri.parse((String) path);
//                imageView.setImageURI(uri);
//            }
//        }
//
//        @Override
//        public ImageView createImageView(Context context) {
//            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
//            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
//            return simpleDraweeView;
//        }
//    }
//
//    public interface OnTabChangeListener {
//        void onTabChange(int tab);
//    }
//
//    private GuideListener<View> bannerListener;
//    private GuideListener<View> rankListener;
//
//    public void setBannerListener(GuideListener<View> bannerListener) {
//        this.bannerListener = bannerListener;
//    }
//
//    public void setRankListener(GuideListener<View> rankListener) {
//        this.rankListener = rankListener;
//    }
//
//
//    public interface onClickInterestListener {
//        void onclickInterestItem(UserBase.Intrest intrest);
//    }


}

package com.redefine.welike.business.feeds.ui.view;

import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.Switcher;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUserCallBack;
import com.redefine.welike.business.browse.management.dao.FollowUserCountCallBack;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedHeadContract;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwb on 2018/1/10.
 */

public class CommonFeedHeadView implements ICommonFeedHeadContract.ICommonFeedHeadView {

    private final VipAvatar mHeadView;
    private final TextView mName;
    private final TextView mTime;
    private final AppCompatImageView mArrowBtn;
    private final View mHot;
    private final View mTop;
    private final TextView mHotText;
    private final TextView mTopText;
    private final UserFollowBtn mFollowBtn;
    private final TextView mReadCount;
    private final View topPostFlag;
    /*  private final TextView mReadCountTv;
      private final TextView mReadCountViews;*/
    private final SimpleDraweeView mEventMark;
    public final IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;
    //    private final FrameLayout mUserHeadLayout;
    private ICommonFeedHeadContract.ICommonFeedHeadPresenter mPresenter;
    private View.OnClickListener mFollowClickListener;
    private OnPostButtonClickListener mPostButtonClickListener;

    // feed的显示的信息
    private ImageView mFeedFlagImage;
    private TextView mFeedFlagValue, tvOperation;
    private ViewGroup mFeedFlagViewGroup;

    private boolean isTrending = false;

    public CommonFeedHeadView(View rootView) {
        mHeadView = rootView.findViewById(R.id.common_feed_head);
        mName = rootView.findViewById(R.id.common_feed_name);
        mTime = rootView.findViewById(R.id.common_feed_time);
//        mUserHeadLayout = rootView.findViewById(R.id.common_feed_user_head_layout);
        mEventMark = rootView.findViewById(R.id.common_feed_event_mark);
        mFeedFlagViewGroup = rootView.findViewById(R.id.common_trending_layout);
        mArrowBtn = rootView.findViewById(R.id.common_feed_arrow_btn);
        mFollowBtn = rootView.findViewById(R.id.common_feed_follow_btn);
        mReadCount = rootView.findViewById(R.id.common_feed_read_count);
       /* mReadCountTv = rootView.findViewById(R.id.read_counts);
        mReadCountViews = rootView.findViewById(R.id.read_views);*/
        mHot = rootView.findViewById(R.id.common_hot);
        mTop = rootView.findViewById(R.id.common_top);
        topPostFlag = rootView.findViewById(R.id.top_post_flag);
        mHotText = rootView.findViewById(R.id.common_hot_text);
        mTopText = rootView.findViewById(R.id.common_top_text);

        mFeedFlagImage = rootView.findViewById(R.id.common_trending_image);
        mFeedFlagValue = rootView.findViewById(R.id.common_trending_value);

        mHotText.setText(R.string.trending_flag);
        mTopText.setText(R.string.top_flag);
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mFollowBtn, false);
        tvOperation = rootView.findViewById(R.id.tv_common_feed_operation);
    }

    @Override
    public void bindViews(final PostBase postBase, final RecyclerView.Adapter adapter) {
        if (Switcher.SHOW_FEED_INFO) {
            tvOperation.setVisibility(View.VISIBLE);
            StringBuilder buffer = new StringBuilder("Language=");
            buffer.append(postBase.getLanguage());
            buffer.append(";tags=[");
            if (postBase.getTags() != null) {
                for (String t : postBase.getTags()) {
                    buffer.append(t);
                    buffer.append(",");
                }
            }
            buffer.append("];Strategy=");
            buffer.append(postBase.getStrategy());
            buffer.append(";origin=");
            buffer.append(postBase.origin);
            buffer.append(";operationType=");
            buffer.append(postBase.getOperationType());
            buffer.append(";postId=");
            buffer.append(postBase.getPid());
            tvOperation.setText(buffer.toString());
        } else {
            tvOperation.setVisibility(View.GONE);
        }
//        HeadUrlLoader.getInstance().loadHeaderUrl(mHeadView, postBase.getHeadUrl());
        VipUtil.set(mHeadView, postBase.getHeadUrl(), postBase.getVip());
        VipUtil.setNickName(mName, postBase.getCurLevel(), postBase.getNickName());

        if (postBase.isHot()) {
            mFeedFlagViewGroup.setVisibility(View.VISIBLE);
            mFeedFlagImage.setImageResource(R.drawable.ic_feed_post_trending_flag);
            mFeedFlagValue.setText(MyApplication.getAppContext().getText(R.string.trending_flag));
        } else {
            mFeedFlagViewGroup.setVisibility(View.GONE);
        }

        mHot.setVisibility(postBase.isHot() ? View.VISIBLE : View.GONE);
        mHotText.setVisibility(postBase.isHot() ? View.VISIBLE : View.GONE);

        String time;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (!isTrending) {
            time = DateTimeUtil.INSTANCE.formatPostPublishTime(mTime.getResources(),postBase.getTime());
            spannableStringBuilder.append(time);
            spannableStringBuilder.append(" ");
        }

        String verified = VipUtil.getDescription(postBase.getVip());
        if (isTrending && !TextUtils.isEmpty(verified)) {
            spannableStringBuilder.append(verified);
        } else if (!TextUtils.isEmpty(postBase.getShareSource())) {
            spannableStringBuilder.append(mTime.getResources().getString(R.string.feed_from_upper));
            spannableStringBuilder.append(postBase.getShareSource());
        } else if (!TextUtils.isEmpty(postBase.getFrom())) {
            spannableStringBuilder.append(mTime.getResources().getString(R.string.feed_from_upper));
            spannableStringBuilder.append(postBase.getFrom());
        }

        if (spannableStringBuilder.length() > 0) {
            mTime.setVisibility(View.VISIBLE);
        } else {
            mTime.setVisibility(View.GONE);
        }
        mTime.setText(spannableStringBuilder);

        if (postBase.isTop()) {
            topPostFlag.setVisibility(View.VISIBLE);
        } else {
            topPostFlag.setVisibility(View.INVISIBLE);
        }

//        from.setSpan(new ForegroundColorSpan(mHeadView.getResources().getColor(R.color.post_from_text_color)),
//                0, from.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPostButtonClickListener != null) {
                    mPostButtonClickListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_HEAD);
                }

                if (iBrowseClickListener != null) {
                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_HEAD, false, 0);
                }
                UserHostPage.launch(true, postBase.getUid());
                ExposeEventReporter.INSTANCE.reportPostClick(postBase, getFeedSource(adapter), EventLog1.FeedView.FeedClickArea.AVATAR);
            }
        });
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mPostButtonClickListener != null) {
                    mPostButtonClickListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_HEAD);
                }

                if (iBrowseClickListener != null) {
                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_HEAD, false, 0);
                }

                UserHostPage.launch(true, postBase.getUid());
                ExposeEventReporter.INSTANCE.reportPostClick(postBase, getFeedSource(adapter), EventLog1.FeedView.FeedClickArea.AVATAR);
            }
        });

        mFollowBtn.setTag(postBase);
        mFollowBtnPresenter.bindView(postBase.getUid(), postBase.isFollowing(), false, new FollowEventModel(getFeedSource(adapter), postBase));

        try {
            UserHonor shownHonor = null;
            for (UserHonor honor : postBase.userhonors) {
                if (honor.type == UserHonor.TYPE_EVENT) {
                    shownHonor = honor;
                }
            }
            if (shownHonor != null) {
                mEventMark.setVisibility(View.VISIBLE);
                mEventMark.setImageURI(Uri.parse(shownHonor.honorPic));
                final UserHonor finalShownHonor = shownHonor;
                mEventMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iBrowseClickListener != null) {
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                            return;
                        }
                        new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TASK).onUrlRedirect(finalShownHonor.forwardUrl);
                    }
                });
            } else {
                mEventMark.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mArrowBtn.setVisibility(View.VISIBLE);
        mArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPostButtonClickListener != null) {
                    mPostButtonClickListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_MORE);
                }
                if (iBrowseClickListener != null) {
                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                    return;
                }
                mPresenter.onMenuBtnClick(postBase);
                if (guideListener != null) {
                    guideListener.onClick(v);
                }
                ExposeEventReporter.INSTANCE.reportPostClick(postBase, getFeedSource(adapter), EventLog1.FeedView.FeedClickArea.MORE);
            }
        });
    }

    private String getFeedSource(RecyclerView.Adapter adapter) {
        if (adapter instanceof FeedRecyclerViewAdapter) {
            return ((FeedRecyclerViewAdapter) adapter).getFeedSource();
        } else {
            return "INVALID";
        }
    }

    @Override
    public void setReadCount(String readCount) {
        String views = mReadCount.getResources().getString(R.string.feed_views);

        mReadCount.setText(readCount + GlobalConfig.NEW_LINE + views);
       /* if (readCount.length() < 4) {
            mReadCount.setText(readCount + " " + views);
        } else {
            mReadCount.setText(readCount + GlobalConfig.NEW_LINE + views);
        }*/
    }

    @Override
    public void setPresenter(ICommonFeedHeadContract.ICommonFeedHeadPresenter
                                     commonFeedHeadPresenter) {
        mPresenter = commonFeedHeadPresenter;
    }

    @Override
    public void performClickDeleteBtn() {
        mArrowBtn.performClick();
    }

    @Override
    public void dismissFollowBtn(boolean b) {
        mFollowBtn.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    public void dismissArrowBtn(boolean b) {

        if (b) {
            mArrowBtn.setEnabled(false);
            mArrowBtn.setImageResource(R.color.transparent);
            mArrowBtn.setPadding(0, 0, ScreenUtils.dip2Px(mArrowBtn.getContext(), 12), 0);
        }

    }


    @Override
    public void showTopFlag(boolean b) {
        if (b) {
            mFeedFlagViewGroup.setVisibility(View.VISIBLE);
            mFeedFlagImage.setImageResource(R.drawable.ic_common_feed_top_flag);
            mFeedFlagValue.setText(MyApplication.getAppContext().getText(R.string.top_flag));
        } else {
            mFeedFlagViewGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void showHotFlag(boolean b) {
        if (b) {
            mFeedFlagViewGroup.setVisibility(View.VISIBLE);
            mFeedFlagImage.setImageResource(R.drawable.ic_feed_post_trending_flag);
            mFeedFlagValue.setText(MyApplication.getAppContext().getText(R.string.trending_flag));
        } else {
            mFeedFlagViewGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void showReadCount(boolean b) {
        mReadCount.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showTopPostFlag(boolean b) {
        topPostFlag.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void setGuideListener(View.OnClickListener listener) {
        this.guideListener = listener;
    }

    public void setFollowBtnListener(View.OnClickListener listener) {
        mFollowClickListener = listener;
        mFollowBtn.addOtherListener(listener);
    }

    public void setPostButtonClickListener(OnPostButtonClickListener mPostButtonClickListener) {
        this.mPostButtonClickListener = mPostButtonClickListener;
    }

    private View.OnClickListener guideListener;

    private IBrowseClickListener iBrowseClickListener;

    public void setBrowseClickListener(final IBrowseClickListener iBrowseClickListener) {

        this.iBrowseClickListener = iBrowseClickListener;

        if (iBrowseClickListener != null) {
            setBrowseMode();
        }
    }

    private void setBrowseMode() {
        dismissArrowBtn(true);

        mFollowBtn.setOnClickFollowBtnListener(null);

        BrowseEventStore.INSTANCE.getFollowUser(((PostBase) mFollowBtn.getTag()).getUid(), new FollowUserCallBack() {
            @Override
            public void onLoadEntity(FollowUser user) {
                if (user == null) return;
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mFollowBtn.setVisibility(View.GONE);
                    }
                });
            }
        });

        mFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PostBase postBase = (PostBase) v.getTag();
                BrowseSchemeManager.getInstance().setUserProfile(postBase.getUid());

                if (mFollowClickListener != null) {
                    mFollowClickListener.onClick(mFollowBtn);
                }
                BrowseEventStore.INSTANCE.getFollowUserCount(new FollowUser(postBase.getUid(), postBase.getNickName()), new FollowUserCountCallBack() {
                    @Override
                    public void onLoadEntity(boolean inserted, final int count) {
                        if (inserted) {
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    mFollowBtn.setVisibility(View.GONE);
                                    if (count % 3 == 1) {
                                        HalfLoginManager.getInstancce().showLoginDialog(mFollowBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
//                                        RegisterActivity.Companion.show(mFollowBtn.getContext(), 1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                                    }
                                }
                            });

                        } else {
                            HalfLoginManager.getInstancce().showLoginDialog(mFollowBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                        }

                        if (iBrowseClickListener != null)
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_FOLLOW, false, 1);

                    }
                });
            }
        });
    }


    public void setTrending(boolean trending) {
        isTrending = trending;
    }
}

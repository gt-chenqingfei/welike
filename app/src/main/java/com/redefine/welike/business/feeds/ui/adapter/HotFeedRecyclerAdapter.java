package com.redefine.welike.business.feeds.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.business.feeds.ui.bean.HotFeedHeaderBean;
import com.redefine.welike.business.feeds.ui.listener.GuideListener;
import com.redefine.welike.business.feeds.ui.view.CommonFeedBottomView;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;

import java.util.List;

/**
 *
 */
public class HotFeedRecyclerAdapter extends FeedRecyclerViewAdapter<HotFeedHeaderBean> {

    public HotFeedRecyclerAdapter(IPageStackManager pageStackManager, String source) {
        super(pageStackManager, source);

    }

    @Override
    protected void onBindItemViewHolder(final BaseRecyclerViewHolder holder, final int position) {
//        if (position == 0) {
//            if (firstGuide != null) {
//                firstGuide.onShow(holder, null);
//            }
//        }
//        if (position == 5) {
//            if (hotGuide != null) {
//                hotGuide.onClick(null);
//            }
//        }
        if (holder instanceof BaseFeedViewHolder) {

            ((BaseFeedViewHolder) holder).getFeedHeadView().setTrending(true);

            ((BaseFeedViewHolder) holder).getFeedHeadView().setGuideListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (firstGuide != null) {
//                        //dismiss Guide.
//                        firstGuide.onClick(v);
//                    }
                }
            });

            final CommonFeedBottomView bottomView = ((BaseFeedViewHolder) holder).getmCommonFeedBottomView();
            bottomView.setLikeListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        if (firstShareGuide != null) {
                            firstShareGuide.onShow(holder, null);
                            bottomView.setShareListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (firstShareGuide != null) {
                                        firstShareGuide.onClick(v);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        super.onBindItemViewHolder(holder, position);

    }


    @Override
    public BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder holder = super.onCreateItemViewHolder(parent, viewType);
        return holder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }


    private GuideListener<BaseRecyclerViewHolder> firstShareGuide;


    public void setFirstShareGuide(GuideListener<BaseRecyclerViewHolder> hotGuide) {
        this.firstShareGuide = hotGuide;
    }

}

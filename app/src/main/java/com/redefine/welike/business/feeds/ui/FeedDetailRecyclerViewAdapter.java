package com.redefine.welike.business.feeds.ui;

import android.content.Context;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.TextFeedViewHolder;
import com.redefine.welike.commonui.share.ShareMenu;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by liwenbo on 2018/3/5.
 */

public class FeedDetailRecyclerViewAdapter extends FeedRecyclerViewAdapter {

    private final IPageStackManager mPageStackManager;

    public FeedDetailRecyclerViewAdapter(IPageStackManager pageStackManager, String feedSource) {
        super(pageStackManager, feedSource);
        mPageStackManager = pageStackManager;
    }

    @Override
    protected List<SharePackageModel> initMenuItemList(PostBase postBase, Function1 menuInvoker) {
        return new ArrayList<>();
    }

    @Override
    public void doRealFeedDelete(Context context, PostBase postBase) {
//        mPageStackManager.popPage();
        SinglePostManager.getInstance().delete(postBase);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).setFeedClickEnable(false);
            ((BaseFeedViewHolder) holder).setForwardFeedClickEnable(true);
            ((BaseFeedViewHolder) holder).setShowContent(true);
            ((BaseFeedViewHolder) holder).setIsImageClickable(true);
        }

        if (holder instanceof TextFeedViewHolder) {
            ((TextFeedViewHolder) holder).showBottomShadowView(false);
        }

        super.onBindItemViewHolder(holder, position);
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).dismissArrowBtn(true);
            ((BaseFeedViewHolder) holder).dismissDivider(true);
            ((BaseFeedViewHolder) holder).setDismissBottomContent(true);
        }
    }

    @Override
    public void onPostDeleted(String pid) {
        // feed详情针对删除操作什么都不做
    }
}

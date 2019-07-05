package com.redefine.welike.business.mysetting.ui.presenter;

import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.commonui.share.CustomShareMenuFactory;
import com.redefine.welike.commonui.share.ShareMenu;
import com.redefine.welike.statistical.EventConstants;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function1;

/**
 * Created by liwenbo on 2018/3/24.
 */

public class MyLikeFeedAdapter extends FeedRecyclerViewAdapter {
    public MyLikeFeedAdapter(IPageStackManager pageStackManager) {
        super(pageStackManager, EventConstants.FEED_PAGE_ME_LIKE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).dismissFollowBtn(true);
        }
    }

    @Override
    protected List<SharePackageModel> initMenuItemList(PostBase postBase, Function1 menuInvoker) {
        SharePackageModel delete = CustomShareMenuFactory.Companion.createMenu(ShareMenu.DELETE, menuInvoker);
        SharePackageModel unlike = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UNLIKE, menuInvoker);
        List<SharePackageModel> list = new ArrayList<>();
        if (postBase.isLike()) {
            list.add(unlike);
        }
        if (AccountManager.getInstance().isSelf(postBase.getUid())) {
            list.add(delete);
        }
        return list;
    }

}
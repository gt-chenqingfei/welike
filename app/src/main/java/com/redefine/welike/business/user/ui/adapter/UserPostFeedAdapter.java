package com.redefine.welike.business.user.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.commonui.share.CustomShareMenuFactory;
import com.redefine.welike.commonui.share.ShareMenu;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function1;

/**
 * Created by gongguan on 2018/2/25.
 */

public class UserPostFeedAdapter extends FeedRecyclerViewAdapter {
    private boolean mShowReadCount;
    private boolean isShowTopMenu=false;
    private IBrowseClickListener iBrowseClickListener;


    public UserPostFeedAdapter(IPageStackManager pageStackManager, String feedSource) {
        super(pageStackManager, feedSource);
    }

    public void setShowTopMenu(boolean b){
        isShowTopMenu=true;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (holder instanceof BaseFeedViewHolder) {
            ((BaseFeedViewHolder) holder).dismissFollowBtn(true);
            ((BaseFeedViewHolder) holder).showReadCount(mShowReadCount);
            if (iBrowseClickListener != null)
                ((BaseFeedViewHolder) holder).setBrowseClickListener(iBrowseClickListener);
        }
    }

    @Override
    public void onMenuBtnClick(final Context context, final PostBase postBase) {
        super.onMenuBtnClick(context, postBase);
    }

    @Override
    protected List<SharePackageModel> initMenuItemList(PostBase postBase, Function1 menuInvoker) {
        SharePackageModel delete = CustomShareMenuFactory.Companion.createMenu(ShareMenu.DELETE, menuInvoker);
        SharePackageModel report = CustomShareMenuFactory.Companion.createMenu(ShareMenu.REPORT, menuInvoker);
        SharePackageModel unlike = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UNLIKE, menuInvoker);
        SharePackageModel top = CustomShareMenuFactory.Companion.createMenu(ShareMenu.TOP, menuInvoker);
        SharePackageModel unTop = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UN_TOP, menuInvoker);
        List<SharePackageModel> list = new ArrayList<>();
        if (postBase.isLike()) {
            list.add(unlike);
        }
        if (AccountManager.getInstance().isSelf(postBase.getUid())) {
            if(postBase.isTop()){
                list.add(unTop);
            }else {
                list.add(top);
            }
            list.add(delete);
        } else {
            list.add(report);
        }
        return list;
    }

    public void showReadCount(boolean b) {
        mShowReadCount = b;
    }


    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;
    }

    public List<PostBase> getPostData() {
        return mPostBaseList;
    }
}

package com.redefine.welike.business.feeds.ui.viewholder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.management.bean.ArtPost;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;

import org.greenrobot.eventbus.EventBus;

public class ForwordTextArtViewHolder extends ForwardFeedTextViewHolder {
    private SimpleDraweeView artBg;
    private SimpleDraweeView artHeadImage;
    private TextView artUsername;
    private TextView artTitle;
    private TextView artTitleFlag;
    private View bgContainer;

    public ForwordTextArtViewHolder(View inflate, IFeedOperationListener mUserFollowListener) {
        super(inflate, mUserFollowListener);
        artBg = itemView.findViewById(R.id.art_bg);
        artHeadImage = itemView.findViewById(R.id.art_card_head_image);
        artUsername = itemView.findViewById(R.id.ard_card_name);
        artTitle = itemView.findViewById(R.id.art_title);
        artTitleFlag = itemView.findViewById(R.id.art_card_title_flag);
        bgContainer = itemView.findViewById(R.id.art_card_contain);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final PostBase postBase) {
        super.bindViews(adapter, postBase);
        if (null != postBase && postBase instanceof ForwardPost) {
            ForwardPost forwardPost = (ForwardPost) postBase;
            if (forwardPost.getRootPost() instanceof ArtPost) {
                final ArtPost post = (ArtPost) forwardPost.getRootPost();
                if (null != post.getArticleInfo().getUser()) {
                    artUsername.setText(post.getArticleInfo().getUser().getNickName());
                    HeadUrlLoader.getInstance().loadHeaderUrl2(artHeadImage, post.getArticleInfo().getUser().getHeadUrl());
                }
                artTitle.setText(post.getArticleInfo().getTitle());
                artTitleFlag.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "article"));
                artBg.setImageURI(post.getArticleInfo().getCover());
                HeadUrlLoader.getInstance().loadHeaderUrl2(artHeadImage, post.getArticleInfo().getUser().getHeadUrl());
                bgContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iBrowseClickListener != null) {
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_TAG, true,0);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(FeedConstant.KEY_FEED, post);
                            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_POST_ARTICLE_PAGE, bundle));
                        }
                    }
                });
            }
        }
    }
}

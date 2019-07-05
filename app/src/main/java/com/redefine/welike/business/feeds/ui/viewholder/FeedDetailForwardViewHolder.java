package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultRichItemClickHandler;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by liwenbo on 2018/2/22.
 */

public class FeedDetailForwardViewHolder extends BaseRecyclerViewHolder<PostBase> {

    public final VipAvatar mUserHeader;
    private final TextView mUserNick;
    public final RichTextView mFeedContent;
    private final TextView mFeedTime;

    public FeedDetailForwardViewHolder(View itemView) {
        super(itemView);
        mUserHeader = itemView.findViewById(R.id.feed_detail_forward_user_head);
        mUserNick = itemView.findViewById(R.id.feed_detail_forward_user_nick);
        mFeedContent = itemView.findViewById(R.id.feed_detail_forward_content);
        mFeedTime = itemView.findViewById(R.id.feed_detail_forward_time);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final PostBase feedBase) {
        super.bindViews(adapter, feedBase);
//        HeadUrlLoader.getInstance().loadHeaderUrl(mUserHeader, feedBase.getHeadUrl());
        VipUtil.set(mUserHeader,feedBase.getHeadUrl(),feedBase.getVip());
        VipUtil.setNickName(mUserNick, feedBase.getCurLevel(), feedBase.getNickName());

        mFeedContent.getRichProcessor().setOnRichItemClickListener(new DefaultRichItemClickHandler(mFeedContent.getContext()));

        mUserHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHostPage.launch(true, feedBase.getUid());
            }
        });
        String content = feedBase.getSummary();
        mFeedContent.getRichProcessor().setRichContent(content, feedBase.getRichItemList());
        mFeedTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mFeedTime.getResources(), feedBase.getTime()));
    }
}

package com.redefine.welike.business.feeds.ui.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.AdAttachment;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;

/**
 * @author redefine honlin
 * @Date on 2018/11/6
 * @Description
 */
public class CommonFeedAdCardView {

    private View mRootView;

    private TextView tvTitle, tvJoin, tvDesc;

    private TextView mTvAdStatus;

    private AdAttachment adAttachment;

    private SimpleDraweeView adCardPic;


    public CommonFeedAdCardView(View view) {

        mRootView = view;

    }

    public void bindViews(PostBase postBase) {
        PostBase realPost;
        if (postBase instanceof ForwardPost) {
            ForwardPost forwardPost = (ForwardPost) postBase;

            realPost = forwardPost.getRootPost();
        } else {
            realPost = postBase;
        }
        if (realPost != null && realPost.getAdAttachment() != null) {
            adAttachment = realPost.getAdAttachment();

            if (System.currentTimeMillis() < adAttachment.getStartTime()) {
                setVisible(View.GONE);
            } else {
                mRootView.setVisibility(View.VISIBLE);
                initViews();

                setEvent();
            }

        } else {
            setVisible(View.GONE);
        }


    }

    private void initViews() {

        tvDesc = mRootView.findViewById(R.id.common_feed_active_desc);
        tvTitle = mRootView.findViewById(R.id.common_feed_active_title);
        tvJoin = mRootView.findViewById(R.id.tv_common_feed_active_join);
        mTvAdStatus = mRootView.findViewById(R.id.tv_post_status);
        adCardPic = mRootView.findViewById(R.id.iv_ad_pic);

        tvTitle.setText(adAttachment.getTitle());

        tvDesc.setVisibility(TextUtils.isEmpty(adAttachment.getContent()) ? View.GONE : View.VISIBLE);

        adCardPic.setVisibility(TextUtils.isEmpty(adAttachment.getCover()) ? View.GONE : View.VISIBLE);

        tvDesc.setText(adAttachment.getContent());


        if (System.currentTimeMillis() > adAttachment.getEndTime()) {

            if (!TextUtils.isEmpty(adAttachment.getStatusInfo())) {
                mTvAdStatus.setVisibility(View.VISIBLE);
                mTvAdStatus.setText(adAttachment.getStatusInfo());
            } else {
                mTvAdStatus.setVisibility(View.GONE);
            }
        } else {
            mTvAdStatus.setVisibility(View.GONE);
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(adAttachment.getCover())
                .setOldController(adCardPic.getController())
                .build();

        adCardPic.setController(controller);

    }

    private void setEvent() {


        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_AD).onUrlRedirect(adAttachment.getForwardUrl());
            }
        });

    }

    public void setVisible(int visible) {

        if (mRootView != null) {

            mRootView.setVisibility(visible);

        }
    }


}

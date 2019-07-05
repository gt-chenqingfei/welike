package com.redefine.welike.business.feeds.ui.view;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.AdAttachment;
import com.redefine.welike.business.feeds.management.bean.HeaderAttachment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;

/**
 * @author redefine honlin
 * @Date on 2018/11/6
 * @Description
 */
public class CommonFeedHeadCardView {


    private View mRootView;

    private SimpleDraweeView headerIcon;

    private TextView tvTitle;


    private HeaderAttachment headerAttachment;


    public CommonFeedHeadCardView(View view) {

        mRootView = view;

    }

    public void bindViews(PostBase postBase) {

        if (postBase.getHeaderAttachment() != null) {
            mRootView.setVisibility(View.VISIBLE);
            headerAttachment = postBase.getHeaderAttachment();

            initViews();

        } else {
            setVisible(View.GONE);
        }


    }

    private void initViews() {

        headerIcon = mRootView.findViewById(R.id.iv_common_feed_ad_icon);
        tvTitle = mRootView.findViewById(R.id.tv_common_feed_ad_title);
        tvTitle.setText(headerAttachment.getTitle());

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(headerAttachment.getIcon())
                .setOldController(headerIcon.getController())
                .build();

        headerIcon.setController(controller);


    }


    public void setVisible(int visible) {

        if (mRootView != null) {

            mRootView.setVisibility(visible);

        }
    }


}

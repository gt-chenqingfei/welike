package com.redefine.welike.business.feeds.ui.view;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.ActiveAttachment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.statistical.EventLog;

/**
 * @author redefine honlin
 * @Date on 2018/11/6
 * @Description
 */
public class CommonFeedActiveCardView {


    private View mRootView;

    private ConstraintLayout clParent;

    private SimpleDraweeView activeCardPic;


    private TextView tvTitle, tvJoin;


    private ActiveAttachment activeAttachment;
    private PostBase postBase;


    public CommonFeedActiveCardView(View view) {

        mRootView = view;

    }

    public void bindViews(PostBase postBase) {

        if (postBase != null && postBase.getActiveAttachment() != null) {
            activeAttachment = postBase.getActiveAttachment();
            this.postBase = postBase;
            if (System.currentTimeMillis() < activeAttachment.getStartTime()) {
                setVisible(View.GONE);
            } else {
                mRootView.setVisibility(View.VISIBLE);
                initViews();
                setEvent();
                EventLog.ActiveCard.report1(activeAttachment.getId(), postBase.getPid(), postBase.getLanguage(), postBase.getTags(), postBase.getSequenceId());
            }

        } else {
            setVisible(View.GONE);
        }


    }

    private void initViews() {

        clParent = mRootView.findViewById(R.id.active_card_layout);
        activeCardPic = mRootView.findViewById(R.id.bg_lg_active_card);
        tvTitle = mRootView.findViewById(R.id.common_feed_active_title);
        tvJoin = mRootView.findViewById(R.id.tv_common_feed_active_join);


        tvTitle.setText(activeAttachment.getTitle());


        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(activeAttachment.getPicUrl())
                .setOldController(activeCardPic.getController())
                .build();

        activeCardPic.setController(controller);

    }

    private void setEvent() {


        clParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_ACTIVE).onUrlRedirect(activeAttachment.getLandPage());
//                EventLog.ActiveCard.report2(activeAttachment.getId(), postBase.getPid());
                EventLog.ActiveCard.report2(activeAttachment.getId(), postBase.getPid(), postBase.getLanguage(), postBase.getTags(), postBase.getSequenceId());
            }
        });

    }

    public void setVisible(int visible) {

        if (mRootView != null) {

            mRootView.setVisibility(visible);

        }
    }


}

package com.redefine.welike.business.feeds.ui.viewholder;

import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.widget.FlowLayout;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.bean.InterestPost;
import com.redefine.welike.business.browse.ui.listener.InterestChangeListener;
import com.redefine.welike.business.user.management.bean.Interest;

/**
 * Created by honglin on 2018/7/20.
 */

public class FeedInterestViewHolder extends BaseRecyclerViewHolder<InterestPost> {


    private FlowLayout flInterest;
    private TextView tvTitle;
    private TextView tvConfirm;
    private ImageView ivCancel;
    private LottieAnimationView lvBottomLine;

    private InterestChangeListener listener;

    private int viewWidth = 0;

    public FeedInterestViewHolder(View itemView) {
        super(itemView);

        flInterest = itemView.findViewById(R.id.fl_interest);
        tvTitle = itemView.findViewById(R.id.tv_item_title);
        ivCancel = itemView.findViewById(R.id.iv_cancel);
        tvConfirm = itemView.findViewById(R.id.tv_item_confirm);
        lvBottomLine = itemView.findViewById(R.id.lv_bottom_line);

        viewWidth = (ScreenUtils.getSreenWidth(itemView.getContext()) - ScreenUtils.dip2Px(itemView.getContext(), 60)) / 2;

    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, InterestPost data) {
        super.bindViews(adapter, data);
        flInterest.removeAllViews();

        for (Interest intrest : (data).getList()) {

            View view = LayoutInflater.from(flInterest.getContext()).inflate(
                    R.layout.item_choose_interest_layout, flInterest, false);
            TextView tv = view.findViewById(R.id.tv_interest_name);
            tv.setText(intrest.getName());

            ConstraintLayout.LayoutParams cl = new ConstraintLayout.LayoutParams(
                    viewWidth-1, ViewGroup.LayoutParams.WRAP_CONTENT);


            tv.setLayoutParams(cl);


            setInterestTextView(intrest, tv);
            //点击事件
            tv.setTag(intrest);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Interest intrest = (Interest) v.getTag();
                    intrest.setSelected(!intrest.isSelected());
                    setInterestTextView(intrest, (TextView) v);
                    if (listener != null) {
                        listener.onInterestSelectChange(intrest);
                    }
                }
            });

            flInterest.addView(view);
        }

//        lvBottomLine.setAnimation("interest_streamer_flash_animation.json");
//        lvBottomLine.playAnimation();
//        lvBottomLine.setRepeatCount(LottieDrawable.INFINITE);

        tvTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "interest_select_title", false));
        tvConfirm.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "interest_select", false));

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });

    }

    public void setListener(InterestChangeListener listener) {
        this.listener = listener;
    }

    //设置兴趣点状态
    private void setInterestTextView(Interest intrest, TextView tv) {
        if (intrest.isSelected()) {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.white));
            tv.setBackgroundResource(R.drawable.common_interest_checked);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        } else {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.main_orange_dark));
            tv.setBackgroundResource(R.drawable.common_interest_uncheck);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }
}

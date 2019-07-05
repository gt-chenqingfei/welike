package com.redefine.welike.business.feedback.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.welike.R;

/**
 * Created by nianguowang on 2018/10/15
 */
public class AbsReportViewHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView mPic;
    public AbsReportViewHolder(View itemView) {
        super(itemView);
        mPic = itemView.findViewById(R.id.report_desc_pic);
    }
}

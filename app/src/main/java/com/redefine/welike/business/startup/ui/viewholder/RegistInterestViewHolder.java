package com.redefine.welike.business.startup.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;

/**
 * Created by gongguan on 2018/1/8.
 */

public class RegistInterestViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_regist_interest;
    public View cb_interests;
    private SimpleDraweeView simple_interest_pic;

    public RegistInterestViewHolder(View itemView) {
        super(itemView);

        initViews();
    }

    public void initViews() {
        tv_regist_interest = (TextView) findView(R.id.tv_regist_interest_recycler);
        cb_interests = findView(R.id.cb_interest_list);
        simple_interest_pic = (SimpleDraweeView) findView(R.id.simple_interests_pic);

    }

    public View findView(int viewId) {
        if (viewId != 0)
            return itemView.findViewById(viewId);
        else return null;
    }

    public void bindViews(UserBase.Intrest intrest) {
        HeadUrlLoader.getInstance().loadHeaderUrl2(simple_interest_pic, intrest.getIcon());
//        VipUtil.set(simple_interest_pic,intrest.getIcon(),0);
        tv_regist_interest.setText(intrest.getLabel());
        cb_interests.setSelected(intrest.getChecked());
    }
}

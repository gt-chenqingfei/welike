package com.redefine.welike.business.startup.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by gongguan on 2018/2/7.
 */

public class RecommondUserViewHolder extends RecyclerView.ViewHolder {
    public TextView nickName, follower, tv_introduce;
    public View cb_recommond;
    private VipAvatar simple_headView;

    public RecommondUserViewHolder(View itemView) {
        super(itemView);
        nickName = itemView.findViewById(R.id.tv_nickName_list);
        follower = itemView.findViewById(R.id.tv_follower_list);
        tv_introduce = itemView.findViewById(R.id.tv_introduce_list);
        cb_recommond = itemView.findViewById(R.id.cb_recommond_list);
        simple_headView = itemView.findViewById(R.id.simple_headview_list);


    }

    public void bindViews(User user) {
        tv_introduce.setText(user.getIntroduction());
        follower.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_recommond_follower") + ":\t" + user.getFollowedUsersCount());

//        HeadUrlLoader.getInstance().loadHeaderUrl(simple_headView, user.getHeadUrl());
        VipUtil.set(simple_headView,user.getHeadUrl(),user.getVip());
        VipUtil.setNickName(nickName, user.getCurLevel(), user.getNickName());
    }

}

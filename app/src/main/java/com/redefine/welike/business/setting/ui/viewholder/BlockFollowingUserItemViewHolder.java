package com.redefine.welike.business.setting.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

public class BlockFollowingUserItemViewHolder extends BaseRecyclerViewHolder<User> {

    private final VipAvatar mHeadView;
    private final TextView mUserNick;
    public final TextView mUnBlockBtn;

    public BlockFollowingUserItemViewHolder(View itemView) {
        super(itemView);
        mHeadView = itemView.findViewById(R.id.block_user_head);
        mUserNick = itemView.findViewById(R.id.block_user_nick);
        mUnBlockBtn = itemView.findViewById(R.id.un_block_btn);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, User data) {
        super.bindViews(adapter, data);
        VipUtil.set(mHeadView, data.getHeadUrl(), data.getVip());
        VipUtil.setNickName(mUserNick, data.getCurLevel(), data.getNickName());
        mUnBlockBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "un_block"));
        mUnBlockBtn.setVisibility(View.INVISIBLE);
    }
}

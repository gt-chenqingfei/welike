package com.redefine.welike.business.user.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/7/26.
 */

public class RecommendUserSubAdapter extends RecyclerView.Adapter<RecommendUserSubAdapter.UserViewHolder> {

    private List<RecommendUser> contactsList = new ArrayList<>();
    private int width;

    public RecommendUserSubAdapter(Context context) {

        width = (ScreenUtils.getSreenWidth(context) - ScreenUtils.dip2Px(context, 24)) / 5;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_sub_user_layout, null);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        final RecommendUser user = contactsList.get(position);
//        holder.tvTitle.setText(user.getName());
        VipUtil.set(holder.vipAvatar, user.getAvatar(), user.getVip());
        VipUtil.setNickName(holder.tvTitle, user.getCurLevel(), user.getName());
        holder.constraintLayout.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (user.getSelect()) {
            holder.isSelect.setImageResource(R.drawable.ic_recommend_user_selected);
        } else {
            holder.isSelect.setImageResource(R.drawable.ic_recommend_user_select);
        }

        holder.isSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) listener.onChange(position);

            }
        });
        holder.vipAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHostPage.launch(true, user.getUid());
                InterestAndRecommendCardEventManager.INSTANCE.addPortrait_click();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }


    interface OnDataSelectStatusChange {
        void onChange(int position);
    }

    private OnDataSelectStatusChange listener;

    public void setListener(OnDataSelectStatusChange listener) {
        this.listener = listener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;

        public VipAvatar vipAvatar;

        public ImageView isSelect;

        public ConstraintLayout constraintLayout;

        public UserViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.cl_parent);
            tvTitle = itemView.findViewById(R.id.tv_user_name);
            vipAvatar = itemView.findViewById(R.id.simpleView_user_follow_recycler);
            isSelect = itemView.findViewById(R.id.iv_select);
        }
    }


    public void addNewData(List<RecommendUser> mList) {
        contactsList = mList;
        notifyDataSetChanged();
    }


}
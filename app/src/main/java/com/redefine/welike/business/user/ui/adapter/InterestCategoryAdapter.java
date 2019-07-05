package com.redefine.welike.business.user.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.user.ui.viewholder.InterestCategoryViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liwenbo on 2018/3/24.
 */

public class InterestCategoryAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private LayoutInflater mLayoutInflater;
    private final List<UserBase.Intrest> mData = new ArrayList<>();
    private final Map<String, UserBase.Intrest> mSelectInterests = new HashMap<>();
    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        return new InterestCategoryViewHolder(mLayoutInflater.inflate(R.layout.interest_category_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseRecyclerViewHolder holder, final int position) {
        holder.bindViews(this, mData.get(position));
        if (mSelectInterests.containsKey(mData.get(position).getIid())) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectInterests.containsKey(mData.get(position).getIid())) {
                    holder.itemView.setSelected(false);
                    mSelectInterests.remove(mData.get(position).getIid());
                } else {
                    holder.itemView.setSelected(true);
                    mSelectInterests.put(mData.get(position).getIid(), mData.get(position));
                }

            }
        });
    }

    public void setData( List<UserBase.Intrest> data) {
        Account account = AccountManager.getInstance().getAccount();
        if (!CollectionUtil.isEmpty(account.getIntrests())) {
            for (UserBase.Intrest intrest : account.getIntrests()) {
                mSelectInterests.put(intrest.getIid(), intrest);
            }
        }
        mData.clear();
        if (!CollectionUtil.isEmpty(data)) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<UserBase.Intrest> getSelectInterest() {
        Set<String> keySet = mSelectInterests.keySet();
        List<UserBase.Intrest> list = new ArrayList<>();
        for (String key : keySet) {
            list.add(mSelectInterests.get(key));
        }
        return list;
    }
}

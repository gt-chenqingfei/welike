package com.redefine.welike.business.startup.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.startup.ui.viewholder.RegistInterestViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/1/8.
 */

public class RegistInterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UserBase.Intrest> dataList;
    private OnRecyclerViewItemClickListener mListener;

    public RegistInterestAdapter() {
        dataList = new ArrayList<>();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public void setDataList(List<UserBase.Intrest> intrests) {
        if (intrests != null) {
            dataList.clear();
            dataList.addAll(intrests);
            notifyDataSetChanged();
        }
    }

    public List<UserBase.Intrest> getSelectedIntrests() {
        if (dataList.size() > 0) {
            List<UserBase.Intrest> selectedItems = new ArrayList<>();
            for (UserBase.Intrest item : dataList) {
                if (item.getChecked()) {
                    selectedItems.add(item);
                }
            }
            return selectedItems;
        } else {
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.regist_interest_recycler_item, parent, false);
        final RegistInterestViewHolder holder = new RegistInterestViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBase.Intrest currentItem = null;
                try {
                    currentItem = dataList.get(holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (currentItem != null) {
                    currentItem.setChecked(!currentItem.getChecked());
                    holder.cb_interests.setSelected(currentItem.getChecked());
                }

                if(mListener != null) {
                    mListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RegistInterestViewHolder mHolder = (RegistInterestViewHolder) holder;

        UserBase.Intrest currentItem = null;
        try {
            currentItem = dataList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentItem != null) {
            mHolder.bindViews(currentItem);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

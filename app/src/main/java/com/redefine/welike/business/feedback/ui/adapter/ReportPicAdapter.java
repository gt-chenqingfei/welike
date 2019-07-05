package com.redefine.welike.business.feedback.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.fresco.loader.NineGridUrlLoader;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.welike.R;
import com.redefine.welike.business.feedback.ui.viewholder.ReportAddViewHolder;
import com.redefine.welike.business.feedback.ui.viewholder.ReportInitViewHolder;
import com.redefine.welike.business.feedback.ui.viewholder.ReportPicViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/10/15
 */
public class ReportPicAdapter extends RecyclerView.Adapter {

    public static final int ITEM_VIEW_TYPE_INIT = 1;
    public static final int ITEM_VIEW_TYPE_ADD = 2;
    public static final int ITEM_VIEW_TYPE_PIC = 3;

    private static final int ITEM_COUNT_MAX = 4;

    private ArrayList<Item> mData = new ArrayList<>();
    private OnItemClickListener mItemClickListener;

    public void setData(ArrayList<Item> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public ArrayList<Item> getData() {
        return mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_INIT) {
            return new ReportInitViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_pic_item, parent, false));
        } else if (viewType == ITEM_VIEW_TYPE_ADD) {
            return new ReportAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_pic_item, parent, false));
        } else {
            return new ReportPicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_pic_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ReportInitViewHolder) {
            ((ReportInitViewHolder) holder).mPic.setImageResource(R.drawable.report_init_pic);
        } else if (holder instanceof ReportAddViewHolder) {
            ((ReportAddViewHolder) holder).mPic.setImageResource(R.drawable.report_add_pic);
        } else if (holder instanceof ReportPicViewHolder) {
            Item item = mData.get(position);
            NineGridUrlLoader.getInstance().loadNineGridItemView(((ReportPicViewHolder) holder).mPic, item.uri, 4);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(holder, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = CollectionUtil.getCount(mData);
        if (count >= ITEM_COUNT_MAX) {
            return ITEM_COUNT_MAX;
        } else {
            return count + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int count = CollectionUtil.getCount(mData);
        if (count == 0) {
            return ITEM_VIEW_TYPE_INIT;
        } else if (count < 4) {
            if (position == count) {
                return ITEM_VIEW_TYPE_ADD;
            } else {
                return ITEM_VIEW_TYPE_PIC;
            }
        } else {
            return ITEM_VIEW_TYPE_PIC;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder holder, int position);
    }
}

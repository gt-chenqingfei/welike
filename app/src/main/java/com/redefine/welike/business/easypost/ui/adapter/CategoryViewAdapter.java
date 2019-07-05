package com.redefine.welike.business.easypost.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.welike.R;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.business.easypost.api.bean.PostStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C) 2018 redefine , Inc.
 *
 * @author qingfei.chen
 * @date 2018-10-17 17:54:44
 */
public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.ViewHolder> {

    private List<PostStatus> data = new ArrayList<>();
    private OnTabSelectedListener listener;
    private int currentSelected = 0;

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    public void notifyDataSetChange(List<PostStatus> list) {
        if (list == null) {
            return;
        }
        this.data.clear();
        this.data.addAll(list);
        this.notifyDataSetChanged();
    }

    public void setCurrentItem(int position) {
        currentSelected = position;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = View.inflate(parent.getContext(), R.layout.item_poststatus_catetory_tab_view, null);
        return new ViewHolder(item, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View line;
        PostStatus data;

        public ViewHolder(View itemView, final OnTabSelectedListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_poststatus_category_tab_view);
            line = itemView.findViewById(R.id.item_poststatus_category_tab_view_line);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSelected = getLayoutPosition();
                    notifyDataSetChanged();
                }
            });
        }

        void bind(PostStatus item, int position) {
            this.data = item;
            this.textView.setText(item.getText());
//            int bottomDrawable = R.drawable.common_transparent;
            if (position == currentSelected) {
                if (!textView.isSelected()) {
                    if (listener != null) {
                        listener.onTabSelect(getLayoutPosition(), data);
                    }
                }
                textView.setSelected(true);
                line.setVisibility(View.VISIBLE);
//                bottomDrawable = R.drawable.ic_indicator;
            } else {
                textView.setSelected(false);
                line.setVisibility(View.INVISIBLE);
            }
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, bottomDrawable);
        }
    }


    public interface OnTabSelectedListener {
        void onTabSelect(int position, PostStatus data);
    }
}

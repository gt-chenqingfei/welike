package com.redefine.welike.business.easypost.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.BigPicUrlLoader;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.URLCenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C) 2018 redefine , Inc.
 *
 * @author qingfei.chen
 * @date 2018-10-17 17:54:44
 */
public class ThumbViewAdapter extends RecyclerView.Adapter<ThumbViewAdapter.ViewHolder> {

    private List<String> data = new ArrayList<>();
    private OnImageSelectedListener listener;

    public ThumbViewAdapter(OnImageSelectedListener listener) {
        this.listener = listener;
    }

    public void notifyDataSetChange(List<String> list) {
        if(list == null){
            return;
        }
        this.data.clear();
        this.data.addAll(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = View.inflate(parent.getContext(), R.layout.item_poststatus_thumb_view, null);
        return new ViewHolder(item, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(data.get(position), data.size());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        String url;

        public ViewHolder(View itemView, final OnImageSelectedListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_thumb_view_image);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onImageSelect(getLayoutPosition(), url);
                    }
                }
            });
        }

        public void bind(String url, int count) {
            this.url = url;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            if (getAdapterPosition() == count - 1) {
                params.rightMargin = ScreenUtils.dip2Px(8);
            } else {
                params.rightMargin = 0;
            }
            imageView.setLayoutParams(params);
            BigPicUrlLoader.getInstance().loadBigImage(imageView,url);
//            HeadUrlLoader.getInstance().loadHeaderUrl2(imageView, url);
        }
    }

    public interface OnImageSelectedListener {
        void onImageSelect(int position, String url);
    }
}

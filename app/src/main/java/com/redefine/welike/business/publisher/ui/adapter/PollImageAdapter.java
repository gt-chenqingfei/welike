package com.redefine.welike.business.publisher.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.CommonUrlLoader;
import com.redefine.commonui.fresco.size.PollSizeProvider;
import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.business.publisher.ui.viewholder.PublishPollImageTextViewHolder;
import com.redefine.welike.commonui.photoselector.PhotoSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class PollImageAdapter extends BaseRecyclerAdapter<PollImageAdapter.PollImageViewHolder> {

    PublishPollImageTextViewHolder.PollItemChangeListener listner;
    private LayoutInflater mInflater;
    private int mFocusePosition;

    List<PollItemInfo> mItems = new ArrayList<>();

    public void setData(List<PollItemInfo> data) {
        this.mItems.clear();
        this.mItems.addAll(data);

        notifyDataSetChanged();
    }

    public PollImageAdapter(PublishPollImageTextViewHolder.PollItemChangeListener listner) {
        this.listner = listner;
    }


    @NonNull
    @Override
    public PollImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        View convertView = mInflater.inflate(R.layout.publish_poll_image_item_layout, parent, false);
        return new PollImageViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PollImageViewHolder holder, int position) {
        holder.bindViews(position, mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PollImageViewHolder extends BaseRecyclerViewHolder<PollItemInfo> {
        private final View mRootView;
        private final EditText mPollDes;
        private final SimpleDraweeView mPollImage;
        private final View mDeleteBtn;

        public PollImageViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mPollDes = itemView.findViewById(R.id.publish_poll_image_item_edit);
            mPollImage = itemView.findViewById(R.id.publish_poll_item_img);
            mDeleteBtn = itemView.findViewById(R.id.publish_poll_delete);
        }


        public void bindViews(final int position, final PollItemInfo pollItemInfo) {

            Log.e("publish", "bindViews:" + pollItemInfo.toString());
            if(!TextUtils.isEmpty(pollItemInfo.pollItemPic)){
                if (pollItemInfo.pollItemPic.contains("http://") || pollItemInfo.pollItemPic.contains("https://")) {
                    CommonUrlLoader.getInstance().loadUrl(mPollImage, pollItemInfo.pollItemPic, new PollSizeProvider());
                } else {
                    CommonUrlLoader.getInstance().loadLocalFile(mPollImage, pollItemInfo.pollItemPic, new PollSizeProvider());
                }
            }

            boolean isCanDelete = mItems.size() > GlobalConfig.MIN_POLL_ITEM_SIZE;
            mDeleteBtn.setVisibility(isCanDelete ? View.VISIBLE : View.GONE);
            mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onPollItemDelete(pollItemInfo, position);
                }
            });
            mPollImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PublishPostStarter.EXTRA_PUBLISH_POLL_POSITION, position);
                    PhotoSelector.launchPhotoSelectorForPoll((Activity) v.getContext(), bundle);
                }
            });
            String optionHint = ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "option_num");
            mPollDes.setHint(String.format(optionHint, position + 1));
            mPollDes.setText(pollItemInfo.pollItemText);
            mPollDes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mFocusePosition = position;
                    }
                }
            });

            if (mFocusePosition == position) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mPollDes.requestFocus();
                    }
                }, 10, TimeUnit.MILLISECONDS);
            }

            mPollDes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    pollItemInfo.pollItemText = s.toString().trim();
                    listner.onPollEditorChange(pollItemInfo, position);
                }
            });
        }
    }
}

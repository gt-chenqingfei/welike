package com.redefine.welike.business.publisher.ui.viewholder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redefine.richtext.RichEditText;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.commonui.photoselector.PhotoSelector;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class PublishPollTextViewHolder {

    private final LayoutInflater mInflater;
    private final ViewGroup mPollTextRootView;
    //    private final PostEditorView mEditorView;
    private int mFocusePosition = 0;
    PublishPollImageTextViewHolder.PollItemChangeListener listener;

    public PublishPollTextViewHolder(Context context, PublishPollImageTextViewHolder.PollItemChangeListener listener) {
        this.listener = listener;
        mInflater = LayoutInflater.from(context);
//        mEditorView = postEditorView;
        mPollTextRootView = (ViewGroup) mInflater.inflate(R.layout.publish_poll_text_layout, null);

    }

    public void bindViews(List<PollItemInfo> pollItemInfos) {
        mPollTextRootView.removeAllViews();
        for (PollItemInfo pollItemInfo : pollItemInfos) {
            PollTextViewHolder view = new PollTextViewHolder(mInflater.inflate(R.layout.publish_poll_text_item_layout, null));
            view.bindView(pollItemInfo, pollItemInfos.size() > GlobalConfig.MIN_POLL_ITEM_SIZE, pollItemInfos.indexOf(pollItemInfo));
            mPollTextRootView.addView(view.mRootView);
        }
    }

    public void bindGroup(ViewGroup mPollLayout) {
        mPollLayout.addView(mPollTextRootView);
    }

    public class PollTextViewHolder {
        private final View mRootView;
        private final RichEditText mPollDes;
        private final ImageView mPollImage;
        private final View mDeleteBtn;

        public PollTextViewHolder(View rootView) {
            mRootView = rootView;
            mPollDes = rootView.findViewById(R.id.publish_poll_item_edit);
            mPollImage = rootView.findViewById(R.id.publish_poll_item_img);
            mDeleteBtn = rootView.findViewById(R.id.publish_poll_delete);
        }

        public void bindView(final PollItemInfo pollItemInfo, boolean isCanDelete, final int position) {
            mDeleteBtn.setEnabled(isCanDelete);
            mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPollItemDelete(pollItemInfo,position);
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
            mPollDes.setText(pollItemInfo.pollItemText);
            mPollDes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mFocusePosition = position;
                    }

                    listener.onPollEditorChange(pollItemInfo, position);
                }
            });
            String optionHint = ResourceTool.getString("option_num");
            mPollDes.setHint(String.format(optionHint, position + 1));
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
                    listener.onPollEditorChange(pollItemInfo, position);
                }
            });
        }
    }
}

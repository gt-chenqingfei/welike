package com.redefine.welike.business.videoplayer.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by nianguowang on 2018/8/9
 */
public class VideoErrorView extends FrameLayout {

    private AppCompatImageView mReloadBtn;
    private TextView mErrorText;
    private OnErrorClickListener mListener;

    public VideoErrorView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(com.redefine.commonui.R.layout.common_video_error_view, this, true);
        mReloadBtn = findViewById(R.id.common_error_reload);
        mErrorText = findViewById(R.id.common_error_text);

        mErrorText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "video_player_net_error"));
        mReloadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onErrorClick();
                }
            }
        });
    }

    public void setErrorText(String text) {
        mErrorText.setText(text);
    }

    public void setReloadImage(int resId) {
        mReloadBtn.setImageResource(resId);
    }

    public void setOnErrorClickListener(OnErrorClickListener listener) {
        mListener = listener;
    }

    public interface OnErrorClickListener {
        void onErrorClick();
    }
}

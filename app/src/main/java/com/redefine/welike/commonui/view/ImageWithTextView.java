package com.redefine.welike.commonui.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.multimedia.widget.CircleDownloadProgressBar;
import com.redefine.welike.R;

/**
 * Created by nianguowang on 2018/8/7
 */
public class ImageWithTextView extends ConstraintLayout {

    private TextView mText;
    private ImageView mImage;
    private CircleDownloadProgressBar mProgressBar;

    public ImageWithTextView(Context context) {
        super(context);
        init();
    }

    public ImageWithTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageWithTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_image_text, this, true);
        mText = findViewById(R.id.text_view);
        mImage = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.download_progress_bar);
        mProgressBar.setVisibility(View.GONE);
    }

    public void setImageText(String text, int resId) {
        setImage(resId);
        setText(text);
    }

    public void setText(String text) {
        mText.setText(text);
    }

    public void setImage(int resId) {
        mImage.setImageResource(resId);
    }

    public void setProgress(int progress) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(progress);
    }

    public void goneProgressBar() {
        mProgressBar.setVisibility(GONE);
    }
}

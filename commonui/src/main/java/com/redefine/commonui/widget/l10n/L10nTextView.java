package com.redefine.commonui.widget.l10n;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.redefine.commonui.R;

/**
 * Created by liwenbo on 2018/2/27.
 */

@SuppressLint("AppCompatCustomView")
public class L10nTextView extends TextView {
    private String mTextResource;

    public L10nTextView(Context context) {
        super(context);
        init(context, null);
    }

    public L10nTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public L10nTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public L10nTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.L10nTextView);
            mTextResource = ta.getString(R.styleable.L10nTextView_l10n_text_resource);
            ta.recycle();
        }
    }

    public void setTextResource(String key) {

    }

}

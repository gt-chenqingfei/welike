package com.redefine.commonui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by liwenbo on 2018/3/20.
 */

public class CommonTitleConfirmDialog extends Dialog {

    private TextView mTitleView;
    private TextView mCancelBtn;
    private TextView mContentView;
    private TextView mOkBtn;
    private String mTitleText;
    private String mContentText;
    private IConfirmDialogListener mListener;
    private String mCancelText;
    private String mOkText;
    private int mConfirmBtnBg = -1;
    private int mCancelBtnBg = -1;
    private int mConfirmTextColor = -2;
    private int mCancelTextColor = -2;

    public CommonTitleConfirmDialog(@NonNull Context context) {
        this(context, R.style.BaseAppTheme_Dialog);
    }

    public CommonTitleConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.common_title_confirm_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected CommonTitleConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setContentView(R.layout.common_title_confirm_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mTitleView = findViewById(R.id.common_confirm_title);
        mContentView = findViewById(R.id.common_confirm_content);
        mCancelBtn = findViewById(R.id.common_confirm_cancel_btn);
        mOkBtn = findViewById(R.id.common_confirm_ok_btn);
        mContentView.setText(mContentText);
        mTitleView.setText(mTitleText);
        mCancelBtn.setText(mCancelText);
        mOkBtn.setText(mOkText);

        if (mCancelBtnBg != -1) {
            mCancelBtn.setBackgroundResource(mCancelBtnBg);
        }

        if (mConfirmBtnBg != -1) {
            mOkBtn.setBackgroundResource(mConfirmBtnBg);
        }

        if (mCancelTextColor != -2) {
            mCancelBtn.setTextColor(mCancelTextColor);
        }

        if (mConfirmTextColor != -2) {
            mOkBtn.setTextColor(mConfirmTextColor);
        }

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClickConfirm();
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClickCancel();
                }
            }
        });
    }

    public static CommonTitleConfirmDialog showConfirmDialog(Context context, String titleView, String content, String cancelText, String okText, IConfirmDialogListener listener) {
        CommonTitleConfirmDialog commonConfirmDialog = new CommonTitleConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(cancelText);
        commonConfirmDialog.setmContentText(content);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonTitleConfirmDialog showConfirmDialog(Context context, String titleView, String content, IConfirmDialogListener listener) {
        CommonTitleConfirmDialog commonConfirmDialog = new CommonTitleConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel"));
        commonConfirmDialog.setOkText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.setmContentText(content);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonTitleConfirmDialog showCancelDialog(Context context, String titleView, String content, String cancelText, String okText, IConfirmDialogListener listener) {
        CommonTitleConfirmDialog commonConfirmDialog = new CommonTitleConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(cancelText);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setCancelTextBtnBg(R.drawable.common_confirm_ok_btn);
        commonConfirmDialog.setConfirmTextBtnBg(R.drawable.common_confirm_cancel_btn);
        commonConfirmDialog.setCancelTextColor(ContextCompat.getColor(context, R.color.white));
        commonConfirmDialog.setConfirmTextColor(ContextCompat.getColor(context, R.color.color_31));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.setmContentText(content);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonTitleConfirmDialog showCancelDialog(Context context, String titleView, String content, IConfirmDialogListener listener) {
        CommonTitleConfirmDialog commonConfirmDialog = new CommonTitleConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel"));
        commonConfirmDialog.setOkText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
        commonConfirmDialog.setCancelTextBtnBg(R.drawable.common_confirm_ok_btn);
        commonConfirmDialog.setConfirmTextBtnBg(R.drawable.common_confirm_cancel_btn);
        commonConfirmDialog.setCancelTextColor(ContextCompat.getColor(context, R.color.white));
        commonConfirmDialog.setConfirmTextColor(ContextCompat.getColor(context, R.color.color_31));
        commonConfirmDialog.setmContentText(content);
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    private void setConfirmTextColor(int color) {
        mConfirmTextColor = color;
    }

    private void setCancelTextColor(int color) {
        mCancelTextColor = color;
    }

    private void setConfirmTextBtnBg(int bg) {
        mConfirmBtnBg = bg;
    }

    private void setCancelTextBtnBg(int bg) {
        mCancelBtnBg = bg;
    }

    private void setOkText(String titleView) {
        mOkText = titleView;
    }

    private void setCancelText(String cancelText) {
        mCancelText = cancelText;
    }

    private void setConfirmDialogListener(IConfirmDialogListener listener) {
        mListener = listener;
    }

    private void setTitleViewText(String titleView) {
        mTitleText = titleView;
    }

    private void setmContentText(String mContentText) {
        this.mContentText = mContentText;
    }

    public static interface IConfirmDialogListener {
        void onClickCancel();

        void onClickConfirm();
    }
}

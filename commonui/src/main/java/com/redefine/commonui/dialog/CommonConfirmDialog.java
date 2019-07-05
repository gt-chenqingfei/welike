package com.redefine.commonui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.dialog.constant.DialogConstant;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.statusbar.StatusBarUtil;

import static com.redefine.welike.base.ext.ExtsKt.setFullScreenActivity;
import static com.redefine.welike.base.ext.ExtsKt.setStatusBarJ;

/**
 * Created by liwenbo on 2018/3/20.
 */

public class CommonConfirmDialog extends Dialog {

    private TextView mTitleView;
    private TextView mCancelBtn;
    private TextView mOkBtn;
    private String mTitleText;
    private IConfirmDialogListener mListener;
    private String mCancelText;
    private String mOkText;
    private int mConfirmBtnBg = -1;
    private int mCancelBtnBg = -1;
    private int mConfirmTextColor = DialogConstant.DEFUALT_COLOR;
    private int mCancelTextColor = DialogConstant.DEFUALT_COLOR;
    private boolean mFullScreen = false;

    public CommonConfirmDialog(@NonNull Context context) {
        this(context, R.style.BaseAppTheme_Dialog);
    }

    public CommonConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.common_confirm_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    public CommonConfirmDialog(@NonNull Context context, boolean isFullScreen) {
        this(context, R.style.BaseAppTheme_Dialog, isFullScreen);
    }

    public CommonConfirmDialog(@NonNull Context context, int themeResId, boolean isFullScreen) {
        this(context, themeResId);
        setFullScreen(isFullScreen);
    }

    protected CommonConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setContentView(R.layout.common_confirm_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    public void show() {
        if (mFullScreen) {
            if (getWindow() != null) {
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                super.show();
                StatusBarUtil.INSTANCE.setFullScreenActivity(getWindow());
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            } else {
                super.show();
            }
        } else {
            super.show();
        }

    }

    private void initViews() {
        mTitleView = findViewById(R.id.common_confirm_title);
        mCancelBtn = findViewById(R.id.common_confirm_cancel_btn);
        mOkBtn = findViewById(R.id.common_confirm_ok_btn);
        mTitleView.setText(mTitleText);
        mCancelBtn.setText(mCancelText);
        mOkBtn.setText(mOkText);

        if (mCancelBtnBg != -1) {
            mCancelBtn.setBackgroundResource(mCancelBtnBg);
        }

        if (mConfirmBtnBg != -1) {
            mOkBtn.setBackgroundResource(mConfirmBtnBg);
        }

        if (mCancelTextColor != DialogConstant.DEFUALT_COLOR) {
            mCancelBtn.setTextColor(mCancelTextColor);
        }

        if (mConfirmTextColor != DialogConstant.DEFUALT_COLOR) {
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

    public static CommonConfirmDialog showConfirmDialog(Context context, String titleView, String cancelText, String okText, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(cancelText);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonConfirmDialog showFullScreenConfirmDialog(Context context, String titleView, String cancelText, String okText, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context, true);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(cancelText);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonConfirmDialog showConfirmDialog(Context context, String titleView, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel"));
        commonConfirmDialog.setOkText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }


    public static CommonConfirmDialog showFullScreenConfirmDialog(Context context, String titleView, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context, R.style.BaseAppTheme_Dialog,true);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel"));
        commonConfirmDialog.setOkText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonConfirmDialog showConfirmDialog1(Context context, String titleView, String cancelText, String okText, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(cancelText);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setCancelTextBtnBg(R.drawable.common_confirm_cancel_btn);
        commonConfirmDialog.setConfirmTextBtnBg(R.drawable.common_confirm_ok_btn);
        commonConfirmDialog.setCancelTextColor(ContextCompat.getColor(context, R.color.white));
        commonConfirmDialog.setConfirmTextColor(ContextCompat.getColor(context, R.color.color_31));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.setCancelable(false);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }


    public static CommonConfirmDialog showCancelDialog(Context context, String titleView, String cancelText, String okText, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(cancelText);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setCancelTextBtnBg(R.drawable.common_confirm_ok_btn);
        commonConfirmDialog.setConfirmTextBtnBg(R.drawable.common_confirm_cancel_btn);
        commonConfirmDialog.setCancelTextColor(ContextCompat.getColor(context, R.color.white));
        commonConfirmDialog.setConfirmTextColor(ContextCompat.getColor(context, R.color.color_31));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonConfirmDialog showCancelDialog(Context context, String titleView, IConfirmDialogListener listener) {
        CommonConfirmDialog commonConfirmDialog = new CommonConfirmDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setCancelText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel"));
        commonConfirmDialog.setOkText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
        commonConfirmDialog.setCancelTextBtnBg(R.drawable.common_confirm_ok_btn);
        commonConfirmDialog.setConfirmTextBtnBg(R.drawable.common_confirm_cancel_btn);
        commonConfirmDialog.setCancelTextColor(ContextCompat.getColor(context, R.color.white));
        commonConfirmDialog.setConfirmTextColor(ContextCompat.getColor(context, R.color.color_31));
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

    public void setFullScreen(boolean isFullScreen) {
        mFullScreen = isFullScreen;
    }

    public static interface IConfirmDialogListener {
        void onClickCancel();

        void onClickConfirm();
    }
}

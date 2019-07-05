package com.redefine.commonui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.dialog.constant.DialogConstant;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by liwenbo on 2018/3/20.
 */

public class CommonSingleConfirmAndCancelDialog extends Dialog {

    private TextView mTitleView;
    private TextView mOkBtn;
    private ImageView cancelView;
    private String mTitleText;
    private IConfirmDialogListener mListener;
    private String mOkText;
    private int mConfirmBtnBg = -1;
    private int mConfirmTextColor = DialogConstant.DEFUALT_COLOR;

    public CommonSingleConfirmAndCancelDialog(@NonNull Context context) {
        this(context, R.style.BaseAppTheme_Dialog);
    }

    public CommonSingleConfirmAndCancelDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.common_single_confirm_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected CommonSingleConfirmAndCancelDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setContentView(R.layout.common_single_confirm_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mTitleView = findViewById(R.id.common_confirm_title);
        mOkBtn = findViewById(R.id.common_confirm_btn);
        cancelView = findViewById(R.id.cancel_action_btn);
        cancelView.setVisibility(View.VISIBLE);
        ((LinearLayout.LayoutParams) mTitleView.getLayoutParams()).topMargin = 0;
        mTitleView.setLayoutParams(mTitleView.getLayoutParams());
        mTitleView.setText(mTitleText);
        mOkBtn.setText(mOkText);


        if (mConfirmBtnBg != -1) {
            mOkBtn.setBackgroundResource(mConfirmBtnBg);
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

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public static CommonSingleConfirmAndCancelDialog showConfirmDialog(Context context, String titleView, String okText, IConfirmDialogListener listener) {
        CommonSingleConfirmAndCancelDialog commonConfirmDialog = new CommonSingleConfirmAndCancelDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }

    public static CommonSingleConfirmAndCancelDialog showConfirmDialog(Context context, String titleView, IConfirmDialogListener listener) {
        CommonSingleConfirmAndCancelDialog commonConfirmDialog = new CommonSingleConfirmAndCancelDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setOkText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_confirm"));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.show();
        return commonConfirmDialog;
    }


    private void setConfirmTextColor(int color) {
        mConfirmTextColor = color;
    }


    private void setConfirmTextBtnBg(int bg) {
        mConfirmBtnBg = bg;
    }

    private void setOkText(String titleView) {
        mOkText = titleView;
    }


    private void setConfirmDialogListener(IConfirmDialogListener listener) {
        mListener = listener;
    }

    private void setTitleViewText(String titleView) {
        mTitleText = titleView;
    }

    public interface IConfirmDialogListener {

        void onClickConfirm();
    }
}

package com.redefine.commonui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.redefine.commonui.R;
import com.redefine.commonui.dialog.constant.DialogConstant;

public class SelfStartConfimDialog extends Dialog {
    private LottieAnimationView mLottieAnimationView;
    private View mCloseView;
    private TextView mTitleContent;
    private TextView mConfirmView;
    private int mConfirmBtnBg = -1;
    private int mCancelBtnBg = -1;
    private int mConfirmTextColor = DialogConstant.DEFUALT_COLOR;
    private int mCancelTextColor = DialogConstant.DEFUALT_COLOR;

    private String mOkText;
    private String mTitleText;
    private String mAnmationName;



    private SelfStartConfimDialog.IConfirmDialogListener mListener;



    public SelfStartConfimDialog(@NonNull Context context) {
        this(context, R.style.BaseAppTheme_Dialog);
    }

    public SelfStartConfimDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.self_start_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected SelfStartConfimDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setContentView(R.layout.self_start_dialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }
    private void initView(){
        mLottieAnimationView=findViewById(R.id.self_start_guide);
        mCloseView=findViewById(R.id.close_button);
        mTitleContent=findViewById(R.id.common_confirm_title);
        mConfirmView=findViewById(R.id.common_confirm_ok_btn);
        mConfirmView.setText(mOkText);
        mTitleContent.setText(mTitleText);
        mLottieAnimationView.setAnimation(mAnmationName);
        mLottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        mLottieAnimationView.playAnimation();


        if (mConfirmBtnBg != -1) {
            mConfirmView.setBackgroundResource(mConfirmBtnBg);
        }
        if (mConfirmTextColor != DialogConstant.DEFUALT_COLOR) {
            mConfirmView.setTextColor(mConfirmTextColor);
        }

        mConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClickConfirm();
                }
            }
        });

        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClickCancel();
                }
            }
        });

    }
    public static SelfStartConfimDialog showConfirmDialog(Context context, String titleView, String okText,String anaName, IConfirmDialogListener listener) {
        SelfStartConfimDialog commonConfirmDialog = new SelfStartConfimDialog(context);
        commonConfirmDialog.setTitleViewText(titleView);
        commonConfirmDialog.setOkText(okText);
        commonConfirmDialog.setAnimationName(anaName);
        commonConfirmDialog.setConfirmTextBtnBg(R.drawable.common_confirm_ok_btn);
        commonConfirmDialog.setConfirmTextColor(ContextCompat.getColor(context, R.color.white));
        commonConfirmDialog.setConfirmDialogListener(listener);
        commonConfirmDialog.setCancelable(false);
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

    private void setAnimationName(String name){
        mAnmationName=name;

    }
    private void setConfirmDialogListener(IConfirmDialogListener listener) {
        mListener = listener;
    }

    private void setTitleViewText(String titleView) {
        mTitleText = titleView;
    }

    public static interface IConfirmDialogListener {
        void onClickCancel();

        void onClickConfirm();
    }
}

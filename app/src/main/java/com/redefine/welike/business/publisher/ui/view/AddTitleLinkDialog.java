package com.redefine.welike.business.publisher.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.commonui.util.Scheme;

/**
 * Created by gongguan on 2018/1/20.
 */

public class AddTitleLinkDialog extends Dialog implements View.OnClickListener, TextWatcher {

    private OnSubmitClickListener onClickListener;
    private View mCloseBtn;
    private TextView mWarnText;
    private TextView mSubmit;
    private EditText mEditText;
    private View mLinkExtBtn;
    private ImageView mLinkExtIcon;
    private TextView mNameWarnText;
    private EditText mEditName;
    private View mLinkDiv;
    private View mNameDiv;
    private boolean mShowExt = false;

    public AddTitleLinkDialog(@NonNull Context context) {
        super(context, R.style.BaseAppTheme_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_title_link_dialog);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        initViews();
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private void initViews() {
        mCloseBtn = findViewById(R.id.add_link_close_btn);
        mCloseBtn.setOnClickListener(this);
        mWarnText = findViewById(R.id.add_link_text_warn);
        mEditText = findViewById(R.id.add_link_edit_text);
        mEditText.addTextChangedListener(this);
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switchToEditUrl();
                }
            }

        });
        mLinkDiv = findViewById(R.id.add_link_edit_div);
        mWarnText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_link_note"));
        mSubmit = findViewById(R.id.add_link_submit);
        mSubmit.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_link_submit"));
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditName.getText().toString().trim();
                String url = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    return ;
                }
                if (url.toLowerCase().startsWith(Scheme.SCHEME_HTTP) || url.toLowerCase().startsWith(Scheme.SCHEME_HTTPS)) {
                    if (onClickListener != null) {
                        onClickListener.onSubmitClick(url, name);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "add_link_error"), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mLinkExtBtn = findViewById(R.id.link_ext_arrow);
        mLinkExtIcon = mLinkExtBtn.findViewById(R.id.link_ext_arrow_icon);
        mLinkExtBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mShowExt) {
                    showExt();
                } else {
                    hideExt();
                }
            }

        });
        mNameWarnText = findViewById(R.id.add_link_name_warn);
        mNameWarnText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "add_link_name_note"));
        mEditName = findViewById(R.id.add_link_edit_name);
        mEditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switchToEditName();
                }
            }

        });
        mNameDiv = findViewById(R.id.add_link_edit_name_div);
    }

    public static void show(Context context, OnSubmitClickListener onClickListener) {
        AddTitleLinkDialog dialog = new AddTitleLinkDialog(context);
        dialog.setOnSubmitClickListener(onClickListener);
        dialog.show();
    }

    private void setOnSubmitClickListener(OnSubmitClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v == mCloseBtn) {
            dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().length() > 0) {
            mSubmit.setEnabled(true);
        } else {
            mSubmit.setEnabled(false);
        }
    }

    public interface OnSubmitClickListener {
        void onSubmitClick(String url, String name);
    }

    private void showExt() {
        mShowExt = true;
        mLinkExtIcon.setImageResource(R.drawable.link_ext_arrow_show);
        mNameWarnText.setVisibility(View.INVISIBLE);
        mEditName.setVisibility(View.VISIBLE);
        mEditName.clearFocus();
        mEditName.setHint(mNameWarnText.getText());
        mNameDiv.setVisibility(View.VISIBLE);
        mNameDiv.setBackgroundColor(mNameDiv.getResources().getColor(R.color.color_ddd));
    }

    private void hideExt() {
        mShowExt = false;
        switchToEditUrl();
        mLinkExtIcon.setImageResource(R.drawable.link_ext_arrow_hide);
        mNameWarnText.setVisibility(View.GONE);
        mEditName.setVisibility(View.GONE);
        mNameDiv.setVisibility(View.GONE);
    }

    private void switchToEditUrl() {
        mEditName.clearFocus();
        mEditName.setHint(mNameWarnText.getText());
        mEditText.requestFocus();
        mEditText.setHint(null);
        mLinkDiv.setBackgroundColor(mLinkDiv.getResources().getColor(R.color.main));
        mNameDiv.setBackgroundColor(mNameDiv.getResources().getColor(R.color.color_ddd));
        if (mNameWarnText.getVisibility() == View.VISIBLE) {
            mNameWarnText.setVisibility(View.INVISIBLE);
        }
        mWarnText.setVisibility(View.VISIBLE);
    }

    private void switchToEditName() {
        mEditText.clearFocus();
        mEditText.setHint(mWarnText.getText());
        mEditName.requestFocus();
        mEditName.setHint(null);
        mLinkDiv.setBackgroundColor(mLinkDiv.getResources().getColor(R.color.color_ddd));
        mNameDiv.setBackgroundColor(mNameDiv.getResources().getColor(R.color.main));
        if (mNameWarnText.getVisibility() == View.INVISIBLE) {
            mNameWarnText.setVisibility(View.VISIBLE);
        }
        mWarnText.setVisibility(View.INVISIBLE);
    }

}

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
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by gongguan on 2018/1/20.
 */

public class AddLinkDialog extends Dialog implements View.OnClickListener, TextWatcher {

    private OnSubmitClickListener onClickListener;
    private View mCloseBtn;
    private TextView mWarnText;
    private TextView mSubmit;
    private EditText mEditText;

    public AddLinkDialog(@NonNull Context context) {
        super(context, R.style.BaseAppTheme_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_link_dialog);
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
        mWarnText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_link_note"));
        mSubmit = findViewById(R.id.add_link_submit);
        mSubmit.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_link_submit"));
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                if (url.toLowerCase().startsWith(GlobalConfig.HTTP) || url.toLowerCase().startsWith(GlobalConfig.HTTPS)) {
                    if (onClickListener != null) {
                        onClickListener.onSubmitClick(url);
                    }
                    dismiss();

                    EventLog.Publish.report13(PublisherEventManager.INSTANCE.getSource(),
                            PublisherEventManager.INSTANCE.getMain_source(),
                            PublisherEventManager.INSTANCE.getPage_type(),
                            PublisherEventManager.INSTANCE.getAt_source());
                } else {
                    ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "add_link_error"));
                    EventLog.Publish.report12(PublisherEventManager.INSTANCE.getSource(),
                            PublisherEventManager.INSTANCE.getMain_source(),
                            PublisherEventManager.INSTANCE.getPage_type(),
                            PublisherEventManager.INSTANCE.getAt_source());
                    if(PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
                        PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report12();
                    }
                }
            }
        });
    }

    public static void show(Context context, OnSubmitClickListener onClickListener) {
        AddLinkDialog dialog = new AddLinkDialog(context);
        dialog.setOnSubmitClickListener(onClickListener);
        dialog.show();

        if(PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel() != null) {
            PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().getProxy().report11();
        }
        EventLog.Publish.report11(PublisherEventManager.INSTANCE.getSource(),
                PublisherEventManager.INSTANCE.getMain_source(),
                PublisherEventManager.INSTANCE.getPage_type(),
                PublisherEventManager.INSTANCE.getAt_source());
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
        void onSubmitClick(String url);
    }

}

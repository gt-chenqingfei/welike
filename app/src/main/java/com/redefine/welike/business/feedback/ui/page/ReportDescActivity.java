package com.redefine.welike.business.feedback.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.oss.adapter.UploadMgr;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.NewReportRequest;
import com.redefine.welike.business.browse.management.request.NewReportRequest1;
import com.redefine.welike.business.feedback.management.ReportPicUploadTask;
import com.redefine.welike.business.feedback.management.bean.ReportModel;
import com.redefine.welike.business.feedback.ui.adapter.ReportPicAdapter;
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.util.EmailReporter;
import com.redefine.welike.commonui.photoselector.PhotoSelector;
import com.redefine.welike.commonui.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/10/15
 */
@Route(path = RouteConstant.REPORT_DESC_ROUTE_PATH)
public class ReportDescActivity extends BaseActivity {
    private static final int GRID_MANAGER_SPAN_COUNT = 4;

    private int mReasonId;
    private String mReason;
    private PostBase mPost;

    private ReportPicAdapter mAdapter;

    private TextView mReportReason;
    private TextView mDescLeftWordCount;
    private EditText mReasonDesc;
    private RecyclerView mRecyclerView;
    private TextView mSubmit;

    private LoadingDlg mLoadDlg;


    public static void show(int reasonId, String reason, PostBase postBase) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedbackConstants.FEEDBACK_KEY_REASON_ID, reasonId);
        bundle.putString(FeedbackConstants.FEEDBACK_KEY_REASON, reason);
        bundle.putSerializable(FeedbackConstants.FEEDBACK_KEY_POST, postBase);

        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PAGE_REPORT_DESC, bundle));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_report_desc);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        Bundle extras = intent.getExtras();
        mReasonId = extras.getInt(FeedbackConstants.FEEDBACK_KEY_REASON_ID);
        mReason = extras.getString(FeedbackConstants.FEEDBACK_KEY_REASON);
        mPost = (PostBase) extras.getSerializable(FeedbackConstants.FEEDBACK_KEY_POST);

        initView();
    }

    private void initView() {
        mReportReason = findViewById(R.id.report_reason);
        mDescLeftWordCount = findViewById(R.id.report_reason_desc_words);
        mReasonDesc = findViewById(R.id.report_reason_et);
        mRecyclerView = findViewById(R.id.report_reason_image_desc);
        mSubmit = findViewById(R.id.report_submit);
        findViewById(R.id.common_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                InputMethodUtil.hideInputMethod(mReasonDesc);
            }
        });

        mReasonDesc.addTextChangedListener(mTextWatcher);

        mReportReason.setText(mReason);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        setDescLeftWordCount();
        initRecyclerView();
    }

    private void onSubmit() {
        ArrayList<Item> data = mAdapter.getData();

        ReportModel model = new ReportModel();
        model.setDescription(mReasonDesc.getText().toString());
        model.setUploadImages(data);
        model.setReportId(mPost.getPid());
        if (AccountManager.getInstance().getAccount() != null) {
            model.setUserId(AccountManager.getInstance().getAccount().getUid());
        }
        model.setPostUserId(mPost.getUid());
        model.setReason(mReason);
        model.setId(String.valueOf(mReasonId));

        shoLoadingDlg();
        ReportPicUploadTask task = new ReportPicUploadTask(model, this, new ReportPicUploadTask.ReportPicUploadCallback() {
            @Override
            public void onUploadComplete(ReportModel model) {
                doRealReport(model);
            }

            @Override
            public void onUploadFail(ReportModel model) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDlg();
                        ToastUtils.showShort(R.string.regist_sms_code_toast);
                    }
                });
            }
        });
        task.upload();
        if (mReasonId == 4) {
            EmailReporter.emailReport(this, mPost.getPid());
        }
    }

    private void doRealReport(ReportModel model) {
        if (AccountManager.getInstance().isLoginComplete()) {
            new NewReportRequest(ReportDescActivity.this).request(model, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDlg();
                            ToastUtils.showShort(R.string.regist_sms_code_toast);
                        }
                    });
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDlg();
                            ToastUtils.showShort(R.string.report_success_toast);
                            finish();
                        }
                    });
                }
            });
        } else {
            new NewReportRequest1(ReportDescActivity.this).request(model, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDlg();
                            ToastUtils.showShort(R.string.regist_sms_code_toast);
                        }
                    });
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDlg();
                            ToastUtils.showShort(R.string.report_success_toast);
                            finish();
                        }
                    });
                }
            });
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, GRID_MANAGER_SPAN_COUNT));
        mAdapter = new ReportPicAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ReportPicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                if (mAdapter.getItemViewType(position) == ReportPicAdapter.ITEM_VIEW_TYPE_ADD ||
                        mAdapter.getItemViewType(position) == ReportPicAdapter.ITEM_VIEW_TYPE_INIT) {
                    PhotoSelector.launchChooseReportPicSelect(ReportDescActivity.this, mAdapter.getData());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CommonRequestCode.CHOSEN_REPORT_PIC_CODE) {
            ArrayList<Item> items = data.getParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS);
            mAdapter.setData(items);
        }
    }

    private void shoLoadingDlg() {
        if (mLoadDlg == null) {
            mLoadDlg = new LoadingDlg(this);
        }
        mLoadDlg.show();
    }

    private void dismissLoadingDlg() {
        if (mLoadDlg != null && mLoadDlg.isShowing()) {
            mLoadDlg.dismiss();
        }
    }

    private void setDescLeftWordCount() {
        int descCount;
        Editable text = mReasonDesc.getText();
        descCount = text == null ? 0 : text.toString().length();
        mDescLeftWordCount.setText(String.valueOf(500 - descCount));
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setDescLeftWordCount();
        }
    };

}

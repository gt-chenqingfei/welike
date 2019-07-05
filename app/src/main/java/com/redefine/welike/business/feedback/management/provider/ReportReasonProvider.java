package com.redefine.welike.business.feedback.management.provider;

import android.util.SparseArray;

import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.request.ReportRequest;
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/4/18
 */
public class ReportReasonProvider implements RequestCallback {

    private ReportCallback mCallback;
    private ReportRequest request;

    public interface ReportCallback {

        void onReportSuccess();

        void onReportFail();
    }

    public void register(ReportCallback callback) {
        mCallback = callback;
    }

    public void unRegister() {
        mCallback = null;
    }

    public SparseArray<String> provide() {
        SparseArray<String> reportReason = new SparseArray<>(4);
        reportReason.put(FeedbackConstants.REPORT_REASON_ONE, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason1"));
        reportReason.put(FeedbackConstants.REPORT_REASON_TWO, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason2"));
        reportReason.put(FeedbackConstants.REPORT_REASON_THREE, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason3"));
        reportReason.put(FeedbackConstants.REPORT_REASON_FOUR, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason4"));
        reportReason.put(FeedbackConstants.REPORT_REASON_FIVE, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason5"));
        reportReason.put(FeedbackConstants.REPORT_REASON_SIX, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason_others"));
        return reportReason;
    }

    public void report(String postId, String postOwnerId, CharSequence reason) {
        if(request != null) {
            return;
        }
        request = new ReportRequest(MyApplication.getAppContext());
        try {
            request.request(postId, postOwnerId, reason, this);
        } catch (Exception e) {
            e.printStackTrace();
            request = null;
            reportFail();
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if(request == this.request) {
            this.request = null;
            reportFail();
        }
    }

    @Override
    public void onSuccess(BaseRequest request, com.alibaba.fastjson.JSONObject result) throws Exception {
        if(request == this.request) {
            this.request = null;
            reportSuccess();
        }
    }

    private void reportSuccess() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if(mCallback != null) {
                    mCallback.onReportSuccess();
                }
            }
        });
    }

    private void reportFail() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if(mCallback != null) {
                    mCallback.onReportFail();
                }
            }
        });
    }
}

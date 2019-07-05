package com.redefine.welike.business.feedback.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants;
import com.redefine.welike.business.feeds.management.bean.PostBase;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/**
 * Created by nianguowang on 2018/4/17
 */
@Route(path = RouteConstant.REPORT_ROUTE_PATH)
public class ReportPage extends BaseActivity {

    private PostBase mPost;
    private TextView mSubTitle;
    private View mReason1, mReason2, mReason3, mReason4, mReason5, mReason6;
    private TextView mReasonText1, mReasonText2, mReasonText3, mReasonText4, mReasonText5, mReasonText6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        setContentView(R.layout.report_page);

        Bundle extras = intent.getExtras();
        mPost = (PostBase) extras.getSerializable(FeedbackConstants.FEEDBACK_KEY_POST);
        if (mPost == null) {
            finish();
            return;
        }

        initView();
    }

    private void initView() {
        mSubTitle = findViewById(R.id.report_title);
        mReason1 = findViewById(R.id.report_reason_1);
        mReason2 = findViewById(R.id.report_reason_2);
        mReason3 = findViewById(R.id.report_reason_3);
        mReason4 = findViewById(R.id.report_reason_4);
        mReason5 = findViewById(R.id.report_reason_5);
        mReason6 = findViewById(R.id.report_reason_6);

        mReasonText1 = findViewById(R.id.report_reason_text_1);
        mReasonText2 = findViewById(R.id.report_reason_text_2);
        mReasonText3 = findViewById(R.id.report_reason_text_3);
        mReasonText4 = findViewById(R.id.report_reason_text_4);
        mReasonText5 = findViewById(R.id.report_reason_text_5);
        mReasonText6 = findViewById(R.id.report_reason_text_6);

        mReason1.setOnClickListener(mClickListener);
        mReason2.setOnClickListener(mClickListener);
        mReason3.setOnClickListener(mClickListener);
        mReason4.setOnClickListener(mClickListener);
        mReason5.setOnClickListener(mClickListener);
        mReason6.setOnClickListener(mClickListener);


        findViewById(R.id.common_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSubTitle(mPost.getNickName());
    }

    private void setSubTitle(String postOwner) {
        if (postOwner == null) {
            postOwner = "";
        }
        String reportPost = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_post");
        int startIndex = reportPost.indexOf("%s");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.common_text_color_48779D));
        String title = String.format(reportPost, postOwner);
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(colorSpan, startIndex, startIndex + postOwner.length(), SPAN_INCLUSIVE_EXCLUSIVE);
        mSubTitle.setText(spannableString);
    }

    private void next(int reasonId, String reason) {
        ReportDescActivity.show(reasonId, reason, mPost);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mReason1) {
                next(0, mReasonText1.getText().toString());
            } else if (v == mReason2) {
                next(1, mReasonText2.getText().toString());
            } else if (v == mReason3) {
                next(2, mReasonText3.getText().toString());
            } else if (v == mReason4) {
                next(3, mReasonText4.getText().toString());
            } else if (v == mReason5) {
                next(4, mReasonText5.getText().toString());
            } else if (v == mReason6) {
                next(5, mReasonText6.getText().toString());
            }
        }
    };
}

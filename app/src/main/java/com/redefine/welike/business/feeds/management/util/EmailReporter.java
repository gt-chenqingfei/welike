package com.redefine.welike.business.feeds.management.util;

import android.content.Context;
import android.content.Intent;

import com.redefine.welike.R;

/**
 * Created by nianguowang on 2018/9/10
 */
public class EmailReporter {

    private EmailReporter(){}

    public static void emailReport(Context context, String postId) {
        String postLink = "https://welike.in/p/" + postId;
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.report_email_address)});
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_email_subject));
            intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.report_email_content, postLink));

            context.startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            //do noting
        }

    }
}

package com.redefine.commonui.share.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.redefine.commonui.R;
import com.redefine.commonui.share.AbsSharePackageManager;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.ShareConstants;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;

import java.io.File;

/**
 * Created by gongguan on 2018/3/20.
 */

public class WhatsAppPackageManager extends AbsSharePackageManager {

    private CommonListener<String> mListener;

    public WhatsAppPackageManager(SharePackageFactory.SharePackage sharePackage) {
        super(sharePackage);
    }

    @Override
    public boolean isInstall(Context context) {
        return isPkgInstalled(context, ShareConstants.PACKAGE_NAME_WHATS_APP);
    }

    @Override
    public boolean supportImage() {
        return true;
    }

    @Override
    public void shareTo(final Context context, final ShareModel shareModel, final CommonListener<String> listener) {
        if (shareModel.getTitle() == null) {
            shareModel.setTitle("");
        }
        mListener = listener;
        if (!TextUtils.isEmpty(shareModel.getFilePath())) {
            shareWithFile(context, shareModel);
        } else if (!TextUtils.isEmpty(shareModel.getVideoPath())) {
            shareWithVideo(context, shareModel);
        } else if (!TextUtils.isEmpty(shareModel.getImagePath())) {
            shareWithImage(context, shareModel);
        } else {
            shareWithText(context, shareModel);
        }

//        TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
//                .setCategory(TrackerConstant.EVENT_EV_CT)
//                .setAction(TrackerConstant.EVENT_SHARE_CLICK)
//                .setLabel("whatsapp").build());
    }

    private void shareWithFile(Context context, ShareModel shareModel) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(ShareConstants.PACKAGE_NAME_WHATS_APP);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(shareModel.getFilePath()));
//        Uri fileUri = Uri.parse(shareModel.getFilePath());
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        try {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.sliding_to_left_in, 0);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mListener != null) {
            mListener.onFinish("Success");
        }
    }

    private void shareWithVideo(Context context, ShareModel shareModel) {
        String shareContent = shareModel.getContent();
        if(TextUtils.isEmpty(shareContent)) {
            shareContent = shareModel.getTitle() + "\t" + shareModel.getH5Link();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(ShareConstants.PACKAGE_NAME_WHATS_APP);
        intent.setType(ShareConstants.INTENT_TYPE_VIDEO);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri videoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(shareModel.getVideoPath()));
//        Uri videoUri = Uri.parse(shareModel.getVideoPath());
        intent.putExtra(Intent.EXTRA_STREAM, videoUri);
        try {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.sliding_to_left_in, 0);
                ((Activity) context).startActivityForResult(intent, ShareConstants.SHARE_REQUEST_CODE_WHATS_APP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void shareWithText(Context context, ShareModel shareModel) {
        String shareContent = shareModel.getContent();
        if(TextUtils.isEmpty(shareContent)) {
            shareContent = shareModel.getTitle() + "\t" + shareModel.getH5Link();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(ShareConstants.PACKAGE_NAME_WHATS_APP);
        intent.setType(ShareConstants.INTENT_TYPE_TEXT);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.sliding_to_left_in, 0);
                ((Activity) context).startActivityForResult(intent, ShareConstants.SHARE_REQUEST_CODE_WHATS_APP);
            }
        } catch (Exception e) {
            // do nothing
        }

    }

    private void shareWithImage(Context context, ShareModel shareModel) {
        String shareContent = shareModel.getContent();
        if(TextUtils.isEmpty(shareContent)) {
            shareContent = shareModel.getTitle() + "\t" + shareModel.getH5Link();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(ShareConstants.PACKAGE_NAME_WHATS_APP);
//        Uri imageUri = Uri.parse(shareModel.getImagePath());
        Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(shareModel.getImagePath()));
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType(ShareConstants.INTENT_TYPE_IMAGE);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.sliding_to_left_in, 0);
                ((Activity) context).startActivityForResult(intent, ShareConstants.SHARE_REQUEST_CODE_WHATS_APP);
            }
        } catch (Exception e) {
            // do nothing
        }

    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode != ShareConstants.SHARE_REQUEST_CODE_WHATS_APP) {
            return;
        }
        if (responseCode == ShareConstants.SHARE_RESULT_CODE_SUCCESS_WHATS_APP) {
            if(mListener != null) {
                mListener.onFinish("Success");
            }
        } else {
            if(mListener != null) {
                mListener.onError("Fail");
            }
        }
    }
}

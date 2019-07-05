package com.redefine.commonui.share.instagram;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;


//import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders;
import com.redefine.commonui.R;
import com.redefine.commonui.share.AbsSharePackageManager;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.ShareConstants;
import com.redefine.commonui.share.ShareManager;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.track.TrackerUtil;

import java.io.File;

/**
 * Created by gongguan on 2018/3/20.
 */

public class InstagramPackageManager extends AbsSharePackageManager {

    private CommonListener<String> mListener;
    public InstagramPackageManager(SharePackageFactory.SharePackage sharePackage) {
        super(sharePackage);
    }

    @Override
    public boolean isInstall(Context context) {
        return isPkgInstalled(context, ShareConstants.PACKAGE_NAME_INSTAGRAM);
    }

    @Override
    public boolean supportImage() {
        return true;
    }

    @Override
    public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {
//        TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
//                .setCategory(TrackerConstant.EVENT_EV_CT)
//                .setAction(TrackerConstant.EVENT_SHARE_CLICK)
//                .setLabel("instagram").build());

        if (shareModel == null || TextUtils.isEmpty(shareModel.getImagePath())) {
            if(listener != null) {
                listener.onError("Fail");
            }
            return;
        }
        this.mListener = listener;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage(ShareConstants.PACKAGE_NAME_INSTAGRAM);
        intent.setType(ShareConstants.INTENT_TYPE_IMAGE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(shareModel.getImagePath()));
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);

        ShareManager.clipboard(context, shareModel, "weLikeCopy");
        try {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.sliding_to_left_in, 0);
                ((Activity) context).startActivityForResult(intent, ShareConstants.SHARE_REQUEST_CODE_INSTAGRAM);
            }
            if(mListener != null) {
                mListener.onFinish("Success");
            }
        } catch (ActivityNotFoundException e) {
            if(listener != null) {
                listener.onError("Fail");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
//        if (requestCode != ShareConstants.SHARE_REQUEST_CODE_INSTAGRAM) {
//            return;
//        }
//        if (responseCode == ShareConstants.SHARE_RESULT_CODE_SUCCESS_INSTAGRAM) {
//            if(mListener != null) {
//                mListener.onFinish("Success");
//            }
//        } else {
//            if(mListener != null) {
//                mListener.onError("Fail");
//            }
//        }
    }
}

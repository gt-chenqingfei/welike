package com.redefine.commonui.share.system;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.text.TextUtils;

import com.redefine.commonui.share.AbsSharePackageManager;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.SharePackageFactory;


/**
 * Created by gongguan on 2018/3/20.
 */

public class SystemPackageManager extends AbsSharePackageManager {
    public SystemPackageManager(SharePackageFactory.SharePackage sharePackage) {
        super(sharePackage);
    }

    @Override
    public boolean isInstall(Context context) {
        return true;
    }

    @Override
    public boolean supportImage() {
        return false;
    }

    @Override
    public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {
        String shareContent = shareModel.getContent();
        if(TextUtils.isEmpty(shareContent)) {
            shareContent = shareModel.getTitle() + "\t" + shareModel.getH5Link();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(intent, "Share Link"));
        if(listener != null) {
            listener.onFinish("Success");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {

    }
}

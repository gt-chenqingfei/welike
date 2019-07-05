package com.redefine.commonui.share.system;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.redefine.commonui.share.AbsSharePackageManager;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.ShareManager;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.welike.base.resource.ResourceTool;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by gongguan on 2018/3/20.
 */

public class SystemCopyManager extends AbsSharePackageManager {
    public SystemCopyManager(SharePackageFactory.SharePackage sharePackage) {
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
    public void shareTo(final Context context, ShareModel shareModel, CommonListener<String> listener) {
        ShareManager.clipboard(context, shareModel, "welike_copy");
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_share_content_copyed"), Toast.LENGTH_SHORT).show();
            }
        });
        if(listener != null) {
            listener.onFinish(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_share_content_copyed"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {

    }
}

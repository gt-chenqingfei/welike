package com.redefine.commonui.share.interceptor;

import android.content.Context;

import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.ISharePackageManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/6/21
 */
public class RealShareInterceptor extends AbstractInterceptor {

    private Context mContext;
    private CommonListener<String> mListener;
    private ISharePackageManager mSharePackageManager;
    public RealShareInterceptor(Context context, ISharePackageManager sharePackageManager, CommonListener<String> listener) {
        mContext = context;
        mSharePackageManager = sharePackageManager;
        mListener = listener;
    }

    @Override
    public void handle(final ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                mSharePackageManager.shareTo(mContext, shareModel, mListener);
            }
        });
    }
}

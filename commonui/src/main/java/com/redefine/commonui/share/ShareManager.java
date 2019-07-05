package com.redefine.commonui.share;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.redefine.commonui.share.activity.ShareActivity;
import com.redefine.commonui.share.interceptor.AbstractInterceptor;
import com.redefine.commonui.share.interceptor.HeadInterceptor;
import com.redefine.commonui.share.interceptor.IShowDialogCallback;
import com.redefine.commonui.share.interceptor.RealShareInterceptor;
import com.redefine.commonui.share.interceptor.ShareApkInterceptor;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.resource.ResourceTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gongguan on 2018/3/20.
 */

public class ShareManager implements IShareManager {

    private Map<SharePackageFactory.SharePackage, ISharePackageManager> mSharePackageManagers;

    private ArrayList<SharePackageModel> packages = new ArrayList<>();
    private AbstractInterceptor mCurrentInterceptor;
    private ArrayList<AbstractInterceptor> mInterceptors = new ArrayList<>();
    private ArrayList<AbstractInterceptor> mImageInterceptors = new ArrayList<>();
    private ISharePackageManager mCurSharePackageManager;
    private static ShareManager instance;
    private Context mContext;

    private ShareManager() {
    }

    public static ShareManager getInstance() {
        if (instance == null) {
            instance = new ShareManager();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        initPackageManager();
    }

    public ArrayList<SharePackageModel> getPackages() {
        return packages;
    }

    private void initPackageManager() {
        packages.clear();

        Map<SharePackageFactory.SharePackage, ISharePackageManager> pool = new HashMap<>();
        Map<SharePackageFactory.SharePackage, ISharePackageManager> temp = SharePackageFactory.getSharePackages();
        packages.add(SharePackageFactory.create(SharePackageFactory.SharePackage.WHATS_APP));
        packages.add(SharePackageFactory.create(SharePackageFactory.SharePackage.FACEBOOK));
        packages.add(SharePackageFactory.create(SharePackageFactory.SharePackage.INSTAGTRAM));
        packages.add(SharePackageFactory.create(SharePackageFactory.SharePackage.SHARE_APK));
        packages.add(SharePackageFactory.create(SharePackageFactory.SharePackage.SYSYTEM));
        packages.add(SharePackageFactory.create(SharePackageFactory.SharePackage.COPY));

        for (SharePackageModel sharePackage : packages) {
            if (temp.get(sharePackage.getSharePackage()).isInstall(mContext)) {
                pool.put(sharePackage.getSharePackage(), temp.get(sharePackage.getSharePackage()));
            }
        }
        mSharePackageManagers = pool;
    }

    @Override
    public boolean isInstall(Context context, SharePackageFactory.SharePackage sharePackage) {
        ISharePackageManager manager = mSharePackageManagers.get(sharePackage);
        return manager != null && manager.isInstall(context);
    }

    public void addMenuList(List<SharePackageModel> menuList) {
        if (CollectionUtil.isEmpty(menuList)) {
            return;
        }
        packages.addAll(menuList);
        for (SharePackageModel menu : menuList) {
            if (menu.getListener().isInstall(mContext)) {
                mSharePackageManagers.put(menu.getSharePackage(), menu.getListener());
            }
        }
    }

    public void customMenu(SharePackageModel... sharePackages) {
        packages.clear();
        mSharePackageManagers.clear();

        Map<SharePackageFactory.SharePackage, ISharePackageManager> pool = new HashMap<>();
        Map<SharePackageFactory.SharePackage, ISharePackageManager> temp = SharePackageFactory.getSharePackages();

        for (SharePackageModel sharePackage : sharePackages) {
            packages.add(sharePackage);
            temp.put(sharePackage.getSharePackage(), sharePackage.getListener());
        }
        for (SharePackageModel sharePackage : packages) {
            if (temp.get(sharePackage.getSharePackage()).isInstall(mContext)) {
                pool.put(sharePackage.getSharePackage(), temp.get(sharePackage.getSharePackage()));
            }
        }
        mSharePackageManagers = pool;
    }

    public void setInterceptorDialogCallback(IShowDialogCallback callback) {
        if (!CollectionUtil.isEmpty(mInterceptors)) {
            for (AbstractInterceptor interceptor : mInterceptors) {
                interceptor.setShowDialogCallback(callback);
            }
        }
        if (!CollectionUtil.isEmpty(mImageInterceptors)) {
            for (AbstractInterceptor interceptor : mImageInterceptors) {
                interceptor.setShowDialogCallback(callback);
            }
        }
    }

    public void reset() {
        mInterceptors.clear();
        mImageInterceptors.clear();
        initPackageManager();
    }

    public void addInterceptor(AbstractInterceptor interceptor) {
        mInterceptors.add(interceptor);
    }

    public void addImageInterceptor(AbstractInterceptor interceptor) {
        mImageInterceptors.add(interceptor);
    }

    public void setCurrentInterceptor(AbstractInterceptor interceptor) {
        mCurrentInterceptor = interceptor;
    }

    public void registerShareCallback(IShareCallbackListener callback) {
        ShareCallbackManager.getInstance().registerShareCallback(callback);
    }

    public void unregisterShareCallback(IShareCallbackListener callback) {
        ShareCallbackManager.getInstance().unRegisterShareCallback(callback);
    }

    public void share(Context context, ShareModel mShareModel, SharePackageFactory.SharePackage mSharePackage, String mEntryType) {
        ShareActivity.launch(context, mShareModel, mEntryType, mSharePackage);
    }

    /**
     * 如果是自定义菜单，则需要进行拦截。拦截的意思是不走责任链流程。
     * @param sharePackage
     * @return
     */
    public boolean shouldIntercept(SharePackageFactory.SharePackage sharePackage) {
        return sharePackage == SharePackageFactory.SharePackage.MENU1 ||
                sharePackage == SharePackageFactory.SharePackage.MENU2 ||
                sharePackage == SharePackageFactory.SharePackage.MENU3 ||
                sharePackage == SharePackageFactory.SharePackage.MENU4 ||
                sharePackage == SharePackageFactory.SharePackage.MENU5 ||
                sharePackage == SharePackageFactory.SharePackage.MENU6;
    }

    public void intercept(Context context, ShareModel shareModel, SharePackageFactory.SharePackage sharePackage, CommonListener<String> listener) {
        ISharePackageManager manager = mSharePackageManagers.get(sharePackage);
        ShareCallbackManager.getInstance().clearShareCallbacks();
        if (manager != null) {
            manager.shareTo(context, shareModel, listener);
        }
    }

    @Override
    public void shareTo(final Context context, final ShareModel shareModel, final SharePackageFactory.SharePackage sharePackage, final CommonListener<String> listener) {
        mCurSharePackageManager = mSharePackageManagers.get(sharePackage);
        if (mCurSharePackageManager != null) {
            AbstractInterceptor head = new HeadInterceptor();
            AbstractInterceptor pre = head;
            if(!CollectionUtil.isEmpty(mInterceptors)) {
                for (AbstractInterceptor interceptor : mInterceptors) {
                    pre.setNext(interceptor);
                    pre = interceptor;
                }
            }

            if(mCurSharePackageManager.supportImage()) {
                if(!CollectionUtil.isEmpty(mImageInterceptors)) {
                    for (AbstractInterceptor interceptor : mImageInterceptors) {
                        pre.setNext(interceptor);
                        pre = interceptor;
                    }
                }
            }
            RealShareInterceptor realShare = new RealShareInterceptor(context, mCurSharePackageManager, listener);
            pre.setNext(realShare);

            head.doCurrent(shareModel, sharePackage);
        } else {
            String toastContent = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_share_no_app");
            String name = sharePackage.name();
            switch (sharePackage) {
                case FACEBOOK:
                    name = "Facebook";
                    break;
                case WHATS_APP:
                    name = "Whatsapp";
                    break;
                case INSTAGTRAM:
                    name = "Instagram";
                    break;
            }
            toastContent = String.format(toastContent, name);
            Toast.makeText(context, toastContent, Toast.LENGTH_SHORT).show();
            ((ShareActivity)context).finish();
        }
    }

    public void sharePackage(Context context, final ShareModel shareModel, final CommonListener<String> listener) {
        if (!SharePackageFactory.getSharePackage(SharePackageFactory.SharePackage.WHATS_APP).isInstall(context)) {
            String toastMsg = String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_share_no_app"), "Whatsapp");
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
            ((ShareActivity)context).finish();
            return;
        }
        mCurSharePackageManager = SharePackageFactory.getSharePackage(SharePackageFactory.SharePackage.WHATS_APP);
        AbstractInterceptor head = new HeadInterceptor();
        AbstractInterceptor shareApk = new ShareApkInterceptor(context);
        AbstractInterceptor realShare = new RealShareInterceptor(context, mCurSharePackageManager, listener);
        head.setNext(shareApk);
        shareApk.setNext(realShare);
        head.doCurrent(shareModel, SharePackageFactory.SharePackage.WHATS_APP);
    }

    public void cancel() {
        if(mCurrentInterceptor != null) {
            mCurrentInterceptor.cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (mCurSharePackageManager != null) {
            mCurSharePackageManager.onActivityResult(requestCode, responseCode, intent);
        }
    }

    @Override
    public void onDestroy() {
        if (mCurSharePackageManager != null) {
            mCurSharePackageManager.onDestroy();
        }
        ShareCallbackManager.getInstance().clearShareCallbacks();
    }


    public static void clipboard(final Context context, final ShareModel model, final String fix) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String shareContent = model.getContent();
                if(TextUtils.isEmpty(shareContent)) {
                    shareContent = model.getTitle() + "\t" + model.getH5Link();
                }
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(fix, shareContent);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                }
            }
        });
    }

}
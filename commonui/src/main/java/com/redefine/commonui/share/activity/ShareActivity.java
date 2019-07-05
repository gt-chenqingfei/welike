package com.redefine.commonui.share.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.ShareCallbackManager;
import com.redefine.commonui.share.ShareDelegate;
import com.redefine.commonui.share.ShareManager;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.adapter.ShareGridAdapter;
import com.redefine.commonui.share.interceptor.IShowDialogCallback;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.commonui.widget.ProgressLoadingDlg;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * 分享页
 */
public class ShareActivity extends BaseActivity {

    private static final String SHARE_MODEL = "share_model";
    private static final String ENTRY_TYPE = "entry_type";
    private static final String PLAT_FORM = "platform";
    private LoadingView mLoadingView;
    private ProgressLoadingDlg mLoadingDlg;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        overridePendingTransition(R.anim.fade_in, 0);

        mLoadingView = findViewById(R.id.common_loading_view);
        final View shareContainer = findViewById(R.id.share_container);
        RelativeLayout rl = findViewById(R.id.share_activity);
        GridView gv = findViewById(R.id.share_gv);

        final ShareManager shareManager = ShareManager.getInstance();

        final ShareModel shareModel = getIntent().getParcelableExtra(SHARE_MODEL);
        final SharePackageFactory.SharePackage sharePackage = (SharePackageFactory.SharePackage) getIntent().getSerializableExtra(PLAT_FORM);
        if(shareModel == null) {
            finish();
            return;
        }

        shareManager.setInterceptorDialogCallback(showDialogCallback);

        if(sharePackage != null && shareManager.isInstall(this, sharePackage)) {
            shareContainer.setVisibility(View.GONE);
            if (!shareManager.shouldIntercept(sharePackage)) {
                ShareDelegate.INSTANCE.shareTo(sharePackage, shareModel.getShareModelType());
            }
            share(shareManager, sharePackage, shareModel);
            return;
        }

        shareContainer.setVisibility(View.VISIBLE);
        ShareDelegate.INSTANCE.shareActivityShow(shareModel.getShareModelType());

        gv.setAdapter(new ShareGridAdapter(this, shareManager.getPackages(), new CommonListener<SharePackageFactory.SharePackage>() {
            @Override
            public void onFinish(final SharePackageFactory.SharePackage sp) {
                if (sp == SharePackageFactory.SharePackage.EMPTY) {
                    return;
                }
                if (sp == SharePackageFactory.SharePackage.SHARE_APK) {
                    shareApk(shareModel, SharePackageFactory.SharePackage.SHARE_APK);
                    return;
                }
                if (shareManager.shouldIntercept(sp)) {
                    finish();
                    shareManager.intercept(ShareActivity.this, shareModel, sp, null);
                    return;
                }
                ShareDelegate.INSTANCE.shareTo(sp, shareModel.getShareModelType());
                share(shareManager, sp, shareModel);
            }

            @Override
            public void onError(SharePackageFactory.SharePackage value) {
                ShareCallbackManager.getInstance().notifyFail(value);
            }
        }));

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCallbackManager.getInstance().notifyCancel();
                finish();
            }
        });
        findViewById(R.id.share_apk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApk(shareModel, SharePackageFactory.SharePackage.SHARE_APK_LONG);
            }
        });
    }

    private void share(ShareManager shareManager, final SharePackageFactory.SharePackage sp, ShareModel shareModel) {
        shareManager.shareTo(ShareActivity.this, shareModel, sp, new CommonListener<String>() {
            @Override
            public void onFinish(String value) {
                ShareCallbackManager.getInstance().notifySuccess(sp);
                finish();
            }

            @Override
            public void onError(String value) {
                ShareCallbackManager.getInstance().notifyFail(sp);
                finish();
            }
        });
    }

    private void shareApk(ShareModel shareModel, final SharePackageFactory.SharePackage sharePackage) {
        showLoadingDlg();
        ShareManager.getInstance().sharePackage(ShareActivity.this, shareModel, new CommonListener<String>() {
            @Override
            public void onFinish(String value) {
                ShareCallbackManager.getInstance().notifySuccess(sharePackage);
                dismissLoadingDlg();
                finish();
            }

            @Override
            public void onError(String value) {
                ShareCallbackManager.getInstance().notifyFail(sharePackage);
                dismissLoadingDlg();
                finish();
            }
        });
    }

    private void showLoadingDlg() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void dismissLoadingDlg() {
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShareManager.getInstance().cancel();
    }

    public static void launch(Context context, ShareModel shareModel, String entryType, SharePackageFactory.SharePackage platform) {
        Intent intent = new Intent();
        intent.setClass(context, ShareActivity.class);
        intent.putExtra(SHARE_MODEL, shareModel);
        intent.putExtra(ENTRY_TYPE, entryType);
        intent.putExtra(PLAT_FORM, platform);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareManager.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        ShareManager.getInstance().setInterceptorDialogCallback(null);
        ShareManager.getInstance().onDestroy();
        this.overridePendingTransition(0, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    private IShowDialogCallback showDialogCallback = new IShowDialogCallback() {
        @Override
        public void showDialog() {
            if (!isFinishing()) {
                if (mLoadingDlg == null) {
                    mLoadingDlg = new ProgressLoadingDlg(ShareActivity.this, getResources().getString(R.string.downloading));
                }
                mLoadingDlg.show();
                mLoadingDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            onBackPressed();
                        }
                        return false;
                    }
                });
            }
        }

        @Override
        public void dismissDialog() {
            if (mLoadingDlg != null) {
                mLoadingDlg.dismiss();
            }
        }

        @Override
        public void updateProgress(int progress) {
            if (mLoadingDlg != null) {
                mLoadingDlg.updateProgress(progress);
            }
        }
    };

}

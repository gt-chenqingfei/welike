package com.redefine.multimedia.photoselector.activity;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.picturelooker.config.PictureConfig;
import com.redefine.multimedia.picturelooker.dialog.CustomDialog;
import com.redefine.multimedia.picturelooker.dialog.PictureDialog;
import com.redefine.multimedia.picturelooker.listener.OnCallBackActivity;
import com.redefine.multimedia.picturelooker.listener.OnImageLongClickListener;
import com.redefine.multimedia.picturelooker.listener.OnImageLookedChangedListener;
import com.redefine.multimedia.picturelooker.manager.PhotoPreviewViewModel;
import com.redefine.multimedia.picturelooker.widget.PicturePreView;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.common.WindowUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liwenbo on 2018/2/26.
 */

public class PhotoPreviewActivity extends BaseActivity implements OnCallBackActivity, OnImageLookedChangedListener, OnImageLongClickListener, EasyPermissions.PermissionCallbacks {


    private PicturePreView pictruePreView;

    private ArrayList<Item> items;

    private PhotoPreviewViewModel viewModel;
    private boolean mIsSavePhoto = true;
    private CustomDialog customDialog;
    private PictureDialog dialog;

    public static void launcher(Activity context, Bundle bundle) {

        Intent intent = new Intent();

        intent.setClass(context, PhotoPreviewActivity.class);

        intent.putExtras(bundle);

        context.startActivity(intent);

        context.overridePendingTransition(R.anim.preview_in, 0);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_pick_preview);
        WindowUtil.INSTANCE.setFullScreen(this);
        viewModel = ViewModelProviders.of(this).get(PhotoPreviewViewModel.class);
        items = getIntent().getParcelableArrayListExtra(PictureConfig.EXTRA_PREVIEW_LIST);

        viewModel.setParams(this, getIntent().getStringExtra(PictureConfig.EXTRA_NICK_NAME));

        mIsSavePhoto = getIntent().getBooleanExtra(PictureConfig.EXTRA_ENABLE_SAVE_PHOTO, true);
        if (items == null) finish();

        int position = getIntent().getExtras().getInt(PictureConfig.EXTRA_POSITION, 0);

        pictruePreView = findViewById(R.id.picturePreView);

        pictruePreView.bindData(items, position, this);

        pictruePreView.setOnchangedListener(this);

        pictruePreView.setOnLongClickListener(this);

        viewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) return;
                switch (pageStatusEnum) {
                    case CONTENT:
                        dismissDialog();
                        break;
                    case LOADING:
                        showPleaseDialog();
                        break;
                }
            }
        });
        AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_PICTUREOPEN_NUMBER);
    }

    @Override
    public void onActivityBackPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        viewModel.download();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        viewModel.showToast(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"));
    }

    protected void closeActivity() {
        finish();
        overridePendingTransition(0, R.anim.preview_out);
    }

    @Override
    public void onLookedChanged(int pos, @NotNull Item item) {
        Log.e("hl", "onLookedChanged: " + pos);
    }

    @Override
    public void onLongClick(int position, @NotNull Item obj) {
        if (!mIsSavePhoto) {
            return;
        }
        showDownLoadDialog();
        viewModel.setOldItem(obj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (customDialog != null) customDialog.cancel();
        if (dialog != null) dialog.cancel();

    }

    public void showDownLoadDialog() {
        if (customDialog == null)
            customDialog = new CustomDialog(this,
                    ScreenUtils.getSreenWidth(this) * 3 / 4,
                    ScreenUtils.getScreenHeight(this) / 4,
                    R.layout.picture_wind_base_dialog_xml, R.style.Theme_dialog);
        Button btn_cancel = customDialog.findViewById(R.id.btn_cancel);
        Button btn_commit = customDialog.findViewById(R.id.btn_commit);
        TextView tv_title = customDialog.findViewById(R.id.tv_title);
        TextView tv_content = customDialog.findViewById(R.id.tv_content);
        btn_commit.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_confirm"));
        btn_cancel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_cancel"));
        tv_title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_prompt"));
        tv_content.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_prompt_content"));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (EasyPermissions.hasPermissions(PhotoPreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    viewModel.download();
                } else {
                    EasyPermissions.requestPermissions(PhotoPreviewActivity.this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "sd_write_permission")
                            , PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                customDialog.dismiss();
            }
        });
        if (!isFinishing())
            customDialog.show();
    }


    /**
     * loading dialog
     */
    private void showPleaseDialog() {
        {
            dialog = new PictureDialog(this);
            if (!isFinishing())
                dialog.show();
        }
    }


    /**
     * dismiss dialog
     */
    private void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




}

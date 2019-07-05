package com.redefine.welike.business.user.ui.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.picturelooker.dialog.CustomDialog;
import com.redefine.multimedia.picturelooker.dialog.PictureDialog;
import com.redefine.multimedia.picturelooker.listener.OnCallBackActivity;
import com.redefine.multimedia.picturelooker.listener.OnImageLongClickListener;
import com.redefine.multimedia.picturelooker.listener.OnImageLookedChangedListener;
import com.redefine.multimedia.picturelooker.manager.PhotoPreviewViewModel;
import com.redefine.multimedia.picturelooker.widget.PicturePreView;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.user.management.ProfilePhotoDataSource;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.business.videoplayer.management.bean.ImageAttachment;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by nianguowang on 2018/10/16
 */
@Route(path = RouteConstant.PROFILE_PHOTO_PREVIEW_ROUTE_PATH)
public class ProfilePhotoPreviewActivity extends BaseActivity implements OnCallBackActivity, EasyPermissions.PermissionCallbacks {

    private PicturePreView pictruePreView;
    private View mTitleBack;
    private View mTitleShare;
    private View mTitleContainer;
    private RichTextView mPostContent;

    private int mInitSelectPosition;
    private PictureDialog dialog;
    private PhotoPreviewViewModel viewModel;
    private ImageAttachment mCurrentImage;
    private List<ImageAttachment> mAttachmentList = new ArrayList<>();
    private List<Item> mItems = new ArrayList<>();
    private int mChangeCount = 0;

    public static void show(List<AttachmentBase> attachmentBases, int position) {
        Bundle bundle = new Bundle();

        bundle.putInt(UserConstant.PROFILE_PHOTO_POSITION, position);
        //        bundle.putSerializable(UserConstant.PROFILE_PHOTO_LIST, (Serializable) attachmentBases);
        ProfilePhotoDataSource.getInstance().setData(attachmentBases);

        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_PROFILE_PHOTO_PREVIEW, bundle));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_preview);

        parseBundle();
        initViewModel();
        initView();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(PhotoPreviewViewModel.class);
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
        viewModel.setParams(this, mCurrentImage != null ? mCurrentImage.getNickName() : "");
    }

    private void parseBundle() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        mInitSelectPosition = intent.getIntExtra(UserConstant.PROFILE_PHOTO_POSITION, 0);
        List<AttachmentBase> photoList = ProfilePhotoDataSource.getInstance().getData();


        if (CollectionUtil.isEmpty(photoList) || mInitSelectPosition >= photoList.size()) {
            finish();
            return;
        }

        for (AttachmentBase attachmentBase : photoList) {
            if (attachmentBase instanceof ImageAttachment) {
                ImageAttachment imageAttachment = (ImageAttachment) attachmentBase;
                mAttachmentList.add(imageAttachment);
                Item item = new Item(Uri.parse(imageAttachment.getImageUrl()), imageAttachment.getImageUrl(), MimeType.JPEG.toString(), 0, 0, imageAttachment.getImageWidth(), imageAttachment.getImageHeight());
                mItems.add(item);
            }
        }
        mCurrentImage = mAttachmentList.get(mInitSelectPosition);
    }

    private void initView() {
        mTitleBack = findViewById(R.id.profile_photo_title_back);
        mTitleShare = findViewById(R.id.profile_photo_title_share);
        mTitleContainer = findViewById(R.id.profile_photo_title_container);
        mPostContent = findViewById(R.id.profile_photo_post_content);
        pictruePreView = findViewById(com.redefine.multimedia.R.id.picturePreView);
        mTitleBack.setOnClickListener(mOnClickListener);
        mTitleShare.setOnClickListener(mOnClickListener);
        mPostContent.setOnClickListener(mOnClickListener);

        pictruePreView.setOnchangedListener(new OnImageLookedChangedListener() {
            @Override
            public void onLookedChanged(int pos, @NotNull Item item) {
                mCurrentImage = mAttachmentList.get(pos);
                setPostContent();
                if (mChangeCount < 2) {
                    mChangeCount++;
                } else {
                    EventLog.Profile.report16();
                }

            }
        });
        pictruePreView.setOnLongClickListener(new OnImageLongClickListener() {
            @Override
            public void onLongClick(int position, @NotNull Item obj) {
                showDownLoadDialog();
                viewModel.setOldItem(obj);
            }
        });
        pictruePreView.bindData((ArrayList<Item>) mItems, mInitSelectPosition, this);
    }

    private void setPostContent() {
        mPostContent.getRichProcessor().setRichContent(mCurrentImage.getSummary(), null);
    }

    private void doShare() {
        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, PostType.IMAGE, null, EventLog1.Share.ShareFrom.PHOTO_LIST,
                EventLog1.Share.PopPage.OTHER, null, null, null, mCurrentImage.getPid(), null, null,
                mCurrentImage.getUid(), null, null, null, null, null);
        ShareHelper.shareImageAttachment(this, mCurrentImage, eventModel);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mTitleBack) {
                finish();
            } else if (v == mTitleShare) {
                doShare();
            } else if (v == mPostContent) {
                Bundle bundle = new Bundle();
                bundle.putString(FeedConstant.KEY_FEED_ID, mCurrentImage.getPid());
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
                EventLog.Profile.report13();
            }
        }
    };

    /**
     * On image click, this method will be called!
     */
    @Override
    public void onActivityBackPressed() {
        int visibility = mTitleContainer.getVisibility();
        if (visibility == View.VISIBLE) {
            mTitleContainer.setVisibility(View.INVISIBLE);
            mPostContent.setVisibility(View.INVISIBLE);
        } else {
            mTitleContainer.setVisibility(View.VISIBLE);
            mPostContent.setVisibility(View.VISIBLE);
        }
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
        viewModel.showToast(getString(R.string.common_permissions_denied));
    }

    private void showDownLoadDialog() {
        final CustomDialog customDialog = new CustomDialog(this,
                ScreenUtils.getSreenWidth(this) * 3 / 4,
                ScreenUtils.getScreenHeight(this) / 4,
                com.redefine.multimedia.R.layout.picture_wind_base_dialog_xml, com.redefine.multimedia.R.style.Theme_dialog);
        Button btn_cancel = customDialog.findViewById(com.redefine.multimedia.R.id.btn_cancel);
        Button btn_commit = customDialog.findViewById(com.redefine.multimedia.R.id.btn_commit);
        TextView tv_title = customDialog.findViewById(com.redefine.multimedia.R.id.tv_title);
        TextView tv_content = customDialog.findViewById(com.redefine.multimedia.R.id.tv_content);
        btn_commit.setText(getString(R.string.picture_confirm));
        btn_cancel.setText(getString(R.string.picture_cancel));
        tv_title.setText(getString(R.string.picture_prompt));
        tv_content.setText(getString(R.string.picture_prompt_content));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (EasyPermissions.hasPermissions(ProfilePhotoPreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    viewModel.download();
                } else {
                    EasyPermissions.requestPermissions(ProfilePhotoPreviewActivity.this, getString(R.string.sd_write_permission)
                            , PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                customDialog.dismiss();
            }
        });
        if (!isFinishing())
            customDialog.show();
    }

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

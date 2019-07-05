package com.redefine.welike.commonui.share.outshare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BadParcelableException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.util.FileUtil;
import com.redefine.welike.business.publisher.management.bean.DraftPicAttachment;
import com.redefine.welike.business.publisher.management.bean.DraftVideoAttachment;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.commonui.activity.SchemeFilterActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by nianguowang on 2018/11/22
 */
public class OutShareDelegate {

    private static final long IMAGE_SIZE_LIMIT = 5 * 1024 * 1024;
    private static final long VIDEO_SIZE_LIMIT = 25 * 1024 * 1024;
    private static final long VIDEO_DURATION_LIMIT = 5 * 60 * 1000;

    private static final String KEY_TYPE = "type";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_S = "s";

    private String type;
    private String action;
    private String content;
    private Uri uri;
    private List<Uri> uris;
    private Map<String, String> mShareSourceMap = new HashMap<>();
    private Activity mActivity;

    private FinishActivityCallback mFinishActivityCallback;
    private RequestPermissionCallback mRequestPermissionCallback;

    public interface FinishActivityCallback {
        void finishActivity(int resultCode, Intent data);
    }

    public interface RequestPermissionCallback {
        void onRequestPermission(String message, int requestCode, String... permissions);
    }

    public OutShareDelegate(Activity activity) {
        this.mActivity = activity;
        initShareSourceMap();
    }

    public void init(Intent intent) {
        if (intent == null) {
            notifyFinishActivity(Activity.RESULT_CANCELED, null);
            return;
        }
        parseBundle(intent);
        if (checkData()) {
            filterContent();
            handlePublish();
        }
    }

    private void parseBundle(Intent intent) {
        type = intent.getType();
        action = intent.getAction();
        content = intent.getStringExtra(Intent.EXTRA_TEXT);
        uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    private boolean checkData() {
        if (type == null) {
            notifyFinishActivity(Activity.RESULT_CANCELED, null);
            return false;
        }
        if (type.startsWith("text")) {
            if (!TextUtils.isEmpty(content)) {
                return true;
            }
            notifyFinishActivity(Activity.RESULT_CANCELED, null);
            return false;
        } else if (type.startsWith("image")) {
            if (EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (TextUtils.equals(action, Intent.ACTION_SEND_MULTIPLE)) {
                    if (CollectionUtil.isEmpty(uris)) {
                        notifyFinishActivity(Activity.RESULT_CANCELED, null);
                        return false;
                    } else {
                        List<Uri> temp = new ArrayList<>();
                        for (Uri uri : uris) {
                            try {
                                if (checkImage(uri)) {
                                    temp.add(uri);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        if (CollectionUtil.isEmpty(temp)) {
                            notifyFinishActivity(Activity.RESULT_CANCELED, null);
                            return false;
                        }
                        uris.clear();
                        uris.addAll(temp);
                        return true;
                    }
                } else if (TextUtils.equals(action, Intent.ACTION_SEND)) {
                    try {
                        if (checkImage(uri)) {
                            return true;
                        }
                        notifyFinishActivity(Activity.RESULT_CANCELED, null);
                        return false;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    notifyFinishActivity(Activity.RESULT_CANCELED, null);
                    return false;
                } else {
                    notifyFinishActivity(Activity.RESULT_CANCELED, null);
                    return false;
                }
            } else {
                notifyRequestPermission(mActivity.getString(R.string.sd_write_permission), PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                return false;
            }
        } else if (type.startsWith("video")) {
            if (EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                try {
                    if (checkVideo(uri)) {
                        return true;
                    }
                    notifyFinishActivity(Activity.RESULT_CANCELED, null);
                    return false;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                notifyFinishActivity(Activity.RESULT_CANCELED, null);
                return false;
            } else {
                notifyRequestPermission(mActivity.getString(R.string.sd_write_permission), PermissionRequestCode.IMAGE_PICK_PERMISSION_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                return false;
            }
        } else {
            notifyFinishActivity(Activity.RESULT_CANCELED, null);
            return false;
        }
    }

    private boolean checkImage(Uri uri) throws FileNotFoundException {
        if (uri == null) {
            return false;
        }
        if (URLUtil.isHttpsUrl(uri.toString()) || URLUtil.isHttpUrl(uri.toString())) {
            return false;
        }
        AssetFileDescriptor assetFileDescriptor = mActivity.getContentResolver().openAssetFileDescriptor(uri, "r");
        if (assetFileDescriptor == null) {
            return false;
        }
        long length = assetFileDescriptor.getLength();
        return length <= IMAGE_SIZE_LIMIT;
    }

    private boolean checkVideo(Uri uri) throws FileNotFoundException {
        if (uri == null) {
            return false;
        }
        if (URLUtil.isHttpsUrl(uri.toString()) || URLUtil.isHttpUrl(uri.toString())) {
            return false;
        }
        AssetFileDescriptor assetFileDescriptor = mActivity.getContentResolver().openAssetFileDescriptor(uri, "r");
        if (assetFileDescriptor == null) {
            return false;
        }
        long length = assetFileDescriptor.getLength();
        return length <= VIDEO_SIZE_LIMIT;
    }

    private void filterContent() {
        if (!TextUtils.isEmpty(content)) {
            content = content.replace("@", "");
        }
    }


    private void handlePublish() {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null &&
                account.getCompleteLevel() >= Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE &&
                account.isLogin()) {

            if (type.startsWith("text")) {
                doTextType();
            } else if (type.startsWith("image")) {
                doImageType();
            } else if (type.startsWith("video")) {
                doVideoType();
            }
        } else {
            goLogin();
            notifyFinishActivity(Activity.RESULT_CANCELED, null);
        }
    }

    private void doVideoType() {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = FileUtil.from(mActivity, uri);
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mActivity, uri);
                    player.prepare();

                    DraftVideoAttachment video = new DraftVideoAttachment(file.getAbsolutePath());
                    video.setWidth(player.getVideoWidth());
                    video.setHeight(player.getVideoHeight());
                    video.setDuration(player.getDuration());

                    String from = "";
                    if (getReferrer() != null && !TextUtils.isEmpty(getReferrer().getHost())) {
                        from = mShareSourceMap.get(getReferrer().getHost());
                    }

                    PublishPostStarter.INSTANCE.startActivityWithVideo(mActivity, content, video, from);
                    notifyFinishActivity(Activity.RESULT_OK, null);
                } catch (IOException e) {
                    notifyFinishActivity(Activity.RESULT_CANCELED, null);
                }
            }
        });
    }

    private void doImageType() {
        if (TextUtils.equals(action, Intent.ACTION_SEND_MULTIPLE)) {
            doRealImageType(uris);
        } else if (TextUtils.equals(action, Intent.ACTION_SEND)) {
            List<Uri> uris = new ArrayList<>();
            uris.add(uri);
            doRealImageType(uris);
        }
    }

    private void doRealImageType(final List<Uri> uris) {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                List<DraftPicAttachment> picDraftList = new ArrayList<>();
                int size = Math.min(9, CollectionUtil.getCount(uris));
                for (int i = 0; i < size; i++) {
                    Uri uri = uris.get(i);
                    try {
                        File file = FileUtil.from(mActivity, uri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                        DraftPicAttachment picAttachmentDraft = new DraftPicAttachment(file.getAbsolutePath());
                        picAttachmentDraft.setMimeType(MimeType.JPEG.toString());
                        picAttachmentDraft.setWidth(options.outWidth);
                        picAttachmentDraft.setHeight(options.outHeight);
                        picDraftList.add(picAttachmentDraft);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String from = "";
                if (getReferrer() != null && !TextUtils.isEmpty(getReferrer().getHost())) {
                    from = mShareSourceMap.get(getReferrer().getHost());
                }

                PublishPostStarter.INSTANCE.startActivityWithImage(mActivity, content, picDraftList, from);
                notifyFinishActivity(Activity.RESULT_OK, null);
            }
        });
    }

    private void doTextType() {
        try {
            String path = "welike://com.redefine.welike/main/home?page_name=publish&data=" + encodeText(content);
            Intent jumpIntent = new Intent(mActivity, SchemeFilterActivity.class);
            jumpIntent.setData(Uri.parse(path));
            mActivity.startActivity(jumpIntent);
            notifyFinishActivity(Activity.RESULT_OK, null);
        } catch (Exception e) {
            e.printStackTrace();
            notifyFinishActivity(Activity.RESULT_CANCELED, null);
        }
    }

    private String encodeText(String text) {
        String encodedContent;
        if (text != null) {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_TYPE, 1);
            jsonObject.put(KEY_CONTENT, text + " ");
            jsonObject.put(KEY_S, 1);
            jsonArray.add(jsonObject);
            encodedContent = URLEncoder.encode(jsonArray.toJSONString());
        } else {
            encodedContent = "";
        }
        return encodedContent;
    }

    private void goLogin() {
        ARouter.getInstance().build(RouteConstant.PATH_START_REGISTER_PAGE).navigation();
    }

    public void setFinishActivityCallback(FinishActivityCallback callback) {
        this.mFinishActivityCallback = callback;
    }

    public void setRequestPermissionCallback(RequestPermissionCallback callback) {
        this.mRequestPermissionCallback = callback;
    }

    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (checkData()) {
            handlePublish();
        }
    }

    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        notifyFinishActivity(Activity.RESULT_CANCELED, null);
    }

    private void notifyFinishActivity(int resultCode, Intent data) {
        if (mFinishActivityCallback != null) {
            mFinishActivityCallback.finishActivity(resultCode, data);
        }
    }

    private void notifyRequestPermission(String message, int requestCode, String... permissions) {
        if (mRequestPermissionCallback != null) {
            mRequestPermissionCallback.onRequestPermission(message, requestCode, permissions);
        }
    }


    private Uri getReferrer() {
        Intent intent = mActivity.getIntent();
        try {
            Uri referrer = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
            if (referrer != null) {
                return referrer;
            }
            String referrerName = intent.getStringExtra(Intent.EXTRA_REFERRER_NAME);
            if (referrerName != null) {
                return Uri.parse(referrerName);
            }
        } catch (BadParcelableException e) {
            Log.w("OutShareDelegate", "Cannot read referrer from intent;"
                    + " intent extras contain unknown custom Parcelable objects");
        }

        String mReferrer = reflectGetReferrer();
        if (mReferrer != null) {
            return new Uri.Builder().scheme("android-app").authority(mReferrer).build();
        }
        return null;
    }


    private String reflectGetReferrer() {
        try {
            Class activityClass = Class.forName("android.app.Activity");
            Field refererField = activityClass.getDeclaredField("mReferrer");
            if (refererField == null) {
                return null;
            }
            refererField.setAccessible(true);
            if (refererField.get(mActivity) != null) {
                return (String) refererField.get(mActivity);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initShareSourceMap() {
        mShareSourceMap.put("video.like", "Like");
        mShareSourceMap.put("com.ss.android.ugc.boom", "Vigo");
    }

}

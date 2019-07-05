package com.redefine.welike.business.user.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.ProfilePhotoRequest;
import com.redefine.welike.business.browse.management.request.ProfilePhotoRequest1;
import com.redefine.welike.business.browse.management.request.ProfileVideoRequest1;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.business.videoplayer.management.parser.AttachmentParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/9/29
 */
public class ProfilePhotoViewModel extends AndroidViewModel {

    private boolean mAuth;
    private String mUid;
    private String mCursor;
    private BaseRequest mRequest;

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<PageLoadMoreStatusEnum> mLoadMoreStatus = new MutableLiveData<>();
    private MutableLiveData<List<AttachmentBase>> mAttachmentsLiveData = new MutableLiveData<>();

    public ProfilePhotoViewModel(@NonNull Application application) {
        super(application);
        mAuth = AccountManager.getInstance().isLogin();
    }

    public LiveData<PageLoadMoreStatusEnum> getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public LiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public LiveData<List<AttachmentBase>> getAttachmentsLiveData() {
        return mAttachmentsLiveData;
    }

    public void autoRefresh(String uid) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        mUid = uid;
        refresh();
    }

    public void refresh() {
        if (mRequest != null) {
            return;
        }
        mCursor = null;
        if (mAuth) {
            mRequest = new ProfilePhotoRequest(mUid, mCursor, getApplication());
        } else {
            mRequest = new ProfilePhotoRequest1(mUid, mCursor, getApplication());
        }
        try {
            mRequest.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    mRequest = null;
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    mRequest = null;
                    mCursor = AttachmentParser.parseCursor(result);
                    ArrayList<AttachmentBase> attachments = AttachmentParser.parseAttachments(result);
                    if (CollectionUtil.isEmpty(attachments)) {
                        mPageStatus.postValue(PageStatusEnum.EMPTY);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.CONTENT);
                        List<AttachmentBase> value = mAttachmentsLiveData.getValue();
                        if (value == null) {
                            value = new ArrayList<>();
                        }
                        value.clear();
                        value.addAll(attachments);
                        mAttachmentsLiveData.postValue(value);
                    }
                    if (TextUtils.isEmpty(mCursor)) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                    } else {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mRequest = null;
            mPageStatus.postValue(PageStatusEnum.ERROR);
        }
    }

    public void loadMore() {
        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOADING);
        if (mAuth) {
            mRequest = new ProfilePhotoRequest(mUid, mCursor, getApplication());
        } else {
            mRequest = new ProfilePhotoRequest1(mUid, mCursor, getApplication());
        }
        try {
            mRequest.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    mRequest = null;
                    mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    mRequest = null;
                    mCursor = AttachmentParser.parseCursor(result);
                    ArrayList<AttachmentBase> attachments = AttachmentParser.parseAttachments(result);
                    if (TextUtils.isEmpty(mCursor)) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE);
                    } else {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH);
                    }
                    if (!CollectionUtil.isEmpty(attachments)) {
                        List<AttachmentBase> value = mAttachmentsLiveData.getValue();
                        if (value == null) {
                            value = new ArrayList<>();
                        }
                        value.addAll(attachments);
                        mAttachmentsLiveData.postValue(value);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mRequest = null;
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR);
        }
    }
}

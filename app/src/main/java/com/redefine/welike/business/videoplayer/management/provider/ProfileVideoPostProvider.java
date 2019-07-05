package com.redefine.welike.business.videoplayer.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.ProfileVideoRequest;
import com.redefine.welike.business.browse.management.request.ProfileVideoRequest1;
import com.redefine.welike.business.browse.management.request.SimilarVideoRequest;
import com.redefine.welike.business.browse.management.request.SimilarVideoRequest1;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.business.videoplayer.management.bean.VideoAttachment;
import com.redefine.welike.business.videoplayer.management.parser.AttachmentParser;

import java.util.ArrayList;

/**
 * Created by nianguowang on 2018/9/22
 */
public class ProfileVideoPostProvider implements IVideoPostsProvider {

    private boolean mAuth;
    private String mUid;
    private String mCursor;
    private BaseRequest mRequest;
    private SinglePostsProviderCallback mListener;

    @Override
    public void init(PostBase postBase, boolean auth) {
        mAuth = auth;
        mUid = postBase.getUid();
        mCursor = postBase.getTime() + ":" + postBase.getPid();
        tryRefreshPosts();
    }

    @Override
    public void tryRefreshPosts() {
        if (mAuth) {
            mRequest = new ProfileVideoRequest(mUid, mCursor, MyApplication.getAppContext());
        } else {
            mRequest = new ProfileVideoRequest1(mUid, mCursor, MyApplication.getAppContext());
        }
        try {
            mRequest.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    mRequest = null;
                    if (mListener != null) {
                        mListener.onRefreshPosts(null, 0, false, errCode);
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    mRequest = null;
                    mCursor = AttachmentParser.parseCursor(result);
                    ArrayList<PostBase> postBases = new ArrayList<>();
                    ArrayList<AttachmentBase> attachmentBases = AttachmentParser.parseAttachments(result);

                    if (!CollectionUtil.isEmpty(attachmentBases)) {
                        for (AttachmentBase attachmentBase : attachmentBases) {
                            if (attachmentBase instanceof VideoAttachment) {
                                VideoPost videoPost = VideoAttachment.toVideoPost((VideoAttachment) attachmentBase);
                                postBases.add(videoPost);
                            }
                        }
                    }

                    if (mListener != null) {
                        mListener.onRefreshPosts(postBases, CollectionUtil.getCount(postBases), TextUtils.isEmpty(mCursor), ErrorCode.ERROR_SUCCESS);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mRequest = null;
            if (mListener != null) {
                mListener.onRefreshPosts(null, 0, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void tryHisPosts() {
        if (mRequest != null) {
            return;
        }
        if (mAuth) {
            mRequest = new ProfileVideoRequest(mUid, mCursor, MyApplication.getAppContext());
        } else {
            mRequest = new ProfileVideoRequest1(mUid, mCursor, MyApplication.getAppContext());
        }
        try {
            mRequest.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    mRequest = null;
                    if (mListener != null) {
                        mListener.onReceiveHisPosts(null, false, errCode);
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    mRequest = null;
                    mCursor = AttachmentParser.parseCursor(result);
                    ArrayList<PostBase> postBases = new ArrayList<>();
                    ArrayList<AttachmentBase> attachmentBases = AttachmentParser.parseAttachments(result);

                    for (AttachmentBase attachmentBase : attachmentBases) {
                        if (attachmentBase instanceof VideoAttachment) {
                            VideoPost videoPost = VideoAttachment.toVideoPost((VideoAttachment) attachmentBase);
                            postBases.add(videoPost);
                        }
                    }
                    if (mListener != null) {
                        mListener.onReceiveHisPosts(postBases, TextUtils.isEmpty(mCursor), ErrorCode.ERROR_SUCCESS);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mRequest = null;
            if (mListener != null) {
                mListener.onReceiveHisPosts(null, true, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void setListener(SinglePostsProviderCallback callback) {
        this.mListener = callback;
    }
}

package com.redefine.welike.business.videoplayer.management.provider;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.bean.Interest;
import com.redefine.welike.business.browse.management.dao.InterestCallBack;
import com.redefine.welike.business.browse.management.dao.InterestEventStore;
import com.redefine.welike.business.browse.management.request.SimilarVideoRequest;
import com.redefine.welike.business.browse.management.request.SimilarVideoRequest1;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.management.provider.SinglePostsProviderCallback;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/9/22
 */
public class SimilarVideoPostsProvider implements IVideoPostsProvider {

    private String mPostId;
    private boolean mAuth;
    private String mCursor;
    private BaseRequest mRequest;
    private SinglePostsProviderCallback mListener;

    private ArrayList<String> interestIds = new ArrayList<>();

    public void init(PostBase postBase, boolean auth) {
        mAuth = auth;
        mPostId = postBase.getPid();

        loadInterest();
    }

    private void loadInterest() {
        InterestEventStore.INSTANCE.getInterestList(new InterestCallBack() {

            @Override
            public void onLoadEntity(@Nullable List<? extends Interest> interests) {
                if (!CollectionUtil.isEmpty(interests)) {
                    interestIds.clear();
                    for (Interest interest : interests) {
                        interestIds.add(interest.getId());
                    }
                }
                tryRefreshPosts();
            }
        });
    }

    @Override
    public void tryRefreshPosts() {
        if (mAuth) {
            mRequest = new SimilarVideoRequest(MyApplication.getAppContext(), mCursor, mPostId, interestIds);
        } else {
            mRequest = new SimilarVideoRequest1(MyApplication.getAppContext(), mCursor, mPostId, interestIds);
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
                    mCursor = PostsDataSourceParser.parseCursor(result);
                    ArrayList<PostBase> postBases = PostsDataSourceParser.parsePosts(result);
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
            mRequest = new SimilarVideoRequest(MyApplication.getAppContext(), mCursor, mPostId, interestIds);
        } else {
            mRequest = new SimilarVideoRequest1(MyApplication.getAppContext(), mCursor, mPostId, interestIds);
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
                    mCursor = PostsDataSourceParser.parseCursor(result);
                    ArrayList<PostBase> postBases = PostsDataSourceParser.parsePosts(result);
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

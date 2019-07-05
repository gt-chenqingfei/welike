package com.redefine.welike.business.feeds.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.SearchPostRequest;
import com.redefine.welike.business.browse.management.request.SearchPostRequest1;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liubin on 2018/2/12.
 */

public class SearchPostProvider implements RequestCallback {
    private BaseRequest searchPostRequest;
    private String keyword;
    private int pageNum;
    private SearchPostProviderCallback listener;

    public interface SearchPostProviderCallback {

        void onNewSearchPosts(final List<PostBase> posts, final int errCode);
        void onMoreSearchPosts(final List<PostBase> posts, final boolean last, final int errCode);

    }

    public void setListener(SearchPostProviderCallback listener) {
        this.listener = listener;
    }

    public void tryNewSearchPosts(String keyword, boolean auth) {
        if (searchPostRequest != null) {
            searchPostRequest.cancel();
            searchPostRequest = null;
        }

        pageNum = 0;
        this.keyword = keyword;
        if (!TextUtils.isEmpty(keyword)) {
            if (auth) {
                searchPostRequest = new SearchPostRequest(keyword, pageNum, MyApplication.getAppContext());
            } else {
                searchPostRequest = new SearchPostRequest1(keyword, pageNum, MyApplication.getAppContext());
            }
            try {
                searchPostRequest.req(this);
            } catch (Exception e) {
                e.printStackTrace();
                searchPostRequest = null;
                if (listener != null) {
                    listener.onNewSearchPosts(null, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (listener != null) {
                listener.onNewSearchPosts(null, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    public void tryMoreSearchPosts(boolean auth) {
        if (searchPostRequest != null) return;

        pageNum++;
        if (auth) {
            searchPostRequest = new SearchPostRequest(keyword, pageNum, MyApplication.getAppContext());
        } else {
            searchPostRequest = new SearchPostRequest1(keyword, pageNum, MyApplication.getAppContext());
        }
        try {
            searchPostRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            searchPostRequest = null;
            if (listener != null) {
                listener.onMoreSearchPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (searchPostRequest == request) {
            searchPostRequest = null;
            if (pageNum == 0) {
                if (listener != null) {
                    listener.onNewSearchPosts(null, errCode);
                }
            } else {
                if (listener != null) {
                    listener.onMoreSearchPosts(null, false, errCode);
                }
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (searchPostRequest == request) {
            searchPostRequest = null;
            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
            if (posts != null && posts.size() > 0) {
                Set<String> uids = new HashSet<>();
                for (PostBase post : posts) {
                    uids.add(post.getUid());
                    if (post instanceof ForwardPost) {
                        ForwardPost fpost = (ForwardPost)post;
                        PostBase rpost = fpost.getRootPost();
                        if (rpost != null && !fpost.isForwardDeleted()) {
                            uids.add(rpost.getUid());
                        }
                    }
                }
//                PostsDataSourceParser.updateFeedsFollowStatus(posts, uids);
            }

            if (pageNum == 0) {
                if (listener != null) {
                    listener.onNewSearchPosts(posts, ErrorCode.ERROR_SUCCESS);
                }
            } else {
                if (posts != null && posts.size() > 0) {
                    if (listener != null) {
                        listener.onMoreSearchPosts(posts, false, ErrorCode.ERROR_SUCCESS);
                    }
                } else {
                    if (listener != null) {
                        listener.onMoreSearchPosts(null, true, ErrorCode.ERROR_SUCCESS);
                    }
                }
            }
        }
    }

}

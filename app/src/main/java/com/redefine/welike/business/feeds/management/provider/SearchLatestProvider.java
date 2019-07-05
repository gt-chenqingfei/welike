package com.redefine.welike.business.feeds.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.SearchLatestRequest;
import com.redefine.welike.business.browse.management.request.SearchLatestRequest1;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liubin on 2018/3/3.
 */

public class SearchLatestProvider implements RequestCallback {
    private BaseRequest searchLatestRequest;
    private String keyword;
    private int pageNum;
    private SearchLatestProviderCallback listener;

    public interface SearchLatestProviderCallback {

        void onNewSearchLatest(final List<PostBase> posts, final List<User> users, final int errCode);
        void onMoreSearchLatest(final List<PostBase> posts, final boolean last, final int errCode);

    }

    public void setListener(SearchLatestProviderCallback listener) {
        this.listener = listener;
    }

    public void tryNewSearchLatest(String keyword, boolean auth) {
        if (searchLatestRequest != null) {
            searchLatestRequest.cancel();
            searchLatestRequest = null;
        }

        pageNum = 0;
        this.keyword = keyword;
        if (!TextUtils.isEmpty(keyword)) {
            if (auth) {
                searchLatestRequest = new SearchLatestRequest(keyword, pageNum, MyApplication.getAppContext());
            } else {
                searchLatestRequest = new SearchLatestRequest1(keyword, pageNum, MyApplication.getAppContext());
            }
            try {
                searchLatestRequest.req(this);
            } catch (Exception e) {
                e.printStackTrace();
                searchLatestRequest = null;
                if (listener != null) {
                    listener.onNewSearchLatest(null, null, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (listener != null) {
                listener.onNewSearchLatest(null, null, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    public void tryMoreSearchLatest(boolean auth) {
        if (searchLatestRequest != null) return;

        pageNum++;
        if (auth) {
            searchLatestRequest = new SearchLatestRequest(keyword, pageNum, MyApplication.getAppContext());
        } else {
            searchLatestRequest = new SearchLatestRequest1(keyword, pageNum, MyApplication.getAppContext());
        }
        try {
            searchLatestRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            searchLatestRequest = null;
            if (listener != null) {
                listener.onMoreSearchLatest(null, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (searchLatestRequest == request) {
            searchLatestRequest = null;
            if (pageNum == 0) {
                if (listener != null) {
                    listener.onNewSearchLatest(null, null, errCode);
                }
            } else {
                if (listener != null) {
                    listener.onMoreSearchLatest(null, false, errCode);
                }
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (searchLatestRequest == request) {
            searchLatestRequest = null;
            List<PostBase> posts = null;
            List<User> users = null;
            if (result != null) {
                JSONArray usersJSON = result.getJSONArray("users");
                JSONArray postsJSON = result.getJSONArray("posts");
                posts = PostsDataSourceParser.parsePostList(postsJSON);
                users = UserParser.parseToUsers(usersJSON, PostsDataSourceParser.parseSequenceId(result));
            }
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
                    listener.onNewSearchLatest(posts, users, ErrorCode.ERROR_SUCCESS);
                }
            } else {
                if (posts != null && posts.size() > 0) {
                    if (listener != null) {
                        listener.onMoreSearchLatest(posts, false, ErrorCode.ERROR_SUCCESS);
                    }
                } else {
                    if (listener != null) {
                        listener.onMoreSearchLatest(null, true, ErrorCode.ERROR_SUCCESS);
                    }
                }
            }
        }
    }

}

package com.redefine.welike.business.feeds.management.provider;

//import android.text.TextUtils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.redefine.foundation.utils.CommonHelper;
//import com.redefine.welike.MyApplication;
//import com.redefine.welike.base.ErrorCode;
//import com.redefine.welike.base.GlobalConfig;
//import com.redefine.welike.base.request.BaseRequest;
//import com.redefine.welike.business.feeds.management.request.FeedsRequest;
//import com.redefine.welike.base.request.RequestCallback;
//import com.redefine.welike.business.feeds.management.bean.PostBase;
//import com.redefine.welike.business.feeds.management.cache.HotCache;
//import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
//
//import java.util.List;

/**
 * Created by liubin on 2018/1/10.
 */

//public class HotPostsProvider implements ISinglePostsProvider, RequestCallback, HotCache.HotCacheCallback {
//    private FeedsRequest feedsRequest;
//    private String cursor;
//    private String cursorPid;
//    private List<PostBase> previousPageList;
//    private int actionState = GlobalConfig.LIST_ACTION_NONE;
//    private SinglePostsProviderCallback listener;
//
//    public HotPostsProvider() {}
//
//    @Override
//    public void tryRefreshPosts() {
//        if (feedsRequest != null) return;
//
//        actionState = GlobalConfig.LIST_ACTION_REFRESH;
//        cursor = null;
//        cursorPid = null;
//        previousPageList = null;
//        if (CommonHelper.isNetworkAvalible(MyApplication.getAppContext())) {
//            feedsRequest = new FeedsRequest(FeedsRequest.HOT_FEEDS, false, MyApplication.getAppContext());
//            try {
//                feedsRequest.tryFeeds(cursor, this);
//            } catch (Exception e) {
//                e.printStackTrace();
//                feedsRequest = null;
//                if (listener != null) {
//                    listener.onRefreshPosts(null, 0, ErrorCode.networkExceptionToErrCode(e));
//                }
//            }
//        } else {
//            List<PostBase> list = HotCache.getInstance().listNewPosts(GlobalConfig.POSTS_NUM_ONE_PAGE);
//            if (list != null && list.size() > 0) {
//                cursorPid = list.get(list.size() - 1).getPid();
//                if (listener != null) {
//                    listener.onRefreshPosts(list, 0, ErrorCode.ERROR_SUCCESS);
//                }
//            } else {
//                if (listener != null) {
//                    listener.onRefreshPosts(null, 0, ErrorCode.ERROR_SUCCESS);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void tryHisPosts() {
//        if (feedsRequest != null) return;
//
//        actionState = GlobalConfig.LIST_ACTION_HIS;
//        if (CommonHelper.isNetworkAvalible(MyApplication.getAppContext())) {
//            if (!TextUtils.isEmpty(cursor)) {
//                feedsRequest = new FeedsRequest(FeedsRequest.HOT_FEEDS, false, MyApplication.getAppContext());
//                try {
//                    feedsRequest.tryFeeds(cursor, this);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    feedsRequest = null;
//                    if (listener != null) {
//                        listener.onReceiveHisPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
//                    }
//                }
//            } else {
//                if (listener != null) {
//                    listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
//                }
//            }
//        } else {
//            List<PostBase> hisList = HotCache.getInstance().listMoreByPid(cursorPid, GlobalConfig.POSTS_NUM_ONE_PAGE);
//            if (hisList != null && hisList.size() > 0) {
//                cursorPid = hisList.get(hisList.size() - 1).getPid();
//                if (listener != null) {
//                    listener.onReceiveHisPosts(hisList, false, ErrorCode.ERROR_SUCCESS);
//                }
//            } else {
//                if (listener != null) {
//                    listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void setListener(SinglePostsProviderCallback listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public void attachListener() {
//        HotCache.getInstance().setListener(this);
//    }
//
//    @Override
//    public void detachListener() {
//        HotCache.getInstance().setListener(null);
//    }
//
//    @Override
//    public void onError(BaseRequest request, int errCode) {
//        if (request == feedsRequest) {
//            feedsRequest = null;
//            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//                if (listener != null) {
//                    listener.onRefreshPosts(null, 0, errCode);
//                }
//            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//                if (listener != null) {
//                    listener.onReceiveHisPosts(null, false, errCode);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
//        if (request == feedsRequest) {
//            List<PostBase> posts = PostsDataSourceParser.parsePosts(result);
//            if (posts != null && posts.size() > 0) {
//                String newCursor = PostsDataSourceParser.parseCursor(result);
//                try {
//                    HotCache.getInstance().insert(posts, newCursor);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    feedsRequest = null;
//                    cursor = newCursor;
//                    cursorPid = posts.get(posts.size() - 1).getPid();
//                    if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//                        if (listener != null) {
//                            listener.onRefreshPosts(posts, 0, ErrorCode.ERROR_SUCCESS);
//                        }
//                    } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//                        if (!TextUtils.isEmpty(cursor)) {
//                            if (listener != null) {
//                                listener.onReceiveHisPosts(posts, false, ErrorCode.ERROR_SUCCESS);
//                            }
//                        } else {
//                            if (listener != null) {
//                                listener.onReceiveHisPosts(posts, true, ErrorCode.ERROR_SUCCESS);
//                            }
//                        }
//                    }
//                }
//            } else {
//                feedsRequest = null;
//                if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//                    if (listener != null) {
//                        listener.onRefreshPosts(null, 0, ErrorCode.ERROR_SUCCESS);
//                    }
//                } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//                    cursor = null;
//                    cursorPid = null;
//                    if (listener != null) {
//                        listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onHotCacheInsertCompleted(List<PostBase> posts, String newCursor) {
//        feedsRequest = null;
//
//        if (posts != null && posts.size() > 0) {
//            cursor = newCursor;
//            cursorPid = posts.get(posts.size() - 1).getPid();
//            List<PostBase> targetList = filterPosts(posts);
//
//            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//                if (listener != null) {
//                    listener.onRefreshPosts(targetList, 0, ErrorCode.ERROR_SUCCESS);
//                }
//            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//                boolean last = TextUtils.isEmpty(newCursor);
//                if (listener != null) {
//                    listener.onReceiveHisPosts(targetList, last, ErrorCode.ERROR_SUCCESS);
//                }
//            }
//        } else {
//            previousPageList = null;
//            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//                if (listener != null) {
//                    listener.onRefreshPosts(null, 0, ErrorCode.ERROR_SUCCESS);
//                }
//            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//                if (!TextUtils.isEmpty(newCursor)) {
//                    feedsRequest = new FeedsRequest(FeedsRequest.HOME_FEEDS, false, MyApplication.getAppContext());
//                    try {
//                        feedsRequest.tryFeeds(newCursor, this);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (listener != null) {
//                            listener.onReceiveHisPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
//                        }
//                    }
//                } else {
//                    cursor = null;
//                    cursorPid = null;
//                    if (listener != null) {
//                        listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
//                    }
//                }
//            }
//        }
//    }
//
//    private List<PostBase> filterPosts(List<PostBase> source) {
//        List<PostBase> targetList;
//        if (previousPageList != null) {
//            targetList = PostsDataSourceParser.filterPosts(source, previousPageList);
//        } else {
//            targetList = source;
//        }
//        previousPageList = source;
//        return targetList;
//    }
//
//}

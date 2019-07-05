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
//import com.redefine.welike.business.feeds.management.FeedsStatusObserver;
//import com.redefine.welike.business.feeds.management.bean.PostBase;
//import com.redefine.welike.business.feeds.management.cache.HomeCache;
//import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
//
//import java.util.ArrayList;
//import java.util.List;

/**
 * Created by liubin on 2018/1/10.
 */

//public class HomePostsProvider implements ISinglePostsProvider, RequestCallback, HomeCache.HomeCacheCallback, FeedsStatusObserver.FeedsStatusObserverCallback {
//    private FeedsRequest feedsRequest;
//    private PostBase cursorPost;
//    private boolean nextHisFromNet;
//    private List<PostBase> previousPageList;
//    private int actionState = GlobalConfig.LIST_ACTION_NONE;
//    private SinglePostsProviderCallback listener;
//
//    public HomePostsProvider() {
//        nextHisFromNet = true;
//    }
//
//    @Override
//    public void tryRefreshPosts() {
//        if (feedsRequest != null) return;
//        cursorPost = null;
//        previousPageList = null;
//        nextHisFromNet = true;
//        actionState = GlobalConfig.LIST_ACTION_REFRESH;
//
//        if (CommonHelper.isNetworkAvalible(MyApplication.getAppContext())) {
//            feedsRequest = new FeedsRequest(FeedsRequest.HOME_FEEDS, true, MyApplication.getAppContext());
//            try {
//                feedsRequest.tryFeeds(null, this);
//            } catch (Exception e) {
//                e.printStackTrace();
//                feedsRequest = null;
//                if (listener != null) {
//                    listener.onRefreshPosts(null, 0, ErrorCode.networkExceptionToErrCode(e));
//                }
//            }
//        } else {
//            List<PostBase> list = HomeCache.getInstance().listNewPosts(GlobalConfig.POSTS_NUM_ONE_PAGE);
//            if (list != null && list.size() > 0) {
//                cursorPost = list.get(list.size() - 1);
//                nextHisFromNet = true;
//                List<PostBase> targetList = filterPosts(list);
//                if (listener != null) {
//                    listener.onRefreshPosts(targetList, 0, ErrorCode.ERROR_SUCCESS);
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
//        boolean hisFromNet = nextHisFromNet;
//        if (!CommonHelper.isNetworkAvalible(MyApplication.getAppContext())) hisFromNet = false;
//
//        if (cursorPost != null) {
//            actionState = GlobalConfig.LIST_ACTION_HIS;
//            if (hisFromNet) {
//                String cursor = HomeCache.generateCursor(cursorPost);
//                feedsRequest = new FeedsRequest(FeedsRequest.HOME_FEEDS, true, MyApplication.getAppContext());
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
//                List<PostBase> hisList = HomeCache.getInstance().listBeforePid(cursorPost.getPid(), GlobalConfig.POSTS_NUM_ONE_PAGE);
//                if (hisList != null && hisList.size() > 0) {
//                    cursorPost = hisList.get(hisList.size() - 1);
//                    List<String> pids = new ArrayList<>();
//                    for (PostBase postBase : hisList) {
//                        pids.add(postBase.getPid());
//                    }
//                    FeedsStatusObserver.getInstance().reqStatusList(pids);
//                    if (listener != null) {
//                        listener.onReceiveHisPosts(hisList, false, ErrorCode.ERROR_SUCCESS);
//                    }
//                } else {
//                    if (CommonHelper.isNetworkAvalible(MyApplication.getAppContext())) {
//                        String cursor = HomeCache.generateCursor(cursorPost);
//                        feedsRequest = new FeedsRequest(FeedsRequest.HOME_FEEDS, true, MyApplication.getAppContext());
//                        try {
//                            feedsRequest.tryFeeds(cursor, this);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            feedsRequest = null;
//                            if (listener != null) {
//                                listener.onReceiveHisPosts(null, false, ErrorCode.networkExceptionToErrCode(e));
//                            }
//                        }
//                    } else {
//                        cursorPost = null;
//                        previousPageList = null;
//                        if (listener != null) {
//                            listener.onReceiveHisPosts(hisList, true, ErrorCode.ERROR_SUCCESS);
//                        }
//                    }
//                }
//            }
//        } else {
//            if (listener != null) {
//                listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
//            }
//        }
//    }
//
//    @Override
//    public void attachListener() {
//        HomeCache.getInstance().setListener(this);
//        FeedsStatusObserver.getInstance().register(this);
//    }
//
//    @Override
//    public void detachListener() {
//        HomeCache.getInstance().setListener(null);
//        FeedsStatusObserver.getInstance().unregister(this);
//    }
//
//    @Override
//    public void setListener(SinglePostsProviderCallback listener) {
//        this.listener = listener;
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
//                String cursor = PostsDataSourceParser.parseCursor(result);
//                try {
//                    HomeCache.getInstance().insert(posts, cursor);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    feedsRequest = null;
//                    cursorPost = posts.get(posts.size() - 1);
//                    List<PostBase> targetList = filterPosts(posts);
//                    if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//                        if (listener != null) {
//                            listener.onRefreshPosts(targetList, 0, ErrorCode.ERROR_SUCCESS);
//                        }
//                    } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//                        if (!TextUtils.isEmpty(cursor)) {
//                            if (listener != null) {
//                                listener.onReceiveHisPosts(targetList, false, ErrorCode.ERROR_SUCCESS);
//                            }
//                        } else {
//                            if (listener != null) {
//                                listener.onReceiveHisPosts(targetList, true, ErrorCode.ERROR_SUCCESS);
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
//                    if (listener != null) {
//                        listener.onReceiveHisPosts(null, true, ErrorCode.ERROR_SUCCESS);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onHomeCacheInsertCompleted(List<PostBase> posts, boolean nextLoadFromNet, String newCursor) {
//        feedsRequest = null;
//        nextHisFromNet = nextLoadFromNet;
//        if (posts != null && posts.size() > 0) {
//            cursorPost = posts.get(posts.size() - 1);
//        } else {
//            cursorPost = null;
//            previousPageList = null;
//        }
//
//        List<PostBase> targetList = filterPosts(posts);
//        if (targetList == null || targetList.size() == 0) {
//            cursorPost = null;
//        }
//
//        if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
//            if (listener != null) {
//                listener.onRefreshPosts(targetList, 0, ErrorCode.ERROR_SUCCESS);
//            }
//        } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
//            if (listener != null) {
//                if (nextLoadFromNet) {
//                    if (TextUtils.isEmpty(newCursor)) {
//                        listener.onReceiveHisPosts(targetList, true, ErrorCode.ERROR_SUCCESS);
//                    } else {
//                        listener.onReceiveHisPosts(targetList, false, ErrorCode.ERROR_SUCCESS);
//                    }
//                } else {
//                    if (cursorPost == null) {
//                        listener.onReceiveHisPosts(targetList, true, ErrorCode.ERROR_SUCCESS);
//                    } else {
//                        listener.onReceiveHisPosts(targetList, false, ErrorCode.ERROR_SUCCESS);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onListFeedsStatusSuccessed(List<FeedsStatusObserver.FeedStatus> statusList) {
//        if (statusList != null && statusList.size() > 0) {
//            HomeCache.getInstance().updatePostsStatus(statusList);
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

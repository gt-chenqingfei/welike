package com.redefine.welike.business.feeds.management;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.provider.SearchLatestProvider;
import com.redefine.welike.business.feeds.management.provider.SearchPostProvider;
import com.redefine.welike.business.feeds.management.provider.SearchUserProvider;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/12.
 */

public class SearchManager extends BroadcastManagerBase implements SearchPostProvider.SearchPostProviderCallback,
                                                                    SearchUserProvider.SearchUserProviderCallback,
                                                                    SearchLatestProvider.SearchLatestProviderCallback {
    public static final int SEARCH_MANAGER_TYPE_USERS = 1;
    public static final int SEARCH_MANAGER_TYPE_POSTS = 2;
    public static final int SEARCH_MANAGER_TYPE_LATEST = 3;
    private SearchLatestProvider searchLatestProvider;
    private SearchPostProvider searchPostProvider;
    private SearchUserProvider searchUserProvider;
    private int type;

    public interface SearchManagerListener {

        void onNewSearchResult(List<Object> contents, int searchType, int errCode);
        void onMoreSearchResult(List<Object> contents, int searchType, boolean last, int errCode);

    }

    public SearchManager() {
        searchLatestProvider = new SearchLatestProvider();
        searchLatestProvider.setListener(this);
        searchPostProvider = new SearchPostProvider();
        searchPostProvider.setListener(this);
        searchUserProvider = new SearchUserProvider();
        searchUserProvider.setListener(this);
    }

    public void register(SearchManagerListener listener) {
        super.register(listener);
    }

    public void unregister(SearchManagerListener listener) {
        super.unregister(listener);
    }

    public void setSearchType(int type) {
        this.type = type;
    }

    public void search(String keyword, boolean auth) {
        if (type == SEARCH_MANAGER_TYPE_USERS) {
            searchUserProvider.tryNewSearchUsers(keyword, auth);
        } else if (type == SEARCH_MANAGER_TYPE_POSTS) {
            searchPostProvider.tryNewSearchPosts(keyword, auth);
        } else if (type == SEARCH_MANAGER_TYPE_LATEST) {
            searchLatestProvider.tryNewSearchLatest(keyword, auth);
        } else {
            broadcastNewSearchResult(null, type, ErrorCode.ERROR_SUCCESS);
        }
    }

    public void loadMore(boolean auth) {
        if (type == SEARCH_MANAGER_TYPE_USERS) {
            searchUserProvider.tryMoreSearchUsers(auth);
        } else if (type == SEARCH_MANAGER_TYPE_POSTS) {
            searchPostProvider.tryMoreSearchPosts(auth);
        } else if (type == SEARCH_MANAGER_TYPE_LATEST) {
            searchLatestProvider.tryMoreSearchLatest(auth);
        } else {
            broadcastNewSearchResult(null, type, ErrorCode.ERROR_SUCCESS);
        }
    }

    @Override
    public void onNewSearchLatest(List<PostBase> posts, List<User> users, int errCode) {
        List<Object> contents = null;
        if (users != null && users.size() > 0) {
            contents = new ArrayList<>();
            contents.addAll(users);
        }
        if (posts != null && posts.size() > 0) {
            if (contents == null) {
                contents = new ArrayList<>();
            }
            contents.addAll(posts);
        }
        broadcastNewSearchResult(contents, SEARCH_MANAGER_TYPE_LATEST, errCode);
    }

    @Override
    public void onMoreSearchLatest(List<PostBase> posts, boolean last, int errCode) {
        List<Object> contents = null;
        if (posts != null && posts.size() > 0) {
            contents = new ArrayList<>();
            contents.addAll(posts);
        }
        broadcastMoreSearchResult(contents, SEARCH_MANAGER_TYPE_LATEST, last, errCode);
    }

    @Override
    public void onNewSearchUsers(List<User> users, int errCode) {
        List<Object> contents = null;
        if (users != null && users.size() > 0) {
            contents = new ArrayList<>();
            contents.addAll(users);
        }
        broadcastNewSearchResult(contents, SEARCH_MANAGER_TYPE_USERS, errCode);
    }

    @Override
    public void onNewSearchPosts(List<PostBase> posts, int errCode) {
        List<Object> contents = null;
        if (posts != null && posts.size() > 0) {
            contents = new ArrayList<>();
            contents.addAll(posts);
        }
        broadcastNewSearchResult(contents, SEARCH_MANAGER_TYPE_POSTS, errCode);
    }

    @Override
    public void onMoreSearchUsers(List<User> users, boolean last, int errCode) {
        List<Object> contents = null;
        if (users != null && users.size() > 0) {
            contents = new ArrayList<>();
            contents.addAll(users);
        }
        broadcastMoreSearchResult(contents, SEARCH_MANAGER_TYPE_USERS, last, errCode);
    }

    @Override
    public void onMoreSearchPosts(List<PostBase> posts, boolean last, int errCode) {
        List<Object> contents = null;
        if (posts != null && posts.size() > 0) {
            contents = new ArrayList<>();
            contents.addAll(posts);
        }
        broadcastMoreSearchResult(contents, SEARCH_MANAGER_TYPE_POSTS, last, errCode);
    }
    private void broadcastNewSearchResult(final List<Object> contents, final int type, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof SearchManagerListener) {
                            SearchManagerListener listener = (SearchManagerListener)l;
                            listener.onNewSearchResult(contents, type, errCode);
                        }
                    }
                }
            }

        });
    }


    private void broadcastMoreSearchResult(final List<Object> contents, final int type, final boolean last, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof SearchManagerListener) {
                            SearchManagerListener listener = (SearchManagerListener)l;
                            listener.onMoreSearchResult(contents, type, last, errCode);
                        }
                    }
                }
            }

        });
    }

}

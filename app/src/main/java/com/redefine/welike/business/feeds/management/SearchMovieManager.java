package com.redefine.welike.business.feeds.management;




import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.management.provider.SearchMovieProvider;
import com.redefine.welike.business.search.ui.bean.SearchMovieBean;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SearchMovieManager extends BroadcastManagerBase implements SearchMovieProvider.SearchMovieProviderCallback{
    private SearchMovieProvider searchMovieProvider;

    @Override
    public void onGetMovies(List<SearchMovieBean> movieBeans, int errCode) {
        broadcastResult(movieBeans, errCode);

    }

    public interface SearchMovieManagerListener {

        void onNewSearchResult(List<SearchMovieBean> contents, int errCode);

    }

    public SearchMovieManager() {
        searchMovieProvider = new SearchMovieProvider();
        searchMovieProvider.setListener(this);
    }
    public void search(String keyword) {
        searchMovieProvider.tryMovies(keyword, AccountManager.getInstance().isLogin());
    }

    public void register(SearchMovieManagerListener listener) {
        super.register(listener);
    }

    public void unregister(SearchMovieManagerListener listener) {
        super.unregister(listener);
    }

    private void broadcastResult(final List<SearchMovieBean> contents,  final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof SearchMovieManagerListener) {
                            SearchMovieManagerListener listener = (SearchMovieManagerListener)l;
                            listener.onNewSearchResult(contents, errCode);
                        }
                    }
                }
            }

        });
    }
}

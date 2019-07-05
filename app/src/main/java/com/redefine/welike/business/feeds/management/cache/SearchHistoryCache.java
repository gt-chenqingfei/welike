package com.redefine.welike.business.feeds.management.cache;

import android.text.TextUtils;

import com.redefine.welike.base.DBStore;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.dao.welike.DaoSession;
import com.redefine.welike.base.dao.welike.SearchHistory;
import com.redefine.welike.base.dao.welike.SearchHistoryDao;
import com.redefine.welike.business.feeds.management.bean.SugResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liubin on 2018/3/12.
 */

public class SearchHistoryCache {
    private DaoSession daoSession;
    private SearchHistoryDao searchHistoryDao;

    private static class SearchHistoryCacheHolder {
        public static SearchHistoryCache instance = new SearchHistoryCache();
    }

    private SearchHistoryCache() {
        daoSession = DBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            searchHistoryDao = daoSession.getSearchHistoryDao();
        }
    }

    public static SearchHistoryCache getInstance() { return SearchHistoryCacheHolder.instance; }

    public synchronized List<SugResult> listRecentKeywords(String keyword) {
        if (searchHistoryDao == null) return null;

        List<SugResult> keywords = null;
        List<SearchHistory> list = searchHistoryDao.queryBuilder().where(SearchHistoryDao.Properties.Keyword.like("%" + keyword + "%")).orderDesc(SearchHistoryDao.Properties.Time).limit(GlobalConfig.SUG_HIS_SHOW_NUM).build().list();
        if (list != null && list.size() > 0 ) {
            keywords = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String k = list.get(i).getKeyword();
                if (!TextUtils.isEmpty(k)) {
                    SugResult h = new SugResult();
                    h.setType(SugResult.SUG_RESULT_TYPE_HIS);
                    h.setCategory(SugResult.SUG_RESULT_CATEGORY_KEYWORD);
                    h.setObj(k);
                    keywords.add(h);
                }
            }
        }
        return keywords;
    }

    public synchronized List<SugResult> listRecentKeywords(int num) {
        List<SugResult> keywords = null;
        List<SearchHistory> list = searchHistoryDao.queryBuilder().orderDesc(SearchHistoryDao.Properties.Time).limit(num).build().list();
        if (list != null && list.size() > 0 ) {
            keywords = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String k = list.get(i).getKeyword();
                if (!TextUtils.isEmpty(k)) {
                    SugResult h = new SugResult();
                    h.setType(SugResult.SUG_RESULT_TYPE_HIS);
                    h.setCategory(SugResult.SUG_RESULT_CATEGORY_KEYWORD);
                    h.setObj(k);
                    keywords.add(h);
                }
            }
        }
        return keywords;
    }

    public synchronized List<SugResult> listAll() {
        List<SugResult> keywords = null;
        List<SearchHistory> list = searchHistoryDao.queryBuilder().orderDesc(SearchHistoryDao.Properties.Time).build().list();
        if (list != null && list.size() > 0 ) {
            keywords = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String k = list.get(i).getKeyword();
                if (!TextUtils.isEmpty(k)) {
                    SugResult h = new SugResult();
                    h.setType(SugResult.SUG_RESULT_TYPE_HIS);
                    h.setCategory(SugResult.SUG_RESULT_CATEGORY_KEYWORD);
                    h.setObj(k);
                    keywords.add(h);
                }
            }
        }
        return keywords;
    }

    public synchronized int getAllCount() {
        if (searchHistoryDao == null) return 0;
        return (int) searchHistoryDao.queryBuilder().buildCount().count();
    }

    public void insert(SugResult keyword) {
        if (daoSession != null) {
            if (keyword.getType() == SugResult.SUG_RESULT_TYPE_HIS &&
                keyword.getCategory() == SugResult.SUG_RESULT_CATEGORY_KEYWORD) {
                final String k = (String)keyword.getObj();
                daoSession.runInTx(new Runnable() {

                    @Override
                    public void run() {
                        runInsert(k);
                    }

                });
            }
        }
    }

    public synchronized void delete(String keyword) {
        if (searchHistoryDao == null) return;

        SearchHistory searchHistory = searchHistoryDao.queryBuilder().where(SearchHistoryDao.Properties.Keyword.eq(keyword)).build().unique();
        if (searchHistory != null) {
            searchHistoryDao.delete(searchHistory);
        }
    }

    public synchronized void cleanAll() {
        if (searchHistoryDao == null) return;
        searchHistoryDao.deleteAll();
    }

    private synchronized void runInsert(String keyword) {
        SearchHistory newHis = new SearchHistory();
        newHis.setKeyword(keyword);
        newHis.setTime(new Date().getTime());
        try {
            searchHistoryDao.insert(newHis);

            if (searchHistoryDao.queryBuilder().buildCount().count() > GlobalConfig.SUG_HIS_CACHE_NUM) {
                SearchHistory h = searchHistoryDao.queryBuilder().orderAsc(SearchHistoryDao.Properties.Time).limit(1).build().unique();
                if (h != null) {
                    searchHistoryDao.delete(h);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void runDelete(SugResult keyword) {
        SearchHistory searchHistory = searchHistoryDao.queryBuilder().where(SearchHistoryDao.Properties.Keyword.eq(keyword)).build().unique();
        if (searchHistory != null) {
            searchHistoryDao.delete(searchHistory);
        }
    }

}

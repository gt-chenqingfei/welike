package com.redefine.welike.business.publisher.management.cache;

import android.text.TextUtils;

import com.redefine.welike.base.DBStore;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.dao.welike.DaoSession;
import com.redefine.welike.base.dao.welike.TopicSearchHistory;
import com.redefine.welike.base.dao.welike.TopicSearchHistoryDao;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;

import java.util.Date;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchHistoryCache {

    private DaoSession daoSession;
    private TopicSearchHistoryDao searchHistoryDao;

    private TopicSearchHistoryCache() {
        daoSession = DBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            searchHistoryDao = daoSession.getTopicSearchHistoryDao();
        }
    }

    private static class TopicSearchHistoryCacheHolder {
        public static TopicSearchHistoryCache instance = new TopicSearchHistoryCache();
    }

    public static TopicSearchHistoryCache getInstance() {
        return TopicSearchHistoryCacheHolder.instance;
    }


    public synchronized List<TopicSearchHistory> listAll() {
        List<TopicSearchHistory> list = searchHistoryDao.queryBuilder().orderDesc(TopicSearchHistoryDao.Properties.Time).limit(GlobalConfig.TOPIC_SUG_HIS_CACHE_NUM).build().list();
        return list;
    }

    public synchronized int getAllCount() {
        if (searchHistoryDao == null) return 0;
        return (int) searchHistoryDao.queryBuilder().buildCount().count();
    }

    public void insert(final TopicSearchSugBean.TopicBean keyword) {
        String name = keyword.name;
        if (!TextUtils.isEmpty(name) && name.startsWith("#")) {
            name = name.substring(1, name.length());
        }
        if (TextUtils.isEmpty(name)) {
            return;
        }
        final String saveName = name;
        if (daoSession != null) {
            daoSession.runInTx(new Runnable() {

                @Override
                public void run() {
                    runInsert(saveName);
                }

            });
        }
    }

    public synchronized void delete(String keyword) {
        if (searchHistoryDao == null) return;

        TopicSearchHistory searchHistory = searchHistoryDao.queryBuilder().where(TopicSearchHistoryDao.Properties.Keyword.eq(keyword)).build().unique();
        if (searchHistory != null) {
            searchHistoryDao.delete(searchHistory);
        }
    }

    public synchronized void cleanAll() {
        if (searchHistoryDao == null) return;
        searchHistoryDao.deleteAll();
    }

    private synchronized void runInsert(String keyword) {
        TopicSearchHistory newHis = new TopicSearchHistory();
        newHis.setKeyword(keyword);
        newHis.setTime(new Date().getTime());
        try {
            searchHistoryDao.insertOrReplace(newHis);

            if (searchHistoryDao.queryBuilder().buildCount().count() > GlobalConfig.TOPIC_SUG_HIS_CACHE_NUM) {
                TopicSearchHistory h = searchHistoryDao.queryBuilder().orderAsc(TopicSearchHistoryDao.Properties.Time).limit(1).build().unique();
                if (h != null) {
                    searchHistoryDao.delete(h);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

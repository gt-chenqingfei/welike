package com.redefine.welike.business.feeds.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.SearchSugRequest;
import com.redefine.welike.business.browse.management.request.SearchSugRequest1;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.feeds.management.cache.SearchHistoryCache;
import com.redefine.welike.business.search.ui.bean.MovieSugBean;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/12.
 */

public class SugManager {
    private BaseRequest searchSugRequest;

    public interface SugManagerCallback {

        void onSugResult(String query, List<SugResult> results, int errCode);

    }

    public void inputKeyword(final String keyword, final SugManagerCallback callback, boolean auth) {
        if (TextUtils.isEmpty(keyword)) {
            final List<SugResult> list = SearchHistoryCache.getInstance().listRecentKeywords(GlobalConfig.SUG_HIS_SHOW_NUM);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onSugResult(keyword, list, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        } else {
            if (searchSugRequest != null) {
                searchSugRequest.cancel();
                searchSugRequest = null;
            }
            if (auth) {
                searchSugRequest = new SearchSugRequest(keyword, MyApplication.getAppContext());
            } else {
                searchSugRequest = new SearchSugRequest1(keyword, MyApplication.getAppContext());
            }
            try {
                searchSugRequest.putUserInfo("keyword", keyword);
                searchSugRequest.req(new RequestCallback() {

                    @Override
                    public void onError(BaseRequest request, final int errCode) {
                        if (searchSugRequest == request) {
                            searchSugRequest = null;
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.onSugResult(keyword, null, errCode);
                                    }
                                }

                            });
                        }
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                        if (searchSugRequest == request) {
                            final String keyword = (String)searchSugRequest.getUserInfo("keyword");
                            searchSugRequest = null;
                            JSONArray usersJSON = null;
                            JSONArray sugsJSON = null;
                            JSONArray moviesJSON = null;

                            try {
                                moviesJSON = result.getJSONArray("movies");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                usersJSON = result.getJSONArray("user");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                sugsJSON = result.getJSONArray("queries");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final List<SugResult> slist = new ArrayList<>();
                            List<SugResult> hisList = SearchHistoryCache.getInstance().listRecentKeywords(keyword);
                            if (hisList != null && hisList.size() > 0) {
                                slist.addAll(hisList);
                            }
                            if (moviesJSON != null && moviesJSON.size() > 0 ) {
                                for (int i = 0; i < moviesJSON.size(); i++) {
                                    String info = moviesJSON.getString(i);
                                    MovieSugBean movieSugBean=null;
                                    Gson gson = new Gson();
                                    movieSugBean = gson.fromJson(info, MovieSugBean.class);
                                    SugResult movieResult = new SugResult();
                                    movieResult.setType(SugResult.SUG_RESULT_TYPE_SUG);
                                    movieResult.setCategory(SugResult.SUG_RESULT_CATEGORY_MOVIE);
                                    movieResult.setObj(movieSugBean);
                                    slist.add(movieResult);

                                }



                            }
                            if (usersJSON != null && usersJSON.size() > 0) {
                                for (int i = 0; i < usersJSON.size(); i++) {
                                    User user = UserParser.parseUser(usersJSON.getJSONObject(i));
                                    SugResult userResult = new SugResult();
                                    userResult.setType(SugResult.SUG_RESULT_TYPE_SUG);
                                    userResult.setCategory(SugResult.SUG_RESULT_CATEGORY_USER);
                                    userResult.setObj(user);
                                    slist.add(userResult);
                                }
                            }
                            if (sugsJSON != null && sugsJSON.size() > 0 ) {
                                for (int i = 0; i < sugsJSON.size(); i++) {
                                    String suggestion = sugsJSON.getJSONObject(i).getString("text");
                                    SugResult userResult = new SugResult();
                                    userResult.setType(SugResult.SUG_RESULT_TYPE_SUG);
                                    userResult.setCategory(SugResult.SUG_RESULT_CATEGORY_KEYWORD);
                                    userResult.setObj(suggestion);
                                    slist.add(userResult);
                                }
                            }

                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                                @Override
                                public void run() {
                                    if (callback != null) {
                                        callback.onSugResult(keyword, slist, ErrorCode.ERROR_SUCCESS);
                                    }
                                }

                            });
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                searchSugRequest = null;
                final int errCode = ErrorCode.networkExceptionToErrCode(e);
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSugResult(keyword,null, errCode);
                        }
                    }

                });
            }
        }
    }

    public void deleteHistory(String keyword) {
        SearchHistoryCache.getInstance().delete(keyword);
    }

    public List<SugResult> listAllHistory() {
        return SearchHistoryCache.getInstance().listRecentKeywords(GlobalConfig.SUG_EMPTY_HIS_SHOW_NUM);
    }

    public List<SugResult> listRecentKeywords() {
        return SearchHistoryCache.getInstance().listRecentKeywords(GlobalConfig.SUG_HIS_SHOW_NUM);
    }

    public int getAllHistoryCount() {
        return SearchHistoryCache.getInstance().getAllCount();
    }

    public void insert(SugResult keyword) {
        SearchHistoryCache.getInstance().insert(keyword);
    }

    public void cleanAllHistory() {
        SearchHistoryCache.getInstance().cleanAll();
    }

}

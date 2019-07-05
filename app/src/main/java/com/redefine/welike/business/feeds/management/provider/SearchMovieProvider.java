package com.redefine.welike.business.feeds.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.SearchMovieRequest;
import com.redefine.welike.business.browse.management.request.SearchMovieRequest1;
import com.redefine.welike.business.search.ui.bean.SearchMovieBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SearchMovieProvider implements RequestCallback {

    private BaseRequest searchMovieRequest;
    private String keyword;
    private SearchMovieProviderCallback listener;

    public  interface SearchMovieProviderCallback {

        void onGetMovies(final List<SearchMovieBean> movieBeans, final int errCode);

    }
    public void setListener(SearchMovieProviderCallback listener) {
        this.listener = listener;
    }

    public void tryMovies(String keyword, boolean auth) {
        if (searchMovieRequest != null) {
            searchMovieRequest.cancel();
            searchMovieRequest = null;
        }

        this.keyword = keyword;
        if (!TextUtils.isEmpty(keyword)) {
            if (auth) {
                searchMovieRequest = new SearchMovieRequest(keyword, MyApplication.getAppContext());
            } else {
                searchMovieRequest = new SearchMovieRequest1(keyword, MyApplication.getAppContext());
            }
            try {
                searchMovieRequest.req(this);
            } catch (Exception e) {
                e.printStackTrace();
                searchMovieRequest = null;
                if (listener != null) {
                    listener.onGetMovies(null, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (listener != null) {
                listener.onGetMovies(null, ErrorCode.ERROR_SUCCESS);
            }
        }
    }


    @Override
    public void onError(BaseRequest request, int errCode) {
        if (searchMovieRequest == request) {
            searchMovieRequest = null;
            final int error = errCode;

            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (listener != null) {
                        listener.onGetMovies(null,  error);
                    }
                }

            });
        }

    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (searchMovieRequest == request) {
            searchMovieRequest = null;
            final List<SearchMovieBean> movieBeans = new ArrayList<>();
            List<String> infos = new ArrayList<>();


            if(null!=result){
                JSONArray array = result.getJSONArray("list");
                if (array != null && array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        String info = array.getString(i);
                        infos.add(info);
                    }
                }
                SearchMovieBean searchMovieBean=null;
                Gson gson = new Gson();
                if(infos.size()>0){
                    searchMovieBean = gson.fromJson(infos.get(0), SearchMovieBean.class);

                }

                if(null!=searchMovieBean){
                    movieBeans.add(searchMovieBean);
                }

            }
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (listener != null) {
                        listener.onGetMovies(movieBeans, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        }

    }
}

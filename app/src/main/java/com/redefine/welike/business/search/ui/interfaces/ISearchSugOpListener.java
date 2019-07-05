package com.redefine.welike.business.search.ui.interfaces;

/**
 * Created by liwenbo on 2018/3/14.
 */

public interface ISearchSugOpListener {

    void deleteHistory(String searchHistory);

    void copyToEdit(String sug);

    void clearAllHistory();
}

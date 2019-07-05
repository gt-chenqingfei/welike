package com.redefine.welike.business.search.ui.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MovieSugBean implements Serializable{
    private static final long serialVersionUID = 9096318280556667797L;

    @SerializedName("id")
    private String id;
    @SerializedName("searchText")
    private String searchText;
    @SerializedName("showText")
    private String showText;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getShowText() {
        return showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }
}

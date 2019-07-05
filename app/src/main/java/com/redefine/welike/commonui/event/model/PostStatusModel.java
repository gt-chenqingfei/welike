package com.redefine.welike.commonui.event.model;

import com.redefine.welike.commonui.event.commonenums.BooleanValue;
import com.redefine.welike.statistical.EventLog1;

import java.io.Serializable;

/**
 * Created by nianguowang on 2018/11/5
 */
public class PostStatusModel implements Serializable {
    private EventLog1.PostStatus.ButtonFrom buttonFrom;
    private EventLog1.PostStatus.ButtonType buttonType;
    private BooleanValue textChanged;
    private String text;
    private String imageId;
    private String categoryId;
    private String categoryName;

    public PostStatusModel(EventLog1.PostStatus.ButtonFrom buttonFrom) {
        this.buttonFrom = buttonFrom;
    }

    public EventLog1.PostStatus.ButtonFrom getButtonFrom() {
        return buttonFrom;
    }

    public PostStatusModel setButtonFrom(EventLog1.PostStatus.ButtonFrom buttonFrom) {
        this.buttonFrom = buttonFrom;
        return this;
    }

    public EventLog1.PostStatus.ButtonType getButtonType() {
        return buttonType;
    }

    public PostStatusModel setButtonType(EventLog1.PostStatus.ButtonType buttonType) {
        this.buttonType = buttonType;
        return this;
    }

    public BooleanValue getTextChanged() {
        return textChanged;
    }

    public PostStatusModel setTextChanged(BooleanValue textChanged) {
        this.textChanged = textChanged;
        return this;
    }

    public String getText() {
        return text;
    }

    public PostStatusModel setText(String text) {
        this.text = text;
        return this;
    }

    public String getImageId() {
        return imageId;
    }

    public PostStatusModel setImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public PostStatusModel setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public PostStatusModel setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }
}

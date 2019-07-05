package com.redefine.welike.business.feedback.management.bean;

import com.redefine.multimedia.photoselector.entity.Item;

import java.util.List;

/**
 * Created by nianguowang on 2018/10/15
 */
public class ReportModel {
    private long created;
    private String description;
    private String id;
    private List<String> images;
    private List<Item> uploadImages;
    private String reportId;
    private String userId;
    private String postUserId;
    private String reason;

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Item> getUploadImages() {
        return uploadImages;
    }

    public void setUploadImages(List<Item> uploadImages) {
        this.uploadImages = uploadImages;
    }
}

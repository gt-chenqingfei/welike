package com.redefine.welike.business.message.management.bean;

public class BusinessNotification extends NotificationBase{
    private int type;
    private String pushIcon;
    private String pushText;
    private String pushTile;
    private String forwordUrl;
    private String businessImage;
    private String batchId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPushIcon() {
        return pushIcon;
    }

    public void setPushIcon(String pushIcon) {
        this.pushIcon = pushIcon;
    }

    public String getPushText() {
        return pushText;
    }

    public void setPushText(String pushText) {
        this.pushText = pushText;
    }

    public String getPushTile() {
        return pushTile;
    }

    public void setPushTile(String pushTile) {
        this.pushTile = pushTile;
    }

    public String getForwordUrl() {
        return forwordUrl;
    }

    public void setForwordUrl(String forwordUrl) {
        this.forwordUrl = forwordUrl;
    }

    public String getBusinessImage() {
        return businessImage;
    }

    public void setBusinessImage(String businessImage) {
        this.businessImage = businessImage;
    }
}

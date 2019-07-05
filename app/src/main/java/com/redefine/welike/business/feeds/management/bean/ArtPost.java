package com.redefine.welike.business.feeds.management.bean;

import java.io.Serializable;

public class ArtPost extends PostBase implements Serializable{
    private static final long serialVersionUID = 5357102179206185467L;

    public ArtPost() {
        type = POST_TYPE_ART;

    }
}

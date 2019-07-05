package com.redefine.welike.business.common.mission;

import com.redefine.welike.business.feeds.management.bean.PostBase;

/**
 * Created by nianguowang on 2018/5/22
 */
public interface OnPostButtonClickListener {
    void onCommentClick(PostBase postBase);

    void onLikeClick(PostBase postBase, int exp);

    void onForwardClick(PostBase postBase);

    void onFollowClick(PostBase postBase);

    void onShareClick(PostBase postBase, int shareType);

    void onPostBodyClick(PostBase postBase);

    void onPostAreaClick(PostBase postBase, int clickArea);
}

package com.redefine.welike.business.user.ui.view.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Only handle visiable state,not handle click event.
 *
 * Created by nianguowang on 2018/6/28
 */
public interface IProfileState {

    void setTitle(boolean collapsingState, ImageView editProfile);

    void setVerifyInfo(View verifyInfoContainer);

    void setInfluence(View influenceContainer);

    void setInterests(View interestContainer);

    void setIntroduction(TextView introduction, TextView expand);

    void setChatAndFollow(TextView editProfile, View chatFollowContainer);

    void setSocialHost(ImageView facebook, ImageView instgram, ImageView youtube);
}

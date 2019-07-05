package com.redefine.welike.business.user.ui.view.state;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.commonenums.SocialMedia;
import com.redefine.welike.commonui.event.commonenums.UserType;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

import java.util.List;

/**
 * Created by nianguowang on 2018/6/28
 */
public class ProfileVisitState extends AbstractProfileState {

    public ProfileVisitState(User user) {
        super(user);
    }

    @Override
    public void setTitle(boolean collapsingState, ImageView editProfile) {
        if(mUser == null) {
            return;
        }
        editProfile.setVisibility(View.GONE);
    }

    @Override
    public void setVerifyInfo(View verifyInfoContainer) {
        if(mUser == null) {
            return;
        }
        String desc = VipUtil.getDescription(mUser.getVip());
        if(TextUtils.isEmpty(desc)) {
            verifyInfoContainer.setVisibility(View.GONE);
        } else {
            verifyInfoContainer.setVisibility(View.VISIBLE);
            TextView vipExtra = verifyInfoContainer.findViewById(R.id.tv_user_host_vip);
            TextView vipReal = verifyInfoContainer.findViewById(R.id.tv_user_host_vip_real);
            vipExtra.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_verified_ex"));
            vipReal.setText(desc);
        }
    }

    @Override
    public void setInterests(View interestContainer) {
        if(mUser == null) {
            return;
        }
        List<UserBase.Intrest> intrests = mUser.getIntrests();
        sortInterest(intrests);
        if(CollectionUtil.isEmpty(intrests)) {
            interestContainer.setVisibility(View.GONE);
        } else {
            //如果有能力标签，则该用户是标签用户。标签用户不显示兴趣，普通用户显示兴趣。
            List<UserBase.Intrest> influences = mUser.getInfluences();
            if(!CollectionUtil.isEmpty(influences)) {
                interestContainer.setVisibility(View.GONE);
            } else {
                interestContainer.setVisibility(View.VISIBLE);
                TextView interestEx = interestContainer.findViewById(R.id.tv_user_host_interest);
                final TextView interestReal = interestContainer.findViewById(R.id.tv_user_host_interest_real);
                interestEx.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_interest_ex"));
                interestReal.setText(parseInterestToString(intrests));
            }
        }
    }

    @Override
    public void setIntroduction(final TextView introduction, final TextView expand) {
        if(mUser == null) {
            return;
        }

        introduction.setText(mUser.getIntroduction());
        introduction.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = introduction.getLineCount();
                if(lineCount > 1) {
                    expand.setVisibility(View.VISIBLE);
                    introduction.setMaxLines(1);
                } else {
                    expand.setVisibility(View.GONE);
                    introduction.setMaxLines(Integer.MAX_VALUE);
                }
            }
        });
    }

    @Override
    public void setChatAndFollow(TextView editProfile, View chatFollowContainer) {
        editProfile.setVisibility(View.GONE);
        chatFollowContainer.setVisibility(View.VISIBLE);
        chatFollowContainer.setVisibility(View.VISIBLE);
        TextView chat = chatFollowContainer.findViewById(R.id.tv_user_host_chat);
        chat.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_bottom_message"));
    }

    @Override
    public void setSocialHost(final ImageView facebook, final ImageView instgram, final ImageView youtube) {
        if (mUser == null) {
            return;
        }
        List<UserBase.Link> links = mUser.getLinks();
        boolean facebookBind = false, instagramBind = false, youtubeBind = false;
        String facebookUrl = null, instagramUrl = null, youtubeUrl = null;
        if (!CollectionUtil.isEmpty(links)) {
            for (UserBase.Link link : links) {
                if (link.getLinkType() == UserConstant.USER_SOCIAL_LINK_FACEBOOK) {
                    facebookBind = true;
                    facebookUrl = link.getLink();
                }
                if (link.getLinkType() == UserConstant.USER_SOCIAL_LINK_INSTAGRAM) {
                    instagramBind = true;
                    instagramUrl = link.getLink();
                }
                if (link.getLinkType() == UserConstant.USER_SOCIAL_LINK_YOUTUBE) {
                    youtubeBind = true;
                    youtubeUrl = link.getLink();
                }
            }
        }
        facebookBind = mUser.getCurLevel() > 1 ? facebookBind : false;
        instagramBind = mUser.getCurLevel() > 1 ? instagramBind : false;
        youtubeBind = mUser.getCurLevel() > 1 ? youtubeBind : false;

        if (facebookBind) {
            facebook.setImageResource(R.drawable.profile_facebook_host_yes);
            final String finalFacebookUrl = facebookUrl;
            facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventLog.Profile.report2(EventConstants.PROFILE_ICON_TYPE_FACEBOOK,
                            AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);
                    EventLog1.Profile.report2(SocialMedia.FACEBOOK, AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, mUser.getUid());
                    new DefaultUrlRedirectHandler(facebook.getContext(), DefaultUrlRedirectHandler.FROM_PROFILE).onUrlRedirect(finalFacebookUrl);
                }
            });
        } else {
            facebook.setVisibility(View.GONE);
        }
        if (instagramBind) {
            instgram.setImageResource(R.drawable.profile_instgram_host_yes);
            final String finalInstagramUrl = instagramUrl;
            instgram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventLog.Profile.report2(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM,
                            AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);
                    EventLog1.Profile.report2(SocialMedia.INSTAGRAM, AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, mUser.getUid());
                    new DefaultUrlRedirectHandler(instgram.getContext(), DefaultUrlRedirectHandler.FROM_PROFILE).onUrlRedirect(finalInstagramUrl);
                }
            });
        } else {
            instgram.setVisibility(View.GONE);
        }
        if (youtubeBind) {
            youtube.setImageResource(R.drawable.profile_youtube_host_yes);
            final String finalYoutubeUrl = youtubeUrl;
            youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventLog.Profile.report2(EventConstants.PROFILE_ICON_TYPE_YOUTUBE,
                            AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);
                    EventLog1.Profile.report2(SocialMedia.YOUTUBE, AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, mUser.getUid());
                    new DefaultUrlRedirectHandler(youtube.getContext(), DefaultUrlRedirectHandler.FROM_PROFILE).onUrlRedirect(finalYoutubeUrl);
                }
            });
        } else {
            youtube.setVisibility(View.GONE);
        }
    }

}

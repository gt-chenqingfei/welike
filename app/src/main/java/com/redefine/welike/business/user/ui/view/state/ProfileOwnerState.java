package com.redefine.welike.business.user.ui.view.state;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.util.UserGradeRedirectHandler;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.page.UserInterestSelectPage;
import com.redefine.welike.business.user.ui.page.UserSocialHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.commonenums.SocialMedia;
import com.redefine.welike.commonui.event.commonenums.UserType;
import com.redefine.welike.commonui.view.ActionSnackBar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by nianguowang on 2018/6/28
 */
public class ProfileOwnerState extends AbstractProfileState {

    public ProfileOwnerState(User user) {
        super(user);
    }

    @Override
    public void setTitle(boolean collapsingState, ImageView editProfile) {
        if (collapsingState) {
            editProfile.setVisibility(View.VISIBLE);
        } else {
            editProfile.setVisibility(View.GONE);
        }
    }

    @Override
    public void setVerifyInfo(View verifyInfoContainer) {
        if (mUser == null) {
            return;
        }
        String desc = VipUtil.getDescription(mUser.getVip());
        if (TextUtils.isEmpty(desc)) {
            verifyInfoContainer.setVisibility(View.GONE);
        } else {
            verifyInfoContainer.setVisibility(View.VISIBLE);
            TextView vipExtra = verifyInfoContainer.findViewById(R.id.tv_user_host_vip);
            TextView vipReal = verifyInfoContainer.findViewById(R.id.tv_user_host_vip_real);
            vipExtra.setText(ResourceTool.getString("mine_verified_ex"));
            vipReal.setText(desc);
        }
    }

    @Override
    public void setInterests(View interestContainer) {
        if (mUser == null) {
            return;
        }
        List<UserBase.Intrest> intrests = mUser.getIntrests();
        sortInterest(intrests);
        if (CollectionUtil.isEmpty(intrests)) {
            interestContainer.setVisibility(View.GONE);
        } else {
            interestContainer.setVisibility(View.VISIBLE);
            TextView interestEx = interestContainer.findViewById(R.id.tv_user_host_interest);
            TextView interestReal = interestContainer.findViewById(R.id.tv_user_host_interest_real);
            interestEx.setText(ResourceTool.getString("mine_interest_ex"));
            String editText = ResourceTool.getString("user_edit");
            String interestsStr = parseInterestToString(intrests);
            SpannableString spannableString = new SpannableString(interestsStr + editText);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(0xFF48779D);
                }

                @Override
                public void onClick(View widget) {
                    UserInterestSelectPage.launch(3);
                }
            };
            spannableString.setSpan(clickableSpan, interestsStr.length(), interestsStr.length() + editText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            interestReal.setText(spannableString);
            interestReal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInterestSelectPage.launch(3);
                    EventLog.Profile.report4();
                    EventLog1.Profile.report4(mUser.getUid());
                }
            });
        }
    }

    @Override
    public void setIntroduction(TextView introduction, TextView expand) {
        if (mUser == null) {
            return;
        }
        expand.setVisibility(View.GONE);
        introduction.setMaxLines(Integer.MAX_VALUE);
        introduction.setText(mUser.getIntroduction());
    }

    @Override
    public void setChatAndFollow(TextView editProfile, View chatFollowContainer) {
        if (mUser == null) {
            return;
        }
        editProfile.setVisibility(View.VISIBLE);
        chatFollowContainer.setVisibility(View.GONE);
        editProfile.setText(ResourceTool.getString("user_edit_profile"));
    }

    @Override
    public void setSocialHost(final ImageView facebook, final ImageView instgram, final ImageView youtube) {
        if (mUser == null) {
            return;
        }
        List<UserBase.Link> links = mUser.getLinks();
        final int curLevel = mUser.getCurLevel();

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
        facebookBind = curLevel > 1 ? facebookBind : false;
        instagramBind = curLevel > 1 ? instagramBind : false;
        youtubeBind = curLevel > 1 ? youtubeBind : false;

        facebook.setImageResource(facebookBind ? R.drawable.profile_facebook_host_yes : R.drawable.profile_facebook_host_no);
        instgram.setImageResource(instagramBind ? R.drawable.profile_instgram_host_yes : R.drawable.profile_instgram_host_no);
        youtube.setImageResource(youtubeBind ? R.drawable.profile_youtube_host_yes : R.drawable.profile_youtube_host_no);

        final boolean finalFacebookBind = facebookBind;
        final String finalFacebookUrl = facebookUrl;
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog.Profile.report2(EventConstants.PROFILE_ICON_TYPE_FACEBOOK,
                        AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);
                EventLog1.Profile.report2(SocialMedia.FACEBOOK, AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, mUser.getUid());
                if (finalFacebookBind) {
                    new DefaultUrlRedirectHandler(facebook.getContext(), DefaultUrlRedirectHandler.FROM_PROFILE).onUrlRedirect(finalFacebookUrl);
                } else {
                    if (curLevel > 1) {
                        UserSocialHostPage.show(UserConstant.USER_SOCIAL_LINK_FACEBOOK, null);
                    } else {
                        showSnackBar(facebook, SocialMedia.YOUTUBE);
                    }
                }
            }
        });
        final boolean finalInstagramBind = instagramBind;
        final String finalInstagramUrl = instagramUrl;
        instgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog.Profile.report2(EventConstants.PROFILE_ICON_TYPE_INSTAGRAM,
                        AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);
                EventLog1.Profile.report2(SocialMedia.INSTAGRAM, AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, mUser.getUid());
                if (finalInstagramBind) {
                    new DefaultUrlRedirectHandler(facebook.getContext(), DefaultUrlRedirectHandler.FROM_IM).onUrlRedirect(finalInstagramUrl);
                } else {
                    if (curLevel > 1) {
                        UserSocialHostPage.show(UserConstant.USER_SOCIAL_LINK_INSTAGRAM, null);
                    } else {
                        showSnackBar(instgram, SocialMedia.YOUTUBE);
                    }
                }
            }
        });
        final boolean finalYoutubeBind = youtubeBind;
        final String finalYoutubeUrl = youtubeUrl;
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog.Profile.report2(EventConstants.PROFILE_ICON_TYPE_YOUTUBE,
                                AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT);
                EventLog1.Profile.report2(SocialMedia.YOUTUBE, AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, mUser.getUid());
                if (finalYoutubeBind) {
                    new DefaultUrlRedirectHandler(facebook.getContext(), DefaultUrlRedirectHandler.FROM_PROFILE).onUrlRedirect(finalYoutubeUrl);
                } else {
                    if (curLevel > 1) {
                        UserSocialHostPage.show(UserConstant.USER_SOCIAL_LINK_YOUTUBE, null);
                    } else {
                        showSnackBar(youtube,SocialMedia.YOUTUBE);
                    }
                }
            }
        });
    }

    private void showSnackBar(final View view, final SocialMedia iconType) {

        Account account = AccountManager.getInstance().getAccount();
        if (account == null || account.getStatus() == Account.ACCOUNT_HALF) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, true);
            bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));
            return;
        }

        EventLog.Profile.report11(AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT, iconType.getValue());
        ActionSnackBar.getInstance().showLoginSnackBar(view,
                ResourceTool.getString("profile_guru_permission"),
                ResourceTool.getString("profile_apply_now"), 3000, new ActionSnackBar.ActionBtnClickListener() {
                    @Override
                    public void onActionClick() {
                        new UserGradeRedirectHandler().onRedirect();
                        EventLog.Profile.report12(AccountManager.getInstance().isSelf(mUser.getUid()) ? EventConstants.PROFILE_USER_TYPE_SELF : EventConstants.PROFILE_USER_TYPE_VISIT, iconType.getValue());
                        EventLog1.Profile.report12(AccountManager.getInstance().isSelf(mUser.getUid()) ? UserType.OWNER : UserType.VISIT, iconType, mUser.getUid());
                    }
                });
    }
}

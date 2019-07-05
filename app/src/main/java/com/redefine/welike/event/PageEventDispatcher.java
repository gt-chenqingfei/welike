package com.redefine.welike.event;

import android.os.Bundle;
import android.os.Message;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.foundation.framework.Event;
import com.redefine.multimedia.player.youtube.YoutubePlayerActivity;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.fragment.MainFragmentPageSwitcher;
import com.redefine.welike.business.feeds.ui.page.HomePage;
import com.redefine.welike.business.feeds.ui.page.MainPage;
import com.redefine.welike.business.location.ui.constant.LocationConstant;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.business.search.ui.page.SearchSugPage;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.commonui.activity.MainActivityEx;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareManagerWrapper;
import com.redefine.welike.commonui.share.model.ShareTitleModel;
import com.redefine.welike.commonui.share.model.ShortLinkModel;
import com.redefine.welike.commonui.share.model.WaterMarkModel;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by liwenbo on 2018/2/1.
 */

public class PageEventDispatcher implements IEventDispatcher {
    private final IPageStackManager mPageStackManager;

    public PageEventDispatcher(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public void handleEvent(Event event) {
        if (event.id == EventIdConstant.LAUNCH_DISCOVER_PAGE) {
//            ARouter.getInstance().build(RouteConstant.MAIN_ROUTE_PATH).with(event.bundle).navigation();

            mPageStackManager.popToRootPage();
            List<PageConfig> configs = mPageStackManager.findPageConfigByType(MainPage.class);
            mPageStackManager.dispatchMessageToPagesByConfig(configs, Event.toMessage(MessageIdConstant.MESSAGE_LAUNCH_HOME_PAGE, event));
            ARouter.getInstance().build(RouteConstant.MAIN_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_FEED_DETAIL_EVENT) {
            ARouter.getInstance().build(RouteConstant.PATH_FEED_DETAIL).with(event.bundle).navigation();
//            mPageStackManager.pushPage(new PageConfig.Builder(FeedDetailPage.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_COMMENT_DETAIL_EVENT) {
            ARouter.getInstance().build(RouteConstant.PATH_FEED_COMMENT_DETAIL).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MESSAGE_LIST_EVENT) {
//            mPageStackManager.pushPage(new PageConfig.Builder(MessageBoxPage.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_SETTING_EVENT) {
//            mPageStackManager.pushPage(new PageConfig.Builder(SettingActivity.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.SETTING_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_USER_HOST_EVENT) {
            ARouter.getInstance().build(RouteConstant.PROFILE_ROUTE_PATH).with(event.bundle).navigation();
//            mPageStackManager.pushPage(new PageConfig.Builder(UserHostPage.class).setFitSystemWindow(false).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_USER_FOLLOW_EVENT) {
//            mPageStackManager.pushPage(new PageConfig.Builder(UserFollowActivity.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.FOLLOW_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_CHAT_EVENT) {
//            TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_IM_CHAT);
//            TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
//            mPageStackManager.pushPage(new PageConfig.Builder(ImChatPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.IM_CHAT_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_LOGOUT_EVENT) {
            StartManager.getInstance().logout();
        } else if (event.id == EventIdConstant.LAUNCH_SEARCH_RESULT_EVENT) {
            ARouter.getInstance().build(RouteConstant.PATH_SEARCH).with(event.bundle).navigation();
//            mPageStackManager.pushPage(new PageConfig.Builder(SearchResultPage.class).setPageBundle(event.bundle)
//                    .setUserGestureEnable(false)
//                    .setCanDragFromEdge(false)
//                    .setPushWithAnimation(false)
//                    .setPopWithAnimation(false).build());
        } else if (event.id == EventIdConstant.LAUNCH_SETTING_LANGUAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(SettingLanguagePage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.SETTING_LANGUAGE_ROUTE_PATH).with(event.bundle).navigation();
//        } else if (event.id == EventIdConstant.LAUNCH_SETTING_PUSH) {
//            mPageStackManager.pushPage(new PageConfig.Builder(PushSettingPage.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_MINE_MY_LIKE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(MyLikePage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.MY_LIKE_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MINE_USER_PERSONAL_INFO) {
//            mPageStackManager.pushPage(new PageConfig.Builder(PersonalInformationPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.EDIT_PROFILE_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MINE_USER_PERSONAL_EDIT_NAME) {
//            mPageStackManager.pushPage(new PageConfig.Builder(UserPersonalInfoEditNamePage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.EDIT_NAME_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MINE_USER_PERSONAL_EDIT_BRIEF) {
//            mPageStackManager.pushPage(new PageConfig.Builder(UserPersonalInfoEditBriefPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.EDIT_BRIE_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MINE_USER_PERSONAL_EDIT_SEX) {
//            mPageStackManager.pushPage(new PageConfig.Builder(EditMySexPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.EDIT_SEX_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE) {
            mPageStackManager.getActivity().recreate();

        } else if (event.id == EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_HOME) {
            mPageStackManager.clearPageStack();
            Bundle bundle;
            if (event.bundle != null) {
                bundle = event.bundle;
            } else {
                bundle = new Bundle();
            }
            bundle.putInt(FeedConstant.KEY_MAIN_PAGE_INDEX, MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION);
            mPageStackManager.pushPage(new PageConfig.Builder(MainPage.class).setPageBundle(bundle)
                    .setAlwaysRetain(true)
                    .setFitSystemWindow(false)
                    .setPushWithAnimation(false)
                    .setPopWithAnimation(false).build());
        } else if (event.id == EventIdConstant.LAUNCH_SEARCH_SUG_EVENT) {
            mPageStackManager.pushPage(new PageConfig.Builder(SearchSugPage.class).setPageBundle(event.bundle)
                    .setPopWithAnimation(false).setPushWithAnimation(false).setCanDragFromEdge(false).setUserGestureEnable(false).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_ASSIGNMENT_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(WebViewPage.class).setCanDragFromEdge(true).setUserGestureEnable(false).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.WEB_VIEW_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MAIN_HOME) {
            mPageStackManager.popToRootPage();
            List<PageConfig> configs = mPageStackManager.findPageConfigByType(MainPage.class);
            mPageStackManager.dispatchMessageToPagesByConfig(configs, Event.toMessage(MessageIdConstant.MESSAGE_LAUNCH_HOME_PAGE, event));
            ARouter.getInstance().build(RouteConstant.MAIN_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_HOME_TO_FORYOU) {
            List<PageConfig> configs = mPageStackManager.findPageConfigByType(MainPage.class);
            mPageStackManager.dispatchMessageToPagesByConfig(configs, Event.toMessage(MessageIdConstant.MESSAGE_LAUNCH_FORYOU_SUB, event));
            ARouter.getInstance().build(RouteConstant.MAIN_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_CHOICE_INTEREST_PAGE) {
            ARouter.getInstance().build(RouteConstant.PATH_USER_CHOICE_INTEREST).with(event.bundle).navigation();
//            mPageStackManager.pushPage(new PageConfig.Builder(ChoiceInterestActivity.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_COMMON_FEED_LIST) {
//            mPageStackManager.pushPage(new PageConfig.Builder(CommonFeedListPage.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_SHARE_PAGE) {
            Bundle bundle = event.bundle;
            String from = bundle.getString("from");
            int fromInt;
            try {
                fromInt = Integer.parseInt(from);
            } catch (Exception e) {
                fromInt = 3;
            }
            String postId = bundle.getString("postId");
            String imageUrl = bundle.getString("imageUrl");
            String nickName = bundle.getString("nickName");
            String content = bundle.getString("content");
            String entryType = bundle.getString("entry_type");
            String platform = bundle.getString("platform");
            ShareModel shareModel = new ShareModel();
            shareModel.setShareModelType(fromInt);
            shareModel.setImageUrl(imageUrl);
            shareModel.setContent(content);
            ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
            builder.with(mPageStackManager.getContext())
                    .login(AccountManager.getInstance().isLoginComplete())
                    .shareModel(shareModel)
                    .eventModel(new EventModel(EventLog1.Share.ContentType.H5, null, null, null, EventLog1.Share.PopPage.OTHER, null, null, null, null,
                            null, null, null, null, null, null, null, null))
                    .shortLinkModel(new ShortLinkModel(fromInt, postId))
                    .shareTitleModel(new ShareTitleModel(fromInt, nickName))
                    .waterMarkModel(new WaterMarkModel("", nickName, ""))
                    .entryType(entryType)
                    .sharePackage(ShareHelper.getSharePackage(platform))
                    .build().share();
        } else if (event.id == EventIdConstant.LAUNCH_PUBLISH_PAGE) {
            PublishPostStarter.INSTANCE.startActivityFromTopic(mPageStackManager.getContext(), event.bundle);
        } else if (event.id == EventIdConstant.LAUNCH_PASSER_BY_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(LocationPasserByPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_LOCATION_PASS).withBundle(LocationConstant.BUNDLE_KEY_LOCATION, event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_LOCATION_NEAR_BY_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(LocationMixPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_LOCATION_MIX).withBundle(LocationConstant.BUNDLE_KEY_LOCATION, event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_WEB_VIEW) {
//            mPageStackManager.pushPage(new PageConfig.Builder(WebViewPage.class).setCanDragFromEdge(true).setUserGestureEnable(false).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.WEB_VIEW_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(TopicLandingPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_TOPIC_LANDING).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_TOPIC_USER_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(TopicUserPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_TOPIC_USER).with(event.bundle).navigation();
//        } else if (event.id == EventIdConstant.LAUNCH_INTEREST_CATEGORY_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder((InterestCategoryDetailPage.class)).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_REPOER_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(ReportPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.REPORT_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_MAIN_MESSAGE) {
            mPageStackManager.popToRootPage();
            List<PageConfig> configs = mPageStackManager.findPageConfigByType(MainPage.class);
            mPageStackManager.dispatchMessageToPagesByConfig(configs, Event.toMessage(MessageIdConstant.MESSAGE_LAUNCH_MESSAGE_PAGE, event));
        } else if (event.id == EventIdConstant.LAUNCH_MAIN_MINE) {
            mPageStackManager.popToRootPage();
            List<PageConfig> configs = mPageStackManager.findPageConfigByType(MainPage.class);
            mPageStackManager.dispatchMessageToPagesByConfig(configs, Event.toMessage(MessageIdConstant.MESSAGE_LAUNCH_MINE_PAGE, event));
        } else if (event.id == EventIdConstant.LAUNCH_PUBLISH_SHORT_CUT_ENTRANCE_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(EditorShortCutEntrancePage.class).setTransitionEffect(TransitionEffect.Stack)
//                    .setPushWithAnimation(false).setCanDragFromEdge(false)
//                    .setUserGestureEnable(false).setPopWithAnimation(false)
//                    .setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_IM_STRANGER_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(StrangeSessionListPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.STRANGE_SESSION_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_BLOCK_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(BlockUsersPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.BLOCK_USER_ROUTE_PATH).with(event.bundle).navigation();

        } else if (event.id == EventIdConstant.LAUNCH_DEACTIVATE_CONFIRM_PAGE) {
            ARouter.getInstance().build(RouteConstant.PATH_USER_DEACT_CONFIRM).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_DEACTIVATE_RECALL_PAGE) {
            ARouter.getInstance().build(RouteConstant.PATH_USER_DEACT_FINAL).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_DEACTIVATE_PAGE) {
            ARouter.getInstance().build(RouteConstant.PATH_USER_DEACT_REASON).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_SETTING_PRIVACY_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(SettingPrivacyPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.SETTING_PRIVACY_ROUTE_PATH).with(event.bundle).navigation();

        } else if (event.id == EventIdConstant.LAUNCH_DEACTIVATE_NOTIFICATION_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(NotificationPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_NOTIFICATION_SETTING).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_QUITE_HOURS_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(QuiteTimePage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_QUITE_TIME).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_YOUTUBE_PLAYER) {
            YoutubePlayerActivity.player(mPageStackManager.getContext(), event.bundle);
        } else if (event.id == EventIdConstant.POP_TOP_PAGE) {
            mPageStackManager.popPage();
        } else if (event.id == EventIdConstant.LAUNCH_BLOCK_FOLLOWING_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(BlockFollowingUserPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.BLOCK_FOLLOW_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_BLOCK_SEARCH_PAGE) {
//            mPageStackManager.pushPage(new PageConfig.Builder(BlockSearchUserPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.BLOCK_SEARCH_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_CONTACT) {
//            mPageStackManager.pushPage(new PageConfig.Builder(ContactPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_CONTACTS).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_USER_INTEREST) {
//            mPageStackManager.pushPage(new PageConfig.Builder(UserInterestSelectPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.EDIT_INTEREST_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_RECOMMEND_USER_ACTIVITY) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_RECOMMEND_ACTIVITY).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_RECOMMEND_USER_PAGE) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_RECOMMEND_FOLLOW_PAGE).with(event.bundle).navigation();
//            mPageStackManager.pushPage(new PageConfig.Builder(RecommendFollowActivity.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_PAGE_IM) {
//            mPageStackManager.pushPage(new PageConfig.Builder(RouteImChatPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.ROUTE_CHAT_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_PAGE_TOPIC_LIST) {
//            mPageStackManager.pushPage(new PageConfig.Builder(TopicListPage.class).setPageBundle(event.bundle).build());
            ARouter.getInstance().build(RouteConstant.PATH_TOPIC_LIST).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_PROFILE_BIND_SOCIAL) {
            ARouter.getInstance().build(RouteConstant.SOCIAL_HOST_ROUTE_PATH).with(event.bundle).navigation();
            //mPageStackManager.pushPage(new PageConfig.Builder(UserSocialHostPage.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_SUPER_TOPIC_PAGE) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_SUPER_TOPIC_PAGE).with(event.bundle).navigation();
            //mPageStackManager.pushPage(new PageConfig.Builder(SuperTopicLandingActivity.class).setPageBundle(event.bundle).build());
        } else if (event.id == EventIdConstant.LAUNCH_BROWSE_LATEST_CAMPAIGN) {
            ARouter.getInstance().build(RouteConstant.PATH_LATEST_CAMPAIGN).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_POST_ARTICLE_PAGE) {
            ARouter.getInstance().build(RouteConstant.PATH_ARTICLE_DETAIL).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_REGISTER_ACTIVITY) {
            ARouter.getInstance().build(RouteConstant.PATH_START_REGISTER_PAGE).with(event.bundle).navigation();
//            mPageStackManager.pushPage(new PageConfig.Builder(ArticleDetailPage.class).setPageBundle(event.bundle).build());
//        } else if (event.id == EventIdConstant.LAUNCH_PAGE_REPORT_DESC) {
//            ARouter.getInstance().build(RouteConstant.REPORT_DESC_ROUTE_PATH).with(event.bundle).navigation();
//        } else if (event.id == EventIdConstant.LAUNCH_PROFILE_PHOTO_PREVIEW) {
//            ARouter.getInstance().build(RouteConstant.PROFILE_PHOTO_PREVIEW_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_VERIFY_DIALOG) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_VERIFY_DIALOG).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_VERIFY_PAGE) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_VERIFY_ACTIVITY).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_LOGIN_PAGE) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_LOGIN_ACTIVITY).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_SIGN_LOGIN_PAGE) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_SIGN_LOGIN_ACTIVITY).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_VERIFY_PHONE_PAGE) {
            ARouter.getInstance().build(RouteConstant.LAUNCH_VERIFY_PHONE_ACTIVITY).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_PAGE_REPORT_DESC) {
            ARouter.getInstance().build(RouteConstant.REPORT_DESC_ROUTE_PATH).with(event.bundle).navigation();
        } else if (event.id == EventIdConstant.LAUNCH_PROFILE_PHOTO_PREVIEW) {
            ARouter.getInstance().build(RouteConstant.PROFILE_PHOTO_PREVIEW_ROUTE_PATH).with(event.bundle).navigation();
        }

    }
}

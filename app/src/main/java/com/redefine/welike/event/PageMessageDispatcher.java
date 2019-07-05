package com.redefine.welike.event;

import android.os.Message;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.business.feeds.ui.page.MainPage;
import com.redefine.welike.sync.SyncProfileSettingsHelper;

/**
 * Created by liwenbo on 2018/3/5.
 */

public class PageMessageDispatcher implements IMessageDispatcher {
    private final IPageStackManager mPageStackManager;

    public PageMessageDispatcher(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == MessageIdConstant.MESSAGE_DELETE_POST) {
            mPageStackManager.dispatchMessageToAll(message);
        } else if (message.what == MessageIdConstant.MESSAGE_COMMENT_PUBLISH) {
//            mPageStackManager.dispatchMessageToPage(FeedDetailPage.class, message, false);
//            mPageStackManager.dispatchMessageToPage(CommentDetailPage.class, message, false);
//            mPageStackManager.dispatchMessageToPage(ArticleDetailPage.class, message, false);
        } else if (message.what == MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH) {
//            mPageStackManager.dispatchMessageToPage(FeedDetailPage.class, message, false);
//            mPageStackManager.dispatchMessageToPage(ArticleDetailPage.class, message, false);
//        } else if (message.what == MessageIdConstant.MESSAGE_COMMENT_PUBLISH) {
//            mPageStackManager.dispatchMessageToPage(FeedDetailPage.class, message, false);
//            mPageStackManager.dispatchMessageToPage(CommentDetailPage.class, message, false);
//        } else if (message.what == MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH) {
//            mPageStackManager.dispatchMessageToPage(FeedDetailPage.class, message, false);
        } else if (message.what == MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT) {
//            mPageStackManager.dispatchMessageToPage(WebViewPage.class, message, true);
            mPageStackManager.dispatchMessageToPage(MainPage.class, message, true);
        } else if (message.what == MessageIdConstant.MESSAGE_EDIT_NICK_NAME_SUCCESS) {
//            mPageStackManager.dispatchMessageToPage(PersonalInformationPage.class, message, true);
        } else if (message.what == MessageIdConstant.MESSAGE_EDIT_SEX_SUCCESS) {
//            mPageStackManager.dispatchMessageToPage(PersonalInformationPage.class, message, true);
        } else if (message.what == MessageIdConstant.MESSAGE_EDIT_INTRODUCTION_SUCCESS) {
//            mPageStackManager.dispatchMessageToPage(PersonalInformationPage.class, message, true);
        } else if (message.what == MessageIdConstant.MESSAGE_SYNC_ACCOUNT_PROFILE) {
            SyncProfileSettingsHelper.modifyIMMessagesCursor((String) message.obj, mPageStackManager.getContext());
        }else if(message.what == MessageIdConstant.MESSAGE_SYNC_NEW_HOME_FEED){
            mPageStackManager.dispatchMessageToPage(MainPage.class, message, true);
        }else if(message.what == MessageIdConstant.MESSAGE_SHOW_HOME_REFRESH){
            mPageStackManager.dispatchMessageToPage(MainPage.class, message, true);
        }else if(message.what == MessageIdConstant.MESSAGE_HIDE_HOME_REFRESH){
            mPageStackManager.dispatchMessageToPage(MainPage.class, message, true);
        }else if(message.what == MessageIdConstant.MESSAGE_SYNC_NEW_LEAST_FEED){
            mPageStackManager.dispatchMessageToPage(MainPage.class, message, true);
        } else if (message.what == MessageIdConstant.MESSAGE_SYNC_BLOCK_USER) {
            //TODO
//            mPageStackManager.dispatchMessageToPage(BlockUsersPage.class, message, false);
        }
    }
}

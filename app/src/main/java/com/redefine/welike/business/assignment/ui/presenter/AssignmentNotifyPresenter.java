package com.redefine.welike.business.assignment.ui.presenter;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;

import com.pekingese.pagestack.framework.page.BasePage;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.assignment.ui.contract.IAssignmentNotifyContract;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by liwenbo on 2018/3/22.
 */

public class AssignmentNotifyPresenter implements IAssignmentNotifyContract.IAssignmentNotifyPresenter {

    @Override
    public void onPageStateChange(BasePage basePage, int oldPageState, int newPageState) {
        if (basePage == null) {
            return ;
        }

        if (newPageState != BasePage.PAGE_STATE_DESTROY) {
            return ;
        }

        if (basePage.getPageConfig() == null) {
            return ;
        }

        if (basePage.getPageConfig().pageBundle == null) {
            return ;
        }
        Message message;
        String type = basePage.getPageConfig().pageBundle.getString(RouteConstant.ROUTE_KEY_ENTRY_TYPE);
        if (TextUtils.equals(type, RouteConstant.ROUTE_ENTRY_TYPE_VALUE_ASSIGNMENT)) {
            message = Message.obtain();
            message.what = MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT;
            EventBus.getDefault().post(message);
        }
    }

    public static void checkNotify(Intent intent) {
        if (intent != null) {
            String entryType = intent.getStringExtra(RouteConstant.ROUTE_KEY_ENTRY_TYPE);
            if (TextUtils.equals(entryType, RouteConstant.ROUTE_ENTRY_TYPE_VALUE_ASSIGNMENT)) {
                Message message = Message.obtain();
                message.what = MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT;
                EventBus.getDefault().post(message);
            }
        }
    }
}

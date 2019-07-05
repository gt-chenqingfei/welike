package com.redefine.welike.business.im.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.im.room.SESSION;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.constant.ImConstant;
import com.redefine.welike.commonui.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 路由跳转到ImChatPage页面到中转页面
 * Created by nianguowang on 2018/7/28
 */
@Route(path = RouteConstant.ROUTE_CHAT_ROUTE_PATH)
public class RouteImChatPage extends BaseActivity {

    private String mUserId;
    private String mNickName;
    private String mUserAvatar;
    private boolean mServiceSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            finish();
            return;
        }
        parseBundle(extras);

        initData();
    }

    private void initData() {
        if (mServiceSession) {
            IMHelper.INSTANCE.getCustomerSession(new Function1<SESSION, Unit>() {
                @Override
                public Unit invoke(final SESSION session) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ImConstant.IM_SESSION_KRY, session);
                            bundle.putBoolean(ImConstant.IM_SESSION_CUSTOMER, true);
                            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
                        }
                    });
                    return null;
                }
            }, new Function1<Integer, Unit>() {
                @Override
                public Unit invoke(Integer integer) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "create_chat_failed"));
                        }
                    });
                    return null;
                }
            });
            return;
        }
        if (TextUtils.isEmpty(mUserId) || TextUtils.isEmpty(mNickName) || TextUtils.isEmpty(mUserAvatar)) {
            finish();
            return;
        }

        IMHelper.INSTANCE.getSession(mUserId, mNickName, mUserAvatar, new Function1<SESSION, Unit>() {
            @Override
            public Unit invoke(final SESSION session) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        finish();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ImConstant.IM_SESSION_KRY, session);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
                    }
                });
                return null;
            }
        });
    }

    private void parseBundle(Bundle pageBundle) {
        mUserId = pageBundle.getString(RouteConstant.ROUTE_KEY_UID);
        mNickName = pageBundle.getString(RouteConstant.ROUTE_KEY_NICKNAME);
        mUserAvatar = pageBundle.getString(RouteConstant.ROUTE_KEY_AVATAR);
        mServiceSession = pageBundle.getBoolean(RouteConstant.ROUTE_KEY_SERVICESESSION);


    }

}

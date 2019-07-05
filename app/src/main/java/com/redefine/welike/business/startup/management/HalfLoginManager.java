package com.redefine.welike.business.startup.management;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.CountCallBack;
import com.redefine.welike.business.startup.management.bean.HalfAccountBean;
import com.redefine.welike.business.startup.management.bean.ResultBean;
import com.redefine.welike.business.startup.management.bean.UserinfoBean;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.request.CheckLoginStateRequest;
import com.redefine.welike.business.startup.ui.dialog.LoginDialog;
import com.redefine.welike.business.startup.ui.dialog.SignOrLoginDialog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;

/**
 * @author redefine honlin
 * @Date on 2018/11/2
 * @Description
 */
public class HalfLoginManager {

    private static HalfLoginManager halfLoginManager;

    public boolean isExistAccount = false;

    public HalfAccountBean userInfoBean;

    public static HalfLoginManager getInstancce() {

        if (halfLoginManager == null) {
            halfLoginManager = new HalfLoginManager();
        }
        return halfLoginManager;
    }


    //检测账号是否存在
    public void checkIsExistAccount() {

        try {

            if (AccountManager.getInstance().isLogin()) return;

            new CheckLoginStateRequest(MyApplication.getAppContext()).request(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isExistAccount = false;

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {


                    Gson gson = new Gson();
                    ResultBean resultBean = gson.fromJson(result.toString(), ResultBean.class);

                    if (resultBean != null) {

                        UserinfoBean userinfoBean1 = resultBean.getUserinfo();
                        if (userinfoBean1 != null) {
                            isExistAccount = true;

                            userInfoBean = new HalfAccountBean(userinfoBean1.getNickName(),
                                    TextUtils.isEmpty(userinfoBean1.getPhone()) ? "" : userinfoBean1.getPhone(), userinfoBean1.getSource(),
                                    userinfoBean1.getRegisterWay(), userinfoBean1.getStatus(), userinfoBean1.getAvatarUrl());

                        } else {
                            isExistAccount = false;
                        }
                    } else {
                        isExistAccount = false;
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            isExistAccount = false;
        }

    }

    public void showLoginDialog(Context context, RegisterAndLoginModel eventModel) {


        if (isExistAccount) {
            LoginDialog.Companion.show(context, eventModel);
        } else {
            SignOrLoginDialog.Companion.show(context, eventModel);
        }
    }

    public static void updateClickCount(final RegisterAndLoginModel model) {

        BrowseEventStore.INSTANCE.updateClickCount(new CountCallBack() {
            @Override
            public void onLoadEntity(int count) {
                if (count == 3) {
                    Account account = AccountManager.getInstance().getAccount();

                    if (account != null && account.getStatus() == Account.ACCOUNT_HALF) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(RegisteredConstant.WHEN_FINISH_NEED_LAUNCH_MAIN, false);
                        bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, model);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));
                    }
                }
            }
        });


    }


}

package com.redefine.welike.business.mysetting.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.mysetting.management.request.ContactOptionRequest;
import com.redefine.welike.business.mysetting.management.request.ContactsChangeRequest;
import com.redefine.welike.business.mysetting.management.request.FollowChangeRequest;
import com.redefine.welike.business.mysetting.ui.listener.IUserOptionListener;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honglin on 2018/5/18.
 */

public class OptionManager {


    private String TAG = "hl";

    private String followByContact = "followByContact";

    private String notifyNewContactUser = "notifyNewContactUser";


    private ContactOptionRequest contactOptionRequest;
    private FollowChangeRequest followChangeRequest;
    private ContactsChangeRequest contactsChangeRequest;

    private static class OptionManagerHolder {
        public static OptionManager instance = new OptionManager();
    }

    public static OptionManager getInstance() {
        return OptionManagerHolder.instance;
    }

    private IUserOptionListener listener;

    public void setListener(IUserOptionListener listener) {
        this.listener = listener;
    }

    public void check() {
        contactOptionRequest = new ContactOptionRequest(MyApplication.getAppContext());

        try {
            contactOptionRequest.check(requestCallback);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(false, false);
                }
            });
        }
    }

    public void changeFollowOption(boolean isFollow) {
        followChangeRequest = new FollowChangeRequest(MyApplication.getAppContext());

        try {
            followChangeRequest.change(isFollow ? 1 : 0, requestCallback);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(false, false);
                }
            });
        }
    }

    public void changeContactsOption(boolean isContacts) {
        contactsChangeRequest = new ContactsChangeRequest(MyApplication.getAppContext());

        try {
            contactsChangeRequest.change(isContacts ? 1 : 0, requestCallback);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(false, false);
                }
            });
        }
    }


    private RequestCallback requestCallback = new RequestCallback() {
        @Override
        public void onError(BaseRequest request, int errCode) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(false, false);
                }
            });

        }

        @Override
        public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

            final JSONObject result1 = result;

            if (request instanceof ContactOptionRequest) {
                if (request != contactOptionRequest) return;
            }
            if (request instanceof FollowChangeRequest) {
                if (request != followChangeRequest) return;
            }
            if (request instanceof ContactsChangeRequest) {
                if (request != contactsChangeRequest) return;
            }
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {

                    if (result1 != null) {

                        boolean isFollow, isContacts;
                        if (result1.containsKey(followByContact)) {
                            isFollow = result1.getBooleanValue(followByContact);
                        } else {
                            isFollow = SpManager.Setting.getSettingFollowByContactsModel(MyApplication.getAppContext());
                        }
                        if (result1.containsKey(notifyNewContactUser)) {
                            isContacts = result1.getBooleanValue(notifyNewContactUser);
                        } else {
                            isContacts = SpManager.Setting.getSettingMobileContactsModel(MyApplication.getAppContext());
                        }
                        if (listener != null)
                            listener.onOptionChanged(isFollow, isContacts);

                    } else {
                        if (listener != null) listener.onOptionChanged(false, false);
                    }

                }
            });
        }
    };

}

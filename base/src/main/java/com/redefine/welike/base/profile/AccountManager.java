package com.redefine.welike.base.profile;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.welike.base.DBStore;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.dao.welike.DaoSession;
import com.redefine.welike.base.dao.welike.Profile;
import com.redefine.welike.base.dao.welike.ProfileDao;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.request.AddLinkRequest;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.request.UpdateAccountRequest;
import com.redefine.welike.base.request.UpdateInterestRequest;
import com.redefine.welike.base.request.UpdateLinkRequest;
import com.redefine.welike.base.uploading.UploadingManager;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/1/5.
 */

public class AccountManager extends BroadcastManagerBase implements UploadingManager.UploadingCallback, RequestCallback {
    private Context context;
    private Account account;
    private MutableLiveData<Account> liveAccount = new MutableLiveData<>();
    private UpdateAccountRequest updateRequest;
    private UpdateInterestRequest updateInterestRequest;
    private AddLinkRequest addLinkRequest;
    private UpdateLinkRequest updateLinkRequest;
    private HeadUploadTask headUploadTask;

    public interface AccountCallback {

        void onModified();

        void onModifyFailed(int errCode);

    }

    private class HeadUploadTask {
        public String uploadId;
        public String localFileName;

        public void end() {
            File f = new File(localFileName);
            if (f.exists() && f.isFile()) {
                f.delete();
            }
        }
    }

    private static class AccountManagerHolder {
        public static AccountManager instance = new AccountManager();
    }

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        return AccountManagerHolder.instance;
    }

    public void register(AccountCallback listener) {
        super.register(listener);
    }

    public void unregister(AccountCallback listener) {
        super.unregister(listener);
    }

    public void init(Context context) {
        this.context = context;
        getAccount();
        UploadingManager.getInstance().register(this);
    }

    public void updateAccount(final Account newAccount) {
        account = newAccount;
        DaoSession daoSession = DBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            try {
                daoSession.runInTx(new Runnable() {

                    @Override
                    public void run() {
                        refreshAccountInDB(newAccount);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public Account getAccount() {
        if (account == null) {
            DaoSession daoSession = DBStore.getInstance().getDaoSession();
            if (daoSession != null) {
                ProfileDao profileDao = daoSession.getProfileDao();
                if (profileDao != null) {
                    synchronized (this) {
                        try {
                            account = new AccountParserHelper().profileToAccount(profileDao.queryBuilder().unique());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (account != null) {
                liveAccount.postValue(account);
            }
        }
        return account;
    }

    public synchronized void logout() {
        DaoSession daoSession = DBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            ProfileDao profileDao = daoSession.getProfileDao();
            if (profileDao != null) {
                if (account != null) {
                    account.setLogin(false);
                    liveAccount.postValue(account);
                    try {
                        Profile profile = new AccountParserHelper().accountToProfile(account);
                        profileDao.update(profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void modifyAccountHeadUrl(String fileName) {
        modifyAccountHeadUrl(fileName, null);
    }

    public void modifyAccountHeadUrl(String fileName, UploadListener listener) {
        if (TextUtils.isEmpty(fileName)) return;

        File file = new File(fileName);
        if (!file.exists()) return;

        String suffix = WeLikeFileManager.parseTmpFileSuffix(fileName);
        headUploadTask = new HeadUploadTask();
        headUploadTask.uploadId = UploadingManager.getInstance().upload(null, fileName, suffix, UploadingManager.UPLOAD_TYPE_IMG, listener);
        headUploadTask.localFileName = fileName;
    }

    public void modifyAccount(Account account) {
        if (account != null) {
            if (updateRequest == null) {
                updateRequest = new UpdateAccountRequest(account, context);
                try {
                    updateRequest.req(this);
                } catch (Exception e) {
                    e.printStackTrace();
                    broadcast(ErrorCode.networkExceptionToErrCode(e));
                }
            }
        }
    }

    public void modifyAccountInterest(List<UserBase.Intrest> interestIds) {
        if (updateInterestRequest == null) {
            updateInterestRequest = new UpdateInterestRequest(context, interestIds);
        }
        try {
            updateInterestRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            broadcast(ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void addAccountLink(int linkType, String link) {
        if (addLinkRequest == null) {
            addLinkRequest = new AddLinkRequest(context);
        }
        try {
            addLinkRequest.req(linkType, link, this);
        } catch (Exception e) {
            e.printStackTrace();
            broadcast(ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void updateAccountLink(long linkId, int linkType, String link) {
        if (updateLinkRequest == null) {
            updateLinkRequest = new UpdateLinkRequest(context);
        }
        try {
            updateLinkRequest.req(linkId, linkType, link, this);
        } catch (Exception e) {
            e.printStackTrace();
            broadcast(ErrorCode.networkExceptionToErrCode(e));
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (updateRequest == request) {
            updateRequest = null;
            broadcast(errCode);
        } else if (updateInterestRequest == request) {
            updateInterestRequest = null;
            broadcast(errCode);
        } else if (addLinkRequest == request) {
            addLinkRequest = null;
            broadcast(errCode);
        } else if (updateLinkRequest == request) {
            updateLinkRequest = null;
            broadcast(errCode);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (updateRequest == request) {
            parseAccount(result);
        } else if (updateInterestRequest == request) {
            updateAccountInterest();
        } else if (addLinkRequest == request) {
            updateAccountLink(addLinkRequest);
        } else if (updateLinkRequest == request) {
            updateAccountLink(updateLinkRequest);
        }
    }

    private void updateAccountLink(BaseRequest baseRequest) {
        List<UserBase.Link> links = null;
        try {
            links = (List<UserBase.Link>) baseRequest.getUserInfo(UpdateLinkRequest.UPDATE_ACCOUNT_LINK_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (baseRequest == addLinkRequest) {
            addLinkRequest = null;
        } else if (baseRequest == updateLinkRequest) {
            updateLinkRequest = null;
        }
        account.setLinks(links);
        broadcast(ErrorCode.ERROR_SUCCESS);
        updateAccount(account);
    }

    private void updateAccountInterest() {
        List<UserBase.Intrest> intrests = null;
        try {
            intrests = (List<UserBase.Intrest>) updateInterestRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_INTERESTS_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateInterestRequest = null;
        account.setIntrests(intrests);
        broadcast(ErrorCode.ERROR_SUCCESS);
        updateAccount(account);
    }

    public void parseAccount(JSONObject result) {
        String nickName = null;
        byte sex = 0;
        String head = null;
        String intro = null;
        int id = 0;
        int completeLevel = 0;
        boolean allowUpdateNickName = false;
        boolean allowUpdateSex = false;
        long nextUpdateNickNameDate = 0;
        int sexUpdateCount = 0;
        List<UserBase.Intrest> intrests = null;
        int status = 0;
        int curLevel = 0;
        int changeNameCount = 0;
        List<UserBase.Link> links = null;
        try {
            nickName = (String) updateRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_NICK_NAME_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sex = (byte) updateRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_SEX_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            head = (String) updateRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_HEAD_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intro = (String) updateRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_INTRO_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            completeLevel = (int) updateRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_FINISH_LEVEL_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            allowUpdateNickName = result.getBooleanValue("allowUpdateNickName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            allowUpdateSex = result.getBooleanValue("allowUpdateSex");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            nextUpdateNickNameDate = result.getLongValue("nextUpdateNickNameDate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sexUpdateCount = result.getIntValue("sexUpdateCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            status = result.getIntValue("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            id = result.getIntValue("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            intrests = (List<UserBase.Intrest>) updateRequest.getUserInfo(UpdateAccountRequest.UPDATE_ACCOUNT_INTERESTS_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            curLevel = result.getIntValue("curLevel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            changeNameCount = result.getIntValue("changeNameCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateRequest = null;
        account.setNickName(nickName);
        account.setSex(sex);
        account.setUid(String.valueOf(id));
        account.setHeadUrl(head);
        account.setAllowUpdateNickName(allowUpdateNickName);
        account.setAllowUpdateSex(allowUpdateSex);
        account.setNextUpdateNickNameDate(nextUpdateNickNameDate);
        account.setSexUpdateCount(sexUpdateCount);
        account.setIntroduction(intro);
        account.setCompleteLevel(completeLevel);
        account.setIntrests(intrests);
        account.setStatus(status);
        account.setCurLevel(curLevel);
        account.setChangeNameCount(changeNameCount);
        broadcast(ErrorCode.ERROR_SUCCESS);
        account.setLogin(true);
        updateAccount(account);
    }

    @Override
    public void onUploadingProcess(String objKey, float process) {
    }

    @Override
    public void onUploadingCompleted(String objKey, String url) {
        if (headUploadTask == null) return;
        if (TextUtils.isEmpty(headUploadTask.uploadId)) return;
        if (TextUtils.equals(headUploadTask.uploadId, objKey)) {
            if (account != null) {
                headUploadTask.end();
                headUploadTask = null;
                Account tmpAcc = account.copy();
                tmpAcc.setHeadUrl(url);
                reqUpdateAccount(tmpAcc);
            } else {
                headUploadTask.end();
                headUploadTask = null;
                broadcast(ErrorCode.ERROR_NETWORK_USER_NOT_FOUND);
            }
        }
    }

    @Override
    public void onUploadingFailed(String objKey) {
        if (headUploadTask == null) return;
        if (TextUtils.isEmpty(headUploadTask.uploadId)) return;
        if (TextUtils.equals(headUploadTask.uploadId, objKey)) {
            headUploadTask.end();
            headUploadTask = null;
            broadcast(ErrorCode.ERROR_NETWORK_UPLOAD_FAILED);
        }
    }

    private void refreshAccountInDB(final Account account) {
        DaoSession daoSession = DBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            ProfileDao profileDao = daoSession.getProfileDao();
            if (profileDao != null) {
                synchronized (this) {
                    profileDao.deleteAll();
                    Profile dbProfile = new AccountParserHelper().accountToProfile(account);
                    profileDao.insert(dbProfile);
                    this.account = account;
                    liveAccount.postValue(account);
                }
            }
        }
    }

    private void reqUpdateAccount(Account account) {
        if (account != null) {
            if (updateRequest == null) {
                updateRequest = new UpdateAccountRequest(account, context);
                try {
                    updateRequest.req(this);
                } catch (Exception e) {
                    e.printStackTrace();
                    broadcast(ErrorCode.networkExceptionToErrCode(e));
                }
            }
        }
    }

    private void broadcast(final int errCode) {
        updateRequest = null;
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (errCode == ErrorCode.ERROR_SUCCESS) {
                    synchronized (listenerRefList) {
                        for (int i = 0; i < listenerRefList.size(); i++) {
                            ListenerRefExt callbackRef = listenerRefList.get(i);
                            Object l = callbackRef.getListener();
                            if (l != null && l instanceof AccountCallback) {
                                AccountCallback listener = (AccountCallback) l;
                                listener.onModified();
                            }
                        }
                    }
                } else {
                    synchronized (listenerRefList) {
                        for (int i = 0; i < listenerRefList.size(); i++) {
                            ListenerRefExt callbackRef = listenerRefList.get(i);
                            Object l = callbackRef.getListener();
                            if (l != null && l instanceof AccountCallback) {
                                AccountCallback listener = (AccountCallback) l;
                                listener.onModifyFailed(errCode);
                            }
                        }
                    }
                }
            }

        });
    }

    public boolean isLoginComplete() {
        return account != null &&
                account.getCompleteLevel() >= Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE &&
                account.getStatus() != Account.ACCOUNT_DEACTIVATE &&
                account.isLogin();

    }

    public boolean isLogin() {
        return account != null &&
                account.isLogin();
    }

    public boolean isHalfLogin() {
        return account != null &&
                account.getStatus() == Account.ACCOUNT_HALF &&
                account.isLogin();
    }

    public boolean isDeactive() {
        return account != null &&
                account.getStatus() == Account.ACCOUNT_DEACTIVATE &&
                account.isLogin();
    }


    public MutableLiveData<Account> getLiveAccount() {
        return liveAccount;
    }

    @Deprecated
    public boolean isSelf(String uid) {
        if (account != null && account.isLogin()) {
            return TextUtils.equals(uid, account.getUid());
        } else {
            return false;
        }

    }
}

package com.redefine.welike.base.upgraded;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.StorageDBStore;
import com.redefine.welike.base.dao.storage.DaoSession;
import com.redefine.welike.base.dao.storage.Upgraded;
import com.redefine.welike.base.dao.storage.UpgradedDao;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.request.UpgradedRequest;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by liubin on 2018/3/5.
 */

public class UpdateHelper implements RequestCallback {
    private Context context;
    private String os;
    private String updateContent;
    private String version;
    private String updateTitle;
    private String url;
    private int updateType;

    private static class UpdateHelperHolder {
        public static UpdateHelper instance = new UpdateHelper();
    }

    private UpdateHelper() {
    }

    public static UpdateHelper getInstance() {
        return UpdateHelperHolder.instance;
    }

    public void init(Context context) {
        this.context = context;
        updateType = 0;
        delete();
    }

    public void checkUpgraded() {
        UpgradedRequest request = new UpgradedRequest(context);
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getOs() {
        return os;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public String getVersion() {
        return version;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (updateType == 2) {
            SpManager.Setting.setCurrentForceUpgraded(true, context);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if(result == null) {
            return;
        }
        if(!result.containsKey("version")) {
            return;
        }
        if(!result.containsKey("updateType")) {
            return;
        }
        version = result.getString("version");
        updateType = result.getIntValue("updateType");

        os = result.getString("operationSystem");
        if(os == null) {
            os = "";
        }
        updateContent = result.getString("updateContent");
        if(updateContent == null) {
            updateContent = "";
        }
        updateTitle = result.getString("updateTitle");
        if(updateTitle == null) {
            updateTitle = "";
        }
        url = result.getString("url");
        if(url == null) {
            url = "";
        }

        if (updateType == 1) {
            SpManager.Setting.setCurrentUpgraded(true, context);
            SpManager.Setting.setCurrentForceUpgraded(false, context);
            save();
        } else if (updateType == 2) {
            SpManager.Setting.setCurrentUpgraded(false, context);
            SpManager.Setting.setCurrentForceUpgraded(true, context);
            save();
        } else {
            SpManager.Setting.setCurrentUpgraded(false, context);
            SpManager.Setting.setCurrentForceUpgraded(false, context);
            delete();
        }
    }

    private void save() {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {

                try {
                    DaoSession daoSession = StorageDBStore.getInstance().getDaoSession();
                    if (daoSession != null) {
                        UpgradedDao upgradedDao = daoSession.getUpgradedDao();
                        if (upgradedDao != null) {
                            upgradedDao.deleteAll();
                            Upgraded upgraded = new Upgraded();
                            upgraded.setUpdateId(CommonHelper.generateUUID());
                            upgraded.setOs(os);
                            upgraded.setUpdateContent(updateContent);
                            upgraded.setVersion(version);
                            upgraded.setUpdateTitle(updateTitle);
                            upgraded.setUrl(url);
                            upgraded.setUpdateType(updateType);
                            upgradedDao.insert(upgraded);
                        }
                    }
                } catch (Throwable e) {
                    // do nothing
                }

            }
        });
    }

    private void delete() {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    DaoSession daoSession = StorageDBStore.getInstance().getDaoSession();
                    if (daoSession != null) {
                        UpgradedDao upgradedDao = daoSession.getUpgradedDao();
                        if (upgradedDao != null) {
                            upgradedDao.deleteAll();
                        }
                    }
                } catch (Throwable e) {
                    //do nothing
                }
            }
        });

    }

}

package com.redefine.welike.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.redefine.commonui.dialog.SelfStartConfimDialog;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.UnLoginEventManager;

import java.util.ArrayList;
import java.util.List;


public class PushIntentWrapper {
    public static final int VIVO = 100;
    public static final int OPPO = 101;

    protected Intent intent;
    protected int type;

    public PushIntentWrapper(Intent intent, int type) {
        this.intent = intent;
        this.type = type;
    }

    private static List<PushIntentWrapper> sIntentWrapperList;

    public static List<PushIntentWrapper> getIntentWrapperList() {

        if (null == sIntentWrapperList) {
            sIntentWrapperList = new ArrayList<>();
        }else {
            sIntentWrapperList.clear();
        }


        //Oppo self-start
        Intent oppoIntent = new Intent();
        oppoIntent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity"));
        sIntentWrapperList.add(new PushIntentWrapper(oppoIntent, OPPO));
/*
        //Oppo self-start-old
        Intent oppoOldIntent = new Intent();
        oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.permission.startup.StartupAppListActivity"));
        sIntentWrapperList.add(new IntentWrapper(oppoOldIntent, OPPO_OLD));*/

        //Vivo self-start
        Intent vivoGodIntent = new Intent();
        vivoGodIntent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
        sIntentWrapperList.add(new PushIntentWrapper(vivoGodIntent, VIVO));

        return sIntentWrapperList;
    }

    public boolean doesActivityExists() {
        PackageManager pm = MyApplication.getApp().getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    protected void startActivitySafely(Activity activityContext) {
        try {
            activityContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<PushIntentWrapper> whiteListMatters(final Activity a, String reason) {
        List<PushIntentWrapper> showed = new ArrayList<>();

        if (reason == null) reason = "";

        List<PushIntentWrapper> intentWrapperList = getIntentWrapperList();

        for (final PushIntentWrapper iw : intentWrapperList) {
            if (!iw.doesActivityExists()) continue;
            switch (iw.type) {
                case VIVO:
                    UnLoginEventManager.INSTANCE.report30();

                    SelfStartConfimDialog.showConfirmDialog(a, a.getString(R.string.self_start_vivo), a.getString(R.string.self_start_yes), "vivo_self_start.json", new SelfStartConfimDialog.IConfirmDialogListener() {                        @Override
                        public void onClickCancel() {
                            int count=SpManager.Setting.getNotificationSelfStartCount(a);
                            SpManager.Setting.setNotificationSelfStartCount(++count,a);
                            SpManager.Setting.setNotificationSelfStartTime(a);
                            UnLoginEventManager.INSTANCE.report31(EventConstants.UNLOGIN_SELECT_TYPE_NO);

                        }

                        @Override
                        public void onClickConfirm() {
                            SpManager.Setting.setNotificationSelfStartTime(a);

                            SpManager.Setting.setNotificationSelfStart(true,a);
                            iw.startActivitySafely(a);
                            UnLoginEventManager.INSTANCE.report31(EventConstants.UNLOGIN_SELECT_TYPE_YES);

                        }
                    });
                    showed.add(iw);
                    break;
                case OPPO:
                    UnLoginEventManager.INSTANCE.report30();

                    SelfStartConfimDialog.showConfirmDialog(a, a.getString(R.string.self_start_oppo), a.getString(R.string.self_start_yes), "oppo_self_start.json", new SelfStartConfimDialog.IConfirmDialogListener() {                        @Override
                        public void onClickCancel() {
                            int count=SpManager.Setting.getNotificationSelfStartCount(a);
                            SpManager.Setting.setNotificationSelfStartCount(++count,a);
                            SpManager.Setting.setNotificationSelfStartTime(a);
                            UnLoginEventManager.INSTANCE.report31(EventConstants.UNLOGIN_SELECT_TYPE_NO);

                        }

                        @Override
                        public void onClickConfirm() {
                            SpManager.Setting.setNotificationSelfStart(true,a);
                            iw.startActivitySafely(a);
                            UnLoginEventManager.INSTANCE.report31(EventConstants.UNLOGIN_SELECT_TYPE_YES);

                        }
                    });
                    showed.add(iw);

                    break;


            }


        }


        return showed;
    }

}

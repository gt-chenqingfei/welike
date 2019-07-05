package com.redefine.welike.keepalive.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SyncService extends Service {
    private static final Object syncLock = new Object();
    private static SyncAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

    class SyncAdapter extends AbstractThreadedSyncAdapter {


        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
            Log.d("lizard", "SyncAdapter-->onPerformSync--1");
        }

        public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
            super(context, autoInitialize, allowParallelSyncs);
            Log.d("lizard", "SyncAdapter-->onPerformSync--2");

        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        }
    }


    public static void startAccountSync(Context context) {
        Log.d("lizard", "----startAccountSync------");
        String accountType = "com.redefine.welike.account";
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = null;

        Account[] accounts = new Account[0];
        if (accountManager != null) {
            accounts = accountManager.getAccountsByType(accountType);
        }
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = new Account("Welike", accountType);
        }
        try {
            if (accountManager != null && accountManager.addAccountExplicitly(account, null, null)) {
                String authority = accountType;
                long sync_interval = 15 * 60;
                ContentResolver.setIsSyncable(account, authority, 1);
                ContentResolver.setSyncAutomatically(account, authority, true);
                ContentResolver.addPeriodicSync(account, authority, new Bundle(), sync_interval);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}

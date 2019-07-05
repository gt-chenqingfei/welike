package com.redefine.welike.keepalive;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.redefine.welike.IKeepAliveInterface;

public class RemoteService extends Service {
    private MyBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IKeepAliveInterface iMyAidlInterface = IKeepAliveInterface.Stub.asInterface(service);
            Log.d("lizard","RemoteService-->onServiceConnected");
            try {
                Log.d("Lizard", "connected with " + iMyAidlInterface.getServiceName());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("lizard","RemoteService-->onServiceDisconnected");
            bindService(new Intent(RemoteService.this,LocalService.class),connection, Context.BIND_IMPORTANT);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("lizard","RemoteService-->onStartCommand");
        bindService(new Intent(this,LocalService.class),connection,Context.BIND_IMPORTANT);
        return START_STICKY;
    }


    public RemoteService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }


    private class MyBinder extends IKeepAliveInterface.Stub{

        @Override
        public String getServiceName() throws RemoteException {
            return RemoteService.class.getName();
        }

    }
}

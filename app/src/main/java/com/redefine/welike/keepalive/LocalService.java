package com.redefine.welike.keepalive;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.redefine.welike.IKeepAliveInterface;
import com.redefine.welike.MyApplication;
import com.redefine.welike.statistical.EventLog1;

public class LocalService extends Service {
    private MyBinder mBinder;

   /* public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, new Notification());
        }
    }*/

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IKeepAliveInterface iMyAidlInterface = IKeepAliveInterface.Stub.asInterface(service);
            try {
                Log.d("Lizard", "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("lizard", "LocalService-->onDISServiceConnected");
            bindService(new Intent(LocalService.this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String source = "redefine";

        if (null != intent) {
            source = intent.getStringExtra("source");
        }
        Log.d("lizard", "LocalService-->onStartCommand-->:" + source);
        if (MyApplication.appInitServiceCount < 1) {
            if (null != source && source.equals("com.nemo.vidmate")) {
                EventLog1.KeepAlive.report1(EventLog1.KeepAlive.KeepAliveType.BY_VIDMATE);
            } else {
                EventLog1.KeepAlive.report1(EventLog1.KeepAlive.KeepAliveType.BY_SELF);
            }
        }
        MyApplication.appInitServiceCount++;
        bindService(new Intent(this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }


    public LocalService() {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    private class MyBinder extends IKeepAliveInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return LocalService.class.getName();
        }


    }

}

package com.redefine.im.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.redefine.foundation.utils.LogUtil;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMServiceFactory {
    private ServiceConnection conn;
    private IMServiceFactoryCallback listener;
    private IIMServiceFactoryExceptionListener exceptionListener;

    public interface IMServiceFactoryCallback {

        void onIMServiceCreated(IMService service);

        void onIMServiceDestroy();

    }

    public IMServiceFactory() {
        conn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtil.d("welike-liubin", "onServiceConnected");
                IMService.IMBinder binder = (IMService.IMBinder)service;
                if (listener != null) {
                    listener.onIMServiceCreated(binder.getService());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtil.d("welike-liubin", "onServiceDisconnected");
                if (listener != null) {
                    listener.onIMServiceDestroy();
                }
                if (exceptionListener != null) {
                    exceptionListener.onIMServiceFactoryDeathException();
                }
            }

        };
    }

    public void setListener(IMServiceFactoryCallback listener) {
        this.listener = listener;
    }

    public void bindService(Context context, IIMServiceFactoryExceptionListener exceptionListener) {
        Intent intent = new Intent(context, IMService.class);
        context.bindService(intent, conn, Service.BIND_AUTO_CREATE);
        this.exceptionListener = exceptionListener;
    }

    public void unbindService(Context context) {
        exceptionListener = null;
        context.unbindService(conn);
    }

}

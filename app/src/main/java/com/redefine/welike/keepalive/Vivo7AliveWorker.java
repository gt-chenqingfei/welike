package com.redefine.welike.keepalive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class Vivo7AliveWorker extends Worker {
    public Vivo7AliveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("lizard","start do work");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WorkManagerInstance.getInstance().startVivo7Alive();
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);

                } catch (InterruptedException e) {
                    Log.d("lizard","doWork-->:"+e);
                    e.printStackTrace();
                }
            }
        });*/

        return Result.SUCCESS;
    }
}

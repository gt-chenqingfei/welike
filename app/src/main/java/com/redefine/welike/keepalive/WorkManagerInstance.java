package com.redefine.welike.keepalive;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class WorkManagerInstance {
    private static WorkManagerInstance instance;

    private WorkManagerInstance(){

    }

    public static WorkManagerInstance getInstance() {
        if(null==instance){
            instance=new WorkManagerInstance();
        }
        return instance;
    }

    public void startVivo7Alive(){
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Vivo7AliveWorker.class).build();
        WorkManager.getInstance().enqueue(request);

        //PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(Vivo7AliveWorker.class, 15, TimeUnit.MINUTES).build();


    }
}

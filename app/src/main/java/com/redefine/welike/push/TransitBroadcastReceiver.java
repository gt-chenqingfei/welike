package com.redefine.welike.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.PushEventManager;


/**
 * Created by mengnan on 2018/4/26.
 **/
public class TransitBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent jumpIntent = intent.getParcelableExtra("jumpIntent");
        context.startActivity(jumpIntent);
        EventLog.Push.report3(Integer.parseInt(intent.getStringExtra("pushType")), intent.getStringExtra("pushId"), 1);
        EventLog.LaunchApp.report1(2);
        EventLog1.LaunchApp.report1(EventLog1.LaunchApp.FromPush.FROM_PUSH);
        PushEventManager.onPushOpen(Integer.parseInt(intent.getStringExtra("pushType")), intent.getStringExtra("pushId"),
                EventLog1.Push.Result.POP,EventLog1.Push.PushChannel.UT, intent.getStringExtra("businessType"),intent.getStringExtra("schemeUrl"));
    }
}

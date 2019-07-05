package com.redefine.foundation.framework;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by liwenbo on 2018/2/1.
 */

public class Event {

    public final int id;
    public final Bundle bundle;

    public Event(int id, Bundle bundle) {
        this.id = id;
        this.bundle = bundle;
    }

    public Event(int id) {
        this.id = id;
        this.bundle = new Bundle();
    }

    public static Message toMessage(int id, Event event) {
        Message message = Message.obtain();
        message.what = id;
        message.setData(event.bundle);
        return message;
    }
}

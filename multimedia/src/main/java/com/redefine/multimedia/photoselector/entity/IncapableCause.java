package com.redefine.multimedia.photoselector.entity;

import android.content.Context;
import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class IncapableCause {
    public static final int TOAST = 0x00;
    public static final int NONE = 0x01;

    @Retention(SOURCE)
    @IntDef({TOAST, NONE})
    public @interface Form {
    }

    private int mForm = TOAST;
    private String mTitle;
    private String mMessage;

    public IncapableCause(String message) {
        mMessage = message;
    }

    public IncapableCause(String title, String message) {
        mTitle = title;
        mMessage = message;
    }

    public IncapableCause(@Form int form, String message) {
        mForm = form;
        mMessage = message;
    }

    public IncapableCause(@Form int form, String title, String message) {
        mForm = form;
        mTitle = title;
        mMessage = message;
    }

    public static void handleCause(Context context, IncapableCause cause) {
        if (cause == null)
            return;

        switch (cause.mForm) {
            case NONE:
                // do nothing.
                break;
            case TOAST:
            default:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

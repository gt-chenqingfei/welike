package com.redefine.multimedia.player.mediacontroller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

public class ScreenOrientationManager implements OrientationEventManager.IOrientationChangedListener, ISettingChangeListener {

    private final WeakReference<Activity> mActivity;
    private final ScreenRotationObserver mScreenOrientation;
    private int mActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private OrientationEventManager mOrientationManager;
    private int mScreenRotateState;

    public ScreenOrientationManager(Activity activity) {
        mActivity = new WeakReference<>(activity);
        mOrientationManager = new OrientationEventManager(activity);
        mOrientationManager.registerListener(this);
        mScreenOrientation = new ScreenRotationObserver(activity, this, new Handler());
        mScreenRotateState = getScreenRotate(activity);
    }

    public void toggleLandScreen() {
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        Configuration mConfiguration = activity.getResources().getConfiguration();
        int requestOri = activity.getRequestedOrientation();
        int ori = mConfiguration.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            if (requestOri != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            if (requestOri != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    public void toggleLandScreen(boolean LANDSCAPE) {
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        activity.setRequestedOrientation(LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void forcePortraitScreen() {
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        mActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        Configuration mConfiguration = activity.getResources().getConfiguration();
        int requestOri = activity.getRequestedOrientation();
        int ori = mConfiguration.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            if (requestOri != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public void enable() {
        if (mScreenRotateState == SwitchState.ON) {
            mOrientationManager.enable();
        }
        mScreenOrientation.startObserver();
    }

    public void disable() {
        mOrientationManager.disable();
    }

    public void onDestroy() {
        mOrientationManager.disable();
        mScreenOrientation.stopObserver();
        mActivity.clear();
    }

    @Override
    public void onOrientationChanged(int orientation) {
        Activity activity = mActivity.get();

        if (mScreenRotateState == SwitchState.OFF) {
            forcePortraitScreen();
            return;
        }

        if (orientation > 45 && orientation < 135) {
            // SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            if (mActivityOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                return;
            }
            mActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            if (activity != null && activity.getRequestedOrientation() != mActivityOrientation) {
                activity.setRequestedOrientation(mActivityOrientation);
            }
        } else if (orientation > 135 && orientation < 225) {
            // SCREEN_ORIENTATION_REVERSE_PORTRAIT
            if (mActivityOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                return;
            }
            mActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            if (activity != null && activity.getRequestedOrientation() != mActivityOrientation) {
                activity.setRequestedOrientation(mActivityOrientation);
            }
        } else if (orientation > 225 && orientation < 315) {
            // SCREEN_ORIENTATION_LANDSCAPE
            if (mActivityOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                return;
            }
            mActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            if (activity != null && activity.getRequestedOrientation() != mActivityOrientation) {
                activity.setRequestedOrientation(mActivityOrientation);
            }
        } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
            // SCREEN_ORIENTATION_PORTRAIT
            if (mActivityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                return;
            }
            mActivityOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (activity != null && activity.getRequestedOrientation() != mActivityOrientation) {
                activity.setRequestedOrientation(mActivityOrientation);
            }
        }
    }

    @Override
    public void onSettingChange(String key, boolean selfChange) {
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        if (!TextUtils.equals(key, Settings.System.ACCELEROMETER_ROTATION)) {
            return;
        }
        mScreenRotateState = getScreenRotate(activity);
        if (mScreenRotateState == SwitchState.ON) {
            mOrientationManager.enable();
        } else {
            mOrientationManager.disable();
            forcePortraitScreen();
        }
    }

    private int getScreenRotate(Activity activity) {
        try {
            return Settings.System.getInt(activity.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static class ScreenRotationObserver extends ContentObserver {

        private final WeakReference<ContentResolver> mResolver;
        private final WeakReference<ISettingChangeListener> mListener;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public ScreenRotationObserver(Activity activity, ISettingChangeListener listener, Handler handler) {
            super(handler);
            mResolver = new WeakReference<>(activity.getContentResolver());
            mListener = new WeakReference<>(listener);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            ISettingChangeListener listener = mListener.get();
            if (listener != null) {
                listener.onSettingChange(uri.getLastPathSegment(), selfChange);
            }
        }

        public void startObserver() {
            ContentResolver resolver = mResolver.get();
            if (resolver == null) {
                return;
            }
            resolver.registerContentObserver(Settings.System
                            .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                    this);
        }

        public void stopObserver() {
            ContentResolver resolver = mResolver.get();
            if (resolver == null) {
                return;
            }
            resolver.unregisterContentObserver(this);
        }
    }
}

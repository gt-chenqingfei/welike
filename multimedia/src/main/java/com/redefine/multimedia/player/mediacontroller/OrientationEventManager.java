package com.redefine.multimedia.player.mediacontroller;

import android.content.Context;
import android.view.OrientationEventListener;

import java.util.HashSet;
import java.util.Set;

public class OrientationEventManager extends OrientationEventListener {

    private Set<IOrientationChangedListener> mListeners = new HashSet<>();

    public OrientationEventManager(Context context) {
        super(context);
    }

    public OrientationEventManager(Context context, int rate) {
        super(context, rate);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        notifyListeners(orientation);
    }

    private void notifyListeners(int orientation) {
        for (IOrientationChangedListener listener : mListeners) {
            listener.onOrientationChanged(orientation);
        }
    }

    public void registerListener(IOrientationChangedListener orientationChangedListener) {
        mListeners.add(orientationChangedListener);
    }

    public void unRegisterListener(IOrientationChangedListener orientationChangedListener) {
        mListeners.remove(orientationChangedListener);
    }


    public static interface IOrientationChangedListener {
        void onOrientationChanged(int orientation);
    }
}
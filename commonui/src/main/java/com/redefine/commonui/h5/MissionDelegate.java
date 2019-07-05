package com.redefine.commonui.h5;

/**
 * Created by nianguowang on 2018/5/23
 */
public enum MissionDelegate {

    INSTANCE;

    public interface OnStartMissionListener {
        void startMission(int type);
    }

    private OnStartMissionListener mListener;

    public void startMission(int type) {
        if(mListener != null) {
            mListener.startMission(type);
        }
    }

    public void registerMissionListener(OnStartMissionListener listener) {
        mListener = listener;
    }

    public void unregisterMissionListener(OnStartMissionListener listener) {
        mListener = null;
    }

}

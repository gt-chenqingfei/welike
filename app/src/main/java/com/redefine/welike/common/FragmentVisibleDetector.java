package com.redefine.welike.common;

/**
 * Created by nianguowang on 2018/9/7
 */
public class FragmentVisibleDetector {

    private boolean attached;
    private boolean shown;
    private boolean resumed;

    private AutoPlayVideoListener mListener;

    public FragmentVisibleDetector(AutoPlayVideoListener listener) {
        mListener = listener;
    }

    public interface AutoPlayVideoListener {
        void autoPlay();
        void stopPlay();
    }

    public void onResume() {
        resumed = true;
        if (shown && attached) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
    }

    public void onPause() {
        resumed = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
    }

    public void show() {
        shown = true;
        if (resumed && attached) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
    }

    public void hide() {
        shown = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
    }

    public void attach() {
        attached = true;
        if (resumed && shown) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
    }

    public void detach() {
        attached = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
    }

    public void onDataLoaded() {
        if (resumed && attached && shown) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
    }

}

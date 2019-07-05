package com.redefine.welike.common;

import com.redefine.foundation.utils.LogUtil;

/**
 * Created by nianguowang on 2018/9/7
 */
public class PageVisibleDetector {

    private boolean attached;
    private boolean shown;
    private boolean resumed;
    private boolean pageShown;

    private AutoPlayVideoListener mListener;

    public PageVisibleDetector(AutoPlayVideoListener listener) {
        mListener = listener;
    }

    public interface AutoPlayVideoListener {
        void autoPlay();
        void stopPlay();
    }

    public void onResume() {
        resumed = true;
        if (shown && attached && pageShown) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
        LogUtil.d("PageVisibleDetector", this + "onResume shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void onPause() {
        resumed = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
        LogUtil.d("PageVisibleDetector", this + "onPause shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void show() {
        shown = true;
        if (resumed && attached && pageShown) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
        LogUtil.d("PageVisibleDetector", this + "show shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void hide() {
        shown = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
        LogUtil.d("PageVisibleDetector", this + "hide shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void pageShow() {
        pageShown = true;
        if (resumed && shown && attached) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
        LogUtil.d("PageVisibleDetector", this + "pageShow shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void pageHide() {
        pageShown = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
        LogUtil.d("PageVisibleDetector", this + "pageHide shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void attach() {
        attached = true;
        if (resumed && shown && pageShown) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
        LogUtil.d("PageVisibleDetector", this + "attach shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void detach() {
        attached = false;
        if (mListener != null) {
            mListener.stopPlay();
        }
        LogUtil.d("PageVisibleDetector", this + "detach shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

    public void onDataLoaded() {
        if (resumed && attached && shown && pageShown) {
            if (mListener != null) {
                mListener.autoPlay();
            }
        }
        LogUtil.d("PageVisibleDetector", this + "onDataLoaded shown : " + shown + " , attached : " + attached + " , pageShown : " + pageShown + " , resumed : " + resumed);
    }

}

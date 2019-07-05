package com.redefine.multimedia.recorder.view;

import android.app.Activity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.redefine.multimedia.R;

/**
 * Created by liwenbo on 2018/4/8.
 */

public class VideoSurfaceRecorderView extends AbsVideoRecorderView<SurfaceHolder> implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;

    @Override
    protected void initSurfaceView(Activity activity) {
        mSurfaceView = activity.findViewById(R.id.video_recorder_surface_view);
        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        onSurfaceAvailable(holder, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

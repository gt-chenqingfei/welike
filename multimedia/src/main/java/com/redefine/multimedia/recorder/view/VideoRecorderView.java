package com.redefine.multimedia.recorder.view;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import com.redefine.multimedia.R;

/**
 * Created by liwenbo on 2018/4/8.
 */

public class VideoRecorderView extends AbsVideoRecorderView<SurfaceTexture> implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;

    @Override
    protected void initSurfaceView(Activity activity) {

        mTextureView = activity.findViewById(R.id.video_recorder_surface_view);

        mTextureView.setSurfaceTextureListener(this);

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        onSurfaceAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        onSurfaceAvailable(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}

package com.pekingese.pagestack.framework.layer;

/**
 * Created by liwenbo on 2018/4/2.
 */

public interface IStackLayerPresenter {

    void onActivityResume();

    void onActivityStart();

    void onActivityStop();

    void onActivityPause();

    void onActivityDestroy();
}

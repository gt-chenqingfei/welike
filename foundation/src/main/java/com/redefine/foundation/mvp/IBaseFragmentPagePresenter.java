package com.redefine.foundation.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Created by liwenbo on 2018/2/11.
 */

public interface IBaseFragmentPagePresenter extends IBasePresenter {
    View createView(Context context, Bundle savedInstanceState);

    void attach();

    void detach();

    void destroy();
}

package com.redefine.foundation.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Created by liwenbo on 2018/2/5.
 */

public interface IBasePageView {

    View createView(Context context, Bundle savedInstanceState);

    void attach();

    void detach();

    void destroy();
}

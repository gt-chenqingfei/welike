package com.redefine.foundation.mvp;

import android.os.Bundle;
import android.view.View;

/**
 * Created by liwb on 2018/1/11.
 */

public interface IBaseActivityView extends IBaseView {
    
    void onCreate(View rootView, Bundle savedInstanceState);
    void onDestroy();
}

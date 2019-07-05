package com.redefine.foundation.mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by liwb on 2018/1/11.
 */

public interface IBaseActivityPresenter extends IBasePresenter {

    void onCreate(View rootView, Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onDestroy();
}

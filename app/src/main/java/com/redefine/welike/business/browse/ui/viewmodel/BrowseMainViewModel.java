package com.redefine.welike.business.browse.ui.viewmodel;


import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.welike.frameworkmvvm.AndroidViewModel;


/**
 * Created by honglin on 2018/7/9.
 * <p>
 */
public class BrowseMainViewModel extends AndroidViewModel {


    public BrowseMainViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<Boolean> mTabShouldShow = new MutableLiveData<>();
    private MutableLiveData<Boolean> mSnakeBarShouldShow = new MutableLiveData<>();

    public MutableLiveData<Boolean> getTabStatus() {
        return mTabShouldShow;
    }

    public void setTabStatus(boolean isShow) {

        mTabShouldShow.postValue(isShow);

    }

    public MutableLiveData<Boolean> getmSnakeBarShouldShow() {
        return mSnakeBarShouldShow;
    }

    public void setSnakeBarShouldShow(Boolean mSnakeBarShouldShow) {
        this.mSnakeBarShouldShow .postValue( mSnakeBarShouldShow);
    }
}

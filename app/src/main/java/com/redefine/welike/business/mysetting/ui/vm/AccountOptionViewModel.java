package com.redefine.welike.business.mysetting.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.welike.business.mysetting.management.OptionManager;
import com.redefine.welike.business.mysetting.ui.listener.IUserOptionListener;

/**
 * Created by honglin on 2018/5/8.
 */

public class AccountOptionViewModel extends AndroidViewModel implements IUserOptionListener {

    private MutableLiveData<Boolean> accountFollowStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> accountContactsStatus = new MutableLiveData<>();


    public AccountOptionViewModel(@NonNull Application application) {
        super(application);
        OptionManager.getInstance().setListener(this);
    }

    public MutableLiveData<Boolean> getAccountFollowStatus() {
        return accountFollowStatus;
    }

    public MutableLiveData<Boolean> getAccountContactsStatus() {
        return accountContactsStatus;
    }

    public void getOption() {
        OptionManager.getInstance().check();
    }


    public void changeFollowOption(boolean isFollow) {
        OptionManager.getInstance().changeFollowOption(isFollow);
    }

    public void changeContactsOption(boolean isContacts) {
        OptionManager.getInstance().changeContactsOption(isContacts);
    }

    @Override
    public void onOptionChanged(boolean isFollow, boolean isContacts) {
        accountFollowStatus.postValue(isFollow);
        accountContactsStatus.postValue(isContacts);
    }
}

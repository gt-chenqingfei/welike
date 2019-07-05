package com.redefine.welike.business.feeds.ui.vm

import android.app.Application
import android.arch.lifecycle.LiveData
import com.redefine.im.room.AccountSetting
import com.redefine.im.room.DatabaseCenter
import com.redefine.im.threadTry
import com.redefine.im.threadUITry
import com.redefine.welike.frameworkmvvm.AndroidViewModel

class MessageFragmentViewModel(application: Application) : AndroidViewModel(application) {

    fun loadMessageCount(t: (LiveData<AccountSetting>) -> Unit) {
        threadTry {
            DatabaseCenter.database?.accountSettingDao()?.getLiveSetting()?.let {
                threadUITry {
                    t.invoke(it)
                }
            }
        }
    }
}
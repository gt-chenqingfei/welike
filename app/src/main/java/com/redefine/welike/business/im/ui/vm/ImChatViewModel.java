package com.redefine.welike.business.im.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.redefine.im.room.DatabaseCenter;
import com.redefine.im.room.MESSAGE;
import com.redefine.im.room.MessageDao;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nianguowang on 2018/6/8
 */
public class ImChatViewModel extends AndroidViewModel {

    private final MessageDao mMessageDao;

    public interface OnChatLiveDataLoadListener {
        void onChatLiveDataLoad(LiveData<List<MESSAGE>> messageLiveData);
    }

    public ImChatViewModel(@NonNull Application application) {
        super(application);
        mMessageDao = DatabaseCenter.INSTANCE.getDatabase().messageDao();
    }

    public void loadMessages(final String sid, final OnChatLiveDataLoadListener listener) {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                final LiveData<List<MESSAGE>> messages = mMessageDao.getMessages(sid);
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if(listener != null) {
                            listener.onChatLiveDataLoad(messages);
                        }
                    }
                });
            }
        });
    }

}

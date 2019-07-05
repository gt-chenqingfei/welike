package com.redefine.welike.business.mysetting.ui.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.redefine.welike.business.mysetting.management.MutePushBean;
import com.redefine.welike.business.mysetting.management.MutePushOptions;
import com.redefine.welike.business.mysetting.management.NotificationMuteOptionManager;
import com.redefine.welike.business.mysetting.ui.listener.INotificationOptionListener;

import java.util.List;


/**
 * Created by mengnan on 2018/6/6.
 **/
public class NotificationOptionViewModel extends AndroidViewModel implements INotificationOptionListener {

    public static final int PUSH_TYPE_POST = 1;

    public static final int PUSH_TYPE_COMMENT = 2;

    public static final int PUSH_TYPE_LIKE = 3;

    public static final int PUSH_TYPE_FRIEND_MESSAGE = 4;

    public static final int PUSH_TYPE_STRANGER_MESSAGE = 5;

    public static final int PUSH_TYPE_NEW_POST = 6;

    public static final int PUSH_TYPE_TIME_LIMIT = 7;//免打扰时间是否开启按钮
    public static final int PUSH_TYPE_NEW_FOLLOWER = 8;

    private MutableLiveData<Boolean> muteAt = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteComment = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteLike = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteNewFollower = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteFriend = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteStranger = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteNewPost = new MutableLiveData<>();
    private MutableLiveData<Boolean> muteScheduled = new MutableLiveData<>();
    private MutableLiveData<String> muteLimitTime = new MutableLiveData<>();

    public NotificationOptionViewModel(@NonNull Application application) {
        super(application);
        NotificationMuteOptionManager.getInstance().setListener(this);
    }

    public MutableLiveData<Boolean> getMuteAt() {
        return muteAt;
    }

    public MutableLiveData<Boolean> getMuteComment() {
        return muteComment;
    }

    public MutableLiveData<Boolean> getMuteLike() {
        return muteLike;
    }

    public MutableLiveData<Boolean> getMuteNewFollower() {
        return muteNewFollower;
    }

    public MutableLiveData<Boolean> getMuteFriend() {
        return muteFriend;
    }

    public MutableLiveData<Boolean> getMuteStranger() {
        return muteStranger;
    }

    public MutableLiveData<Boolean> getMuteNewPost() {
        return muteNewPost;
    }

    public MutableLiveData<Boolean> getMuteScheduled() {
        return muteScheduled;
    }

    public MutableLiveData<String> getMuteLimitTime() {
        return muteLimitTime;
    }

    public void getOption() {
        NotificationMuteOptionManager.getInstance().check();
    }


    public void changeOption(MutePushOptions options) {
        NotificationMuteOptionManager.getInstance().changeMute(options);
    }

    @Override
    public void onOptionChanged(MutePushOptions options) {

        if(null!=options){
            muteLimitTime.postValue(options.getTimeLimit());
            List<MutePushBean>list= options.getSwitchs();

            if(null!=list&&list.size()>0){
                for(MutePushBean bean:list){

                    notifiChange(bean);
                }

            }

        }

    }

    private void notifiChange(MutePushBean bean){

        switch (bean.getType()){
            case PUSH_TYPE_POST:
                muteAt.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_COMMENT:
                muteComment.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_LIKE:
                muteLike.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_FRIEND_MESSAGE:
                muteFriend.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_STRANGER_MESSAGE:
                muteStranger.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_NEW_POST:
                muteNewPost.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_TIME_LIMIT:
                muteScheduled.postValue(Integer.parseInt(bean.getValue())==1);
                break;
            case PUSH_TYPE_NEW_FOLLOWER:
                muteNewFollower.postValue(Integer.parseInt(bean.getValue())==1);
                break;

        }

    }

}

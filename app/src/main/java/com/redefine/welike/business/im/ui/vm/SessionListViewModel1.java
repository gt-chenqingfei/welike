package com.redefine.welike.business.im.ui.vm;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.im.Constants;
import com.redefine.im.room.DatabaseCenter;
import com.redefine.im.room.MessageDao;
import com.redefine.im.room.MessageSession;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.assignment.management.AssignmentTopUserReviewManagement;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.business.im.model.CommentSessionModel;
import com.redefine.welike.business.im.model.GreetingSessionModel;
import com.redefine.welike.business.im.model.LikeSessionModel;
import com.redefine.welike.business.im.model.MentionSessionModel;
import com.redefine.welike.business.im.model.PushSessionModel;
import com.redefine.welike.business.im.model.SessionModel;
import com.redefine.welike.business.im.model.ShakeSessionModel;
import com.redefine.welike.business.im.model.SingleSessionModel;
import com.redefine.welike.business.im.ui.utils.StrangerUtil;
import com.redefine.welike.frameworkmvvm.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by nianguowang on 2018/6/8
 */
public class SessionListViewModel1 extends AndroidViewModel implements AssignmentTopUserReviewManagement.AssignmentTopUserReviewManagementCallback {

    private final MessageDao mMessageDao;
    private AssignmentTopUserReviewManagement mAssignmentTopUserModel;
    private MutableLiveData<List<SessionModel>> mSessionLiveData = new MutableLiveData<>();

    public interface OnSessionLoadListener {
        void onSessionLoaded(LiveData<List<MessageSession>> sessionLiveData);
    }

    public SessionListViewModel1(@NonNull Application application) {
        super(application);
        mMessageDao = DatabaseCenter.INSTANCE.getDatabase().messageDao();
        mAssignmentTopUserModel = new AssignmentTopUserReviewManagement();
    }

    public MutableLiveData<List<SessionModel>> getSessionLiveData() {
        return mSessionLiveData;
    }

    public void loadAll(OnSessionLoadListener listener) {
        List<SessionModel> value = mSessionLiveData.getValue();
        if(value == null) {
            value = new ArrayList<>();
        }
        value.add(new MentionSessionModel());
        value.add(new CommentSessionModel());
        value.add(new LikeSessionModel());
        value.add(new PushSessionModel());

        mSessionLiveData.postValue(value);

        mAssignmentTopUserModel.load(this);

        loadSessions(listener);
    }

    public void loadSessions(final OnSessionLoadListener listener) {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                final LiveData<List<MessageSession>> sessions = mMessageDao.getSessions();
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        if(listener != null) {
                            listener.onSessionLoaded(sessions);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAssignmentTopUserReviewManagementEnd(TopUserShakeBean bean, int errCode) {
        if(errCode == ErrorCode.ERROR_SUCCESS && bean != null) {
            if(!TextUtils.isEmpty(bean.shakeUrl)) {
                List<SessionModel> value = mSessionLiveData.getValue();
                if(CollectionUtil.isEmpty(value) || value.size() < 4) {
                    return;
                }
                ShakeSessionModel shakeSessionModel = new ShakeSessionModel();
                shakeSessionModel.setShakeUrl(bean.shakeUrl);
                value.add(4, shakeSessionModel);
                mSessionLiveData.postValue(value);
            }
        }
    }

    public void updateSingleSessions(List<MessageSession> singleSession) {
        List<SessionModel> value = mSessionLiveData.getValue();
        if(CollectionUtil.isEmpty(value) || value.size() < 4) {
            return;
        }
        SessionModel mentionModel = value.get(0);
        SessionModel commentModel = value.get(1);
        SessionModel likeModel = value.get(2);
        SessionModel pushModel = value.get(3);

        SessionModel shakeModel = null;
        if(value.size() > 4) {
            SessionModel sessionModel = value.get(4);
            if(sessionModel instanceof ShakeSessionModel) {
                shakeModel = sessionModel;
            }
        }
        value.clear();
        value.add(mentionModel);
        value.add(commentModel);
        value.add(likeModel);
        value.add(pushModel);

        if(shakeModel != null) {
            value.add(shakeModel);
        }
        if(CollectionUtil.isEmpty(singleSession)) {
            mSessionLiveData.postValue(value);
            return;
        }
        for (MessageSession messageSession : singleSession) {
            if(StrangerUtil.isGreet(messageSession)) {
                GreetingSessionModel greetingSessionModel = new GreetingSessionModel();
                greetingSessionModel.setMessageSession(messageSession);
                value.add(greetingSessionModel);
            } else {
                SingleSessionModel singleSessionModel = new SingleSessionModel();
                singleSessionModel.setMessageSession(messageSession);
                value.add(singleSessionModel);
            }
        }

        mSessionLiveData.postValue(value);
    }

    public void updateMCLSessions(int mentionCount, int commentCount, int likeCount,int pushCount) {
        List<SessionModel> value = mSessionLiveData.getValue();
        if(CollectionUtil.isEmpty(value) || value.size() < 4) {
            return;
        }
        SessionModel mentionModel = value.get(0);
        if(mentionModel != null && mentionModel instanceof  MentionSessionModel) {
            ((MentionSessionModel) mentionModel).setUnreadCount(mentionCount);
        }
        SessionModel commentModel = value.get(1);
        if(commentModel != null && commentModel instanceof  CommentSessionModel) {
            ((CommentSessionModel) commentModel).setUnreadCount(commentCount);
        }
        SessionModel likeModel = value.get(2);
        if(likeModel != null && likeModel instanceof LikeSessionModel) {
            ((LikeSessionModel) likeModel).setUnreadCount(likeCount);
        }

        SessionModel pushModel = value.get(3);
        if(pushModel != null && pushModel instanceof PushSessionModel) {
            ((PushSessionModel) pushModel).setUnreadCount(pushCount);
        }
        mSessionLiveData.postValue(value);
    }

    public List<MessageSession> filterStrangeSessions(List<MessageSession> sessions) {
        if(CollectionUtil.isEmpty(sessions)) {
            return sessions;
        }
        List<MessageSession> newSessions = new ArrayList<>();
        boolean hasStrange = false;
        int strangeUnreadCount = 0;
        for (MessageSession session : sessions) {
            if(StrangerUtil.isGreet(session)) {
                strangeUnreadCount += session.getSUnread();
            }
        }
        MessageSession strangeSession = null;
        for (MessageSession session : sessions) {
            if(StrangerUtil.isGreet(session)) {
                if(!hasStrange) {
                    hasStrange = true;
                    strangeSession = new MessageSession(session.getMessage(), Constants.STRANGER_SESSION_SID, session.getSName(), session.getSHead(), strangeUnreadCount, Constants.SESSION_STRANGE);
                    newSessions.add(0, strangeSession);
                }
            } else {
                newSessions.add(session);
            }
        }
        return newSessions;
    }

    public List<MessageSession> filterUnStrangeSessions(List<MessageSession> sessions) {
        if(CollectionUtil.isEmpty(sessions)) {
            return sessions;
        }
        List<MessageSession> newSessions = new ArrayList<>();
        for (MessageSession session : sessions) {
            if(StrangerUtil.isGreet(session)) {
                newSessions.add(session);
            }
        }
        return newSessions;
    }

}

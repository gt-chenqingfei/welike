package com.redefine.welike.business.im.ui.adapter;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.framework.Event;
import com.redefine.im.Constants;
import com.redefine.im.room.MessageSession;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.im.CountManager;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.model.SessionModel;
import com.redefine.welike.business.im.model.ShakeSessionModel;
import com.redefine.welike.business.im.model.SingleSessionModel;
import com.redefine.welike.business.im.ui.holder.BaseSessionItemViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionCommentViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionGreetingViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionItemViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionLikeViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionMentionViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionPushViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionShakeViewHolder;
import com.redefine.welike.business.im.ui.utils.StrangerUtil;
import com.redefine.welike.business.message.ui.constant.MessageConstant;
import com.redefine.welike.business.message.ui.page.MessageBoxActivity;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.IMEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.redefine.welike.business.im.ui.constant.ImConstant.IM_SESSION_KRY;

/**
 * Created by liwenbo on 2018/2/6.
 */

public class SessionListAdapter extends RecyclerView.Adapter<BaseSessionItemViewHolder> {

    private final IPageStackManager mStackManager;
    private List<SessionModel> mSessionList = new ArrayList<>();
    private SessionItemViewHolder.IDeleteSessionCallback mSessionDeleteCallback;
    private SessionItemViewHolder.OnSessionLongClickListener mSessionLongClickListener;
    private SessionShakeViewHolder mSessionShakeViewHolder;

    public SessionListAdapter(IPageStackManager stackManager) {
        mStackManager = stackManager;
    }

    public void setData(final List<SessionModel> sessions) {
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mSessionList.size();
            }

            @Override
            public int getNewListSize() {
                return sessions.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                SessionModel oldModel = mSessionList.get(oldItemPosition);
                SessionModel newModel = sessions.get(newItemPosition);
                if (oldModel instanceof SingleSessionModel && newModel instanceof SingleSessionModel) {
                    return ((SingleSessionModel) oldModel).getMessageSession().getSSid().equals(((SingleSessionModel) newModel).getMessageSession().getSSid());
                }
                return oldModel.getSessionType() == newModel.getSessionType();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }
        }).dispatchUpdatesTo(this);
//        diffResult.dispatchUpdatesTo(new AdapterListUpdateCallback(this));
        mSessionList.clear();
        mSessionList.addAll(sessions);
    }

    @Override
    public BaseSessionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.SESSION_MENTIONS) {
            return new SessionMentionViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_stranger_list_item, parent, false), mSessionDeleteCallback);
        } else if (viewType == Constants.SESSION_COMMENTS) {
            return new SessionCommentViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_stranger_list_item, parent, false), mSessionDeleteCallback);
        } else if (viewType == Constants.SESSION_LIKES) {
            return new SessionLikeViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_stranger_list_item, parent, false), mSessionDeleteCallback);
        } else if (viewType == Constants.SESSION_PUSH) {
            return new SessionPushViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_stranger_list_item, parent, false), mSessionDeleteCallback);
        } else if (viewType == Constants.SESSION_SHAKE) {
            if (mSessionShakeViewHolder == null) {
                mSessionShakeViewHolder = new SessionShakeViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_shake_list_item, parent, false), mSessionDeleteCallback);
            }
            return mSessionShakeViewHolder;
        } else if (viewType == Constants.SESSION_STRANGE) {
            return new SessionGreetingViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_stranger_list_item, parent, false), mSessionDeleteCallback);
        } else {
            return new SessionItemViewHolder(mStackManager.getLayoutInflater().inflate(R.layout.im_session_list_item, parent, false), mSessionDeleteCallback, mSessionLongClickListener);
        }
    }

    @Override
    public void onBindViewHolder(final BaseSessionItemViewHolder holder, int position) {
        final SessionModel sessionModel = mSessionList.get(position);

        holder.bindViews(this, sessionModel);
        holder.mSessionContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccountManager.getInstance().getAccount() == null ||
                        AccountManager.getInstance().getAccount().getStatus() == Account.ACCOUNT_HALF) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.CHAT));
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));

                    return;
                }


                if (holder instanceof SessionMentionViewHolder) {
                    IMEventManager.INSTANCE.report2();
                    Schedulers.newThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            MessageBoxActivity.Companion.launch(MessageConstant.FRAGMNET_ME, CountManager.INSTANCE.getMentionCount());
                            CountManager.INSTANCE.setMentionCount(0);
                        }
                    });
                } else if (holder instanceof SessionCommentViewHolder) {
                    IMEventManager.INSTANCE.report3();
                    Schedulers.newThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            MessageBoxActivity.Companion.launch(MessageConstant.FRAGMNET_COMMENT, CountManager.INSTANCE.getCommnetCount());
                            CountManager.INSTANCE.setCommentCount(0);
                        }
                    });
                } else if (holder instanceof SessionLikeViewHolder) {
                    IMEventManager.INSTANCE.report4();
                    Schedulers.newThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            MessageBoxActivity.Companion.launch(MessageConstant.FRAGMNET_LIKE, CountManager.INSTANCE.getLikeCount());
                            CountManager.INSTANCE.setLikeCount(0);
                        }
                    });
                } else if (holder instanceof SessionPushViewHolder) {
                    IMEventManager.INSTANCE.report7();
                    Schedulers.newThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            MessageBoxActivity.Companion.launch(MessageConstant.FRAGMNET_PUSH, 0);
                            // CountManager.INSTANCE.setPushCount(0);
                        }
                    });
                } else if (holder instanceof SessionShakeViewHolder) {
                    if (sessionModel instanceof ShakeSessionModel) {
                        MissionManager.INSTANCE.notifyEvent(MissionType.RANK);
                        new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_SHAKE).onUrlRedirect(((ShakeSessionModel) sessionModel).getShakeUrl());
                    }
                } else if (holder instanceof SessionGreetingViewHolder) {
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_IM_STRANGER_PAGE));
                    IMEventManager.INSTANCE.report5();
                } else if (holder instanceof SessionItemViewHolder) {
                    MessageSession messageSession = ((SingleSessionModel) sessionModel).getMessageSession();
                    IMHelper.INSTANCE.getSession(messageSession.getSSid(), new Function1<SESSION, Unit>() {
                        @Override
                        public Unit invoke(final SESSION session) {
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(IM_SESSION_KRY, session);
                                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
                                }
                            });
                            return null;
                        }
                    });
                    IMEventManager.INSTANCE.report6(messageSession.getSUnread(), messageSession.getMessage().getSenderUid(),
                            1, StrangerUtil.isGreet(messageSession) ? 1 : 2);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mSessionList.get(position).getSessionType();
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

    public void playAnimation() {
        if (mSessionShakeViewHolder != null) {
            mSessionShakeViewHolder.playAnimation();
        }
    }

    public void setSessionDeleteCallback(SessionItemViewHolder.IDeleteSessionCallback callback) {
        mSessionDeleteCallback = callback;
    }

    public void setSessionLongClickListener(SessionItemViewHolder.OnSessionLongClickListener listener) {
        mSessionLongClickListener = listener;
    }
}

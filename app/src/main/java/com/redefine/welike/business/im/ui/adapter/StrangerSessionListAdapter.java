package com.redefine.welike.business.im.ui.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.foundation.framework.Event;
import com.redefine.im.room.MessageSession;
import com.redefine.im.room.SESSION;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.holder.BaseSessionItemViewHolder;
import com.redefine.welike.business.im.ui.holder.SessionGreetItemViewHolder;
import com.redefine.welike.business.im.ui.utils.StrangerUtil;
import com.redefine.welike.statistical.manager.IMEventManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.redefine.welike.business.im.ui.constant.ImConstant.IM_SESSION_KRY;

/**
 * Created by liwenbo on 2018/2/6.
 */

public class StrangerSessionListAdapter extends RecyclerView.Adapter<BaseSessionItemViewHolder> {

    private List<MessageSession> mSessionList = new ArrayList<>();
    private SessionGreetItemViewHolder.IDeleteSessionCallback mSessionDeleteCallback;
    private SessionGreetItemViewHolder.OnSessionLongClickListener mSessionLongClickListener;

    public void setData(final List<MessageSession> sessions) {
        List<MessageSession> temp = new ArrayList<>(sessions);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
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
                return mSessionList.get(oldItemPosition).getSSid().equals(sessions.get(newItemPosition).getSSid());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mSessionList.get(oldItemPosition) == sessions.get(newItemPosition);
            }
        });
        diffResult.dispatchUpdatesTo(new AdapterListUpdateCallback(this));
        mSessionList.clear();
        mSessionList.addAll(temp);
    }

    @Override
    public BaseSessionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SessionGreetItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.im_session_list_item, parent, false), mSessionDeleteCallback, mSessionLongClickListener);
    }

    @Override
    public void onBindViewHolder(BaseSessionItemViewHolder holder, int position) {
        final MessageSession messageSession = mSessionList.get(position);
        holder.bindViews(this, messageSession);
        holder.mSessionContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMHelper.INSTANCE.getSession(messageSession.getSSid(),
                        new Function1<SESSION, Unit>() {
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
                        2, StrangerUtil.isGreet(messageSession) ? 1 : 2);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mSessionList.get(position).getSIsGreet();
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

    public void setSessionDeleteCallback(SessionGreetItemViewHolder.IDeleteSessionCallback callback) {
        mSessionDeleteCallback = callback;
    }

    public void setSessionLongClickListener(SessionGreetItemViewHolder.OnSessionLongClickListener listener) {
        mSessionLongClickListener = listener;
    }

}

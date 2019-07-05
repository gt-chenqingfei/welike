package com.redefine.welike.business.feeds.ui.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.common.mission.Mission;

/**
 * Created by nianguowang on 2018/5/23
 */
public class HomeTaskViewHolder {

    private final View mRootView;

    private final View mTaskView1;
    private final TextView mTaskTitle;
    private final TextView mTaskContent;
    private final TextView mTaskAction;

    private final View mTaskView2;
    private final TextView mTaskTitleNew;
    private final TextView mTaskContentNew;
    private final TextView mTaskActionNew;

    private OnTaskActionListener mListener;

    public interface OnTaskActionListener {
        void onTaskAction(Mission mission);
    }

    public HomeTaskViewHolder(View parent) {
        mRootView = View.inflate(parent.getContext(), R.layout.home_header_task, null);

        mTaskView1 = mRootView.findViewById(R.id.home_task_view);
        mTaskTitle = mRootView.findViewById(R.id.home_task_title);
        mTaskContent = mRootView.findViewById(R.id.home_task_content);
        mTaskAction = mRootView.findViewById(R.id.home_task_action);

        mTaskView2 = mRootView.findViewById(R.id.home_task_view_new);
        mTaskTitleNew = mRootView.findViewById(R.id.home_task_title_new);
        mTaskContentNew = mRootView.findViewById(R.id.home_task_content_new);
        mTaskActionNew = mRootView.findViewById(R.id.home_task_action_new);
    }

    public void bindViews(final Mission mission, boolean old) {
        layoutViews();
        if(old) {
            mTaskTitle.setText(mission.getTitle());
            mTaskContent.setText(mission.getContent());
            mTaskAction.setText(mission.getButton());
            mTaskAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.onTaskAction(mission);
                    }
                }
            });
        } else {
            mTaskTitleNew.setText(mission.getTitle());
            mTaskContentNew.setText(mission.getContent());
            mTaskActionNew.setText(mission.getButton());
            mTaskActionNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        mListener.onTaskAction(mission);
                    }
                }
            });
        }
    }

    private void layoutViews() {
        int screenWidth = ScreenUtils.getSreenWidth(mRootView.getContext());

        ViewGroup.LayoutParams layoutParams = mRootView.getLayoutParams();
        layoutParams.width = screenWidth * 2;

        ViewGroup.LayoutParams layoutParams1 = mTaskView1.getLayoutParams();
        layoutParams1.width = screenWidth;
        mTaskView1.requestLayout();

        ViewGroup.LayoutParams layoutParams2 = mTaskView2.getLayoutParams();
        layoutParams2.width = screenWidth;
        mTaskView2.requestLayout();

        mRootView.requestLayout();
    }

    public void animate(Mission oldMission, Mission newMission) {
        layoutViews();
        int screenWidth = ScreenUtils.getSreenWidth(mRootView.getContext());
        mRootView.setTranslationX(0);

        bindViews(oldMission, true);
        bindViews(newMission, false);

        mTaskTitleNew.setText(newMission.getTitle());
        mTaskContentNew.setText(newMission.getContent());
        mTaskActionNew.setText(newMission.getButton());

        mRootView.animate().translationX(-screenWidth).setInterpolator(new OvershootInterpolator()).setDuration(500).start();
    }

    public View getRootView() {
        return mRootView;
    }

    public void setActionClickListener(OnTaskActionListener listener) {
        mListener = listener;
    }

}

package com.redefine.welike.business.feeds.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.foundation.framework.Event;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.commonui.adapter.BaseVoteTextAdapter;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mengnan on 2018/5/13.
 **/
public class VoteTextAdapter extends BaseVoteTextAdapter<PollItemInfo> {
    private LayoutInflater mInflater;


    private boolean isEnd;//是否结束

    private boolean isAccout;//是否是自己的

    private boolean isVote;//是否已经投票

    private long total;

    private String source;

    private PostBase postBase;

    public VoteTextAdapter() {
    }

    public void setIsEndAndIsVote(boolean isVote, boolean isEnd, boolean isAccout) {
        this.isAccout = isAccout;
        this.isVote = isVote;
        this.isEnd = isEnd;
    }

    public void bindSourceAndPost(String source, PostBase postBase) {
        this.source = source;
        this.postBase = postBase;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    private VoteTextAdapter.OnVoteTextItemCheckedListener voteTextItemCheckedListener;

    public void setVoteItemCheckedListener(VoteTextAdapter.OnVoteTextItemCheckedListener voteTextItemCheckedListener) {
        this.voteTextItemCheckedListener = voteTextItemCheckedListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }

        VoteTextAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vote_text_item_view, parent, false);

            holder = new VoteTextAdapter.ViewHolder();
            holder.voteTextCl = convertView.findViewById(R.id.vote_text_item_layout);
            holder.tvVoteIntro = convertView.findViewById(R.id.vote_item_text);
            holder.tvVoteRate = convertView.findViewById(R.id.vote_item_process_text);
            holder.pbVoteRate = convertView.findViewById(R.id.vote_item_process_bar);
            holder.hideView = convertView.findViewById(R.id.hide_view);
            convertView.setTag(holder);
        } else {
            holder = (VoteTextAdapter.ViewHolder) convertView.getTag();
        }

        PollItemInfo itemInfo = mDatas.get(position);


        holder.tvVoteIntro.setText(itemInfo.pollItemText);

        holder.voteTextCl.setTag(R.id.id_text_vote_tag, position);
        holder.voteTextCl.setOnTouchListener(onTouchListener);

       /* if (isAccout) {
            holder.voteTextCl.setBackgroundResource(R.drawable.vote_master_mode_stroke);
        } else {
            if (itemInfo.isSelected) {
                holder.voteTextCl.setBackgroundResource(R.drawable.vote_guest_mode_sucess_stroke);
            } else {
                if (isEnd || isVote) {
                    holder.voteTextCl.setBackgroundResource(R.drawable.vote_master_mode_stroke);
                } else {
                    holder.voteTextCl.setBackgroundResource(R.drawable.vote_guest_mode_stroke);
                }


            }

        }*/


        if (total != 0) {
            if (itemInfo.isSelected) {
                setSelectedProcessBarColor(holder);
                TextPaint paint = holder.tvVoteRate.getPaint();
                paint.setFakeBoldText(true);

                TextPaint paint1 = holder.tvVoteIntro.getPaint();
                paint1.setFakeBoldText(true);
            } else {
                setUnSelectedProcessBarColor(holder);
                TextPaint paint = holder.tvVoteRate.getPaint();
                paint.setFakeBoldText(false);
                TextPaint paint1 = holder.tvVoteIntro.getPaint();
                paint1.setFakeBoldText(false);
            }
            if (isEnd || isVote || isAccout) {
                holder.tvVoteRate.setVisibility(View.VISIBLE);
                long rate = 0;
                rate = itemInfo.choiceCount * 100 / total;
                holder.tvVoteRate.setText(rate + "%");
                setProcess(rate, holder);
            } else {
                setProcess(100, holder);
                holder.tvVoteRate.setVisibility(View.GONE);
            }


        } else {
            setUnSelectedProcessBarColor(holder);
            setProcess(100, holder);

            if (isAccout || isEnd) {
                holder.tvVoteRate.setVisibility(View.VISIBLE);
                holder.tvVoteRate.setText("0%");
            } else {
                holder.tvVoteRate.setVisibility(View.GONE);
            }


        }

      /*  if (isAccout || isVote || isEnd) {
            holder.tvVoteRate.setVisibility(View.VISIBLE);
            holder.pbVoteRate.setVisibility(View.VISIBLE);
            long rate = 0;
            if (total != 0) {
                rate = itemInfo.choiceCount * 100 / total;
            }
            holder.tvVoteRate.setText(rate + "%");
            setProcess(rate, holder);
        } else {
            holder.tvVoteRate.setVisibility(View.GONE);
        }*/

        return convertView;
    }

    private void setProcess(float process, VoteTextAdapter.ViewHolder holder) {
        if (process < 2) {
            process = 2;
        }
        float rate = process / 100f;
        if (rate == 1) {
            rate = 0.9999f;
        }
        ConstraintSet processConstrainSet = new ConstraintSet();
        processConstrainSet.clone(holder.voteTextCl);
        processConstrainSet.constrainPercentWidth(R.id.vote_item_process_bar, rate);
        processConstrainSet.applyTo(holder.voteTextCl);
    }

    private void setSelectedProcessBarColor(VoteTextAdapter.ViewHolder holder) {
        holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_selected);
    }

    private void setUnSelectedProcessBarColor(VoteTextAdapter.ViewHolder holder) {
        holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_un_select);
    }

    private void setProcessBarColor(VoteTextAdapter.ViewHolder holder, int position) {

       /* if (getCount() == 2) {
            switch (position) {
                case 0:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_3);
                    break;
                case 1:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_2);
                    break;
                default:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_3);

            }

        } else if (getCount() == 3) {
            switch (position) {
                case 0:

                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_1);
                    break;
                case 1:

                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_2);
                    break;
                case 2:

                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_3);
                    break;
                default:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_1);

            }

        } else if (getCount() == 4) {
            switch (position) {
                case 0:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_1);
                    break;
                case 1:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_2);
                    break;
                case 2:

                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_3);
                    break;
                case 3:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_4);
                    break;
                default:
                    holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_1);

            }

        }*/

    }

    class ViewHolder {
        ConstraintLayout voteTextCl;
        TextView tvVoteRate;
        TextView tvVoteIntro;
        View pbVoteRate;
        View hideView;
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isVote || isEnd) return true;
                    if (isAccout) return true;
                    showdownChangeAnimation(v);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isAccout) {
                        ToastUtils.showShort(MyApplication.getAppContext().getResources().getString(R.string.poll_cannot_vote_yourself));
                        return true;
                    }
                    if (isVote || isEnd) {
                        ExposeEventReporter.INSTANCE.reportPostClick(postBase, source, EventLog1.FeedView.FeedClickArea.POLL);
                        onClickFeedRootView(postBase);
                        voteTextItemCheckedListener.onClick();
                        return true;
                    }
                    showupChangeAnimation(v);
                    voteTextItemCheckedListener.onChecked((Integer) v.getTag(R.id.id_text_vote_tag));
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    if (isAccout) return true;
                    showupChangeAnimation(v);
                    break;
            }
            return !isVote;
        }
    };

    private void onClickFeedRootView(PostBase postBase) {
        if (postBase == null || TextUtils.equals(source, EventConstants.FEED_PAGE_POST_DETAIL)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    private void showdownChangeAnimation(View view) {
        view.findViewById(R.id.hide_view).setVisibility(View.VISIBLE);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",
                1f, .96f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",
                1f, .96f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setDuration(100);
        animSet.start();
    }

    private void showupChangeAnimation(View view) {
        view.findViewById(R.id.hide_view).setVisibility(View.GONE);
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX",
                .96f, 1f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY",
                .96f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).with(anim2);
        animSet.setDuration(100);
        animSet.start();
    }


    public interface OnVoteTextItemCheckedListener {
        void onChecked(int pos);
        void onClick();
    }
}

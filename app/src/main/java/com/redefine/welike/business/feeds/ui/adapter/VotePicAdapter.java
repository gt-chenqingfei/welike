package com.redefine.welike.business.feeds.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.CommonUrlLoader;
import com.redefine.commonui.fresco.size.PollSizeProvider;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.commonui.adapter.BaseVoteGridAdapter;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by honglin on 2018/5/11.
 */

public class VotePicAdapter extends BaseVoteGridAdapter<PollItemInfo> {

    private LayoutInflater mInflater;

    private int width;

    private boolean isEnd;//是否结束

    private boolean isAccout;//是否是自己的

    private boolean isVote;//是否已经投票

    private long total;

    private String source;

    private PostBase postBase;

    public VotePicAdapter() {
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

    private OnVoteItemCheckedListener voteItemCheckedListener;

    public void setVoteItemCheckedListener(OnVoteItemCheckedListener voteItemCheckedListener) {
        this.voteItemCheckedListener = voteItemCheckedListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
            width = (ScreenUtils.getSreenWidth(parent.getContext()) - ScreenUtils.dip2Px(parent.getContext(), 38)) / 2;
        }

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.feed_vote_item, parent, false);

            holder = new ViewHolder();

            holder.llVote = convertView.findViewById(R.id.ll_vote);
            holder.rlRate = convertView.findViewById(R.id.rl_rate);
            holder.feedVoteGridItem = convertView.findViewById(R.id.feed_vote_grid_item);
            holder.voteProgressLayout = convertView.findViewById(R.id.vote_progress_layout);
            holder.tvVoteIntro = convertView.findViewById(R.id.tv_vote_intro);
            holder.tvVoteRate = convertView.findViewById(R.id.tv_vote_rate);
            holder.pbVoteRate = convertView.findViewById(R.id.vote_item_process_bar);
            holder.hideView = convertView.findViewById(R.id.hide_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PollItemInfo itemInfo = mUrls.get(position);

        initSimpleDraweeView(holder.feedVoteGridItem);

        CommonUrlLoader.getInstance().loadUrl(holder.feedVoteGridItem, itemInfo.pollItemPic, new PollSizeProvider() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return width;
            }
        });


        holder.tvVoteIntro.setText(itemInfo.pollItemText);

        holder.pbVoteRate.setBackgroundResource(R.drawable.vote_pic_rate);

        long rate = 0;
        if (total != 0)
            rate = itemInfo.choiceCount * 100 / total;
        holder.tvVoteRate.setText(String.valueOf(rate) + "%");
        setProcess(rate, holder);
        holder.llVote.setOnTouchListener(onTouchListener);
        holder.tvVoteIntro.setBackgroundResource(R.drawable.feed_poll_down_defualt_view_bg);
        if (isVote) {
            if (itemInfo.isSelected) {
                holder.pbVoteRate.setBackgroundResource(R.drawable.vote_text_pro_bar_selected);
            } else {
                holder.pbVoteRate.setBackgroundResource(R.drawable.vote_pic_rate);
            }
            holder.rlRate.setVisibility(View.VISIBLE);
        } else {
            holder.llVote.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.transparent));
            holder.llVote.setPadding(0, 0, 0, 0);
            if (isEnd) {
                holder.rlRate.setVisibility(View.VISIBLE);
            } else {
                if (isAccout) {
                    holder.rlRate.setVisibility(View.VISIBLE);
                } else {
                    holder.rlRate.setVisibility(View.INVISIBLE);

                    holder.llVote.setTag(R.id.id_vote_tag, position);
                }
            }
        }
        return convertView;
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
                        ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "poll_cannot_vote_yourself"));
                        return true;
                    }
                    if (isVote || isEnd) {
                        ExposeEventReporter.INSTANCE.reportPostClick(postBase, source, EventLog1.FeedView.FeedClickArea.POLL);
                        onClickFeedRootView(postBase);
                        voteItemCheckedListener.onClickPoll();
                        return true;
                    }
                    showupChangeAnimation(v);
                    voteItemCheckedListener.onChecked((Integer) v.getTag(R.id.id_vote_tag));
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

    public void setProcess(float process, ViewHolder holder) {
        if (process < 2) {
            process = 2;
        }
        ConstraintSet processConstrainSet = new ConstraintSet();
        processConstrainSet.clone(holder.voteProgressLayout);
        processConstrainSet.constrainPercentWidth(R.id.vote_item_process_bar, process / 100);
        processConstrainSet.applyTo(holder.voteProgressLayout);
    }

    private SimpleDraweeView initSimpleDraweeView(SimpleDraweeView simpleDraweeView) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(simpleDraweeView.getContext().getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setFailureImage(R.drawable.feed_nine_grid_img_error)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER)
                .setPlaceholderImage(R.drawable.feed_nine_grid_img_default)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setBackgroundResource(R.color.mine_user_host_bottom_line);
        return simpleDraweeView;
    }


    public interface OnVoteItemCheckedListener {
        void onChecked(int pos);
        void onClickPoll();
    }

    class ViewHolder {
        ConstraintLayout voteProgressLayout;
        RelativeLayout llVote, rlRate;
        SimpleDraweeView feedVoteGridItem;
        TextView tvVoteRate;
        View pbVoteRate;
        TextView tvVoteIntro;
        View hideView;
    }


}

package com.redefine.welike.commonui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.welike.R;

/**
 * Created by mengnan on 2018/5/11.
 **/
public class TextVoteItemView extends LinearLayout {
    public static final int MASTER_MODE = 1;
    public static final int GUEST_MODE = 2;
    public static final int GUEST_SUCCESS_MODE = 3;

    public static final int NORMAL_ONE = 1;
    public static final int NORMAL_TWO = 2;
    public static final int NORMAL_THREE = 3;
    public static final int NORMAL_FOUR = 4;
    //只有两个投票选项时的情况
    public static final int ANOTHER_ONE = 5;
    public static final int ANOTHER_TWO = 6;


    private View mView;
    private ConstraintLayout parent;
    private View voteProcessbar;
    private TextView voteTitleTv;
    private TextView votePercentTv;
    private ConstraintSet processConstrainSet = new ConstraintSet();
    private boolean isPolled = false;

    private int mId = 0;

    private TextVoteItemView.OnVoteItemCheckedListener voteItemCheckedListener;

    public void setVoteItemCheckedListener(TextVoteItemView.OnVoteItemCheckedListener voteItemCheckedListener) {
        this.voteItemCheckedListener = voteItemCheckedListener;
    }

    public TextVoteItemView(Context context) {
        super(context);
        initView(context);
    }

    public TextVoteItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextVoteItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.vote_text_item_view, this);
        parent = mView.findViewById(R.id.vote_text_item_layout);
        voteTitleTv = mView.findViewById(R.id.vote_item_text);
        voteProcessbar = parent.findViewById(R.id.vote_item_process_bar);
        votePercentTv = parent.findViewById(R.id.vote_item_process_text);


    }

    public void setId(int id) {
        mId = id;
    }

    public void setMode(int mode) {
        switch (mode) {
            case MASTER_MODE:
                parent.setBackground(getResources().getDrawable(R.drawable.vote_master_mode_stroke));
                break;
            case GUEST_MODE:
                parent.setBackground(getResources().getDrawable(R.drawable.vote_guest_mode_stroke));
                break;
            case GUEST_SUCCESS_MODE:
                parent.setBackground(getResources().getDrawable(R.drawable.vote_guest_mode_sucess_stroke));
                break;
            default:
                parent.setBackground(getResources().getDrawable(R.drawable.vote_master_mode_stroke));
        }

    }

    public void setProcess(long process) {
        processConstrainSet.clone(parent);
        float rate = process / 100f;
        processConstrainSet.constrainPercentWidth(R.id.vote_item_process_bar, rate);
        processConstrainSet.applyTo(parent);
    }

    public void setColorMode(int colorMode) {
        switch (colorMode) {
            case NORMAL_ONE:
                voteProcessbar.setBackground(getResources().getDrawable(R.drawable.vote_text_pro_bar_1));
                break;
            case NORMAL_TWO:
                voteProcessbar.setBackground(getResources().getDrawable(R.drawable.vote_text_pro_bar_2));

                break;
            case NORMAL_THREE:
                voteProcessbar.setBackground(getResources().getDrawable(R.drawable.vote_text_pro_bar_3));

                break;

            case NORMAL_FOUR:
                voteProcessbar.setBackground(getResources().getDrawable(R.drawable.vote_text_pro_bar_4));

                break;
            case ANOTHER_ONE:
                voteProcessbar.setBackground(getResources().getDrawable(R.drawable.vote_text_pro_bar_3));

                break;
            case ANOTHER_TWO:
                voteProcessbar.setBackground(getResources().getDrawable(R.drawable.vote_text_pro_bar_4));

                break;

        }

    }


    public void setTitle(String title) {
        voteTitleTv.setText(title);

    }

    public void setProcessText(String text) {
        votePercentTv.setText(text);

    }

    protected View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isPolled) {
                        return true;
                    }
                    showdownChangeAnimation(v);
                    break;
                case MotionEvent.ACTION_UP:
                    showupChangeAnimation(v);
                    voteItemCheckedListener.onChecked(mId);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    showupChangeAnimation(v);
                    break;
            }
            return true;
        }
    };


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

    public interface OnVoteItemCheckedListener {
        void onChecked(int pos);
    }

    protected void setIsPoll(boolean isPolled) {
        this.isPolled = isPolled;
        if (isPolled) {
            setOnTouchListener(null);
        }

    }

}

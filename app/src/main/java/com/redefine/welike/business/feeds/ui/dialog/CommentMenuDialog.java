package com.redefine.welike.business.feeds.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.Comment;

/**
 * Created by MR on 2018/1/19.
 */

public class CommentMenuDialog extends Dialog implements View.OnClickListener {
    private RichTextView mTitleView;
    private TextView mForwardView;
    private TextView mReplyView;
    private TextView mDeleteView;//删除按钮
    private OnCommentMenuClickListener mOnMenuClickListener;

    public CommentMenuDialog(@NonNull Context context) {
        super(context, R.style.BaseAppTheme_Dialog);
        setContentView(R.layout.comment_menu_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.windowAnimations = R.style.BottomPopDialogAnim;
        window.setAttributes(lp);
        initViews();
    }

    private void initViews() {
        mTitleView = findViewById(R.id.comment_menu_title);
        mForwardView = findViewById(R.id.comment_menu_forward);
        mReplyView = findViewById(R.id.comment_menu_reply);
        mDeleteView = findViewById(R.id.comment_menu_delete);

        mForwardView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_forward"));
        mReplyView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_menu_reply"));
        mDeleteView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_delete_confirm"));

        mForwardView.setOnClickListener(this);
        mReplyView.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);
    }

    private void checkMyself(String uid){
        mDeleteView.setVisibility(AccountManager.getInstance().isSelf(uid) ? View.VISIBLE : View.GONE);
//        mDeleteView.setVisibility(View.GONE);
    }

    public static void show(Context context, final Comment comment, OnCommentMenuClickListener listener) {
        CommentMenuDialog dialog = new CommentMenuDialog(context);
        dialog.setOnMenuClickListener(listener);
        dialog.checkMyself(comment.getUid());
        ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.comment_menu_text_color));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(comment.getNickName() + "\b");
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialog.mTitleView.getRichProcessor().setRichContent(comment.getContent(), comment.getRichItemList());
        dialog.mTitleView.getRichProcessor().insertCharSequence(0, spannableStringBuilder);
        dialog.show();
    }

    public static interface OnCommentMenuClickListener {
        final int FORWARD_TYPE = 0;
        final int REPLY_TYPE = 1;
        final int DELETE_TYPE = 2;

        void onClick(View v, int type);
    }

    public void setOnMenuClickListener(OnCommentMenuClickListener listener) {
        mOnMenuClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == mForwardView) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onClick(v, OnCommentMenuClickListener.FORWARD_TYPE);
            }
        } else if (v == mReplyView) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onClick(v, OnCommentMenuClickListener.REPLY_TYPE);
            }
        }else if (v == mDeleteView) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onClick(v, OnCommentMenuClickListener.DELETE_TYPE);
            }
        }
        dismiss();
    }
}

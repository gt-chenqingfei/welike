package com.redefine.welike.business.publisher.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.publisher.management.DraftManager;
import com.redefine.welike.business.publisher.management.FeedCommentReplier;
import com.redefine.welike.business.publisher.management.FeedCommentSender;
import com.redefine.welike.business.publisher.management.FeedPoster;
import com.redefine.welike.business.publisher.management.FeedReplyReplier;
import com.redefine.welike.business.publisher.management.FeedReposter;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.management.bean.DraftBase;
import com.redefine.welike.business.publisher.management.bean.DraftCategory;
import com.redefine.welike.business.publisher.management.bean.DraftComment;
import com.redefine.welike.business.publisher.management.bean.DraftForwardPost;
import com.redefine.welike.business.publisher.management.bean.DraftPost;
import com.redefine.welike.business.publisher.management.bean.DraftReply;
import com.redefine.welike.business.publisher.management.bean.DraftReplyBack;
import com.redefine.welike.business.publisher.ui.viewholder.BaseDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.ForwardDeleteDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.ForwardPicDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.ForwardTextDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.ForwardVideoDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.PicDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.PollDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.TextDraftViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.VideoDraftViewHolder;
import com.redefine.welike.commonui.event.model.PublishEventModel;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/19.
 */

public class DraftAdapter extends RecyclerView.Adapter<BaseDraftViewHolder> implements IDraftOperationListener {

    public static final int TEXT_DRAFT = 0;
    public static final int PIC_DRAFT = 1;
    public static final int VIDEO_DRAFT = 2;
    public static final int FORWARD_TEXT_DRAFT = 3;
    public static final int FORWARD_PIC_DRAFT = 4;
    public static final int FORWARD_VIDEO_DRAFT = 5;
    private static final int FORWARD_DELETE_DRAFT = 6;
    public static final int POLL_DRAFT = 7;


    private final List<DraftBase> mDraftBases = new ArrayList<>();
//    private final IDraftContract.IDraftView mView;
    private LayoutInflater mInflater;

    public DraftAdapter() {
//        mView = view;
    }

    public void setDrafts(List<DraftBase> list) {
        mDraftBases.clear();
        if (!CollectionUtil.isEmpty(list)) {
            mDraftBases.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseDraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        if (viewType == TEXT_DRAFT) {
            return new TextDraftViewHolder(mInflater.inflate(R.layout.text_draft_layout, parent, false), this);
        } else if (viewType == PIC_DRAFT) {
            return new PicDraftViewHolder(mInflater.inflate(R.layout.pic_draft_layout, parent, false), this);
        } else if (viewType == POLL_DRAFT) {
            return new PollDraftViewHolder(mInflater.inflate(R.layout.poll_draft_layout, parent, false), this);
        } else if (viewType == VIDEO_DRAFT) {
            return new VideoDraftViewHolder(mInflater.inflate(R.layout.video_draft_layout, parent, false), this);
        } else if (viewType == FORWARD_TEXT_DRAFT) {
            return new ForwardTextDraftViewHolder(mInflater.inflate(R.layout.forward_draft_text_layout, parent, false), this);
        } else if (viewType == FORWARD_PIC_DRAFT) {
            return new ForwardPicDraftViewHolder(mInflater.inflate(R.layout.forward_pic_draft_layout, parent, false), this);
        } else if (viewType == FORWARD_VIDEO_DRAFT) {
            return new ForwardVideoDraftViewHolder(mInflater.inflate(R.layout.forward_draft_video_layout, parent, false), this);
        } else if (viewType == FORWARD_DELETE_DRAFT) {
            return new ForwardDeleteDraftViewHolder(mInflater.inflate(R.layout.forward_draft_delete_layout, parent, false), this);
        } else {
            return new TextDraftViewHolder(mInflater.inflate(R.layout.text_draft_layout, parent, false), this);
        }

    }

    @Override
    public void onBindViewHolder(BaseDraftViewHolder holder, int position) {
        holder.bindViews(this, mDraftBases.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        DraftBase draftBase = mDraftBases.get(position);
        if (draftBase.getType() == DraftCategory.POST) {
            DraftPost draft = (DraftPost) draftBase;
            return getDraftType(draft);
        } else if (draftBase.getType() == DraftCategory.FORWARD) {
            DraftForwardPost draft = (DraftForwardPost) draftBase;
            return getDraftType(draft);
        } else if (draftBase.getType() == DraftCategory.COMMENT) {
            DraftComment draft = (DraftComment) draftBase;
            return getDraftType(draft);
        } else if (draftBase.getType() == DraftCategory.REPLY) {
            DraftReply draft = (DraftReply) draftBase;
            return getDraftType(draft);
        } else if (draftBase.getType() == DraftCategory.COMMENT_REPLY_BACK) {
            DraftReplyBack draft = (DraftReplyBack) draftBase;
            return getDraftType(draft);
        }
        return super.getItemViewType(position);
    }

    private int getDraftType(DraftReplyBack draft) {
        return TEXT_DRAFT;
    }

    private int getDraftType(DraftReply draft) {
        return TEXT_DRAFT;
    }

    private int getDraftType(DraftComment draft) {
        return TEXT_DRAFT;
    }

    private int getDraftType(DraftForwardPost draft) {
        if (draft.isForwardDeleted()) {
            return FORWARD_DELETE_DRAFT;
        } else if (draft.getRootPost().getType() == PostBase.POST_TYPE_VIDEO) {
            return FORWARD_VIDEO_DRAFT;
        } else if (draft.getRootPost().getType() == PostBase.POST_TYPE_PIC) {
            return FORWARD_PIC_DRAFT;
        } else if (draft.getRootPost().getType() == PostBase.POST_TYPE_LINK) {
            return FORWARD_PIC_DRAFT;
        } else {
            return FORWARD_TEXT_DRAFT;
        }
    }

    private int getDraftType(DraftPost draft) {
        if (!CollectionUtil.isEmpty(draft.getPollItemInfos())) {
            return POLL_DRAFT;
        } else if (!CollectionUtil.isEmpty(draft.getPicDraftList())) {
            // 图片样式
            return PIC_DRAFT;
        } else if (draft.getVideo() != null) {
            // 视频样式
            return VIDEO_DRAFT;
        } else {
            return TEXT_DRAFT;
        }
    }

    @Override
    public int getItemCount() {
        return mDraftBases.size();
    }

    @Override
    public void onDelete(DraftBase draftBase) {
        int position = mDraftBases.indexOf(draftBase);
        int size = mDraftBases.size();
        mDraftBases.remove(draftBase);
        if (position >= 0 && position < size) {
            notifyItemRemoved(position);
        }
        DraftManager.getInstance().delete(draftBase);
//        if (CollectionUtil.isEmpty(mDraftBases)) {
//            mView.showEmptyView();
//        } else {
//            mView.showContentView();
//        }
    }

    @Override
    public void onResend(final DraftBase draftBase) {
        int position = mDraftBases.indexOf(draftBase);
        if (position == -1) {
            return;
        }
        doSendPost(draftBase);
//        if () {
//            mDraftBases.remove(draftBase);
//            notifyItemRemoved(position);
//
////            if (CollectionUtil.isEmpty(mDraftBases)) {
////                mView.showEmptyView();
////            } else {
////                mView.showContentView();
////            }
//        }
    }

    private void doSendPost(DraftBase draftBase) {
        if (draftBase.getType() == DraftCategory.POST && draftBase instanceof DraftPost) {
//            ret = FeedPoster.Companion.getInstance().publish((DraftPost) draftBase);
            FeedPoster.Companion.getInstance().publish((DraftPost) draftBase);
//            FeedPublishFactory.INSTANCE.obtainPublisher(draftBase).publish((DraftPost)draftBase);
        } else if (draftBase.getType() == DraftCategory.FORWARD) {

            FeedReposter reposter = new FeedReposter();
            reposter.publish((DraftForwardPost) draftBase);

//            if (ret) {
//                PostBase postBase = reposter.buildOfflineForwardPost((DraftForwardPost) draftBase, ((DraftForwardPost) draftBase).getRootPost());
//                Message message = Message.obtain();
//                message.what = MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH;
//                message.obj = postBase;
//                EventBus.getDefault().post(message);
//            }
        } else if (draftBase.getType() == DraftCategory.COMMENT) {

            FeedCommentSender commentSender = new FeedCommentSender();
            commentSender.publish((DraftComment) draftBase);
//            if (ret) {
//                Comment comment = commentSender.buildOfflineComment((DraftComment) draftBase);
//                Message message = Message.obtain();
//                message.what = MessageIdConstant.MESSAGE_COMMENT_PUBLISH;
//                message.obj = comment;
//                EventBus.getDefault().post(message);
//            }
        } else if (draftBase.getType() == DraftCategory.REPLY) {
            FeedCommentReplier commentReplier = new FeedCommentReplier();
            commentReplier.publish((DraftReply) draftBase);
//            if (ret) {
//                Comment comment = commentReplier.buildOfflineComment((DraftReply) draftBase);
//                Message message = Message.obtain();
//                message.what = MessageIdConstant.MESSAGE_COMMENT_PUBLISH;
//                message.obj = comment;
//                EventBus.getDefault().post(message);
//            }
        } else if (draftBase.getType() == DraftCategory.COMMENT_REPLY_BACK) {
            FeedReplyReplier commentReplier = new FeedReplyReplier();
            commentReplier.publish((DraftReplyBack) draftBase);
//            if (ret) {
//                Comment comment = commentReplier.buildOfflineComment((DraftReplyBack) draftBase);
//                Message message = Message.obtain();
//                message.what = MessageIdConstant.MESSAGE_COMMENT_PUBLISH;
//                message.obj = comment;
//                EventBus.getDefault().post(message);
//            }
        }

        PublishEventModel m = PublishAnalyticsManager.Companion.getInstance().obtainEventModel(draftBase.getDraftId());
        m.setReSendFrom(EventLog1.Publish.ReSendFrom.DRAFT);
        m.getProxy().report34();

    }
}

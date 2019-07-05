package com.redefine.welike.business.message.ui.adapter;

import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.loadmore.viewholder.WhiteBgLoadMoreViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.message.management.bean.CommonNotification;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.ui.viewholder.BigPicViewHolder;
import com.redefine.welike.business.message.ui.viewholder.BigTextViewHolder;
import com.redefine.welike.business.message.ui.viewholder.HeaderViewHolder;
import com.redefine.welike.business.message.ui.viewholder.MessageBoxViewHolder3;
import com.redefine.welike.business.message.ui.viewholder.SmallPicViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/10.
 */

public class MessageBoxAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, NotificationBase> {

    public static final int MESSAGE_UN_SUOOPRT_TYPE = 0;
    public static final int MESSAGE_TEXT_TYPE = 1;
    public static final int MESSAGE_IMAGE_TEXT_TYPE = 2;
    public static final int MESSAGE_VIDEO_TYPE = 3;


    private final List<NotificationBase> mData = new ArrayList<>();
    private int magic = 0;
    private int news = 0;

    private final int TYPE_HEADER_NEW = 1;
    private final int TYPE_HEADER_OLD = 2;
    private final int TYPE_ITEM = 0;

    private final int PUSH_TYPE_ITEM_201 = 201;
    private final int PUSH_TYPE_ITEM_202 = 202;
    private final int PUSH_TYPE_ITEM_203 = 203;


    public MessageBoxAdapter() {

    }

    public void addNewData(List<NotificationBase> postBases, int news) {
        this.news = (postBases != null && postBases.size() > 0) ? news : 0;
        mData.clear();
        if (!CollectionUtil.isEmpty(postBases)) {
            mData.addAll(0, postBases);
        }
        notifyDataSetChanged();
    }

    public void addHisData(List<NotificationBase> postBases) {
        if (CollectionUtil.isEmpty(postBases)) {
            return;
        }
        mData.addAll(postBases);
        notifyDataSetChanged();
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new WhiteBgLoadMoreViewHolder(mInflater.inflate(com.redefine.commonui.R.layout.load_more_layout, parent, false));

    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER_NEW) {
            return new HeaderViewHolder(mInflater.inflate(R.layout.msg_header_layout, parent, false), true);
        } else if (viewType == TYPE_HEADER_OLD) {
            return new HeaderViewHolder(mInflater.inflate(R.layout.msg_header_layout, parent, false), false);
        } else if (viewType == PUSH_TYPE_ITEM_201) {
            return new SmallPicViewHolder(mInflater.inflate(R.layout.notification_push_item_small_icon, parent, false));
        } else if (viewType == PUSH_TYPE_ITEM_202) {
            return new BigPicViewHolder(mInflater.inflate(R.layout.notification_push_item_big_icon, parent, false));
        } else if (viewType == PUSH_TYPE_ITEM_203) {
            return new BigTextViewHolder(mInflater.inflate(R.layout.notification_item, parent, false));
        }

        return new MessageBoxViewHolder3(mInflater.inflate(R.layout.msg_item_layout2, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        if (holder instanceof MessageBoxViewHolder3) {
            MessageBoxViewHolder3 mh = ((MessageBoxViewHolder3) holder);
            mh.setMagic(magic);
            mh.bindViews(this, getRealItem(position));
        }

        if (holder instanceof BigPicViewHolder) {
            BigPicViewHolder mh = ((BigPicViewHolder) holder);
            mh.bindViews(this, getRealItem(position));
        }
        if (holder instanceof SmallPicViewHolder) {
            SmallPicViewHolder mh = ((SmallPicViewHolder) holder);
            mh.bindViews(this, getRealItem(position));
        }
        if (holder instanceof BigTextViewHolder) {
            BigTextViewHolder mh = ((BigTextViewHolder) holder);
            mh.bindViews(this, getRealItem(position));
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        if (news > 0) {
            if (position == 0) {
                return TYPE_HEADER_NEW;
            }
            if (position == (news + 1)) {
                return TYPE_HEADER_OLD;
            }
        }
        if (position < mData.size() && mData.get(position).getAction().equals("201")) {
            return PUSH_TYPE_ITEM_201;
        }
        if (position < mData.size() && mData.get(position).getAction().equals("202")) {
            return PUSH_TYPE_ITEM_202;
        }
        if (position < mData.size() && mData.get(position).getAction().equals("203")) {
            return PUSH_TYPE_ITEM_203;
        }
        return TYPE_ITEM;
    }


    @Override
    public int getRealItemCount() {
        if (mData == null) {
            return 0;
        } else {
            int dataSize = mData.size();
            if (news > 0) {
                //如果有news 就显示1-2个 额外的item 作为header
                return dataSize + (news >= dataSize ? 1 : 2);
            } else {
                return dataSize;
            }
        }
    }

    @Override
    protected NotificationBase getRealItem(int position) {
        if (news > 0) {
            if (position == 0) {
                return new CommonNotification();
            }
            if (position < (news + 1)) {
                return mData.get(position - 1);
            }
            if (position == (news + 1)) {
                return new CommonNotification();
            }
            return mData.get(position - 2);
        }
        return mData.get(position);
    }

}

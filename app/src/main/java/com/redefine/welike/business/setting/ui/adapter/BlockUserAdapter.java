package com.redefine.welike.business.setting.ui.adapter;

import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.CommonTextHeadBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.setting.ui.viewholder.BlockUserHeadViewHolder;
import com.redefine.welike.business.setting.ui.viewholder.BlockUserItemViewHolder;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

public class BlockUserAdapter extends LoadMoreFooterRecyclerAdapter<CommonTextHeadBean, User> {

    private final OnBlockUserOpCallback mCallback;
    private List<User> mUsers = new ArrayList<>();

    public BlockUserAdapter(OnBlockUserOpCallback callback) {
        mCallback = callback;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new BlockUserItemViewHolder(mInflater.inflate(R.layout.block_user_item, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new BlockUserHeadViewHolder(mInflater.inflate(R.layout.block_user_head_title, parent, false));
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getHeader());
    }


    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        final User user = getRealItem(position);
        holder.bindViews(this, user);
        if (holder instanceof BlockUserItemViewHolder) {
            ((BlockUserItemViewHolder) holder).mUnBlockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onUnBlock(user);
                    }
                }
            });
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return mUsers.size();
    }

    @Override
    protected User getRealItem(int position) {
        return mUsers.get(position);
    }

    public void setData(List<User> users) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(mUsers, users), true);
        mUsers.clear();
        if (!CollectionUtil.isEmpty(users)) {
            mUsers.addAll(users);
        }
        diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(getAdapterItemPosition(position), count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(getAdapterItemPosition(position), count);

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(getAdapterItemPosition(fromPosition), getAdapterItemPosition(toPosition));

            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                notifyItemRangeChanged(getAdapterItemPosition(position), count, payload);
            }
        });
    }

    public static interface OnBlockUserOpCallback {
        void onUnBlock(User user);
    }

    public static interface OnAddUserClickListener {
        void onAddUserClick();
    }

    public static class DiffCallBack extends DiffUtil.Callback {
        private List<User> mOldDataList, mNewDataList;

        public DiffCallBack(List<User> mOldDataList, List<User> mNewDataList) {
            this.mOldDataList = mOldDataList;
            this.mNewDataList = mNewDataList;
        }

        @Override
        public int getOldListSize() {
            return mOldDataList != null ? mOldDataList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewDataList != null ? mNewDataList.size() : 0;
        }
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(mOldDataList.get(oldItemPosition).getUid(), mNewDataList.get(newItemPosition).getUid());
        }
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    }
}

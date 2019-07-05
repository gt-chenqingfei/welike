package com.redefine.welike.business.startup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.bean.RecommondUser;
import com.redefine.welike.business.startup.ui.viewholder.RecommondTagViewHolder;
import com.redefine.welike.business.startup.ui.viewholder.RecommondUserViewHolder;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.viewholder.InterestEmptyViewHolder;

import java.util.ArrayList;
import java.util.List;

public class RegisterRecommondUserAdapter2 extends RecyclerView.Adapter {

    private static final int ITEM_TYPE_TAG = 1;
    private static final int ITEM_TYPE_USER = 2;
    private static final int ITEM_TYPE_EMPTY = 3;

    private List<UserItem> mData;

    public RegisterRecommondUserAdapter2() {
        mData = new ArrayList<>();
    }

    private class UserItem {
        /**
         * 用户对象
         */
        private User user;
        /**
         * 标签
         */
        private String tag;
        /**
         * 是否选中
         */
        private boolean select;
        /**
         * 如果是标签则为true，如果是用户则为false
         */
        private boolean isTag;

        private boolean isEmpty = false;

        UserItem(User user, String tag, boolean isTag) {
            this.user = user;
            this.tag = tag;
            this.isTag = isTag;
            select = true;
        }

        public UserItem(boolean isEmpty) {
            this.isEmpty = isEmpty;
        }
    }

    public void setData(List<RecommondUser> users) {
        if (users == null || users.size() < 1) {
            return;
        }
        mData.clear();
        int userCount = 0;
        for (RecommondUser recommondUser : users) {
            if (recommondUser != null) {
                if (TextUtils.isEmpty(recommondUser.tagName) || CollectionUtil.isEmpty(recommondUser.userList)) {
                    continue;
                }
                mData.add(new UserItem(null, recommondUser.tagName, true));
                List<User> userList = recommondUser.userList;
                if (userList != null && userList.size() > 0) {
                    for (User user : userList) {
                        mData.add(new UserItem(user, null, false));
                        userCount++;
                    }
                }
            }
        }
        mData.add(new UserItem(true));
        StartEventManager.getInstance().setFollow_list(userCount);
        notifyDataSetChanged();
    }

    public List<String> getSelectedUids() {
        List<String> list = new ArrayList<>();
        for (UserItem item : mData) {
            if (!item.isTag && !item.isEmpty) {
                if (item.select) {
                    list.add(item.user.getUid());
                }
            }
        }
        return list;
    }

    public int getAllUsersCount() {
        int count = 0;
        for (UserItem item : mData) {
            if (!item.isTag && !item.isEmpty) {
                count++;
            }
        }
        return count;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if (viewType == ITEM_TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest_empty, null);
            return new InterestEmptyViewHolder(view);
        } else if (viewType == ITEM_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommond_user_list_item, parent, false);
            final RecommondUserViewHolder recommondUserViewHolder = new RecommondUserViewHolder(view);

            recommondUserViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserClick(recommondUserViewHolder.getAdapterPosition(), recommondUserViewHolder);
                }
            });
            return recommondUserViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommond_tag_list_item, parent, false);
            final RecommondTagViewHolder recommondTagViewHolder = new RecommondTagViewHolder(view);

            recommondTagViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTagClick(recommondTagViewHolder.getAdapterPosition(), recommondTagViewHolder);
                }
            });
            return recommondTagViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position > mData.size() - 1) {
            return;
        }
        UserItem userItem = mData.get(position);
        if (userItem == null) {
            return;
        }
        if (holder instanceof RecommondUserViewHolder) {
            ((RecommondUserViewHolder) holder).bindViews(userItem.user);
            ((RecommondUserViewHolder) holder).cb_recommond.setSelected(userItem.select);
        } else if (holder instanceof RecommondTagViewHolder) {
            ((RecommondTagViewHolder) holder).bindViews(userItem.tag);
            ((RecommondTagViewHolder) holder).mChecked.setSelected(userItem.select);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        UserItem userItem = mData.get(position);
        if (userItem.isEmpty) {
            return ITEM_TYPE_EMPTY;
        } else {
            if (userItem.isTag) {
                return ITEM_TYPE_TAG;
            } else {
                return ITEM_TYPE_USER;
            }
        }
    }

    private void onUserClick(int position, RecommondUserViewHolder holder) {
        UserItem item = mData.get(position);
        if (item != null && !item.isEmpty) {
            item.select = !item.select;
            holder.cb_recommond.setSelected(item.select);
        }
    }

    private void onTagClick(int position, RecommondTagViewHolder holder) {
        UserItem tagItem = mData.get(position);
        if (tagItem != null) {
            tagItem.select = !tagItem.select;
            holder.mChecked.setSelected(tagItem.select);
            position++;
            int start = position;
            while (position < mData.size()) {
                UserItem userItem = mData.get(position);
                if (userItem == null || userItem.isTag) {
                    break;
                }
                userItem.select = tagItem.select;
                position++;
            }
            notifyItemRangeChanged(start, position - start);
        }
    }
}

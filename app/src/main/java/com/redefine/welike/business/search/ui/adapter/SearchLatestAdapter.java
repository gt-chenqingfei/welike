package com.redefine.welike.business.search.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchResultLatestUserViewHolder;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.statistical.EventConstants;

import java.util.List;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class SearchLatestAdapter extends FeedRecyclerViewAdapter<SearchUserHeaderBean> {

    private final ISearchLatestListener mSearchUserAllClickListener;
    private IBrowseClickListener mBrowseClickListener;

    public SearchLatestAdapter(IPageStackManager pageStackManager, ISearchLatestListener listener) {
        super(pageStackManager, EventConstants.FEED_PAGE_SEARCH_LATEST);
        mSearchUserAllClickListener = listener;
    }

    public void setData(List<User> users, List<PostBase> postBases) {
        if (CollectionUtil.isEmpty(users)) {
            setHeader(null);
        } else {
            setHeader(new SearchUserHeaderBean(users));
        }

        addNewData(postBases);
    }

    public int indexOfUser(User user) {
        SearchUserHeaderBean header = getHeader();
        if (header != null) {
            List<User> users = header.getUsers();
            if (users != null) {
                return users.indexOf(user);
            }
        }
        return -1;
    }

    public int indexOfPost(PostBase postBase) {
        return mPostBaseList.indexOf(postBase);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultLatestUserViewHolder(mInflater.inflate(R.layout.search_latest_user, parent, false), mSearchUserAllClickListener);
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getHeader());
    }

    @Override
    public BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateItemViewHolder(parent, viewType);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        super.onBindItemViewHolder(holder, position);

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        holder.itemView.setLayoutParams(marginLayoutParams);
        if (holder instanceof BaseFeedViewHolder && mBrowseClickListener != null) {
            ((BaseFeedViewHolder) holder).setBrowseClickListener(mBrowseClickListener);
        }
    }

    public void setBrowseClickListener(IBrowseClickListener listener) {
        mBrowseClickListener = listener;
    }

    public static interface ISearchLatestListener {
        void onClickAllBtn();

        void onClickUser(User user);
    }

}

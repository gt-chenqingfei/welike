package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.search.ui.adapter.SearchLatestAdapter;
import com.redefine.welike.business.search.ui.adapter.SearchUserHeaderBean;
import com.redefine.welike.business.user.management.bean.User;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/5.
 */

public class SearchResultLatestUserViewHolder extends BaseRecyclerViewHolder<SearchUserHeaderBean> {
    private final TextView mUserAllBtn;
    private final ViewGroup mUserContainer;
    private final SearchLatestAdapter.ISearchLatestListener mListener;
    private final LayoutInflater mInflater;
    private final TextView mPeopleTitle;

    public SearchResultLatestUserViewHolder(View rootView, SearchLatestAdapter.ISearchLatestListener listener) {
        super(rootView);
        mUserAllBtn = rootView.findViewById(R.id.search_latest_all_user_btn);
        mPeopleTitle = rootView.findViewById(R.id.search_latest_people_title);
        mPeopleTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "search_latest_people_title"));
        mUserAllBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH, "search_user_all_btn"));
        mUserContainer = rootView.findViewById(R.id.search_latest_user_container);
        mListener = listener;
        mInflater = LayoutInflater.from(rootView.getContext());
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, SearchUserHeaderBean userHeaderBean) {
        if (userHeaderBean == null || CollectionUtil.isEmpty(userHeaderBean.getUsers())) {
            return ;
        }
        mUserAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickAllBtn();
            }
        });

        mUserContainer.removeAllViews();
        List<User> userList = userHeaderBean.getUsers();
        SearchResultUserViewHolder viewHolder;
        int size = userList.size();
        for (int i = 0; i < size; i++) {
            final User user = userList.get(i);
            viewHolder = new SearchResultUserViewHolder(mInflater.inflate(R.layout.search_user_item_layout, null));
            viewHolder.bindViews(adapter, userList.get(i));
            mUserContainer.addView(viewHolder.itemView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2Px(76)));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickUser(user);
                }
            });
        }
    }
}

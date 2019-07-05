package com.redefine.welike.business.user.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.publisher.ui.viewholder.ContactListViewHolder;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gongguan on 2018/2/27.
 */

public class ContactsSearchAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, User> {
    private List<User> mList;
    private LayoutInflater mLayoutInflater;
    private String searchStr;

    public ContactsSearchAdapter(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
    }

    public void setSearchKey(String searchStr){
        this.searchStr = searchStr;
    }

    public void setData(List<User> contactsList) {
        if (contactsList != null) {
            mList = new ArrayList<>();
            mList.addAll(contactsList);
            notifyDataSetChanged();
        }
    }

    public void addNewData(List<User> users) {
        mList.clear();
        if (!CollectionUtil.isEmpty(users)) {
            mList.addAll(users);
        }
        notifyDataSetChanged();
    }

    public void addHisData(List<User> users) {
        if (!CollectionUtil.isEmpty(users)) {
            mList.addAll(users);
            notifyDataSetChanged();
        }
    }

    @Override
    protected User getRealItem(int position) {
        return mList.get(position);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ContactListViewHolder(
                mLayoutInflater.inflate(R.layout.contact_list_item, parent, false));
    }


    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        ((ContactListViewHolder)holder).bindBean( mList.get(position),searchStr);
//        holder.bindViews(this, mList.get(position));
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }


    @Override
    public int getRealItemCount() {
        return mList.size();
    }


}

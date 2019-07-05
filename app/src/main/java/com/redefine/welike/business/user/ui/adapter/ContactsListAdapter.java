package com.redefine.welike.business.user.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.ui.viewholder.ContactListViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.SearchContactViewHolder;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.viewholder.OnContactsBeanClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.redefine.welike.business.publisher.ui.constant.EditorConstant.PUBLISH_CONTACTS_LIST;
import static com.redefine.welike.business.publisher.ui.constant.EditorConstant.PUBLISH_CONTACTS_LIST_TITLE;
import static com.redefine.welike.business.publisher.ui.constant.EditorConstant.PUBLISH_CONTACTS_RECENT_TITLE;

/**
 * @author gongguan
 * @time 2018/1/17 下午5:20
 */
public class ContactsListAdapter extends RecyclerView.Adapter {
    private List<ContactItem> mList = new ArrayList<>();
    private boolean recentHas;
    private LayoutInflater mLayoutInflater;
    private OnContactsBeanClickListener mOnClickListener;

    private class ContactItem {
        private int viewType;
        private User user;
    }

    public ContactsListAdapter(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
    }

    public void setData(List<User> recentContactsList, List<User> contactsList) {
        mList.clear();
        if (contactsList != null && contactsList.size() > 0) {
            if (recentContactsList != null && recentContactsList.size() > 0) {
                ContactItem recentTitle = new ContactItem();
                recentTitle.viewType = PUBLISH_CONTACTS_RECENT_TITLE;
                mList.add(recentTitle);
                for (User user : recentContactsList) {
                    ContactItem recentItem = new ContactItem();
                    recentItem.viewType = PUBLISH_CONTACTS_LIST;
                    recentItem.user = user;
                    mList.add(recentItem);
                }
                recentHas = true;
            } else {
                recentHas = false;
            }

            ContactItem contactTitle = new ContactItem();
            contactTitle.viewType = PUBLISH_CONTACTS_LIST_TITLE;
            mList.add(contactTitle);

            for (User u : contactsList) {
                ContactItem contactItem = new ContactItem();
                contactItem.viewType = PUBLISH_CONTACTS_LIST;
                contactItem.user = u;
                mList.add(contactItem);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PUBLISH_CONTACTS_RECENT_TITLE || viewType == PUBLISH_CONTACTS_LIST_TITLE) {
            SearchContactViewHolder holder = new SearchContactViewHolder(mLayoutInflater.inflate(R.layout.contacts_list_header, parent, false));
            holder.getText().setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, (viewType == PUBLISH_CONTACTS_RECENT_TITLE) ? "contact" : "recent_people"));
            boolean showEmpty = (viewType == PUBLISH_CONTACTS_LIST_TITLE) && recentHas;
            holder.getEmpty().setVisibility(showEmpty ? View.VISIBLE : View.GONE);
            return holder;
//            return new ContactsListDividerTextViewHolder(
//                    , EditorConstant.PUBLISH_CONTACTS_LIST_FIRST_TITLE, R.drawable.contacts_recently_divider);
//        } else if (viewType == PUBLISH_CONTACTS_LIST_TITLE) {
//            if (recentHas) {
//                return new ContactsListDividerTextViewHolder(
//                        mLayoutInflater.inflate(R.layout.contacts_list_second_divider_title, parent, false), EditorConstant.PUBLISH_CONTACTS_LIST_SECOND_TITLE, R.drawable.contacts_list_divider);
//            } else {
//                return new ContactsListDividerTextViewHolder(
//                        mLayoutInflater.inflate(R.layout.contacts_list_first_divider_title, parent, false), EditorConstant.PUBLISH_CONTACTS_LIST_FIRST_TITLE, R.drawable.contacts_list_divider);
//            }
        } else {
            return new ContactListViewHolder(mLayoutInflater.inflate(R.layout.contact_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final User user = mList.get(position).user;
        if (holder instanceof ContactListViewHolder && user != null) {
            ((ContactListViewHolder) holder).bindBean(user, "");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onContactsBeanClicked(user);
                }
            });
        } else if (holder instanceof SearchContactViewHolder) {
            SearchContactViewHolder header = (SearchContactViewHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).viewType;
    }

    public void setOnContactsBeanClickListener(OnContactsBeanClickListener listener) {
        mOnClickListener = listener;
    }

}


package com.redefine.welike.business.browse.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.feeds.ui.viewholder.InterestCategoryItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class BrowseDiscoverInterestAdapter extends BaseRecyclerAdapter<InterestCategoryItemViewHolder> {

    private List<UserBase.Intrest> mInterestCategories = new ArrayList<>();
    private onClickInterestListener mListener;

    private int oldPosition = 0;


    public interface onClickInterestListener {
        void onclickInterestItem(int pos);
    }


    public List<UserBase.Intrest> getInterestCategories() {
        return mInterestCategories;
    }

    public void setListener(onClickInterestListener listener) {
        mListener = listener;

    }

    @NonNull
    @Override
    public InterestCategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InterestCategoryItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InterestCategoryItemViewHolder holder, final int position) {
        if (position < mInterestCategories.size()) {
            final UserBase.Intrest intrest = mInterestCategories.get(position);
            holder.bindViews(intrest);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeUI(position);
                    mListener.onclickInterestItem(position);
                }
            });
        }
    }

    public void setSelectPos(int pos) {
        if (pos < mInterestCategories.size()) {
            mInterestCategories.get(pos).setSelected(true);
            changeUI(pos);
        }

    }


    @Override
    public int getItemCount() {
        return mInterestCategories.size();
    }

    private void changeUI(int pos) {
        if (pos == oldPosition) return;

        if (oldPosition >= mInterestCategories.size()) return;
        if (pos >= mInterestCategories.size()) return;

        mInterestCategories.get(oldPosition).setSelected(false);
        notifyItemChanged(oldPosition);
        mInterestCategories.get(pos).setSelected(true);
        notifyItemChanged(pos);
        oldPosition = pos;

    }


    public void addInterestCategroy(List<UserBase.Intrest> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        mInterestCategories.clear();
        mInterestCategories.add(createForYou());
        mInterestCategories.add(createVideo());
        mInterestCategories.addAll(list);
        notifyDataSetChanged();
    }

    public void initInterestAdapter() {
        mInterestCategories.add(createForYou());
        notifyDataSetChanged();
    }

    private UserBase.Intrest createForYou() {
        UserBase.Intrest intrest = new UserBase.Intrest();
        intrest.setShowIcon(true);
        intrest.setSelected(true);
        intrest.setIid(BrowseConstant.INTEREST_ALL);
        intrest.setLabel(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_For_You"));
        return intrest;
    }

    private UserBase.Intrest createVideo() {
        UserBase.Intrest intrest = new UserBase.Intrest();
        intrest.setShowIcon(true);
        intrest.setSelected(false);
        intrest.setIid(BrowseConstant.INTEREST_VIDEO);
        intrest.setLabel(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_status_video"));
        return intrest;
    }

}

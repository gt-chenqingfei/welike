package com.redefine.welike.business.feeds.ui.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.viewholder.DiscoverHeaderViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.InterestCategoryItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class DiscoverInterestCategoryAdapter extends BaseRecyclerAdapter<InterestCategoryItemViewHolder> {

    private List<UserBase.Intrest> mInterestCategories = new ArrayList<>();
    private onClickInterestListener mListener;

    public interface onClickInterestListener {
        void onclickInterestItem(UserBase.Intrest intrest);
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
                    intrest.setSelected(true);
                    changeUI(intrest);
                    mListener.onclickInterestItem(intrest);
                   /* Bundle bundle = new Bundle();
                    List<UserBase.Intrest> copy = new ArrayList<>(mInterestCategories);
                    bundle.putSerializable(FeedConstant.KEY_INTEREST_DATA, (Serializable) copy);
                    bundle.putInt(FeedConstant.KEY_INTEREST_POSITION, position);
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_INTEREST_CATEGORY_PAGE, bundle));*/
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mInterestCategories.size();
    }

    private void changeUI(UserBase.Intrest intrest) {
        for (UserBase.Intrest item : mInterestCategories) {
            if (null != item && !TextUtils.isEmpty(item.getIid())){

                if (item.getIid().equals(intrest.getIid())) {
                    item.setSelected(true);
                } else {
                    item.setSelected(false);
                }
            }
        }
        notifyDataSetChanged();

    }

    public void addInterestCategroy(List<UserBase.Intrest> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        mInterestCategories.clear();
        UserBase.Intrest intrest=new UserBase.Intrest();
        intrest.setShowIcon(list.get(0).isShowIcon());
        intrest.setSelected(true);
        intrest.setIid(DiscoverHeaderViewHolder.INTEREST_ALL);
        intrest.setLabel(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_For_You"));
        for (UserBase.Intrest item:list){
            if (item.isSelected()){
                intrest.setSelected(false);
                break;
            }
        }
        mInterestCategories.add(intrest);
        mInterestCategories.addAll(list);
        notifyDataSetChanged();
    }

}

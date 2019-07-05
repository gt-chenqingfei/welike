package com.redefine.welike.business.user.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.user.management.bean.EmptyBean;
import com.redefine.welike.business.user.management.bean.Interest;
import com.redefine.welike.business.user.management.bean.InterestRequestBean;
import com.redefine.welike.business.user.ui.viewholder.InterestEmptyViewHolder;
import com.redefine.welike.business.user.ui.viewholder.InterestFirstViewHolder;
import com.redefine.welike.business.user.ui.viewholder.InterestFlowViewHolder;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by honglin on 2018/6/29.
 */

public class UserInterestAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int EMPTY = -1;

    private ArrayList items = new ArrayList();

    private Context context;

    private OnRecyclerViewItemClickListener mListener;

    public UserInterestAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList items) {
        if (CollectionUtil.isEmpty(items)) return;
        this.items.clear();
        for (int i = 0; i < items.size(); i++) {
            Interest interest = (Interest) items.get(i);
            this.items.add(interest);
//            ArrayList<Interest> subset = interest.getSubset();
//            if (subset != null && subset.size() > 0) {
//                InterestRequestBean interestRequestBean = new InterestRequestBean(new ArrayList<Interest>());
//                interestRequestBean.getList().addAll(subset);
//                this.items.add(interestRequestBean);
//                interest.setExpose(true);
//            }
        }
//        this.items = items;
        this.items.add(new EmptyBean());
        notifyDataSetChanged();

    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick();
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public void addSecondItem(int position) {

        if (items.get(position) instanceof Interest) {
            InterestRequestBean interestRequestBean = new InterestRequestBean(new ArrayList<Interest>());
            interestRequestBean.getList().addAll(((Interest) items.get(position)).getSubset());
            items.add(position + 1, interestRequestBean);
            notifyItemInserted(position + 1);
            notifyItemRangeChanged(0, items.size());
        }
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == FIRST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_interest_layout, null);
            return new InterestFirstViewHolder(view);
        } else if (viewType == SECOND) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest_flow, null);
            return new InterestFlowViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest_empty, null);
            return new InterestEmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder holder, final int position) {

        if (getItemViewType(position) == FIRST) {
            final Interest interest = (Interest) items.get(position);
            final InterestFirstViewHolder firstViewHolder = (InterestFirstViewHolder) holder;
            firstViewHolder.tvTitle.setText(interest.getName());


            setInterestTextView(interest, firstViewHolder.tvTitle);
            //点击事件
            firstViewHolder.tvTitle.setTag(interest);

            firstViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Interest intrest = (Interest) v.getTag();
                    intrest.setSelected(!intrest.isSelected());
                    setInterestTextView(intrest, (TextView) v);
                    if (mListener != null) mListener.onItemClick();
                }
            });


//            showIcon(firstViewHolder.simpleInterestsPic, interest.getIcon(), firstViewHolder.background);
//            showIsSelect(firstViewHolder.ivSelect, interest);
//            firstViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    interest.setSelected(!interest.isSelected());
//                    showIsSelect(firstViewHolder.ivSelect, interest);
//                    if (mListener != null) mListener.onItemClick();
//                    if (interest.getSubset() != null && interest.getSubset().size() > 0 && !interest.isExpose()) {
//                        addSecondItem(position);
//                        interest.setExpose(true);
//                    } else {
//                        interest.setSelected(!interest.isSelected());
//                        if (interest.isExpose()) {
//                            for (Interest interest1 : interest.getSubset()) {
//                                interest1.setSelected(interest.isSelected());
//                                notifyItemChanged(position + 1);
//                            }
//                        }
//                        showIsSelect(firstViewHolder.ivSelect, interest);
//                    }
//                }
//            });
//        } else if (getItemViewType(position) == SECOND) {
//            final InterestRequestBean interestRequestBean = (InterestRequestBean) items.get(position);
//            InterestFlowViewHolder flowViewHolder = (InterestFlowViewHolder) holder;
//            flowViewHolder.flInterest.removeAllViews();
//            View subView;
//            for (Interest interest : interestRequestBean.getList()) {
//
//                subView = LayoutInflater.from(flowViewHolder.flInterest.getContext()).inflate(
//                        R.layout.item_interest_common1, flowViewHolder.flInterest, false);
//                final InterestFirstViewHolder firstViewHolder = new InterestFirstViewHolder(subView);
//                firstViewHolder.tvTitle.setText(interest.getName());
//                firstViewHolder.simpleInterestsPic.setImageResource(R.drawable.app_interest_item_bg);
//                firstViewHolder.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.color_3B6393));
//                firstViewHolder.tvTitle.setTag(interest);
//                showIsSelect(firstViewHolder.ivSelect, interest);
//                firstViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Interest interest = (Interest) v.getTag();
//                        interest.setSelected(!interest.isSelected());
//                        showIsSelect(firstViewHolder.ivSelect, interest);
//                        if (mListener != null) mListener.onItemClick();
//                    }
//                });
//                flowViewHolder.flInterest.addView(subView);
//            }
        }
    }


    //设置兴趣点状态
    private void setInterestTextView(Interest intrest, TextView tv) {
        if (intrest.isSelected()) {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.white));
            tv.setBackgroundResource(R.drawable.common_interest_checked);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        } else {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.main_orange_dark));
            tv.setBackgroundResource(R.drawable.common_interest_uncheck);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    private void showIsSelect(ImageView checkBox, Interest interest) {


        if (interest.isSelected()) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

    }

    private void showIcon(SimpleDraweeView draweeView, String url, final AppCompatImageView bg) {
        TempTool.Companion.loadSinglePicUri(draweeView, Uri.parse(TextUtils.isEmpty(url) ? "" : url), new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer color) {
                bg.setColorFilter(color);
                return null;
            }
        });
    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (items.get(position) instanceof Interest) {
            return FIRST;
        } else if (items.get(position) instanceof InterestRequestBean)
            return SECOND;
        else return EMPTY;
    }


    public List<UserBase.Intrest> getSelectInterest() {
        if (items == null) return new ArrayList<>();
        return getSelectInterest(items);
    }

    public List<UserBase.Intrest> getSelectInterest(ArrayList items) {

        List<UserBase.Intrest> list = new ArrayList<>();
        UserBase.Intrest intrest2;
        for (Object obj : items) {
            if (obj instanceof Interest) {
                Interest interest = (Interest) obj;
                if (interest.isSelected()) {
                    intrest2 = new UserBase.Intrest();
                    intrest2.setLabel(interest.getName());
                    intrest2.setIid(interest.getId());
                    intrest2.setIcon(interest.getIcon());
                    list.add(intrest2);
                }

                if (interest.getSubset() != null && interest.getSubset().size() > 0) {
                    list.addAll(getSelectInterest(interest.getSubset()));
                }
            }
        }

        return list;
    }


}

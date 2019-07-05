package com.redefine.welike.business.user.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.DeactivateReasonBean;
import com.redefine.welike.business.user.ui.listener.IReasonItemClickListener;
import com.redefine.welike.business.user.ui.viewholder.ReasonViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honglin on 2018/5/16.
 */

public class ReasonAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {


    private List<DeactivateReasonBean> dataList;
    private IReasonItemClickListener clickListener;

    public void setClickListener(IReasonItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDataList(List<DeactivateReasonBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deactivate_reason_recycler_item, parent, false);

        return new ReasonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder holder, final int position) {

        final DeactivateReasonBean bean = dataList.get(position);

        ReasonViewHolder reasonViewHolder = (ReasonViewHolder) holder;
        reasonViewHolder.rlItem.setTag(bean);

        showCbStatus(bean, reasonViewHolder.rlItem);
        reasonViewHolder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeactivateReasonBean bean = (DeactivateReasonBean) v.getTag();

                bean.setChecked(!bean.isChecked());

                showCbStatus(bean, v);
                if (clickListener != null&&bean.isChecked()) {
                    clickListener.onItemClick(position, bean.getUrl());
                }
            }
        });


        reasonViewHolder.tvTitle.setText(bean.getReason());

        HeadUrlLoader.getInstance().loadHeaderUrl2(reasonViewHolder.simplePic, bean.getIcon());

    }

    private void showCbStatus(DeactivateReasonBean bean, View view) {

        if (bean == null) return;

        if (bean.isChecked()) {
            ((ImageView) view.findViewById(R.id.cb_reason_check)).setImageResource(R.drawable.regist_recommond_user_check);
        } else
            ((ImageView) view.findViewById(R.id.cb_reason_check)).setImageResource(R.drawable.regist_recommond_check);

        boolean check = false;

        for (DeactivateReasonBean deactivateReasonBean : dataList) {

            if (deactivateReasonBean.isChecked()) {
                check = true;

                break;
            }
        }

        if (clickListener != null) clickListener.onItemChecked(check);

    }


    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public ArrayList<Integer> getCheckedList() {

        ArrayList<Integer> ls = new ArrayList<>();
        for (DeactivateReasonBean deactivateReasonBean : dataList) {

            if (deactivateReasonBean.isChecked()) {
                ls.add(deactivateReasonBean.getId());
            }
        }

        return ls;
    }


}

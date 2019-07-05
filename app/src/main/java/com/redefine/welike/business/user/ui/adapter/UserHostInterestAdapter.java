package com.redefine.welike.business.user.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

/**
 * Created by gongguan on 2018/1/18.
 */

public class UserHostInterestAdapter extends TagAdapter {
    private List<String> data;

    public UserHostInterestAdapter(List datas) {
        super(datas);

        this.data = datas;
    }

    @Override
    public View getView(FlowLayout parent, int position, Object o) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_host_list_head_interest_recycler_item, null);
        TextView deletView = itemView.findViewById(R.id.tv_user_host_interestType_del);
        TextView textView = itemView.findViewById(R.id.tv_user_host_interestType);
        textView.setText(data.get(position));
        return itemView;
    }

}

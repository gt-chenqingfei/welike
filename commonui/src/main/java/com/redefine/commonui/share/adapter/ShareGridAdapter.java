package com.redefine.commonui.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.share.CommonListener;
import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.SharePackageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/3/21.
 */

public class ShareGridAdapter extends BaseAdapter {
    private List<SharePackageModel> packagesList = new ArrayList<>();
    private CommonListener<SharePackageFactory.SharePackage> listener;

    public ShareGridAdapter(Context context, List<SharePackageModel> data, CommonListener<SharePackageFactory.SharePackage> listener) {
        this.listener = listener;
        packagesList.addAll(data);
    }

    @Override
    public int getCount() {
        return packagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_layout, null);
        }

        final SharePackageModel sharePackageModel = packagesList.get(position);
        TextView tv = convertView.findViewById(R.id.gv_tv);
        ImageView imageView = convertView.findViewById(R.id.gv_iv);
        imageView.setImageResource(sharePackageModel.getPackageIconResId());
        tv.setText(sharePackageModel.getPackageName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFinish(sharePackageModel.getSharePackage());
            }
        });

        return convertView;
    }
}

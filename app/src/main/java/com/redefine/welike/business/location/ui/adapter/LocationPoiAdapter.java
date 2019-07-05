package com.redefine.welike.business.location.ui.adapter;

import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.loadmore.viewholder.WhiteBgLoadMoreViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.location.management.bean.PoiInfo;
import com.redefine.welike.business.location.ui.viewholder.LocationPoiViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/26.
 */

public class LocationPoiAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, PoiInfo> {

    private final List<PoiInfo> mInfos = new ArrayList<>();

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LocationPoiViewHolder(mInflater.inflate(R.layout.location_poi_item, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, mInfos.get(position));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new WhiteBgLoadMoreViewHolder(mInflater.inflate(com.redefine.commonui.R.layout.load_more_layout, parent, false));
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }


    @Override
    public int getRealItemCount() {
        return mInfos.size();
    }

    @Override
    protected PoiInfo getRealItem(int position) {
        return mInfos.get(position);
    }

    public void addHisData(List<PoiInfo> poiInfoList) {
        if (!CollectionUtil.isEmpty(poiInfoList)) {
            mInfos.addAll(poiInfoList);
        }
        notifyDataSetChanged();
    }

    public void addNewData(List<PoiInfo> poiInfoList) {
        mInfos.clear();
        if (!CollectionUtil.isEmpty(poiInfoList)) {
            mInfos.addAll(0, poiInfoList);
        }
        notifyDataSetChanged();
    }
}

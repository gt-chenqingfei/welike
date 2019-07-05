package com.redefine.welike.business.location.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.location.management.bean.PoiInfo;

/**
 * Created by liwenbo on 2018/3/26.
 */

public class LocationPoiViewHolder extends BaseRecyclerViewHolder<PoiInfo> {

    private final TextView mLocationText;
    private final TextView mLocationNearByCount;
    private final String mHasUserText;
    private final String mNoUserText;

    public LocationPoiViewHolder(View itemView) {
        super(itemView);
        mLocationText = itemView.findViewById(R.id.location_poi_text);
        mLocationNearByCount = itemView.findViewById(R.id.location_poi_near_by_count);
        mHasUserText = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_has_user");
        mNoUserText = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_no_user");
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, PoiInfo data) {
        super.bindViews(adapter, data);
        mLocationText.setText(data.getLocation().getPlace());
        if (data.getUserCount() == 0) {
            mLocationNearByCount.setText(mNoUserText);
        } else {
            mLocationNearByCount.setText(String.format(mHasUserText, data.getUserCount()));
        }
    }
}

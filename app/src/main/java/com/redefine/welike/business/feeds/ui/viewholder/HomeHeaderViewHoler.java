package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.ui.bean.HomeHeaderBean;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by mengnan on 2018/5/9.
 **/
public class HomeHeaderViewHoler extends BaseRecyclerViewHolder<HomeHeaderBean> {
    private HomeBannerViewHolder mBannerViewHolder;
//    private HomeTaskViewHolder mTaskViewHolder;

    public HomeHeaderViewHoler(View itemView) {
        super(itemView);
        mBannerViewHolder = new HomeBannerViewHolder(itemView);
//        mTaskViewHolder = new HomeTaskViewHolder(itemView);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, HomeHeaderBean data) {
        FrameLayout container = (FrameLayout) itemView.findViewById(R.id.home_header_banner);
        container.removeAllViews();
//        final Mission newMission = data.getNewMission();
//        Mission currentMission = data.getCurrentMission();
        if (!CollectionUtil.isEmpty(data.getBanner())) { //显示banner
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.height = DensityUtil.dp2px(121);
            container.requestLayout();
            container.addView(mBannerViewHolder.getRootView());
            mBannerViewHolder.bindViews(data);
        } else {
            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
            layoutParams.height = DensityUtil.dp2px(1);
            container.requestLayout();
            container.addView(mBannerViewHolder.getRootView());
        }
//        } else if(currentMission == null) {//显示任务卡片
//            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
//            layoutParams.height = DensityUtil.dp2px(165);
//            container.requestLayout();
//            container.addView(mTaskViewHolder.getRootView());
//            mTaskViewHolder.bindViews(newMission, true);
//            mTaskViewHolder.bindViews(newMission, false);
//            data.setCurrentMission(newMission);
//        } else {//显示任务卡片，播放动画
//            ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
//            layoutParams.height = DensityUtil.dp2px(165);
//            container.addView(mTaskViewHolder.getRootView());
//            container.requestLayout();
//            if(!currentMission.getType().equals(newMission.getType())) {
//                mTaskViewHolder.animate(currentMission, newMission);
//                data.setCurrentMission(newMission);
//            }
//            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
//                @Override
//                public void run() {
//                    mTaskViewHolder.bindViews(newMission, true);
//                }
//            }, 500, TimeUnit.MILLISECONDS);
//        }
    }

//    public void setOnActionClickListener(HomeTaskViewHolder.OnTaskActionListener listener) {
//        mTaskViewHolder.setActionClickListener(listener);
//    }

    public void setCurrentActivityState(HomeHeaderBean data, boolean isResume) {
        mBannerViewHolder.setCurrentActivityState(data, isResume);
    }

    public void stopBanner() {
        mBannerViewHolder.stopBanner();
    }

}

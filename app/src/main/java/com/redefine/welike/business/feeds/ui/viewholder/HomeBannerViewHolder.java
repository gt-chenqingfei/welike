package com.redefine.welike.business.feeds.ui.viewholder;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.feeds.ui.bean.HomeHeaderBean;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.statistical.EventLog1;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

/**
 * Created by nianguowang on 2018/5/23
 */
public class HomeBannerViewHolder {

    private final com.youth.banner.Banner mBannerView;
    private final View mBannerLayout;
    private final View mRootView;

    private ArrayList<Banner> bannerMap = new ArrayList<>();

    public HomeBannerViewHolder(View parent) {
        mRootView = View.inflate(parent.getContext(), R.layout.home_header_banner, null);
        mBannerView = mRootView.findViewById(R.id.hot_banner_view);
        mBannerLayout = mRootView.findViewById(R.id.hot_banner_layout);
    }

    public void bindViews(HomeHeaderBean data) {
        if (CollectionUtil.isEmpty(data.getBanner())) {
            mBannerLayout.setVisibility(View.GONE);
        } else {
            mBannerLayout.setVisibility(View.VISIBLE);
            bannerMap.clear();

            ArrayList<String> bannerUrls = new ArrayList<>();
            for (Banner banner : data.getBanner()) {
                bannerUrls.add(banner.getPhoto());
                bannerMap.add(banner);
            }

            mBannerView.setImages(bannerUrls)
                    .setImageLoader(new MyImageLoader())
                    .setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            final Banner bean = bannerMap.get(position);
                            if (!TextUtils.isEmpty(bean.getAction())) {
                                new DefaultUrlRedirectHandler(mBannerView.getContext(), DefaultUrlRedirectHandler.FROM_BANNER).onUrlRedirect(bean.getAction());
                            }
                            EventLog1.Banner.report2(EventLog1.Banner.PageName.HOME, bean.getId(), bean.getLanguage());
                        }
                    })
                    .start();


        }
    }

    public View getRootView() {
        return mRootView;
    }

    public void setCurrentActivityState(HomeHeaderBean data, boolean isResume) {
        if (!CollectionUtil.isEmpty(data.getBanner())) {
            if (isResume) {
                mBannerView.startAutoPlay();
            } else {
                mBannerView.stopAutoPlay();
            }
        }
    }

    public void stopBanner() {
        if (mBannerView != null) {
            mBannerView.stopAutoPlay();
        }
    }


    public class MyImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            if (path != null) {
                Uri uri = Uri.parse((String) path);
                imageView.setImageURI(uri);
            }
        }

        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
            return simpleDraweeView;
        }
    }
}

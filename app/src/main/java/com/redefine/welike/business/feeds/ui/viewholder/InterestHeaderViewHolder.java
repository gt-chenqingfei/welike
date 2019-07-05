package com.redefine.welike.business.feeds.ui.viewholder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/4/16
 */
public class InterestHeaderViewHolder extends BaseRecyclerViewHolder<List<Banner>> {

    private final com.youth.banner.Banner mBannerView;
    private final View mBannerLayout;
    private List<Banner> mBannerList = new ArrayList<>();

    public InterestHeaderViewHolder(View itemView) {
        super(itemView);

        mBannerView = itemView.findViewById(R.id.hot_banner_view);
        mBannerLayout = itemView.findViewById(R.id.hot_banner_layout);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, List<Banner> data) {
        if(CollectionUtil.isEmpty(data)) {
            itemView.setVisibility(View.GONE);
        } else {
            itemView.setVisibility(View.VISIBLE);

            mBannerList.clear();
            ArrayList<String> bannerUrls = new ArrayList<>();
            for (Banner banner : data) {
                bannerUrls.add(banner.getPhoto());
                mBannerList.add(banner);
            }

            mBannerView.setImages(bannerUrls)
                    .setImageLoader(new MyImageLoader())
                    .setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            final Banner bean = mBannerList.get(position);
                            if (!TextUtils.isEmpty(bean.getAction())) {
                                new DefaultUrlRedirectHandler(mBannerView.getContext(), DefaultUrlRedirectHandler.FROM_BANNER).onUrlRedirect(bean.getAction());
                            }
                        }
                    })
                    .start();
        }
    }

    public void setCurrentActivityState(boolean resumed) {
        if(resumed) {
            mBannerView.startAutoPlay();
        } else {
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

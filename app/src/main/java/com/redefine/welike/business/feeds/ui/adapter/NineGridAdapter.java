package com.redefine.welike.business.feeds.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.NineGridUrlLoader;
import com.redefine.commonui.fresco.loader.SinglePicCrop;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.picturelooker.loader.SinglePicUrlLoader;
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.PicInfo;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.business.feeds.ui.anko.FeedImageGridItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedLinkItemViewUI;
import com.redefine.welike.commonui.view.PicBaseAdapter;

import org.jetbrains.anko.AnkoContext;

/**
 * Created by liwb on 2018/1/7.
 */

public class NineGridAdapter extends PicBaseAdapter<PicInfo> {

    private boolean isForwardFeed;
    private LayoutInflater mInflater;

    @Override
    protected void loadNineGridView(int count, SimpleDraweeView view, PicInfo picInfo) {
        if (count == 2 || count == 4) {
            NineGridUrlLoader.getInstance().loadNineGridUrl(view, picInfo.getThumbUrl(), 2);
        } else {
            NineGridUrlLoader.getInstance().loadNineGridUrl(view, picInfo.getThumbUrl());
        }
    }

    public void setForwardFeed(boolean isForwardFeed) {
        this.isForwardFeed = isForwardFeed;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        SimpleDraweeView simpleDraweeView;

        TextView gifTag;
        if (convertView == null) {
            convertView = new FeedImageGridItemViewUI().createView(AnkoContext.Companion.create(parent.getContext(), parent, false));
//                    mInflater.inflate(R.layout.feed_nine_grid_item, parent, false);
        }
        simpleDraweeView = convertView.findViewById(R.id.feed_nine_grid_item);
        gifTag = convertView.findViewById(R.id.gif_tag);
        SubsamplingScaleImageView scaleImageView = convertView.findViewById(R.id.longImg);
        if (MimeType.isGif(mUrls.get(position).getThumbUrl()) || MimeType.isWebP(mUrls.get(position).getThumbUrl())) {
            gifTag.setVisibility(View.VISIBLE);
        } else {
            gifTag.setVisibility(View.INVISIBLE);
        }
        scaleImageView.setTouchEnabled(false);
        initSimpleDraweeView(simpleDraweeView, position);
        PicInfo picInfo = mUrls.get(position);
        scaleImageView.setVisibility(View.GONE);
        convertView.findViewById(R.id.tv_view_full).setVisibility(View.GONE);
        if (getCount() == 1) {

            switch (ABTest.INSTANCE.check(ABKeys.TEST_IMAGE_DISPLAY)) {
                case 1:
                    SinglePicCrop.PicType type = SinglePicUrlLoader.getInstance().loadSinglePicUrlTest(simpleDraweeView, scaleImageView, picInfo.getWidth(), picInfo.getHeight(), picInfo.getThumbUrl());
                    if (type == SinglePicCrop.PicType.LONG_VER && gifTag.getVisibility() != View.VISIBLE) {
                        convertView.findViewById(R.id.tv_view_full).setVisibility(View.VISIBLE);
                    }
                    break;
                case 0:
                default:
                    SinglePicUrlLoader.getInstance().loadSinglePicUrl(simpleDraweeView, scaleImageView, picInfo.getWidth(), picInfo.getHeight(), picInfo.getThumbUrl());
            }


        } else {
            loadNineGridView(getCount(), simpleDraweeView, picInfo);
        }
        return convertView;
    }

    protected SimpleDraweeView initSimpleDraweeView(SimpleDraweeView simpleDraweeView, int position) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(simpleDraweeView.getContext().getResources());
        RoundingParams roundingParams = formatForwardRoundParams(position);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setFailureImage(R.drawable.feed_nine_grid_img_error)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER)
                .setPlaceholderImage(isForwardFeed ? R.drawable.feed_nine_grid_img_default_gray : R.drawable.feed_nine_grid_img_default)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER)
                .setRoundingParams(roundingParams)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setBackgroundResource(isForwardFeed ? R.color.common_setting_bg : R.color.feed_nine_grid_img_bg_color);
        return simpleDraweeView;
    }


    /**
     * 更新图片底部的圆角
     */
    private RoundingParams formatForwardRoundParams(int position) {
        RoundingParams roundingParams = RoundingParams.fromCornersRadii(0, 0, 0, 0);
        if (!isForwardFeed) {
            return roundingParams;
        }

        switch (mUrls.size()) {
            case 1:
                bottomAll(roundingParams);
                break;
            case 2:
                switch (position) {
                    case 0:
                        bottomLeft(roundingParams);
                        break;
                    case 1:
                        bottomRight(roundingParams);
                        break;
                }
                break;
            case 3:
                switch (position) {
                    case 0:
                        bottomLeft(roundingParams);
                        break;
                    case 2:
                        bottomRight(roundingParams);
                        break;
                }
                break;
            case 4:
                switch (position) {
                    case 2:
                        bottomLeft(roundingParams);
                        break;
                    case 3:
                        bottomRight(roundingParams);
                        break;
                }
                break;
            case 5:
                switch (position) {
                    case 3:
                        bottomLeft(roundingParams);
                        break;
                }
                break;
            case 6:
                switch (position) {
                    case 3:
                        bottomLeft(roundingParams);
                        break;
                    case 5:
                        bottomRight(roundingParams);
                        break;
                }
                break;
            case 7:
            case 8:
                switch (position) {
                    case 6:
                        bottomLeft(roundingParams);
                        break;
                }
                break;
            case 9:
                switch (position) {
                    case 6:
                        bottomLeft(roundingParams);
                        break;
                    case 8:
                        bottomRight(roundingParams);
                        break;
                }
                break;
        }
        return roundingParams;
    }

    private void bottomLeft(RoundingParams roundingParams) {
        roundingParams.setCornersRadii(0, 0,
                0,
                ScreenUtils.dip2Px(4));
    }

    private void bottomRight(RoundingParams roundingParams) {
        roundingParams.setCornersRadii(0, 0,
                ScreenUtils.dip2Px(4), 0);
    }

    private void bottomAll(RoundingParams roundingParams) {
        roundingParams.setCornersRadii(0, 0,
                ScreenUtils.dip2Px(4),
                ScreenUtils.dip2Px(4));
    }

    @Override
    public int getItemWidth(int position) {
        return mUrls.get(position).getWidth();
    }

    @Override
    public int getItemHeight(int position) {
        return mUrls.get(position).getHeight();
    }
}

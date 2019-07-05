package com.redefine.multimedia.picturelooker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.redefine.commonui.photoview.PhotoDraweeView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.picturelooker.config.PictureMimeType;
import com.redefine.multimedia.picturelooker.listener.OnLargeImageLoadListener;
import com.redefine.multimedia.picturelooker.listener.OnCallBackActivity;
import com.redefine.multimedia.picturelooker.listener.OnImageLongClickListener;
import com.redefine.multimedia.picturelooker.loader.LargePicUrlLoader;
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView;
import com.redefine.multimedia.player.VideoPlayerActivity;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.multimedia.recorder.constant.VideoRecorderConstant;
import com.redefine.welike.base.ErrorCode;

import java.util.List;

/**
 * zhl 2018/6/2
 */

public class SimpleFragmentAdapter extends PagerAdapter {
    private List<Item> images;
    private Context mContext;
    private OnCallBackActivity onBackPressed;
    private OnImageLongClickListener longClickListener;


    public SimpleFragmentAdapter(List<Item> images, Context context, OnCallBackActivity onBackPressed) {
        super();
        this.images = images;
        this.mContext = context;
        this.onBackPressed = onBackPressed;
    }

    public void setLongClickListener(OnImageLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public int getCount() {
        if (images != null) {
            return images.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View contentView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.picture_image_preview, container, false);
        // 常规图控件
        final PhotoDraweeView imageView = contentView.findViewById(R.id.preview_image);
        // 长图控件
        final SubsamplingScaleImageView longImg = contentView.findViewById(R.id.longImg);

        final LoadingView loadImg = contentView.findViewById(R.id.preview_load);
        final ImageView errorView = contentView.findViewById(R.id.preview_error);

        ImageView iv_play = contentView.findViewById(R.id.iv_play);
        final Item media = images.get(position);
        if (media != null) {
            iv_play.setVisibility(media.isVideo() ? View.VISIBLE : View.GONE);
            showImageView(imageView, longImg, loadImg, errorView, media, media.mimeType);
            imageView.setOnViewTapListener(new com.redefine.commonui.photoview.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (onBackPressed != null) {
                        onBackPressed.onActivityBackPressed();
                    }
                }
            });
            longImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBackPressed != null) {
                        onBackPressed.onActivityBackPressed();
                    }

                }
            });
            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_PATH, media.filePath);
                    bundle.putString(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, PlayerConstant.VIDEO_SITE_DEFAULT);
                    intent.putExtras(bundle);
                    intent.setClass(mContext, VideoPlayerActivity.class);
                    ((Activity) mContext).overridePendingTransition(com.redefine.commonui.R.anim.sliding_right_in, com.redefine.commonui.R.anim.sliding_to_left_out);
                    mContext.startActivity(intent);
                }
            });

            if (this.longClickListener != null && media.isImage()) {
                longImg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onLongClick(position, media);
                        return true;
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onLongClick(position, media);
                        return true;
                    }
                });
            }
        }
        (container).addView(contentView);
        return contentView;
    }

    private void showImageView(PhotoDraweeView imageView, SubsamplingScaleImageView longImg, final LoadingView loadImg, final ImageView errorView, Item media, String pictureType) {
        boolean isHttp = PictureMimeType.isHttp(media.filePath);
        LargePicUrlLoader.getInstance().loadBigImage(longImg, imageView, media, isHttp, new OnLargeImageLoadListener() {
            @Override
            public void onLargeImageLoad(int errCode) {
                if (errCode == ErrorCode.ERROR_SUCCESS) {
                    loadImg.setVisibility(View.GONE);
                } else {
                    loadImg.setVisibility(View.GONE);
                    errorView.setImageResource(R.drawable.pic_preview_error);
                }
            }
        });
    }


}

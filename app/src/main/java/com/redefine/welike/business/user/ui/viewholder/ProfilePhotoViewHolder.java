package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.NineGridUrlLoader;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView;
import com.redefine.welike.R;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.business.videoplayer.management.bean.ImageAttachment;

/**
 * Created by nianguowang on 2018/10/10
 */
public class ProfilePhotoViewHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView simpleDraweeView;
    public TextView gifTag;

    public ProfilePhotoViewHolder(View itemView) {
        super(itemView);
        simpleDraweeView = itemView.findViewById(R.id.feed_nine_grid_item);
        gifTag = itemView.findViewById(R.id.gif_tag);
    }

    public void bindView(AttachmentBase attachmentBase) {
        if (attachmentBase == null || !(attachmentBase instanceof ImageAttachment)) {
            return;
        }
        ImageAttachment attachment = (ImageAttachment) attachmentBase;
        String imageUrl = attachment.getImageUrl();
        if (MimeType.isGif(imageUrl)) {
            gifTag.setVisibility(View.VISIBLE);
            gifTag.setText(R.string.gif_tag_text);
        } else if (MimeType.isWebP(imageUrl)) {
            gifTag.setVisibility(View.VISIBLE);
            gifTag.setText(R.string.gif_tag_text);
        } else {
            gifTag.setVisibility(View.INVISIBLE);
        }
        initSimpleDraweeView(simpleDraweeView);
        NineGridUrlLoader.getInstance().loadNineGridUrl(simpleDraweeView, imageUrl, 3);
    }

    protected SimpleDraweeView initSimpleDraweeView(SimpleDraweeView simpleDraweeView) {
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(simpleDraweeView.getContext().getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setFailureImage(R.drawable.feed_nine_grid_img_error)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER)
                .setPlaceholderImage(R.drawable.feed_nine_grid_img_default)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setBackgroundResource(R.color.feed_nine_grid_img_bg_color);

        return simpleDraweeView;
    }
}
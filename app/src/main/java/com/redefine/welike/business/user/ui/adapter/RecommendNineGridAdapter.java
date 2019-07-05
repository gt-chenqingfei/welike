package com.redefine.welike.business.user.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.NineGridUrlLoader;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.multimedia.picturelooker.loader.SinglePicUrlLoader;
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.PicInfo;
import com.redefine.welike.business.publisher.api.bean.AttachmentCategory;
import com.redefine.welike.business.user.management.bean.AttachmentBean;
import com.redefine.welike.commonui.view.PicBaseAdapter;

/**
 */

public class RecommendNineGridAdapter extends PicBaseAdapter<AttachmentBean> {

    private LayoutInflater mInflater;


    @Override
    protected void loadNineGridView(int count, SimpleDraweeView view, AttachmentBean attachmentBean) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        SimpleDraweeView simpleDraweeView;

        TextView gifTag;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recommed_nine_grid_item, parent, false);
        }
        simpleDraweeView = convertView.findViewById(R.id.feed_nine_grid_item);
        gifTag = convertView.findViewById(R.id.gif_tag);

        AttachmentBean attachmentBean = mUrls.get(position);

        String imageUrl;

        if (TextUtils.equals(attachmentBean.getType(), "VIDEO")) {
            imageUrl = attachmentBean.getIcon();
        } else imageUrl = attachmentBean.getSource();
        if (MimeType.isGif(imageUrl)) {
            gifTag.setVisibility(View.VISIBLE);
            gifTag.setText(R.string.gif_tag_text);
        } else if (MimeType.isWebP(imageUrl)) {
            gifTag.setVisibility(View.VISIBLE);
            gifTag.setText(R.string.gif_tag_text);
        } else {
            gifTag.setVisibility(View.INVISIBLE);
        }

        NineGridUrlLoader.getInstance().loadNineGridUrl(simpleDraweeView, imageUrl);
        return convertView;
    }


    @Override
    public int getItemWidth(int position) {
        return 100;
    }

    @Override
    public int getItemHeight(int position) {
        return 100;
    }
}

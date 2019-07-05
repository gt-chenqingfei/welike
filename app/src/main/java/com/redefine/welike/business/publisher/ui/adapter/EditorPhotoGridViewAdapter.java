package com.redefine.welike.business.publisher.ui.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.NineGridUrlLoader;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.welike.R;
import com.redefine.welike.commonui.view.PicBaseAdapter;
import com.redefine.welike.factory.SimpleDraweeViewFactory;

/**
 * Created by MR on 2018/1/17.
 */

public class EditorPhotoGridViewAdapter extends PicBaseAdapter<Item> {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_ADD = 1;
    public static final int MAX_TYPE_COUNT = 2;

    public EditorPhotoGridViewAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        if (position == super.getCount()) {
            return TYPE_ADD;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_TYPE_COUNT;
    }

    @Override
    public Object getItem(int position) {
        if (position == super.getCount()) {
            return null;
        }
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount() == 0 ? 0 : super.getCount() + 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_ADD) {
            ImageView view;
            if (convertView instanceof ImageView) {
                view = (ImageView) convertView;
            } else {
                view = new ImageView(parent.getContext());
                view.setScaleType(ImageView.ScaleType.CENTER);
            }
            view.setBackgroundResource(R.color.editor_photo_add_btn_bg_color);
            view.setImageResource(R.drawable.editor_photo_add_btn);
            return view;
        } else {
            ViewHolder viewHolder;
            if (convertView instanceof FrameLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                FrameLayout rootView = new FrameLayout(parent.getContext());
                SimpleDraweeView simpleDraweeView = SimpleDraweeViewFactory.newInstance(parent.getContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                rootView.addView(simpleDraweeView);
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setImageResource(R.drawable.editor_delete_icon);
                int padding = ScreenUtils.dip2Px(5);
                imageView.setPadding(padding, padding, padding, padding);
                FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);
                deleteParams.gravity = Gravity.TOP | Gravity.RIGHT;
                rootView.addView(imageView, deleteParams);
                viewHolder = new ViewHolder();
                viewHolder.rootView = rootView;
                viewHolder.closeBtn = imageView;
                viewHolder.simpleDraweeView = simpleDraweeView;
                convertView = rootView;
                convertView.setTag(viewHolder);
            }
            loadNineGridView(getCount(), viewHolder.simpleDraweeView, mUrls.get(position));
            viewHolder.closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUrls.remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

    }

    @Override
    protected void loadNineGridView(int count, SimpleDraweeView view, Item item) {
        if (item.filePath.contains("http://") || item.filePath.contains("https://")) {
            NineGridUrlLoader.getInstance().loadNineGridUrl(view, item.filePath);
            return;
        }
        NineGridUrlLoader.getInstance().loadNineGridFile(view, item.filePath);
    }

    public void clear() {
        if (mUrls != null) {
            mUrls.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemWidth(int position) {
        return 0;
    }

    @Override
    public int getItemHeight(int position) {
        return 0;
    }

    public static class ViewHolder {
        public SimpleDraweeView simpleDraweeView;
        public ImageView closeBtn;
        public FrameLayout rootView;
    }
}

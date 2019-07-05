package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.AlbumUrlLoader;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.UserDetailManager;
import com.redefine.welike.business.user.ui.constant.UserConstant;

/**
 * Created by gongguan on 2018/1/11.
 */

public class UserHostAlbumViewHolder extends BaseRecyclerViewHolder<UserDetailManager.UserAlbumPic> {
    private SimpleDraweeView iv_album;

    public UserHostAlbumViewHolder(int spanCount, View itemView) {
        super(itemView);
        int width = ScreenUtils.getSreenWidth(itemView.getContext());
        int realAllWidth = width - (spanCount + 1) * ScreenUtils.dip2Px(itemView.getContext(), UserConstant.ALBUM_SPAN_PADDING);
        int realSize = realAllWidth  / spanCount;
        iv_album = itemView.findViewById(R.id.simpleView_user_host_album);
        iv_album.setLayoutParams(new ViewGroup.LayoutParams(realSize, realSize));
        iv_album.setAspectRatio(1);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, UserDetailManager.UserAlbumPic data) {
        AlbumUrlLoader.getInstance().loadAlbumUrl(iv_album, data.getThumbnail());
    }

}

package com.redefine.welike.business.feeds.ui.util;

import android.content.Context;
import android.os.Bundle;

import com.redefine.foundation.framework.Event;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.util.CollectionUtil;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.feeds.management.bean.PicInfo;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.commonui.photoselector.PhotoSelector;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/11.
 */

public class PostRichItemClickHandler extends DefaultRichItemClickHandler {

    private final PostBase mPostBase;

    public PostRichItemClickHandler(Context context, PostBase postBase) {
        super(context);
        mPostBase = postBase;
    }

    @Override
    public void onRichItemClick(RichItem richItem) {
        super.onRichItemClick(richItem);
        if (richItem.isMoreItem()) {
            Bundle bundle = new Bundle();
            bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.ERROR_INDEX);
            bundle.putSerializable(FeedConstant.KEY_FEED, mPostBase);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
        } else if (richItem.isArticleItem()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(FeedConstant.KEY_FEED, mPostBase);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_POST_ARTICLE_PAGE, bundle));
        } else if (richItem.isInnerImageItem()) {
            if (mPostBase.getArticleInfo() == null) {
                return ;
            }
            List<RichItem> richItems = mPostBase.getArticleInfo().getRichItemList();
            if (CollectionUtil.isEmpty(richItems)) {
                return ;
            }
            int position = 0;
            List<RichItem> result = new ArrayList<>();
            for (int i = 0; i < richItems.size(); i++) {
                RichItem item = richItems.get(i);
                if (item.isInnerImageItem()) {
                    result.add(item);
                }
                if (item.index == richItem.index) {
                    position = i;
                }
            }
            if (CollectionUtil.isEmpty(result)) {
                return ;
            }
            List<PicInfo> list = new ArrayList<>();
            for (RichItem item : result) {
                PicInfo picInfo = new PicInfo();
                picInfo.setThumbUrl(item.source);
                picInfo.setPicUrl(item.source);
                picInfo.setWidth(item.width);
                picInfo.setHeight(item.height);
                list.add(picInfo);
            }
            String nickName = "";
            if (mPostBase.getArticleInfo().getUser() != null) {
                nickName = mPostBase.getArticleInfo().getUser().getNickName();
            }
            PhotoSelector.previewPics(mContext, nickName, position, list);
        }
    }
}

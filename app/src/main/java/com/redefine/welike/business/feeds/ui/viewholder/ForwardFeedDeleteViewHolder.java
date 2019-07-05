package com.redefine.welike.business.feeds.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;

/**
 * Created by liwenbo on 2018/3/5.
 */

public class ForwardFeedDeleteViewHolder extends TextFeedViewHolder {
    private final TextView mErrorText;

    public ForwardFeedDeleteViewHolder(View view, IFeedOperationListener listener) {
        super(view, listener);
        mErrorText = view.findViewById(R.id.forward_feed_delete_content);
        mErrorText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "forward_feed_delete_content"));
    }
}

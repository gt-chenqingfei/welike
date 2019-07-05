package com.redefine.welike.business.im.ui.holder;

import android.view.View;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;

/**
 * Created by liwenbo on 2018/4/18.
 */

public class BaseSessionItemViewHolder<T> extends BaseRecyclerViewHolder<T> {
    public View mSessionContentView;

    public BaseSessionItemViewHolder(View itemView) {
        super(itemView);
        mSessionContentView = itemView.findViewById(R.id.im_session_content_view);
    }
}

package com.redefine.welike.business.message.ui.viewholder

import android.view.View
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder
import com.redefine.welike.kext.translate
import kotlinx.android.synthetic.main.msg_header_layout.view.*

class HeaderViewHolder(view: View, news: Boolean) : BaseRecyclerViewHolder<String>(view) {
    init {
        view.textView.text = if (news) "message_box_new".translate() else "earlier".translate()
    }
}
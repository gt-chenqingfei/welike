package com.redefine.welike.business.publisher.ui.viewholder

import android.view.View
import kotlinx.android.synthetic.main.contacts_list_header.view.*

class SearchContactViewHolder(itemView: View) : ContactListViewHolder(itemView) {
    val text = itemView.header_title!!
    val empty = itemView.empty!!
}
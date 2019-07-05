package com.redefine.welike.business.user.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.welike.R
import com.redefine.welike.business.contact.bean.Friend
import com.redefine.welike.business.user.management.bean.RecommendUser1
import com.redefine.welike.common.VipUtil
import com.redefine.welike.commonui.widget.VipAvatar
import kotlinx.android.synthetic.main.item_avator_layout.view.*

/**
 * @author redefine honlin
 * @Date on 2018/12/20
 * @Description
 */

class BottomFollowUserAdapter : RecyclerView.Adapter<UserViewHolder>() {


    var users = ArrayList<RecommendUser1>()

    fun setData(nusers: ArrayList<RecommendUser1>) {
        users = nusers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_avator_layout, null))

    }

    override fun getItemCount(): Int {
        return if (users.size > 3) 3 else users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        VipUtil.set(holder.avatar, users.get(position).avatar, users.get(position).vip)

    }
}


class UserViewHolder : RecyclerView.ViewHolder {

    var avatar: VipAvatar

    constructor(itemView: View) : super(itemView) {

        avatar = itemView.simpleView_user_0

    }

}

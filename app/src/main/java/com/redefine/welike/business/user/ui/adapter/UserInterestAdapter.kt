package com.redefine.welike.business.user.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redefine.commonui.fresco.loader.HeadUrlLoader
import com.redefine.welike.R
import com.redefine.welike.base.profile.bean.UserBase
import com.redefine.welike.business.user.management.bean.Interest
import kotlinx.android.synthetic.main.register_interest_item.view.*


class UserInterestAdapter2(val context: Context, val itemCheck: () -> Unit) : RecyclerView.Adapter<UserInterestViewHolder>() {

    private val items = ArrayList<Interest>()
    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    fun setData(newData: ArrayList<Interest>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserInterestViewHolder(inflater.inflate(R.layout.register_interest_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserInterestViewHolder, position: Int) {
        items[position].let {
            HeadUrlLoader.getInstance().loadHeaderUrl2(holder.image, it.icon)
            holder.title.text = it.name
            holder.check.isSelected = it.isSelected
            holder.itemView.setOnClickListener {
                items[position].let {
                    if (it.isSelected) {
                        it.isSelected = false
                        holder.check.isSelected = false
                    } else {
                        it.isSelected = true
                        holder.check.isSelected = true
                    }
                    itemCheck.invoke()
                }
            }
        }
    }

    fun getSelectInterest(): List<UserBase.Intrest> {
        return items.filter { it.isSelected }.map {
            UserBase.Intrest().apply {
                label = it.name
                iid = it.id
                icon = it.icon
            }
        }
    }
}


class UserInterestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image = view.interest_image!!
    val title = view.interest_title!!
    val check = view.interest_check!!
}
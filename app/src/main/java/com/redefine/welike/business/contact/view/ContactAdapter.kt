package com.redefine.welike.business.contact.view

import android.annotation.SuppressLint
import android.support.v7.util.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter
import com.redefine.commonui.loadmore.bean.CommonTextHeadBean
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder
import com.redefine.commonui.share.SharePackageFactory
import com.redefine.commonui.share.sharemedel.ShareModel
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.browse.management.bean.FollowUser
import com.redefine.welike.business.browse.management.dao.BrowseEventStore
import com.redefine.welike.business.browse.management.dao.FollowUserCallBack
import com.redefine.welike.business.contact.bean.Friend
import com.redefine.welike.business.contact.getState
import com.redefine.welike.common.VipUtil
import com.redefine.welike.commonui.event.expose.base.IDataProvider
import com.redefine.welike.commonui.event.model.EventModel
import com.redefine.welike.commonui.share.ShareManagerWrapper
import com.redefine.welike.commonui.widget.IFollowBtn
import com.redefine.welike.commonui.widget.UserFollowBtn
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog1
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.contact_header_layout.view.*
import kotlinx.android.synthetic.main.contact_invite_header_layout.view.*
import kotlinx.android.synthetic.main.layout_contact_item.view.*

class ContactAdapter(val data: ArrayList<Friend> = ArrayList()) : LoadMoreFooterRecyclerAdapter<CommonTextHeadBean, Friend>(), IDataProvider<List<Friend>> {

    lateinit var followAll: () -> Unit
    lateinit var invite: (Friend) -> Unit
    lateinit var onFollow: (Friend, doFollow: Boolean) -> Unit
    lateinit var onClickProfile: (Friend) -> Unit
    var isAuth = AccountManager.getInstance().isLoginComplete
    lateinit var onClickFaceToFace: () -> Unit

    fun setData(news: List<Friend>) {
        if (data.isEmpty()) {
            data.addAll(news)
            notifyDataSetChanged()
        } else {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = data.size
                override fun getNewListSize(): Int = news.size
                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = news[newItemPosition] == data[oldItemPosition]
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = data[oldItemPosition].id == (news[newItemPosition].id)
            }).apply {
                dispatchUpdatesTo(this@ContactAdapter)
            }
            data.clear()
            data.addAll(news)
        }
    }

    override fun getData(): List<Friend> {
        return data
    }

    override fun getSource(): String {
        return EventConstants.FEED_PAGE_CONTACT
    }

    override fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): BaseRecyclerViewHolder<*> {
        return when (viewType) {
            1 -> ContactHeaderViewHolder(mInflater.inflate(R.layout.contact_header_layout, parent, false))
            2 -> ContactHeaderViewHolder(mInflater.inflate(R.layout.contact_header_layout, parent, false))
            3 -> ContactFaceToFaceHeaderViewHolder(mInflater.inflate(R.layout.contact_face_to_face_header_layout, parent, false))
            4 -> ContactInviteHeaderViewHolder(mInflater.inflate(R.layout.contact_invite_header_layout, parent, false))
            else -> ContactViewHolder(mInflater.inflate(R.layout.layout_contact_item, parent, false))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindItemViewHolder(holder: BaseRecyclerViewHolder<*>?, position: Int) {
        if (holder is ContactViewHolder) { //set item
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 0
            if (position + 1 <= data.size - 1) {
                if (data[position + 1] is FriendHeader) {
                    holder.itemView.setBackgroundResource(R.drawable.common_b_t_shadow)
                } else {
                    holder.itemView.setBackgroundResource(R.color.white)
                }
            }


            val friend = data[position]
            holder.name.text = friend.nickName ?: friend.contactName
            holder.avatar.avatar.hierarchy.setPlaceholderImage(R.drawable.ic_default_contact)
            VipUtil.set(holder.avatar, friend.avatarUrl, friend.vip)
            if (friend.registed) {
                holder.description.text = (friend.contactName
                        ?: "") + "in_your_contacts".translate()
            } else {
                holder.description.text = "${"contact_invite".translate()} ${friend.contactName
                        ?: ""}"
            }
            //set button  "follow" or "invite"
            holder.follow.visibility = if (friend.registed) View.VISIBLE else View.GONE
            if (!isAuth) {
                BrowseEventStore.INSTANCE.getFollowUser(friend.id!!, object : FollowUserCallBack {
                    override fun onLoadEntity(user: FollowUser) {

                        AndroidSchedulers.mainThread().scheduleDirect {
                            holder.follow.visibility = View.GONE
                        }
                    }
                })
                holder.follow.visibility = if (friend.follow || !friend.registed) View.GONE else View.VISIBLE
            }
            holder.invite.visibility = if (friend.registed) View.GONE else View.VISIBLE
            holder.invite.text = "contact_invite".translate()
            //set listeners
            holder.invite.setOnClickListener { invite.invoke(friend) }
            if (friend.registed) {
                setFollowButton(holder.follow, friend, onFollow)
            }
            holder.name.setOnClickListener { onClickProfile.invoke(friend) }
            holder.description.setOnClickListener { onClickProfile.invoke(friend) }
            holder.avatar.setOnClickListener { onClickProfile.invoke(friend) }


        } else if (holder is ContactHeaderViewHolder) {//set header.
            if (position == 0) {
                (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = ScreenUtils.dip2Px(8f)
            } else {
                (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                        holder.itemView.resources.getDimension(R.dimen.common_feed_card_bottom).toInt()
            }

            val friend = data[position]
            if (friend is FriendHeader) {
                if (friend.register) {//set header 1
                    holder.title.text = if (friend.count > 1) {
                        "${friend.count} " + "contacts".translate()
                    } else {
                        "${friend.count} " + "contact".translate()
                    }
                    holder.description.text = "contact_header1".translate()
                    if (isAuth)
                        holder.followAll.visibility = View.VISIBLE
                    else
                        holder.followAll.visibility = View.GONE
                    holder.followAll.text = "contact_follow_all".translate()
                    holder.followAll.setOnClickListener { followAll?.invoke() }
                } else {//set header 2
                    holder.title.text = "contact_invite_friends".translate()
                    holder.description.text = "contact_header2".translate()
                    holder.followAll.visibility = View.GONE
                }
            }
        } else if (holder is ContactFaceToFaceHeaderViewHolder) {
            (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = ScreenUtils.dip2Px(8f)
            holder.itemView.setBackgroundResource(R.drawable.common_b_t_shadow)
            holder.itemView.setOnClickListener {
                onClickFaceToFace()
            }
        } else if (holder is ContactInviteHeaderViewHolder) {
            if (position == 0) {
                (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = ScreenUtils.dip2Px(8f)
            } else {
                (holder.itemView.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                        holder.itemView.resources.getDimension(R.dimen.common_feed_card_bottom).toInt()
            }
            holder.itemView.setBackgroundResource(R.drawable.common_b_t_shadow)

            holder.add_friend_whatsapp.setOnClickListener {
                val shareModel = ShareModel()
                shareModel.shareModelType = ShareModel.SHARE_MODEL_TYPE_FILE
                val build = ShareManagerWrapper.Builder()
                        .with(holder.add_friend_whatsapp.context)
                        .shareModel(shareModel)
                        .sharePackage(SharePackageFactory.SharePackage.WHATS_APP)
                        .eventModel(EventModel(EventLog1.Share.ContentType.APK, null, null, EventLog1.Share.ShareFrom.OTHER, EventLog1.Share.PopPage.OTHER
                                ,null, null, null, null, null, null, null, null, null, null, null, null ))
                        .build()
                build.shareApk()
            }

        }
    }

    override fun getRealItemViewType(position: Int): Int {
        data[position].let {
            return if (it is FriendHeader) {
                if (it.register) 1 else 2
            } else if (it is FriendFaceToFaceHeader) {
                3
            } else if (it is InviteFriendHeader) {
                4
            } else {
                0
            }
        }
    }

    override fun getRealItemCount() = data.size

    override fun getRealItem(position: Int) = data[position]

    private fun setFollowButton(view: UserFollowBtn, friend: Friend, event: (Friend, doFollow: Boolean) -> Unit) {
        if (friend.id == null) {
            view.visibility = View.GONE
            return
        }
        view.setUnFollowEnable(true)
        view.setFollowStatus(view.getState(friend.id!!, friend.follow, friend.followed))
        view.setOnClickFollowBtnListener {
            view.followStatus?.let {
                when (it) {
                    IFollowBtn.FollowStatus.FOLLOW -> {
                        view.setFollowStatus(IFollowBtn.FollowStatus.FOLLOWING_LOADING)
                        event.invoke(friend, true)
                    }
                    IFollowBtn.FollowStatus.FRIEND -> {
                        view.setFollowStatus(IFollowBtn.FollowStatus.UN_FOLLOWING_LOADING)
                        event.invoke(friend, false)
                    }
                    IFollowBtn.FollowStatus.FOLLOWING -> {
                        view.setFollowStatus(IFollowBtn.FollowStatus.UN_FOLLOWING_LOADING)
                        event.invoke(friend, false)
                    }
                    else -> {
                    }
                }
            }
        }
    }
}

class ContactViewHolder(view: View) : BaseRecyclerViewHolder<Friend>(view) {
    val name = view.contact_name!!
    val description = view.contact_desc!!
    val avatar = view.contact_avatar!!
    val follow = view.follow!!
    val invite = view.invite!!
}

class ContactHeaderViewHolder(view: View) : BaseRecyclerViewHolder<Friend>(view) {
    val title = view.header_title!!
    val description = view.header_description!!
    val followAll = view.follow_all!!
}

class ContactFaceToFaceHeaderViewHolder(view: View) : BaseRecyclerViewHolder<Friend>(view) {

}

class ContactInviteHeaderViewHolder(view: View) : BaseRecyclerViewHolder<Friend>(view) {
    val add_friend_whatsapp = view.add_friend_whatsapp
}
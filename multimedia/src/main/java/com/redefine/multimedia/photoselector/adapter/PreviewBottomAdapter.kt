package com.redefine.multimedia.photoselector.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import com.redefine.commonui.fresco.loader.HeadUrlLoader
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.multimedia.R
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.multimedia.photoselector.model.SelectedItemCollection

class PreviewBottomAdapter(var mSelectedCollection: SelectedItemCollection, var mItems: ArrayList<Item>, var onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<PreviewBottomAdapter.PreViewBottomViewHolder>() {

    var selectPosition = 0
        set(value) {
            notifyItemChanged(selectPosition)
            field = value
            notifyItemChanged(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreViewBottomViewHolder {
        return PreViewBottomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_preview_bottom, parent, false))
    }

    override fun getItemCount(): Int {
        return if (mItems != null) mItems!!.size else 0
    }

    override fun onBindViewHolder(holder: PreViewBottomViewHolder, position: Int) {
        HeadUrlLoader.getInstance().loadHeaderFile(holder.mLocalImage, mItems!![position].filePath)
        val checkedNum = mSelectedCollection.checkedNumOf(mItems[position])
        holder.mHolderView.visibility = when (checkedNum) {
            -1 -> View.VISIBLE
            else -> View.GONE
        }
        holder.mRootView.isSelected = selectPosition == position
        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(position, mItems[position], holder.itemView) }
        if (position == 0) {
            (holder.mRootView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = ScreenUtils.dip2Px(12F)
        } else {
            (holder.mRootView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = ScreenUtils.dip2Px(4F)
        }
    }

    class PreViewBottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mLocalImage: SimpleDraweeView = itemView.findViewById(R.id.item_preview_image)
        var mHolderView: View = itemView.findViewById(R.id.item_preview_image_holder)
        var mRootView: View = itemView.findViewById(R.id.item_preview_root_view)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: Item, itemView: View)
    }

}
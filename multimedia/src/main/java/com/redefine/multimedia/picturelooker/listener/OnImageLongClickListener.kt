package com.redefine.multimedia.picturelooker.listener

import com.redefine.multimedia.photoselector.entity.Item

/**
 * Created by honglin on 2018/6/4.
 */
interface OnImageLongClickListener {
    fun onLongClick(position: Int, obj: Item)
}
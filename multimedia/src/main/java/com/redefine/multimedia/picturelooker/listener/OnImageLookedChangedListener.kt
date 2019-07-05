package com.redefine.multimedia.picturelooker.listener

import com.redefine.multimedia.photoselector.entity.Item

/**
 * Created by honglin on 2018/6/2.
 */
interface OnImageLookedChangedListener {

    fun onLookedChanged(pos: Int,item : Item)

}
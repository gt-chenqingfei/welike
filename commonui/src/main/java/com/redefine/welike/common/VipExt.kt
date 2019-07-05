package com.redefine.welike.common

import com.redefine.commonui.R

fun getVipResource(type: Int): Int {
    return when {
        type < 1 -> R.drawable.common_transparent
        type in 1000000..1999999 -> R.drawable.ic_vip2
        type in 2000000..2999999 -> R.drawable.ic_vip_red
        else -> R.drawable.ic_vip1
    }
}


fun getVipSlideResource(type: Int): Int {
    return when (type) {

        1 -> {
            R.drawable.ic_icon_profile_verified_1
        }
        2 -> {
            R.drawable.ic_icon_profile_verified_2
        }
        3 -> {
            R.drawable.ic_icon_profile_verified_3
        }

        else -> R.drawable.common_transparent

    }
}
package com.redefine.welike.business.user.management

import android.content.Context
import com.redefine.welike.R
import com.redefine.welike.business.user.management.bean.RecommendSlideUser

/**
 * Created by nianguowang on 2019/1/18
 */
class UserModelFactory {
    companion object {
        const val RECOMMEND_TYPE_NORMAL = 0
        const val RECOMMEND_TYPE_CONTACT = 1

        fun createRecommendContactUser(context: Context): RecommendSlideUser {
            return RecommendSlideUser(RECOMMEND_TYPE_CONTACT, "", context.getString(R.string.recommend_contact_title), "res:///" + R.drawable.img_recommend_contact,
                    context.getString(R.string.recommend_contact_desc), "", -1, -1, "", -1, -1, 0, false, false, false)
        }
    }
}
package com.redefine.welike.commonui.event.expose.impl

import com.redefine.welike.business.contact.bean.Friend
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.user.management.bean.RecommendSlideUser
import com.redefine.welike.business.user.management.bean.RecommendUser
import com.redefine.welike.business.user.management.bean.RecommendUser1
import com.redefine.welike.business.user.management.bean.User
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback

/**
 * Created by nianguowang on 2019/1/9
 */
class ItemExposeCallbackFactory {

    companion object {
        fun createPostViewCallback(): IItemVisibleCallback<List<PostBase>> {
            return PostViewCallbackImpl()
        }

        fun createPostExposeCallback(): IItemVisibleCallback<List<PostBase>> {
            return PostExposeCallbackImpl()
        }

        fun createPostFollowBtnCallback(): IItemVisibleCallback<List<PostBase>> {
            return PostFollowBtnExposeCallbackImpl()
        }

        fun createRecomUserFollowBtnCallback(): IItemVisibleCallback<List<RecommendSlideUser>> {
            return RecomUserFollowBtnCallbackImpl()
        }

        fun createFollowUserBtnCallback(): IItemVisibleCallback<List<User>> {
            return FollowUserBtnCallbackImpl()
        }

        fun createContactFollowBtnCallback(): IItemVisibleCallback<List<Friend>> {
            return ContactFollowBtnCallbackImpl()
        }

        fun createHorizatalUserCardCallback(): IItemVisibleCallback<List<RecommendSlideUser>> {
            return HorizontalUserCardCallbackImpl()
        }

        fun createFullscreenUserCallback(): IItemVisibleCallback<List<RecommendUser1>> {
            return FullscreenUserCallbackImpl()
        }
    }
}
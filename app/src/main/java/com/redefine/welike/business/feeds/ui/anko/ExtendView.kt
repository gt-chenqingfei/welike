package com.redefine.welike.business.feeds.ui.anko

import android.support.v7.widget.AppCompatImageView
import android.view.ViewManager
import android.widget.BaseAdapter
import com.airbnb.lottie.LottieAnimationView
import com.facebook.drawee.view.SimpleDraweeView
import com.redefine.commonui.widget.FlowLayout
import com.redefine.commonui.widget.loading.ProgressView
import com.redefine.multimedia.picturelooker.widget.longimage.SubsamplingScaleImageView
import com.redefine.richtext.RichTextView
import com.redefine.welike.business.feeds.ui.view.CommonFeedVoteHeader
import com.redefine.welike.commonui.view.*
import com.redefine.welike.commonui.widget.UserFollowBtn
import com.redefine.welike.commonui.widget.UserForwardFollowBtn
import com.redefine.welike.commonui.widget.VipAvatar
import org.jetbrains.anko.custom.ankoView

/**
 * @author redefine honlin
 * @Date on 2019/2/13
 * @Description
 */

//--------------simple drawee view----------/
fun ViewManager.simpleDraweeView(theme: Int = 0): SimpleDraweeView = simpleDraweeView(theme) {}

inline fun ViewManager.simpleDraweeView(theme: Int = 0, init: SimpleDraweeView.() -> Unit) = ankoView(::SimpleDraweeView, theme) { init() }

//--------------- AppCompatImageView  -----------------/

fun ViewManager.appCompatImageView(theme: Int = 0): AppCompatImageView = appCompatImageView(theme) {}

inline fun ViewManager.appCompatImageView(theme: Int = 0, init: AppCompatImageView.() -> Unit) = ankoView(::AppCompatImageView, theme) { init() }

//---------------  vip avatar  -----------------/

inline fun ViewManager.vipAvatar(theme: Int = 0, init: VipAvatar.() -> Unit) = ankoView(::VipAvatar, theme) { init() }

//---------------  userFollowBtn  -----------------/

inline fun ViewManager.userFollowBtn(theme: Int = 0, init: UserFollowBtn.() -> Unit) = ankoView(::UserFollowBtn, theme) { init() }

inline fun ViewManager.userForwardFollowBtn(theme: Int = 0, init: UserForwardFollowBtn.() -> Unit) = ankoView(::UserForwardFollowBtn, theme) { init() }

//---------------  RichTextView  -----------------/

inline fun ViewManager.richTextView(theme: Int = 0, init: RichTextView.() -> Unit) = ankoView(::RichTextView, theme) { init() }


//------------------  video feed content  --------------------//

inline fun ViewManager.feedListVideoController(theme: Int = 0, init: FeedListVideoController.() -> Unit) = ankoView(::FeedListVideoController, theme) { init() }

//---------------  FeedPicMultiGridView  -----------------/
inline fun ViewManager.feedPicMultiGridView(theme: Int = 0, init: FeedPicMultiGridView<PicBaseAdapter<Any>>.() -> Unit): FeedPicMultiGridView<PicBaseAdapter<Any>> {
    return ankoView({ FeedPicMultiGridView(it) }, theme, init)
}

//---------------  CommonFeedVoteHeader  -----------------/
fun ViewManager.commonFeedVoteHeader(theme: Int = 0): CommonFeedVoteHeader = commonFeedVoteHeader(theme) {}

inline fun ViewManager.commonFeedVoteHeader(theme: Int = 0, init: CommonFeedVoteHeader.() -> Unit) = ankoView(::CommonFeedVoteHeader, theme) { init() }


//---------------  MultiGridView  -----------------/
inline fun ViewManager.multiGridView(theme: Int = 0, init: MultiGridView<BaseAdapter>.() -> Unit): MultiGridView<BaseAdapter> {
    return ankoView({ MultiGridView(it) }, theme, init)
}

//---------------  MultiTextVoteView  -----------------/
inline fun ViewManager.multiTextVoteView(theme: Int = 0, init: MultiTextVoteView<BaseAdapter>.() -> Unit): MultiTextVoteView<BaseAdapter> {
    return ankoView({ MultiTextVoteView(it) }, theme, init)
}

//---------------  subSamplingScaleImageView  -----------------/
inline fun ViewManager.subSamplingScaleImageView(theme: Int = 0, init: SubsamplingScaleImageView.() -> Unit) = ankoView(::SubsamplingScaleImageView, theme) { init() }

//---------------  flowLayout  -----------------/
inline fun ViewManager.flowLayout(theme: Int = 0, init: FlowLayout.() -> Unit) = ankoView(::FlowLayout, theme) { init() }


//---------------  lottieAnimationView  -----------------/
inline fun ViewManager.lottieAnimationView(theme: Int = 0, init: LottieAnimationView.() -> Unit) = ankoView(::LottieAnimationView, theme) { init() }

//---------------  lottieAnimationView  -----------------/
inline fun ViewManager.progressView(theme: Int = 0, init: ProgressView.() -> Unit) = ankoView(::ProgressView, theme) { init() }



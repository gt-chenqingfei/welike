package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.RoundingParams
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * @author redefine honlin
 * @Date on 2019/2/20
 * @Description
 */


//---------------  forward head  -----------------/

fun ViewManager.FeedForwardHeaderUI(): View = relativeLayout {
    leftPadding = dip(12)
    rightPadding = dip(12)


    textView {

        id = R.id.forward_feed_nick_name
        textColor = this.resources.getColor(R.color.feed_flag_top_text_color)
        textSizeDimen = R.dimen.common_text_size_14
        ellipsize = TextUtils.TruncateAt.END
        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        visibility = View.GONE
        maxLines = 1
        bottomPadding = dip(8)
        topPadding = dip(12)


    }.lparams(wrapContent, height = wrapContent) {
        leftMargin = dip(4)
    }


    richTextView {
        id = R.id.forward_feed_text_content
        textSizeDimen = R.dimen.common_text_size_14
        textColor = this.resources.getColor(R.color.forward_text_feed_content_text_color)
        topPadding = dip(6)
        rightPadding = dip(12)

    }.lparams(wrapContent, height = wrapContent) {
        addRule(RelativeLayout.RIGHT_OF, R.id.forward_feed_nick_name)
    }

    userForwardFollowBtn {
        id = R.id.forward_follow_btn
        visibility = View.GONE
        backgroundColor = this.resources.getColor(R.color.color_f6)
    }.lparams(width = dip(26), height = dip(26)) {

        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        topMargin = dip(10)
    }
}

// link content


fun ViewManager.FeedForwardLinkUI(): View = linearLayout {


    id = R.id.forward_feed_root_view


    orientation = LinearLayout.VERTICAL

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    FeedForwardHeaderUI()

    FeedLinkContainer().lparams(matchParent, wrapContent) {

        topMargin = dip(7)
        bottomMargin = dip(1)
        rightMargin = dip(1)
        leftMargin = dip(1)

    }

}


//text
fun ViewManager.FeedForwardTextUI(): View = linearLayout {


    id = R.id.forward_feed_root_view


    orientation = LinearLayout.VERTICAL

    bottomPadding = dip(6)

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    FeedForwardHeaderUI()

}


//pic
fun ViewManager.FeedForwardPicUI(): View = linearLayout {

    id = R.id.forward_feed_root_view

    orientation = LinearLayout.VERTICAL

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    FeedForwardHeaderUI()


    feedPicMultiGridView {
        id = R.id.forward_pic_feed_multi_grid_view

    }.lparams(matchParent, wrapContent) {
        topMargin = dip(6)
    }
}

//video
fun ViewManager.FeedForwardVideoUI(): View = linearLayout {

    id = R.id.forward_feed_root_view

    orientation = LinearLayout.VERTICAL

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    FeedForwardHeaderUI()

    VideoFeedContainerUI().lparams(matchParent, dip(186)) {
        topMargin = dip(6)
    }
}


// pic vote

fun ViewManager.FeedForwardPicPollUI(): View = linearLayout {

    id = R.id.forward_feed_root_view

    orientation = LinearLayout.VERTICAL

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    bottomPadding = dip(6)

    FeedForwardHeaderUI()

    multiGridView {
        id = R.id.vote_pic_feed_multi_grid_view
        setChildMargin(dip(4))
        setmRowChildCount(2)
        setmMaxRowCount(2)
        topPadding = dip(6)
    }.lparams(matchParent, wrapContent)



    feedBottomCheckbox().lparams(matchParent, wrapContent) {
    }

    textView {
        id = R.id.tv_right_text
        gravity = Gravity.END
        textSizeDimen = R.dimen.common_text_size_14
        textColor = resources.getColor(R.color.main_grey)
        topPadding = dip(4)
        leftPadding = dip(12)
        rightPadding = dip(12)
    }.lparams(matchParent, wrapContent) {
    }
}


// text poll

//text
fun ViewManager.feedForwardTextPollUI(): View = linearLayout {


    id = R.id.forward_feed_root_view


    orientation = LinearLayout.VERTICAL

    bottomPadding = dip(6)

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    FeedForwardHeaderUI()


    multiTextVoteView {
        id = R.id.vote_text_multi_view
        orientation = LinearLayout.VERTICAL
    }.lparams(matchParent, wrapContent) {
        leftMargin = dip(12)
        rightMargin = dip(12)
        topMargin = dip(8)
    }


    feedBottomCheckbox().lparams(matchParent, wrapContent)

    textView {
        id = R.id.tv_right_text
        gravity = Gravity.END
        textSizeDimen = R.dimen.common_text_size_14
        textColor = resources.getColor(R.color.main_grey)
        topPadding = dip(4)
        leftPadding = dip(12)
        rightPadding = dip(12)
    }.lparams(matchParent, wrapContent) {
    }

}


//art
fun ViewManager.feedForwardArtUI(): View = linearLayout {


    id = R.id.forward_feed_root_view


    orientation = LinearLayout.VERTICAL

    bottomPadding = dip(6)

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    FeedForwardHeaderUI()

    //art
    constraintLayout {

        id = R.id.art_card_contain

        simpleDraweeView {
            id = R.id.art_bg
        }.lparams(0, 0) {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            dimensionRatio = "2:1"
        }.apply {
            setFadingEdgeLength(300)
            hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.CENTER)
            hierarchy.setPlaceholderImage(R.drawable.art_card_default)
        }


        view {

            id = R.id.bottom_gradient_view

            backgroundColor= resources.getColor(R.color.transparent40)

        }.lparams(0, 0) {

            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }

        constraintLayout {


            simpleDraweeView {
                id = R.id.art_card_head_image
            }.lparams(dip(24),dip(24)) {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                leftMargin = dip(12)
                topMargin = dip(12)
            }.apply {
                setFadingEdgeLength(300)
                hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.CENTER_CROP)
                val roundingParams = RoundingParams()
                roundingParams.roundAsCircle = true
                hierarchy.roundingParams = roundingParams
                hierarchy.setPlaceholderImage(R.drawable.user_default_head)
            }

            textView {

                id = R.id.ard_card_name
                textColor = resources.getColor(R.color.white)
                textSizeDimen = R.dimen.common_text_size_12
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                bottomPadding = dip(3)
                setShadowLayer(3f, 0f, 3f, R.color.color_99_dark)

            }.lparams(wrapContent, wrapContent) {
                leftMargin = dip(4)
                bottomToBottom = R.id.art_card_head_image
                startToEnd = R.id.art_card_head_image
                topToTop = R.id.art_card_head_image
            }

            textView {

                id = R.id.art_card_title_flag
                textColor = resources.getColor(R.color.white)
                textSizeDimen = R.dimen.common_text_size_12
                bottomPadding = dip(3)
                setShadowLayer(3f, 0f, 3f, R.color.color_99_dark)

            }.lparams(wrapContent, wrapContent) {
                rightMargin = dip(12)

                bottomToBottom = R.id.art_card_head_image
                topToTop = R.id.art_card_head_image
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }


            appCompatImageView {

                setImageDrawable(resources.getDrawable(R.drawable.ic_art_card_flag))

            }.lparams(wrapContent, wrapContent) {
                rightMargin = dip(12)

                bottomToBottom = R.id.art_card_head_image
                topToTop = R.id.art_card_head_image
                endToStart = R.id.art_card_title_flag
            }


        }.lparams(0, dip(72)) {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }

        textView {
            id = R.id.art_title
            textSizeDimen = R.dimen.common_text_size_16
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 2
            textColor = resources.getColor(R.color.white)
            bottomPadding = dip(3)
            setShadowLayer(3f, 0f, 3f, R.color.color_99_dark)

        }.lparams(0, dip(72)) {
            rightMargin = dip(12)
            leftMargin = dip(12)
            bottomMargin = dip(14)
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }


    }.lparams(matchParent, wrapContent) {
        topMargin = dip(6)
    }

}


//delete
fun ViewManager.feedForwardDeleteUI(): View = linearLayout {


    id = R.id.forward_feed_root_view


    orientation = LinearLayout.HORIZONTAL

    background = this.resources.getDrawable(R.drawable.drawable_shape_forward_bg)

    imageView {
        id = R.id.forward_feed_delete_img

        scaleType = ImageView.ScaleType.CENTER

        setImageResource(R.drawable.forward_card_delete_img)

        backgroundColor = resources.getColor(R.color.forward_feed_delete_img_bg)

    }.lparams(dip(75),dip(75))


    richTextView {
        id = R.id.forward_feed_delete_content
        textSizeDimen = R.dimen.common_text_size_15
        textColor = this.resources.getColor(R.color.text_feed_content_text_color)
        ellipsize = TextUtils.TruncateAt.END
        gravity = Gravity.CENTER_VERTICAL

        topPadding = dip(10)
        rightPadding = dip(15)
        bottomPadding = dip(15)
        leftPadding = dip(10)

    }.lparams(0, matchParent) {
        weight = 1f
    }


}



package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.text.TextUtils
import android.view.ViewGroup
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

//-----------------text view holder-------------------///
class FeedArtItemViewUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        constraintLayout {


            layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent)
            background = resources.getDrawable(R.drawable.ripple_shape_white)


            FeedItemCommonHeadUI().lparams(width = dip(0), height = wrapContent) {
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topToBottom = R.id.header_card_layout
            }

            FeedItemContentUI().lparams(0, wrapContent) {
                topToBottom = R.id.common_feed_head_root_view
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
            }


            //art
            constraintLayout {

                id = R.id.art_card_contain

                simpleDraweeView {
                    id = R.id.art_bg
                }.lparams(0, 0) {
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                    dimensionRatio = "2:1"
                }.apply {
                    setFadingEdgeLength(300)
                    hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.CENTER)
                    hierarchy.setPlaceholderImage(R.drawable.art_card_default)
                }


                view {

                    id = R.id.bottom_gradient_view

                    backgroundColor = resources.getColor(R.color.transparent40)

                }.lparams(0, 0) {

                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                }

                constraintLayout {


                    simpleDraweeView {
                        id = R.id.art_card_head_image
                    }.lparams(dip(24),dip(24)) {
                        startToStart = PARENT_ID
                        topToTop = PARENT_ID
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
                        endToEnd = PARENT_ID
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
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
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
                    bottomToBottom = PARENT_ID
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                }


            }.lparams(0, wrapContent) {
                topToBottom = R.id.text_feed_content
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(6)
            }



            FeedItemADCardUI().lparams(0, wrapContent) {
                topToBottom = R.id.art_card_contain
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
            }
            FeedItemActiveCardUI().lparams(0, wrapContent) {
                topToBottom = R.id.ad_card_layout
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(6)
            }

            FeedItemTopicCardUI().lparams(0, wrapContent) {
                topToBottom = R.id.active_card_layout
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(6)
            }

            FeedCommonBottomUI().lparams(0, wrapContent) {
                topToBottom = R.id.topic_card_layout
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(6)
            }


            frameLayout {
                id = R.id.common_feed_bottom_root_shadow
                backgroundColor = resources.getColor(R.color.common_color_f8f8f8)
                foreground = resources.getDrawable(R.drawable.common_shadow_icon)

            }.lparams(0, dip(8)) {
                topToBottom = R.id.common_feed_bottom_root_view
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
            }
        }
    }
}

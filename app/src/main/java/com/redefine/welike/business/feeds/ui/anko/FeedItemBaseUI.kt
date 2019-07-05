package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import com.facebook.drawee.drawable.ScalingUtils
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * @author redefine honlin
 * @Date on 2019/2/20
 * @Description
 */

//---------------  ad head  -----------------/

fun ViewManager.FeedItemADHeadUI(): View = constraintLayout {
    id = R.id.header_card_layout
    leftPadding = dip(12)
    rightPadding = dip(12)

    simpleDraweeView {
        id = R.id.iv_common_feed_ad_icon

    }.lparams(width = dip(16), height = dip(16)) {
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        topMargin = dip(9)
        bottomMargin = dip(9)
    }.apply {
        hierarchy.fadeDuration = (300)
        hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.CENTER_CROP)
    }

    textView {
        id = R.id.tv_common_feed_ad_title
        textColor = this.resources.getColor(R.color.color_31)
        textSizeDimen = R.dimen.common_text_size_10
    }.lparams(width = dip(0), height = wrapContent) {
        //                gravity = Gravity.CENTER_HORIZONTAL
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        startToEnd = R.id.iv_common_feed_ad_icon
        leftMargin = dip(4)
    }

    view {
        backgroundColor = this.resources.getColor(R.color.color_f6)
    }.lparams(width = dip(0), height = dip(1)) {
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
    }
}


//---------------  common feed  head  -----------------/

fun ViewManager.FeedItemCommonHeadUI(): View = constraintLayout {

    id = R.id.common_feed_head_root_view

    appCompatImageView {

        visibility = View.GONE

        id = R.id.top_post_flag

        setImageDrawable(resources.getDrawable(R.drawable.ic_user_post_top))

    }.lparams(width = dip(30), height = dip(30))



    constraintLayout {

        id = R.id.common_trending_layout

        appCompatImageView {

            id = R.id.common_trending_image

            setImageDrawable(resources.getDrawable(R.drawable.ic_feed_post_trending_flag))

        }.lparams(width = dip(16), height = dip(16)) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        }


        textView {

            id = R.id.common_trending_value
            textColor = resources.getColor(R.color.color_31)
            textSizeDimen = R.dimen.common_text_size_10

        }.lparams(wrapContent, wrapContent) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            topToTop = ConstraintLayout.LayoutParams.PARENT_ID

            leftMargin = dip(8)

            startToEnd = R.id.common_trending_image

        }


        view {

            backgroundColor = resources.getColor(R.color.transparent_07)

        }.lparams(0, dip(1)) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            startToStart = ConstraintLayout.LayoutParams.PARENT_ID

            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }


    }.lparams(width = dip(0), height = dip(28)) {


        startToStart = ConstraintLayout.LayoutParams.PARENT_ID

        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        leftMargin = dip(12)

    }



    textView {

        id = R.id.tv_common_feed_operation

        textColor = resources.getColor(R.color.color_31)

        maxLines = 10

        visibility = View.GONE

    }.lparams(0, wrapContent) {

        topToBottom = R.id.common_trending_layout

        startToStart = ConstraintLayout.LayoutParams.PARENT_ID

        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

        leftMargin = dip(10)

        rightMargin = dip(10)
    }


    vipAvatar {

        id = R.id.common_feed_head

    }.lparams(dip(40), dip(40)) {

        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

        startToStart = ConstraintLayout.LayoutParams.PARENT_ID

        horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD

        bottomMargin = dip(8)

        topToBottom = R.id.tv_common_feed_operation

        bottomMargin = dip(8)

        leftMargin = dip(12)

        topMargin = dip(12)

    }.apply {

    }


    textView {

        id = R.id.common_feed_name

        maxLines = 1

        ellipsize = TextUtils.TruncateAt.END

        textColor = resources.getColor(R.color.common_feed_name_text_color)

        textSizeDimen = R.dimen.common_text_size_14

        typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        text = "11111111111"

    }.lparams(width = wrapContent, height = wrapContent) {

        constrainedWidth = true

        startToEnd = R.id.common_feed_head

        endToStart = R.id.common_feed_event_mark

        topToTop = R.id.common_feed_head

        bottomToTop = R.id.common_feed_time

        leftMargin = dip(8)

        topMargin = dip(2)
    }


    constraintLayout {

        id = R.id.header_flag

        visibility = View.GONE

        appCompatImageView {

            id = R.id.common_hot

            setImageDrawable(resources.getDrawable(R.drawable.ic_feed_post_trending_flag))

        }.lparams(width = dip(16), height = dip(16)) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        }


        textView {
            text = "222222222"
            id = R.id.common_hot_text
            textColor = resources.getColor(R.color.feed_flag_trending_text_color)
            textSizeDimen = R.dimen.common_text_size_10

        }.lparams(wrapContent, wrapContent) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            topToTop = ConstraintLayout.LayoutParams.PARENT_ID

            startToEnd = R.id.common_hot

            leftPadding = dip(2)

            rightPadding = dip(1)

        }


        appCompatImageView {

            id = R.id.common_top

            setImageDrawable(resources.getDrawable(R.drawable.ic_common_feed_top_flag))
            visibility = View.GONE
        }.lparams(width = dip(16), height = dip(16)) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        }


        textView {
            id = R.id.common_top_text
            textColor = resources.getColor(R.color.feed_flag_top_text_color)
            textSizeDimen = R.dimen.common_text_size_10
            visibility = View.GONE
        }.lparams(wrapContent, wrapContent) {

            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            topToTop = ConstraintLayout.LayoutParams.PARENT_ID

            startToEnd = R.id.common_top

            leftPadding = dip(2)

            rightPadding = dip(1)

        }

    }.lparams(dip(0), wrapContent) {

        startToStart = R.id.common_feed_name

        topToBottom = R.id.common_feed_name

        topMargin = dip(1)

    }

    textView {
        id = R.id.common_feed_time
        textColor = resources.getColor(R.color.common_feed_time_text_color)
        textSizeDimen = R.dimen.common_text_size_10
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        text = "11111111111"

    }.lparams(0, wrapContent) {
        startToEnd = R.id.header_flag
        endToStart = R.id.common_feed_follow_btn
        bottomToBottom = R.id.common_feed_head
        rightMargin = dip(8)
        bottomMargin = dip(6)

    }

    simpleDraweeView {

        id = R.id.common_feed_event_mark

        visibility = View.GONE

    }.lparams(width = dip(16), height = dip(16)) {
        bottomToBottom = R.id.common_feed_name
        topToTop = R.id.common_feed_name

        startToEnd = R.id.common_feed_name

        endToStart = R.id.common_feed_empty

        leftMargin = dip(2)

        margin = dip(1)

    }.apply {
        hierarchy.fadeDuration = (300)
        hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.FIT_CENTER)

    }

    view {
        id = R.id.common_feed_empty
    }.lparams(width = dip(0), height = dip(1)) {
        startToEnd = R.id.common_feed_event_mark

        endToStart = R.id.common_feed_follow_btn

        topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        matchConstraintMinWidth = dip(1)

    }

    userFollowBtn {

        id = R.id.common_feed_follow_btn

    }.lparams(dip(76), dip(29)) {

        topToTop = R.id.common_feed_head

        bottomToBottom = R.id.common_feed_head

        startToEnd = R.id.common_feed_empty

        endToStart = R.id.common_feed_read_count

    }


    textView {
        id = R.id.common_feed_read_count
        textColor = resources.getColor(R.color.color_859EBC)
        textSizeDimen = R.dimen.common_text_size_8
        text = "9826k views"
        visibility = View.GONE
        background = resources.getDrawable(R.drawable.shape_read_count_bg)
        leftPadding = dip(3)
        rightPadding = dip(3)
        topPadding = dip(3)
        bottomPadding = dip(3)
    }.lparams(wrapContent, wrapContent) {
        startToEnd = R.id.common_feed_follow_btn
        endToStart = R.id.common_feed_arrow_btn
        bottomToBottom = R.id.common_feed_head
        topToTop = R.id.common_feed_head
    }

    appCompatImageView {

        visibility = View.GONE

        id = R.id.common_feed_arrow_btn

        setImageDrawable(resources.getDrawable(R.drawable.ic_common_feed_post_header_more))

        leftPadding = dip(12)
        rightPadding = dip(12)
        topPadding = dip(5)
        bottomPadding = dip(5)

    }.lparams(wrapContent, wrapContent) {
        startToEnd = R.id.common_feed_read_count
        bottomToBottom = R.id.common_feed_head
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = R.id.common_feed_head
        constrainedWidth = true
    }
}


//---------------  text_feed_content  -----------------/

fun ViewManager.FeedItemContentUI(): View = richTextView {

    id = R.id.text_feed_content
    leftPadding = dip(12)
    rightPadding = dip(12)
    textColor = resources.getColor(R.color.color_31)
    textSizeDimen = R.dimen.common_text_size_14
    gravity = Gravity.LEFT
}

//---------------  ad_content  -----------------/

fun ViewManager.FeedItemADCardUI(): View = constraintLayout {

    id = R.id.ad_card_layout
    backgroundColor = resources.getColor(R.color.common_color_ee)

    simpleDraweeView {
        id = R.id.iv_ad_pic

    }.lparams(width = dip(0), height = dip(0)) {
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        dimensionRatio = "25:14"

    }.apply {
        hierarchy.fadeDuration = (300)
        hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.FIT_CENTER)
    }

    textView {
        id = R.id.tv_post_status
        textColor = this.resources.getColor(R.color.white)
        background = resources.getDrawable(R.drawable.app_common_btn_40_bark_bg)
        text = this.resources.getString(R.string.app_name)
        textSizeDimen = R.dimen.common_text_size_12
        leftPadding = dip(6)
        rightPadding = dip(6)
        topPadding = dip(3.5f)
        bottomPadding = dip(3.5f)
        visibility = View.GONE

    }.lparams(width = wrapContent, height = wrapContent) {
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        topMargin = dip(12)
        rightMargin = dip(12)

    }

    constraintLayout {
        leftPadding = dip(12)
        rightPadding = dip(8)
        topPadding = dip(12)
        bottomPadding = dip(12)


        textView {
            id = R.id.common_feed_active_title
            textColor = this.resources.getColor(R.color.color_31)
            textSizeDimen = R.dimen.common_text_size_14
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }.lparams(0, height = wrapContent) {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = R.id.tv_common_feed_active_join
            bottomToTop = R.id.common_feed_active_desc
            rightMargin = dip(12)
        }


        textView {
            id = R.id.common_feed_active_desc
            textColor = this.resources.getColor(R.color.main_grey)
            textSizeDimen = R.dimen.common_text_size_12
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }.lparams(0, height = wrapContent) {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToBottom = R.id.common_feed_active_title
            endToStart = R.id.tv_common_feed_active_join

            rightMargin = dip(12)
            topMargin = dip(8)
        }

        textView {
            id = R.id.tv_common_feed_active_join
            textColor = this.resources.getColor(R.color.color_859EBC)
            textSizeDimen = R.dimen.common_text_size_12
            text = resources.getString(R.string.common_join)
            leftPadding = dip(12)
            rightPadding = dip(12)
            topPadding = dip(4)
            bottomPadding = dip(4)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            background = resources.getDrawable(R.drawable.common_btn_blue_stroke_bg)
        }.lparams(wrapContent, height = wrapContent) {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }

    }.lparams(0, height = wrapContent) {
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        topToBottom = R.id.iv_ad_pic
    }
}


//---------------  active_card_layout  -----------------/

fun ViewManager.FeedItemActiveCardUI(): View = constraintLayout {

    id = R.id.active_card_layout
    leftPadding = dip(.5f)
    background = resources.getDrawable(R.drawable.drawable_shape_feed_active)

    simpleDraweeView {
        id = R.id.bg_lg_active_card

    }.lparams(width = dip(64), height = dip(63)) {
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        bottomMargin = dip(0.5f)
        topMargin = dip(0.5f)

    }

    constraintLayout {
        leftPadding = dip(8)
        rightPadding = dip(8)
        topPadding = dip(6)
        bottomPadding = dip(6)


        textView {
            id = R.id.common_feed_active_title
            textColor = this.resources.getColor(R.color.color_31)
            textSizeDimen = R.dimen.common_text_size_14
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }.lparams(0, height = wrapContent) {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = R.id.tv_common_feed_active_join
            bottomToTop = R.id.cl_common_feed_active_post
        }


        constraintLayout {
            id = R.id.cl_common_feed_active_post

            visibility = View.GONE

            appCompatImageView {
                id = R.id.nums_flag

                setImageDrawable(resources.getDrawable(R.drawable.ic_topic_card_posts))

            }.lparams(width = dip(16), height = dip(16)) {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }

            textView {

                id = R.id.active_nums_tv
                textColor = this.resources.getColor(R.color.color_62)
                textSizeDimen = R.dimen.common_text_size_12
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 1
                rightPadding = dip(12)

            }.lparams(0, height = wrapContent) {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToEnd = R.id.nums_flag

                leftMargin = dip(4)
            }

        }.lparams(0, 0) {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = R.id.tv_common_feed_active_join
            topToBottom = R.id.common_feed_active_title
        }

        textView {
            id = R.id.tv_common_feed_active_join
            textColor = this.resources.getColor(R.color.color_859EBC)
            textSizeDimen = R.dimen.common_text_size_12
            text = resources.getString(R.string.common_join)
            leftPadding = dip(12)
            rightPadding = dip(12)
            topPadding = dip(4)
            bottomPadding = dip(4)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            background = resources.getDrawable(R.drawable.common_btn_blue_stroke_bg)
        }.lparams(wrapContent, height = wrapContent) {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        }

    }.lparams(0, dip(64)) {
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        startToEnd = R.id.bg_lg_active_card
    }
}

//------------------topic card--------------------////


fun ViewManager.FeedItemTopicCardUI(): View = constraintLayout {

    id = R.id.topic_card_layout
    background = resources.getDrawable(R.drawable.topic_card_backgroud)

    appCompatImageView {
        id = R.id.bg_lg_topic_card
        setImageDrawable(resources.getDrawable(R.drawable.ic_super_topic))

    }.lparams(width = dip(64), height = dip(63)) {
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        bottomMargin = dip(0.5f)
        topMargin = dip(0.5f)

    }

    constraintLayout {

        linearLayout {

            id = R.id.card_info

            leftPadding = dip(8)
            rightPadding = dip(16)

            linearLayout {

                orientation = LinearLayout.HORIZONTAL

                appCompatImageView {

                    id = R.id.super_topic_flag

                    setImageDrawable(resources.getDrawable(R.drawable.ic_super_topic_flag))

                }.lparams(dip(20), dip(20)) {

                    bottomMargin = dip(1)
                    topMargin = dip(1)

                }


                textView {
                    id = R.id.topic_name_tv
                    textColor = this.resources.getColor(R.color.splash_bg_start_color)
                    textSizeDimen = R.dimen.common_text_size_14
                    text = resources.getString(R.string.common_join)
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER_VERTICAL
                    maxLines = 1
                    rightPadding = dip(12)
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)

                }.lparams(dip(280), matchParent) {
                    leftMargin = dip(2)
                }


            }.lparams(wrapContent, wrapContent) {
                topMargin = dip(5)
            }



            constraintLayout {

                id = R.id.post_info

                appCompatImageView {

                    id = R.id.nums_flag

                    setImageDrawable(resources.getDrawable(R.drawable.ic_topic_card_posts))

                }.lparams(wrapContent, wrapContent) {

                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID

                }


                textView {
                    id = R.id.topic_nums_tv
                    textColor = this.resources.getColor(R.color.feed_card_text_color)
                    textSizeDimen = R.dimen.common_text_size_12
                    text = resources.getString(R.string.common_join)
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER_VERTICAL
                    maxLines = 1
                    rightPadding = dip(12)
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)


                }.lparams(dip(280), wrapContent) {

                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                    startToEnd = R.id.nums_flag

                }


            }.lparams(0, wrapContent) {

                bottomMargin = dip(5)
                topMargin = dip(6)
            }

        }.lparams(0, matchParent) {
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }






        constraintLayout {

            id = R.id.card_info_with_introduce
            visibility = View.GONE
            leftPadding = dip(8)
            rightPadding = dip(12)

            linearLayout {

                id = R.id.ll_title
                orientation = LinearLayout.HORIZONTAL

                appCompatImageView {

                    setImageDrawable(resources.getDrawable(R.drawable.ic_super_topic_flag))

                }.lparams(dip(20), dip(20)) {

                    bottomMargin = dip(1)
                    topMargin = dip(1)

                }


                textView {
                    id = R.id.topic_name_tv_with_introduce
                    textColor = this.resources.getColor(R.color.splash_bg_start_color)
                    textSizeDimen = R.dimen.common_text_size_14
                    text = resources.getString(R.string.common_join)
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER_VERTICAL
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }.lparams(wrapContent, matchParent) {
                    leftMargin = dip(2)
                }


            }.lparams(wrapContent, wrapContent) {
                topMargin = dip(2)
                topToTop = PARENT_ID
            }

            textView {
                id = R.id.topic_introduce_tv
                textColor = this.resources.getColor(R.color.splash_bg_start_color)
                textSizeDimen = R.dimen.common_text_size_12
                text = resources.getString(R.string.common_join)
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 1
            }.lparams(0, wrapContent) {

                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topToBottom = R.id.ll_title


            }

            constraintLayout {

                id = R.id.post_info_with_introduce

                appCompatImageView {

                    id = R.id.nums_flag_with_introduce

                    setImageDrawable(resources.getDrawable(R.drawable.ic_topic_card_posts))

                }.lparams(wrapContent, wrapContent) {

                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID

                }

                textView {
                    id = R.id.topic_nums_tv_with_introduce
                    textColor = this.resources.getColor(R.color.feed_card_text_color)
                    textSizeDimen = R.dimen.common_text_size_12
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }.lparams(wrapContent, wrapContent) {

                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                    startToEnd = R.id.nums_flag_with_introduce

                }


            }.lparams(wrapContent, wrapContent) {
                topToBottom = R.id.topic_introduce_tv
                topMargin = dip(2)
            }

        }.lparams(0, 0) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToTop = PARENT_ID
            bottomToBottom = PARENT_ID
        }


    }.lparams(0, dip(64)) {
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        startToEnd = R.id.bg_lg_topic_card
    }
}


//----------------common bottom ------------------/////


fun ViewManager.FeedCommonBottomUI(): View = linearLayout {

    id = R.id.common_feed_bottom_root_view
    orientation = LinearLayout.VERTICAL


    textView {
        id = R.id.feed_location_view
        textColor = this.resources.getColor(R.color.color_normal_48779D)
        textSizeDimen = R.dimen.common_text_size_14
        text = resources.getString(R.string.common_join)
        leftPadding = dip(12)
        rightPadding = dip(12)
        topPadding = dip(4)
        bottomPadding = dip(4)
        ellipsize = TextUtils.TruncateAt.END
        gravity = Gravity.CENTER_VERTICAL
        maxLines = 1
        leftPadding = dip(6)
        rightPadding = dip(6)
        topPadding = dip(2)
        bottomPadding = dip(2)

        background = resources.getDrawable(R.drawable.commom_location_backgroud)
        setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.common_location_drawable),
                null, null, null)
        compoundDrawablePadding = dip(7)

    }.lparams(wrapContent, wrapContent) {
        leftMargin = dip(12)
        rightMargin = dip(12)
        bottomMargin = dip(8)
        topMargin = dip(8)
    }

    view {
        id = R.id.common_feed_top_divider
        backgroundColor = this.resources.getColor(R.color.transparent_07)
    }.lparams(matchParent, height = dip(1))


    constraintLayout {

        id = R.id.common_feed_bottom_content

        constraintLayout {

            id = R.id.common_feed_share_tab

            background = resources.getDrawable(R.drawable.ripple3)

            view {
                id = R.id.common_feed_center_share_view
            }.lparams(0, 0) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            }

            appCompatImageView {

                id = R.id.common_feed_share_icon

                setImageDrawable(resources.getDrawable(R.drawable.video_share_icon))

            }.lparams(dip(23), dip(23)) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToTop = R.id.common_feed_share
                topMargin = dip(5.9f)
            }


            textView {

                id = R.id.common_feed_share
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                textColor = resources.getColor(R.color.color_31)
                textSizeDimen = R.dimen.common_text_size_11
                gravity = Gravity.CENTER
                text = "111"
            }.lparams(wrapContent, wrapContent) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToBottom = R.id.common_feed_share_icon
            }


        }.lparams(0, matchParent) {

            startToEnd = R.id.common_feed_forward_tab
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }


        constraintLayout {

            id = R.id.common_feed_forward_tab

            background = resources.getDrawable(R.drawable.ripple3)

            appCompatImageView {

                id = R.id.common_feed_forward_icon

                setImageDrawable(resources.getDrawable(R.drawable.ic_feed_post_bottom_repost))

            }.lparams(dip(20), dip(20)) {

                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToTop = R.id.common_feed_forward_count
                topMargin = dip(8.9f)
            }


            textView {

                id = R.id.common_feed_forward_count
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                textColor = resources.getColor(R.color.color_31)
                textSizeDimen = R.dimen.common_text_size_11
                gravity = Gravity.CENTER
                text = "111"
            }.lparams(wrapContent, wrapContent) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToBottom = R.id.common_feed_forward_icon
            }


        }.lparams(0, matchParent) {

            startToEnd = R.id.common_feed_comment_tab
            endToStart = R.id.common_feed_share_tab

            rightMargin = dip(10)

            horizontalBias = 0.5f
        }


        constraintLayout {

            id = R.id.common_feed_comment_tab

            background = resources.getDrawable(R.drawable.ripple3)

            appCompatImageView {

                id = R.id.common_feed_comment_count_icon

                setImageDrawable(resources.getDrawable(R.drawable.ic_feed_post_bottom_comment))

            }.lparams(dip(20), dip(20)) {

                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToTop = R.id.common_feed_forward_count
                topMargin = dip(8.9f)
            }


            textView {

                id = R.id.common_feed_comment_count
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                textColor = resources.getColor(R.color.color_31)
                textSizeDimen = R.dimen.common_text_size_11
                gravity = Gravity.CENTER
                text = "111"
            }.lparams(wrapContent, wrapContent) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToBottom = R.id.common_feed_comment_count_icon
            }


        }.lparams(0, matchParent) {

            startToEnd = R.id.common_feed_like_tab
            endToStart = R.id.common_feed_forward_tab
            horizontalBias = 0.5f
        }


        constraintLayout {

            id = R.id.common_feed_like_tab

            background = resources.getDrawable(R.drawable.ripple3)

            appCompatImageView {

                id = R.id.common_feed_like_icon

                setImageDrawable(resources.getDrawable(R.drawable.feed_detail_like1))

            }.lparams(dip(20), dip(20)) {

                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToTop = R.id.common_feed_comment_count
                topMargin = dip(8.9f)
            }


            textView {

                id = R.id.common_feed_like_count
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                textColor = resources.getColor(R.color.color_31)
                textSizeDimen = R.dimen.common_text_size_11
                gravity = Gravity.CENTER
                text = "111"
            }.lparams(wrapContent, wrapContent) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToBottom = R.id.common_feed_like_icon
            }


        }.lparams(0, matchParent) {

            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToStart = R.id.common_feed_comment_tab
            horizontalBias = 0.5f
        }

    }.lparams(matchParent, dip(54))

    view {
        id = R.id.common_feed_divider
        backgroundColor = resources.getColor(R.color.common_color_fa)
        visibility = View.GONE
    }.lparams(matchParent, dip(8)) {
    }


}
//-----------------video view holder-------------------///

fun ViewManager.VideoFeedContainerUI(): View = frameLayout {

    id = R.id.video_feed_container


    frameLayout {

        id = R.id.video_feed_video_player
        backgroundColor = resources.getColor(R.color.black)
    }.lparams(matchParent, matchParent)


    feedListVideoController {
        id = R.id.video_controller
    }.lparams(matchParent, matchParent)

}


//-----------------image view holder-------------------///

fun ViewManager.ImageFeedUI(): View = constraintLayout {

    id = R.id.image_feed_root_view


    feedPicMultiGridView {
        id = R.id.pic_feed_multi_grid_view
    }.lparams(0, dip(100)) {
        startToStart = PARENT_ID
        endToEnd = PARENT_ID
        topPadding = dip(10)
    }

    textView {

        id = R.id.tv_post_status
        textColor = resources.getColor(R.color.white)
        textSizeDimen = R.dimen.common_text_size_12
        background = resources.getDrawable(R.drawable.app_common_btn_40_bark_bg)
        visibility = View.GONE
        leftPadding = dip(6)
        rightPadding = dip(6)
        topPadding = dip(3.5f)
        bottomPadding = dip(3.5f)

    }.lparams(wrapContent, wrapContent) {
        endToEnd = PARENT_ID
        topToTop = PARENT_ID
        topToBottom = R.id.common_feed_like_icon
        rightMargin = dip(12)
        topMargin = dip(18)
    }
}

//-----------------feedBottomCheckbox-------------------///

fun ViewManager.feedBottomCheckbox(): View = linearLayout {

    id = R.id.common_feed_bottom_cb

    leftPadding = dip(12)
    rightPadding = dip(12)
    topPadding = dip(6)
    gravity = Gravity.CENTER_VERTICAL


    imageView {

        id = R.id.iv_vote_check_2_post

        setImageResource(R.drawable.ic_vote_checked_icon)

    }.lparams(dip(16), dip(16))

    textView {

        id = R.id.tv_vote_check_2_post_info
        textColor = resources.getColor(R.color.main_grey)
        textSizeDimen = R.dimen.common_text_size_14

    }.lparams(wrapContent, wrapContent) {
        leftMargin = dip(3)
    }
}


//-----------------feedBottomCheckbox-------------------///

fun ViewManager.FeedLinkContainer(): View = relativeLayout {

    id = R.id.link_feed_container
    leftPadding = dip(0.5f)
    gravity = Gravity.CENTER_VERTICAL
    background = resources.getDrawable(R.drawable.topic_card_backgroud)

    simpleDraweeView {
        id = R.id.linked_feed_thumb_image
    }.lparams(dip(64), dip(63)) {
        centerVertically()
        topMargin = dip(0.5f)
        bottomMargin = dip(0.5f)
    }.apply {
        hierarchy.setFailureImage(R.drawable.ic_link_feed_error_image)
        hierarchy.setPlaceholderImage(R.drawable.link_feed_thumb_image)

    }

    textView {

        id = R.id.linked_feed_title
        textColor = resources.getColor(R.color.color_62)
        textSizeDimen = R.dimen.common_text_size_12

    }.lparams(wrapContent, wrapContent) {
        leftMargin = dip(10)
        centerVertically()
        rightMargin = dip(10)

    }
}

//-----------------video feed head-------------------///

fun ViewManager.videoFeedHeadUI(): View = constraintLayout {

    id = R.id.common_feed_head_root_view

    vipAvatar {

        id = R.id.common_feed_head

    }.lparams(dip(28), dip(28)) {

        bottomToBottom = PARENT_ID

        startToStart = PARENT_ID

        topToTop = PARENT_ID

        horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_SPREAD

        leftMargin = dip(8)

    }


    textView {

        id = R.id.common_feed_name

        maxLines = 1

        ellipsize = TextUtils.TruncateAt.END

        textColor = resources.getColor(R.color.common_feed_name_text_color)

        textSizeDimen = R.dimen.common_text_size_14

        typeface = Typeface.defaultFromStyle(Typeface.BOLD)

    }.lparams(width = wrapContent, height = wrapContent) {

        constrainedWidth = true

        startToEnd = R.id.common_feed_head

        endToStart = R.id.common_feed_event_mark

        topToTop = R.id.common_feed_head

        bottomToBottom = R.id.common_feed_head

        leftMargin = dip(8)

    }


    simpleDraweeView {

        id = R.id.common_feed_event_mark

        visibility = View.GONE

    }.lparams(width = dip(16), height = dip(16)) {
        bottomToBottom = R.id.common_feed_name

        topToTop = R.id.common_feed_name

        startToEnd = R.id.common_feed_name

        endToStart = R.id.common_feed_empty

        leftMargin = dip(2)

        margin = dip(1)

    }.apply {
        hierarchy.fadeDuration = (300)
        hierarchy.actualImageScaleType = (ScalingUtils.ScaleType.FIT_CENTER)

    }

    view {
        id = R.id.common_feed_empty
    }.lparams(width = dip(0), height = dip(1)) {
        startToEnd = R.id.common_feed_event_mark

        endToStart = R.id.common_feed_follow_btn

        topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        matchConstraintMinWidth = dip(1)

    }

    userFollowBtn {

        id = R.id.common_feed_follow_btn

    }.lparams(dip(76), dip(29)) {

        topToTop = PARENT_ID

        bottomToBottom = PARENT_ID

        startToEnd = R.id.common_feed_empty

        endToEnd = PARENT_ID

    }
}













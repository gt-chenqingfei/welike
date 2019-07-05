package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.airbnb.lottie.LottieDrawable
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * @author redefine honlin
 * @Date on 2019/2/21
 * @Description
 */

//---------------  interest  -----------------/

class FeedGPScoreItemViewUI : AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {

        constraintLayout {

            layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent)
            background = resources.getDrawable(R.drawable.ripple_shape_white)


            constraintLayout {
                id = R.id.cl_content

                background = resources.getDrawable(R.drawable.ripple1)


                leftPadding = dip(12)
                rightPadding = dip(12)
                topPadding = dip(12)
                bottomPadding = dip(14)



                textView {

                    id = R.id.tv_gp_score_title
                    textColor = this.resources.getColor(R.color.main_orange_dark)
                    textSizeDimen = R.dimen.common_text_size_18
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)


                }.lparams(0, wrapContent) {

                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

                }

                textView {
                    id = R.id.tv_gp_score_description
                    textColor = this.resources.getColor(R.color.main_grey)
                    textSizeDimen = R.dimen.common_text_size_16
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                }.lparams(0, wrapContent) {
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = R.id.tv_gp_score_title
                    topMargin = dip(6)
                }



                lottieAnimationView {
                    id = R.id.lv_gp_score_expression

                }.lparams(0, height = dip(110)) {

                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = R.id.tv_gp_score_description

                    topMargin = dip(13)

                }.apply {

                    repeatCount = LottieDrawable.INFINITE

                }


                lottieAnimationView {
                    id = R.id.lv_gp_score_expression1


                    repeatCount = LottieDrawable.INFINITE
                }.lparams(0, 0) {

                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = R.id.tv_gp_score_title
                    bottomToTop = R.id.ll_gp_score_star

                    topMargin = dip(13)
                }

                lottieAnimationView {
                    id = R.id.lv_gp_score_edit_expression

                    repeatCount = LottieDrawable.INFINITE

                }.lparams(0, dip(110)) {

                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToTop = R.id.tv_gp_score_feedback
                    topMargin = dip(13)
                    bottomMargin = dip(17)
                }



                linearLayout {

                    id = R.id.ll_gp_score_star


                    orientation = LinearLayout.HORIZONTAL

                    gravity = Gravity.CENTER
                }.lparams(wrapContent) {

                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = R.id.tv_gp_score_description

                    topMargin = dip(140)

                }

                textView {

                    id = R.id.tv_gp_score_description1
                    textColor = this.resources.getColor(R.color.main_grey)
                    visibility = View.INVISIBLE

                }.lparams(wrapContent) {
                    startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    topToBottom = R.id.ll_gp_score_star

                    topMargin = dip(18)

                }

                textView {

                    id = R.id.tv_gp_score_feedback
                    textColor = this.resources.getColor(R.color.main_orange_dark)
                    textSizeDimen = R.dimen.common_text_size_14
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    visibility = View.GONE

                }.lparams(wrapContent) {

                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

                }


            }.lparams(0, wrapContent) {

                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

            }


            frameLayout {
                id = R.id.common_feed_bottom_root_shadow
                backgroundColor = resources.getColor(R.color.common_color_f8f8f8)
                foreground = resources.getDrawable(R.drawable.common_shadow_icon)

            }.lparams(0, dip(8)) {
                topToBottom = R.id.cl_content
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }
}

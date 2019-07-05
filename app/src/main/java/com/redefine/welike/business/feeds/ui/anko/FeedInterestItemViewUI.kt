package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.view.ViewGroup
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * @author redefine honlin
 * @Date on 2019/2/21
 * @Description
 */

//---------------  interest  -----------------/

class FeedInterestItemViewUI : AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {

        constraintLayout {

            layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent)
            background = resources.getDrawable(R.drawable.ripple_shape_white)

            textView {

                id = R.id.tv_item_title
                textColor = this.resources.getColor(R.color.color_31)
                textSizeDimen = R.dimen.common_text_size_16
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)

                topPadding = dip(12)
                leftPadding = dip(12)

            }.lparams(wrapContent)





            imageView {

                id = R.id.iv_cancel

                padding = dip(12)

                setImageDrawable(resources.getDrawable(R.drawable.ic_cancel))

            }.lparams(dip(36), dip(36)) {
                endToEnd = PARENT_ID

            }


            flowLayout {

                id = R.id.fl_interest

                leftPadding = dip(10)
                rightPadding = dip(10)

            }.lparams(0, wrapContent) {

                topToBottom = R.id.tv_item_title

                topMargin = dip(6)

            }


            textView {

                id = R.id.tv_item_confirm

                topPadding = dip(4)

                rightPadding = dip(12)

                textColor = this.resources.getColor(R.color.main_orange_dark)

                textSizeDimen = R.dimen.common_text_size_14

            }.lparams(wrapContent, dip(24)) {

                endToEnd = PARENT_ID

                topToBottom = R.id.fl_interest

            }

            lottieAnimationView {
                id = R.id.lv_bottom_line

                rightPadding = dip(12)

            }.lparams(0, height = dip(1)) {

                topToBottom = R.id.tv_item_confirm
                startToStart = R.id.tv_item_confirm
                endToEnd = R.id.tv_item_confirm

                topMargin = dip(8)
            }


            frameLayout {
                id = R.id.common_feed_bottom_root_shadow
                backgroundColor = resources.getColor(R.color.common_color_f8f8f8)
                foreground = resources.getDrawable(R.drawable.common_shadow_icon)

            }.lparams(0, dip(8)) {
                topToBottom = R.id.lv_bottom_line
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
            }
        }
    }

}


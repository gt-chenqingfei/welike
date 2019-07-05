package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.view.ViewGroup
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * @author redefine honlin
 * @Date on 2019/2/20
 * @Description
 */

//-----------------follow view holder-------------------///
class FeedFollowItemViewUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        constraintLayout {


            layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent)


            textView {

                id = R.id.tv_item_title
                textColor = resources.getColor(R.color.color_31)
                textSizeDimen = R.dimen.common_text_size_14
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                topPadding = dip(6)
                leftPadding = dip(12)

            }.lparams(wrapContent, wrapContent)


            textView {
                id = R.id.tv_more_recommend
                textColor = resources.getColor(R.color.color_normal_48779D)
                textSizeDimen = R.dimen.common_text_size_14
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                topPadding = dip(6)
                rightPadding = dip(12)

            }.lparams(wrapContent, wrapContent) {
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }

            recyclerView {
                id = R.id.rv_follow
            }.lparams(wrapContent, wrapContent) {

                topMargin = dip(12)
                topToBottom = R.id.tv_item_title

            }


        }
    }
}
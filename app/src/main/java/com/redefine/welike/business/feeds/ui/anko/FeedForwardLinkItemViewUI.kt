package com.redefine.welike.business.feeds.ui.anko

import android.support.constraint.ConstraintLayout
import android.view.ViewGroup
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * @author redefine honlin
 * @Date on 2019/2/20
 * @Description
 */

//-----------------text view holder-------------------///
class FeedForwardLinkItemViewUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        constraintLayout {


            layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent)
            background = resources.getDrawable(R.drawable.ripple_shape_white)

            FeedItemADHeadUI().lparams(width = dip(0), height = wrapContent) {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }

            FeedItemCommonHeadUI().lparams(width = dip(0), height = wrapContent) {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topToBottom = R.id.header_card_layout
            }

            FeedItemContentUI().lparams(0, wrapContent) {
                topToBottom = R.id.common_feed_head_root_view
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }


            FeedForwardLinkUI().lparams(0, wrapContent) {
                topToBottom = R.id.text_feed_content
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                leftMargin= dip(12)
                rightMargin= dip(12)
                topMargin= dip(8)
            }


            FeedItemADCardUI().lparams(0, wrapContent) {
                topToBottom = R.id.forward_feed_root_view
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            FeedItemActiveCardUI().lparams(0, wrapContent) {
                topToBottom = R.id.ad_card_layout
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = dip(6)
            }

            FeedItemTopicCardUI().lparams(0, wrapContent) {
                topToBottom = R.id.active_card_layout
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = dip(6)
            }

            FeedCommonBottomUI().lparams(0, wrapContent) {
                topToBottom = R.id.topic_card_layout
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = dip(6)
            }


            frameLayout {
                id = R.id.common_feed_bottom_root_shadow
                backgroundColor = resources.getColor(R.color.common_color_f8f8f8)
                foreground = resources.getDrawable(R.drawable.common_shadow_icon)

            }.lparams(0, dip(8)) {
                topToBottom = R.id.common_feed_bottom_root_view
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }
}

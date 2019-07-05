package com.redefine.welike.business.feeds.ui.anko

import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.view.ViewGroup
import com.redefine.welike.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.constraintLayout

/**
 * @author redefine honlin
 * @Date on 2019/2/20
 * @Description
 */

//-----------------video view holder-------------------///
class FeedSpecialVideoItemViewUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {


        constraintLayout {

            layoutParams = ViewGroup.MarginLayoutParams(matchParent, wrapContent)

            cardView {

                cardElevation = dip(1).toFloat()
                radius = dip(4).toFloat()

                constraintLayout {

                    videoFeedHeadUI().lparams(0, dip(44)) {
                        startToStart = PARENT_ID
                        endToEnd = PARENT_ID
                        topToTop = PARENT_ID

                    }


                    frameLayout {


                        id = R.id.video_feed_container

                        background = resources.getDrawable(R.drawable.ripple1)

                        frameLayout {
                            id = R.id.video_feed_video_player

                            backgroundColor = resources.getColor(R.color.white)

                        }.lparams(matchParent, matchParent)

                        feedListVideoController {
                            id = R.id.video_controller
                        }.lparams(matchParent, matchParent)


                    }.lparams(0, dip(186)) {
                        startToStart = PARENT_ID
                        endToEnd = PARENT_ID
                        topToBottom = R.id.common_feed_head_root_view

                    }

                    FeedCommonBottomUI().lparams(0, wrapContent) {
                        topToBottom = R.id.video_feed_container
                        startToStart = PARENT_ID
                        endToEnd = PARENT_ID
                    }


                }.lparams(matchParent, wrapContent)


            }.lparams(0, wrapContent) {
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                bottomToBottom = PARENT_ID
                leftMargin = dip(8)
                rightMargin = dip(8)
                bottomMargin = dip(8)
            }
        }
    }
}

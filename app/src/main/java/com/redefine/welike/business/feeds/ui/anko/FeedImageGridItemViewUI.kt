package com.redefine.welike.business.feeds.ui.anko

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.redefine.welike.R
import org.jetbrains.anko.*

/**
 * @author redefine honlin
 * @Date on 2019/2/21
 * @Description
 */

//---------------  interest  -----------------/

class FeedImageGridItemViewUI : AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {

        frameLayout {

            layoutParams = ViewGroup.MarginLayoutParams(matchParent, matchParent)

            simpleDraweeView {
                id = R.id.feed_nine_grid_item
            }.lparams(matchParent, matchParent)

            subSamplingScaleImageView {
                id = R.id.longImg
                visibility = View.GONE
            }.lparams(matchParent, matchParent)

            textView {

                id = R.id.gif_tag
                textColor = resources.getColor(R.color.transparent_90_white)
                textSizeDimen = R.dimen.common_text_size_8
                background = resources.getDrawable(R.drawable.git_tag_bg)
                visibility = View.INVISIBLE
                text = resources.getString(R.string.gif_tag_text)

                leftPadding = dip(4)
                rightPadding = dip(4)
                topPadding = dip(2f)
                bottomPadding = dip(2f)


            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.BOTTOM or Gravity.END
                margin = dip(4)
            }

            textView {

                id = R.id.tv_view_full
                textColor = resources.getColor(R.color.white)
                textSizeDimen = R.dimen.common_text_size_8
                background = resources.getDrawable(R.drawable.app_common_btn_40_bark_bg)
                visibility = View.GONE
                text = resources.getString(R.string.feed_picture_view_full)

                leftPadding = dip(4)
                rightPadding = dip(4)
                topPadding = dip(2f)
                bottomPadding = dip(2f)

            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.BOTTOM or Gravity.END
                bottomMargin = dip(6)
                rightMargin = dip(6)
            }
        }
    }

}


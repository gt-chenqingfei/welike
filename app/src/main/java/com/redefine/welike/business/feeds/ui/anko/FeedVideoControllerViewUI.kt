package com.redefine.welike.business.feeds.ui.anko

import android.graphics.Typeface
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.ImageView
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.RoundingParams
import com.redefine.welike.R
import kotlinx.android.synthetic.main.third_login_layout.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout


/**
 * @author redefine honlin
 * @Date on 2019/2/20
 * @Description
 */

//-----------------text view holder-------------------///
class FeedVideoControllerViewUI : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        constraintLayout {


            layoutParams = ViewGroup.MarginLayoutParams(matchParent, matchParent)

            simpleDraweeView {
                id = R.id.video_cover
            }.lparams(0, 0) {
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topToTop = PARENT_ID
                bottomToBottom = PARENT_ID
            }.apply {
                setFadingEdgeLength(300)
            }




            progressView {

                id = R.id.video_progress_bar

            }.lparams(dip(33), dip(33)) {

                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topToTop = PARENT_ID
                bottomToBottom = PARENT_ID
            }


            imageView {
                id = R.id.video_feed_play_image
                backgroundColor = resources.getColor(R.color.transparent_40)
                setImageDrawable(resources.getDrawable(R.drawable.video_feed_play))
                scaleType = ImageView.ScaleType.CENTER
            }.lparams(matchParent, matchParent)

            textView {

                id = R.id.video_post_content
                textColor = resources.getColor(R.color.white)
                textSizeDimen = R.dimen.common_text_size_14
                bottomPadding = dip(8)
                topPadding = dip(8)
                leftPadding = dip(18)
                rightPadding = dip(18)

            }.lparams(wrapContent, wrapContent) {
                leftMargin = dip(4)
                topToTop = PARENT_ID
                endToEnd = PARENT_ID
                startToStart = PARENT_ID
            }


            appCompatImageView {

                id = R.id.video_voice_switch

                setImageDrawable(resources.getDrawable(R.drawable.video_voice_off))

            }.lparams(dip(42), dip(42)) {
                rightMargin = dip(12)
                bottomToBottom = PARENT_ID
                endToEnd = PARENT_ID

            }


            textView {
                id = R.id.tv_common_feed_operation
                textSizeDimen = R.dimen.common_text_size_16
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 10
                textColor = resources.getColor(R.color.common_text_black_31)
                backgroundColor = resources.getColor(R.color.common_divider_color_dddddd)
                leftPadding = dip(10)
                rightPadding = dip(10)
            }.lparams(0, wrapContent) {
                topToTop = PARENT_ID
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
            }

        }
    }
}

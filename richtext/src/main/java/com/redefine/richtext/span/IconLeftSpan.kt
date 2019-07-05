package com.redefine.richtext.span

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.style.ReplacementSpan
import android.view.View
import com.redefine.richtext.RichItem
import com.redefine.richtext.block.Block
import com.redefine.richtext.constant.RichConstant

/**
 *
 * Name: IconLeftSpan
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-09-05 19:46
 *
 */

class IconLeftSpan(private val mLeftDrawable: Drawable?, private val mBlock: Block?, private val mListener: OnRichItemClickListener?) : ReplacementSpan(), RichSpan {
    private var mSize: Int = 0
    private var isPressed: Boolean = false

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val leftWidth = mLeftDrawable?.intrinsicWidth ?: 0
        mSize = paint.measureText(text, start, end).toInt() + leftWidth
        return mSize
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val color = paint.color

        paint.color = if (isPressed) RichConstant.richPressedColor else Color.TRANSPARENT //设置背景颜色
        val oval = RectF(x, y + paint.ascent(), x + mSize, y.toFloat() + paint.descent())
        canvas.drawRect(oval, paint)
        paint.isAntiAlias = true// 设置画笔的锯齿效果
        paint.color = RichConstant.richTextColor
        val leftWidth = mLeftDrawable?.intrinsicWidth ?: 0
        canvas.drawText(text, start, end, x + leftWidth.toFloat(), y.toFloat(), paint)//绘制文字
        paint.color = color
        if (mLeftDrawable != null) {
            canvas.save()
            val textHeight = paint.descent() - paint.ascent()
            val offset = (textHeight - mLeftDrawable.bounds.height()) / 2
            val transY = y.toFloat() + paint.ascent() + offset
            canvas.translate(x, transY)
            mLeftDrawable.draw(canvas)
            canvas.restore()
        }

    }

    override fun getBlock(): Block? {
        return mBlock
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = Color.WHITE
    }

    override fun getRichItem(): RichItem {
        return mBlock!!.richItem
    }

    override fun onClick(widget: View) {
        if (mBlock?.richItem == null) {
            return
        }
        mListener?.onRichItemClick(mBlock.richItem)
    }

    override fun setPressed(b: Boolean) {
        isPressed = b
    }
}
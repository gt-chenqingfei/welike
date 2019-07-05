package com.redefine.welike.business.publisher.ui.component

import android.content.Context
import android.support.v7.content.res.AppCompatResources
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import com.redefine.welike.R
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import kotlinx.android.synthetic.main.layout_common_search_bar.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/30
 * Copyright (C) 2018 redefine , Inc.
 */


@LayoutResource(layout = R.layout.layout_common_search_bar)
class SearchBar(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs),
        TextWatcher {

    private var mTextChangedListener: OnSearchBarListener? = null
    override fun onCreateView() {
        val a = context.theme.obtainStyledAttributes(
                attrs, R.styleable.SearchBar, 0, 0)
        val hint = a.getString(R.styleable.SearchBar_hint)
        val id = a.getResourceId(R.styleable.SearchBar_icon, -1)
        if (id != -1) {
            val icon = AppCompatResources.getDrawable(context, id)
            iv_search_icon.setImageDrawable(icon)
        }

        search_edit.hint = hint
        search_edit.addTextChangedListener(this)
        tv_cancel.setOnClickListener {
            mTextChangedListener?.let { listener ->
                listener.onCancelClick()
            }
        }

        iv_edit_clear.setOnClickListener {
            search_edit.setText("")
        }
    }


    override fun afterTextChanged(s: Editable?) {
        if (TextUtils.isEmpty(s)) {
            iv_edit_clear.visibility = INVISIBLE
        } else {
            iv_edit_clear.visibility = VISIBLE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mTextChangedListener?.let {
            it.onTextChanged(s, start, before, count)
        }
    }

    fun setOnSearchBarListener(listener: OnSearchBarListener) {
        mTextChangedListener = listener
    }

    fun getText(): String {
        return search_edit.text.toString()
    }

    fun setSearchDrawable(drawable: Int) {
        iv_search_icon.setImageResource(drawable)
    }

    fun setSearchBarShadow(enable: Boolean) {
        if (enable) {
            divider_line.visibility = View.GONE
            search_bar_root.setBackgroundResource(R.drawable.common_b_t_shadow)
        } else {
            search_bar_root.setBackgroundResource(R.drawable.common_transparent)
            divider_line.visibility = View.VISIBLE
        }
    }

}

interface OnSearchBarListener {
    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
    fun onCancelClick()
}
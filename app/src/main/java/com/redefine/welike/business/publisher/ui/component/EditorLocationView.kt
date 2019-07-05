package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.redefine.welike.R
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import kotlinx.android.synthetic.main.layout_publish_post_location.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.layout_publish_post_location)
class EditorLocationView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs) {

    lateinit var mViewModel: PublishPostViewModel
    override fun onCreateView() {
        mViewModel = ViewModelProviders.of(activityContext).get(PublishPostViewModel::class.java)
        editor_location_delete.setOnClickListener {
            mViewModel.updateLocation(null)
        }

        mViewModel.mLocationLiveData.observe(activityContext, android.arch.lifecycle.Observer {
            it?.let { location ->
                fillData(location.place)
                editor_location.isClickable = location.isClickable
                editor_location_delete.isClickable = location.isClickable
            } ?: fillData("")
        })
    }

    private fun fillData(place: String?) {
        if (!TextUtils.isEmpty(place)) {
            editor_location.text = place
            editor_location_delete.visibility = View.VISIBLE
        } else {
            editor_location.setText(R.string.editor_location_null)
            editor_location_delete.visibility = View.GONE
        }
    }

}


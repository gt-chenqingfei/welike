package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.View
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.multimedia.photoselector.activity.PhotoSelectorPreviewActivity
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.welike.R
import com.redefine.welike.business.publisher.ui.adapter.EditorPhotoGridViewAdapter
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import com.redefine.welike.commonui.photoselector.PhotoSelector
import com.redefine.welike.commonui.view.MultiGridView
import com.redefine.welike.commonui.view.PicMultiGridView
import kotlinx.android.synthetic.main.layout_publish_post_editor_photo.view.*
import java.util.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.layout_publish_post_editor_photo)
class EditorImageView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs)
        , MultiGridView.OnItemClickListener {

    private var mPhotoAdapter: EditorPhotoGridViewAdapter? = null
    override fun onCreateView() {
        mPhotoAdapter = EditorPhotoGridViewAdapter()

        val gridView:PicMultiGridView<EditorPhotoGridViewAdapter>
        if(editor_photo_grid == null){
            return
        }

        gridView = editor_photo_grid as PicMultiGridView<EditorPhotoGridViewAdapter>
        val viewModel = ViewModelProviders.of(activityContext).get(PublishPostViewModel::class.java)
        viewModel.mPhotoLiveData.observe(activityContext, android.arch.lifecycle.Observer {
            fillData(it)
        })

        gridView.setOnItemClickListener(this)
        gridView.setFourGridStyle(false)
        gridView.setAdapter(mPhotoAdapter)
        mPhotoAdapter?.let {
            it.registerDataSetObserver(object : DataSetObserver() {
                //todo
                override fun onChanged() {
                    super.onChanged()
                    if (it.count <= 0) {
                        viewModel.updatePhotos(null)
                    }
                }
            })
        }
    }

    override fun onItemClick(view: View?, position: Int, t: Any?) {

        val list = ArrayList<Item>()
        if (mPhotoAdapter?.count != 0) {
            list.addAll(mPhotoAdapter!!.data)
        }
        if (mPhotoAdapter!!.getItemViewType(position) == EditorPhotoGridViewAdapter.TYPE_ADD) {
            PhotoSelector.launchPhotoSelectorForPublishPost(activityContext, list)
        } else {
            PhotoSelectorPreviewActivity.launch(activityContext, list, position)
//            PhotoSelector.launchPreview(false,
//                    AccountManager.getInstance().account?.nickName, position, list, activityContext)
        }
        InputMethodUtil.hideInputMethod(activityContext.currentFocus)
    }

    private fun fillData(list: List<Item>?) {
        if (list == null) {
//            mPhotoAdapter!!.clear()
            this.visibility = View.GONE
            return
        }
        this.visibility = View.VISIBLE
        mPhotoAdapter!!.data = list
    }

}
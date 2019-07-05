package com.redefine.welike.business.publisher.ui.dialog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import com.pekingese.pagestack.framework.util.DrawableUtil
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.loadmore.adapter.KAdapter
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.richtext.constant.RichConstant
import com.redefine.welike.R
import com.redefine.welike.business.publisher.api.bean.CommonTopic
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.api.bean.Topic
import com.redefine.welike.business.publisher.api.bean.TopicCategory
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.ui.component.OnSearchBarListener
import com.redefine.welike.business.publisher.ui.component.base.BottomSheetBehavior
import com.redefine.welike.business.publisher.ui.component.base.BottomSheetDialog
import com.redefine.welike.business.publisher.ui.contract.ITopicChoiceContract
import com.redefine.welike.business.publisher.viewmodel.SuperTopicChosenViewModel
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog
import kotlinx.android.synthetic.main.super_topic_chosen_layout.*
import kotlinx.android.synthetic.main.topic_category.view.*
import kotlinx.android.synthetic.main.topic_choice_layout_content.view.*
import kotlinx.android.synthetic.main.topic_items.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/29
 * Copyright (C) 2018 redefine , Inc.
 */
class SuperTopicChosenDialog(val theme: Int, val context: AppCompatActivity,
                             val listener: OnSuperTopicChoiceListener,
                             val canChosenSuperTopic: Boolean) : BottomSheetDialog(context, theme),
        OnTopicChoiceListener, OnSearchBarListener {

    private lateinit var mViewModel: SuperTopicChosenViewModel
    private var mCurrentCategory: TopicCategory? = null

    private lateinit var mCategoryAdapter: KAdapter<TopicCategory>

    private lateinit var mTopicsAdapter: KAdapter<Topic>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var mPresenter: ITopicChoiceContract.ITopicChoicePresenter? = null

    companion object {
        fun showDialog(context: AppCompatActivity, listener: OnSuperTopicChoiceListener, canChosenSuperTopic: Boolean) {
            val bottomSheet = SuperTopicChosenDialog(R.style.BottomSheetEdit, context, listener, canChosenSuperTopic)
            val view = View.inflate(context, R.layout.super_topic_chosen_layout, null)
            bottomSheet.setContentView(view)

            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.peekHeight = ScreenUtils.getScreenHeight(context) - ScreenUtils.dip2Px(160f)
            bottomSheet.show()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (TextUtils.isEmpty(s)) {
            super_topic_choice_container.visibility = View.VISIBLE
            topic_choice_container.visibility = View.GONE
        } else {
            topic_choice_container.visibility = View.VISIBLE
            super_topic_choice_container.visibility = View.GONE
        }
        mPresenter?.onTextChanged(s, start, before, count)
    }

    override fun onCancelClick() {
        listener.onSuperTopicChoice(null)
        this.cancel()
    }

    // handle multi scroll view touch behavior
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (topic_choice_container.visibility == View.VISIBLE) {
            bottomSheetBehavior.setNestedScrollingChildRef(topic_choice_container.topic_choice_recycler_view)
        } else {
            if (ev!!.x <= super_topic_category.right) {
                bottomSheetBehavior.setNestedScrollingChildRef(super_topic_category)
            } else {
                bottomSheetBehavior.setNestedScrollingChildRef(super_topic_items)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val designBottomSheet = window.findViewById<View>(R.id.design_bottom_sheet)
        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
        val activity = context

        bottomSheetBehavior = BottomSheetBehavior.from(super_topic_container.parent as View)
        mPresenter = ITopicChoiceContract.TopicChoiceFactory.createPresenter(ownerActivity, this, search_bar)
        mPresenter?.onCreate(findViewById(R.id.topic_choice_container), null)

        super_topic_container.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            super_topic_container.getWindowVisibleDisplayFrame(rect)
            val height = super_topic_container.rootView.height
            val heightDefere = height - rect.bottom
            if (heightDefere > 200) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        drag_view.visibility = View.INVISIBLE
                        search_bar.setSearchDrawable(R.drawable.ic_common_search)
                        search_bar.setSearchBarShadow(true)
                        designBottomSheet.setBackgroundColor(context.resources.getColor(R.color.white))
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        drag_view.visibility = View.VISIBLE
                        search_bar.setSearchDrawable(R.drawable.ic_publish_topic)
                        search_bar.setSearchBarShadow(false)
                        designBottomSheet.setBackgroundResource(R.drawable.shape_bottom_sheet_dialog_drag_bg)
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> cancel()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        mViewModel = ViewModelProviders.of(activity).get(SuperTopicChosenViewModel::class.java)

        search_bar.setOnSearchBarListener(this)

        super_topic_category.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        mCategoryAdapter = KAdapter(R.layout.topic_category) { view: View, category: TopicCategory, i: Int ->
            view.topic_category.text = category.labelName
            view.setOnClickListener {
                selectCategory(category)
            }
            view.topic_category.isSelected = mCurrentCategory != null && TextUtils.equals(mCurrentCategory!!.labelId, category.labelId)
        }
        super_topic_category.adapter = mCategoryAdapter

        mTopicsAdapter = KAdapter(R.layout.topic_items) { view: View, topic: Topic, i: Int ->
            view.topic_item.text = topic.name
            if (topic is CommonTopic) {
                val drawable = DrawableUtil.getDrawable(view.context, R.drawable.common_topic_icon)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                view.topic_item.setCompoundDrawables(drawable, null, null, null)
            } else if (topic is SuperTopic) {
                val drawable = DrawableUtil.getDrawable(view.context, R.drawable.super_topic_icon)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                view.topic_item.setCompoundDrawables(drawable, null, null, null)
            }
            view.setOnClickListener {
                if (topic !is SuperTopic || canChosenSuperTopic) {
                    listener.onSuperTopicChoice(topic)
                    this.cancel()
                    PublisherEventManager.INSTANCE.add_topic_type = EventConstants.PUBLISH_ADD_TOPIC_RECOMMOND
                    EventLog.Publish.report22(PublisherEventManager.INSTANCE.source,
                            PublisherEventManager.INSTANCE.main_source,
                            PublisherEventManager.INSTANCE.page_type,
                            PublisherEventManager.INSTANCE.at_source,
                            topic.id)

                    if (PublishAnalyticsManager.getInstance().obtainCurrentModel() != null) {
                        PublishAnalyticsManager.getInstance().obtainCurrentModel().proxy.report20()
                    }
                } else {
                    ToastUtils.showShort(activity.getString(R.string.super_topic_over_limit))
                }
            }
        }
        super_topic_items.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        super_topic_items.adapter = mTopicsAdapter
        mViewModel.categories.observe(activity, Observer<List<TopicCategory>> { it ->
            it?.let {
                mCategoryAdapter.setData(it.toMutableList())
                if (mCurrentCategory == null && !CollectionUtil.isEmpty(it)) {
                    selectCategory(it[0])
                }
            }
        })

        mViewModel.topics.observe(activity, Observer { it ->
            it?.let {
                if (mCurrentCategory != null) {
                    val topics = it[this.mCurrentCategory!!.labelId]
                    if (!CollectionUtil.isEmpty(topics)) {
                        mTopicsAdapter.setData(topics!!.toMutableList())
                    }
                }
            }
        })

        mViewModel.pageState.observe(activity, Observer { it ->
            it?.let {
                when (it) {
                    PageStatusEnum.EMPTY -> showEmptyView()
                    PageStatusEnum.ERROR -> showErrorView()
                    PageStatusEnum.CONTENT -> showContentView()
                    PageStatusEnum.LOADING -> showLoading()
                }
            }
        })

        mViewModel.topicPageState.observe(activity, Observer { it ->
            it?.let {
                if (TextUtils.equals(it.labelId, mCurrentCategory?.labelId)) {
                    when (it.pageStatusEnum) {
                        PageStatusEnum.EMPTY -> showTopicPageEmptyView()
                        PageStatusEnum.ERROR -> showTopicPageErrorView()
                        PageStatusEnum.CONTENT -> showTopicPageContentView()
                        PageStatusEnum.LOADING -> showTopicPageLoading()
                    }
                }
            }
        })

        common_error_view.setOnErrorViewClickListener {
            mViewModel.requestCategories()
        }

        topics_error_view.setOnErrorViewClickListener {
            if (mCurrentCategory == null && !CollectionUtil.isEmpty(mViewModel.categories.value)) {
                mCurrentCategory = mViewModel.categories.value!![0]
            }
            if (mCurrentCategory != null) {
                selectCategory(mCurrentCategory)
            }
        }

        mViewModel.requestCategories()
    }

    private fun showTopicPageLoading() {
        topics_loading_view.visibility = View.VISIBLE
        topics_empty_view.visibility = View.INVISIBLE
        topics_error_view.visibility = View.INVISIBLE
    }

    private fun showTopicPageContentView() {
        topics_loading_view.visibility = View.INVISIBLE
        topics_empty_view.visibility = View.INVISIBLE
        topics_error_view.visibility = View.INVISIBLE
    }

    private fun showTopicPageErrorView() {
        topics_loading_view.visibility = View.INVISIBLE
        topics_empty_view.visibility = View.INVISIBLE
        topics_error_view.visibility = View.VISIBLE
    }

    private fun showTopicPageEmptyView() {
        topics_loading_view.visibility = View.INVISIBLE
        topics_empty_view.visibility = View.VISIBLE
        topics_error_view.visibility = View.INVISIBLE
        topics_empty_view.showEmptyImg(R.drawable.ic_common_empty)
    }

    fun showLoading() {
        common_loading_view.visibility = View.VISIBLE
        common_empty_view.visibility = View.INVISIBLE
        common_error_view.visibility = View.INVISIBLE
    }

    fun showErrorView() {
        common_loading_view.visibility = View.INVISIBLE
        common_error_view.visibility = View.VISIBLE
        common_empty_view.visibility = View.INVISIBLE
    }

    fun showEmptyView() {
        common_loading_view.visibility = View.INVISIBLE
        common_empty_view.visibility = View.VISIBLE
        common_error_view.visibility = View.INVISIBLE
        common_empty_view.showEmptyImg(R.drawable.ic_common_empty)
    }

    fun showContentView() {
        common_loading_view.visibility = View.INVISIBLE
        common_empty_view.visibility = View.INVISIBLE
        common_error_view.visibility = View.INVISIBLE
    }

    private fun selectCategory(topicCategory: TopicCategory?) {
        if (topicCategory == null || TextUtils.isEmpty(topicCategory.labelId)) {
            return
        }
        val list = if (mViewModel.topics.value != null) mViewModel.topics.value!![topicCategory.labelId] else mutableListOf()
        mCurrentCategory = topicCategory
        if (CollectionUtil.isEmpty(list)) {
            mTopicsAdapter.clearData()
            mViewModel.requestCategoryItems(topicCategory.labelId)
        } else {
            mTopicsAdapter.setData(list!!.toMutableList())
        }
        mCategoryAdapter.notifyDataSetChanged()

    }

    override fun onTopicChoice(bean: TopicSearchSugBean.TopicBean?, isWithFlag: Boolean) {
        if (bean == null) {
            listener.onSuperTopicChoice(null)
            this.cancel()
            return
        }
        val name = if (isWithFlag) bean.name else RichConstant.TOPIC + bean.name
        val topic = CommonTopic(bean.id
                ?: "", name, "")
        listener.onSuperTopicChoice(topic)
        this.cancel()
    }


}

interface OnSuperTopicChoiceListener {
    fun onSuperTopicChoice(topic: Topic?)
}
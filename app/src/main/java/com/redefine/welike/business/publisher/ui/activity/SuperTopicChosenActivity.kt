package com.redefine.welike.business.publisher.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.pekingese.pagestack.framework.util.DrawableUtil
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.loadmore.adapter.KAdapter
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.richtext.constant.RichConstant
import com.redefine.welike.R
import com.redefine.welike.base.constant.CommonRequestCode
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.business.publisher.api.bean.CommonTopic
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.api.bean.Topic
import com.redefine.welike.business.publisher.api.bean.TopicCategory
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.publisher.viewmodel.SuperTopicChosenViewModel
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.statistical.EventConstants
import kotlinx.android.synthetic.main.super_topic_chosen_layout.*
import kotlinx.android.synthetic.main.topic_category.view.*
import kotlinx.android.synthetic.main.topic_items.view.*

/**
 *
 * Name: SuperTopicChosenActivity
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-09-03 19:36
 *
 */

@Route(path = RouteConstant.LAUNCH_SUPER_TOPIC_CHOSEN_PAGE)
class SuperTopicChosenActivity : BaseActivity() {

    companion object {
        const val EXTRA_CAN_CHOSEN_SUPER_TOPIC = "can_chosen_super_topic"
        const val EXTRA_SUPER_TOPIC_RESULT = "super_topic_result"
        const val EXTRA_TOPIC_RESULT_FLAG = "key_topic_result_with_flag"
        const val EXTRA_TOPIC_RESULT = "key_topic_result"
    }

    private lateinit var mViewModel: SuperTopicChosenViewModel
    private var mCurrentCategory: TopicCategory? = null

    private lateinit var mCategoryAdapter: KAdapter<TopicCategory>

    private lateinit var mTopicsAdapter: KAdapter<Topic>

    private var canChosenSuperTopic = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.super_topic_chosen_layout)

        if (intent != null && intent.hasExtra(EXTRA_CAN_CHOSEN_SUPER_TOPIC)) {
            canChosenSuperTopic = intent.getBooleanExtra(EXTRA_CAN_CHOSEN_SUPER_TOPIC, true)
        }
        mViewModel = ViewModelProviders.of(this).get(SuperTopicChosenViewModel::class.java)

        super_topic_category.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

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
                    val intent = Intent()
                    intent.putExtra(EXTRA_SUPER_TOPIC_RESULT, topic)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    PublisherEventManager.INSTANCE.add_topic_type = EventConstants.PUBLISH_ADD_TOPIC_RECOMMOND
                } else {
                    ToastUtils.showShort(getString(R.string.super_topic_over_limit))
                }
            }
        }
        super_topic_items.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        super_topic_items.adapter = mTopicsAdapter
        mViewModel.categories.observe(this, Observer<List<TopicCategory>> { it ->
            it?.let {
                mCategoryAdapter.setData(it.toMutableList())
                if (mCurrentCategory == null && !CollectionUtil.isEmpty(it)) {
                    selectCategory(it[0])
                }
            }
        })

        mViewModel.topics.observe(this, Observer { it ->
            it?.let {
                if (mCurrentCategory != null) {
                    val topics = it[this.mCurrentCategory!!.labelId]
                    if (!CollectionUtil.isEmpty(topics)) {
                        mTopicsAdapter.setData(topics!!.toMutableList())
                    }
                }
            }
        })

        mViewModel.pageState.observe(this, Observer { it ->
            it?.let {
                when (it) {
                    PageStatusEnum.EMPTY -> showEmptyView()
                    PageStatusEnum.ERROR -> showErrorView()
                    PageStatusEnum.CONTENT -> showContentView()
                    PageStatusEnum.LOADING -> showLoading()
                }
            }
        })

        mViewModel.topicPageState.observe(this, Observer { it ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CommonRequestCode.CHOICE_TOPIC_CODE) {
                val bean = data.getSerializableExtra(EXTRA_TOPIC_RESULT) as TopicSearchSugBean.TopicBean
                val isWithFlag = data.getBooleanExtra(EXTRA_TOPIC_RESULT_FLAG, false)
                val name = if (isWithFlag) bean.name else RichConstant.TOPIC + bean.name
                val topic = CommonTopic(bean.id
                        ?: "", name, "")
                val intent = Intent()
                intent.putExtra(EXTRA_SUPER_TOPIC_RESULT, topic)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
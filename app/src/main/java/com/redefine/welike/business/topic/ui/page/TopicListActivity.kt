package com.redefine.welike.business.topic.ui.page

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageLoadMoreStatusEnum
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.commonui.widget.LoadingDlg
import com.redefine.foundation.framework.Event
import com.redefine.welike.R
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.browse.management.request.HotFeedTopicRequest2
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.topic.ui.constant.TopicConstant
import com.redefine.welike.commonui.framework.PageStackManager
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.manager.SearchEventManager
import kotlinx.android.synthetic.main.topic_list_item.view.*
import kotlinx.android.synthetic.main.topic_list_page.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

@Route(path = RouteConstant.PATH_TOPIC_LIST)
class TopicListActivity : BaseActivity() {


    private lateinit var mPageStackManager: BaseLifecyclePageStackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPageStackManager = PageStackManager(this)
        val root = LayoutInflater.from(this).inflate(R.layout.topic_list_page, null)
        setContentView(root)
        initView(root)
        createPageView(root)

        EventBus.getDefault().register(this)
        EventLog1.Discover.report1()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id || event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish()
        }
    }

     override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    val viewModel: TopicListViewModel by lazy { ViewModelProviders.of(this).get(TopicListViewModel::class.java); }

    fun createPageView(root: View) {

        //set views.
        root.common_title_view.text = "trending_topic".translate()
        root.friend_list.layoutManager = LinearLayoutManager(this)
//        root.common_empty_view.showEmptyText("contact_empty".translate())
        root.topic_list_tip.text = "topic_list_tip".translate(ResourceTool.ResourceFileEnum.TOPIC)
        //set listener
        root.common_back_btn.setOnClickListener { mPageStackManager.onBackPressed() }
        root.common_error_view.setOnErrorViewClickListener { viewModel.refresh() }
        root.common_error_view.setErrorText("video_player_net_error".translate())

        root.common_back_btn.setOnClickListener { finish() }
        root.friend_list.layoutManager = LinearLayoutManager(this)
        val adapter = TopicListAdapter(LayoutInflater.from(this), {
            if (TextUtils.isEmpty(it.id)) return@TopicListAdapter
            val bundle = Bundle()
            val bean = TopicSearchSugBean.TopicBean()
            bean.name = it.topicName
            bean.id = it.id
            bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean)
            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle))
            EventLog1.Discover.report2(it.id, EventLog1.Discover.TopicSource.TRENDING_TOPICS)
            SearchEventManager.INSTANCE.setTopic_name(it.topicName)
            SearchEventManager.INSTANCE.setFrom_page(EventConstants.SEARCH_FROM_PAGE_TOPIC_HOT_LIST)
            SearchEventManager.INSTANCE.report2()
        })
        root.friend_list.adapter = adapter

        val mLoadingDlg = LoadingDlg(mPageStackManager.context as Activity)
        viewModel.state.observe(this, Observer {
            it?.let {
                root.common_empty_view.visibility = View.INVISIBLE
                root.common_error_view.visibility = View.INVISIBLE
                mLoadingDlg.dismiss()
                when (it) {
                    PageStatusEnum.LOADING -> mLoadingDlg.show()
                    PageStatusEnum.ERROR -> root.common_error_view.visibility = View.VISIBLE
                    PageStatusEnum.EMPTY -> root.common_empty_view.visibility = View.VISIBLE
                    else -> {
                    }
                }
            }
        })


        viewModel.data.observe(this, Observer {
            it?.let { adapter.setData(it) }
        })
    }

    fun initView(container: View) {
        viewModel.init()
        SearchEventManager.INSTANCE.report3()
    }
}


class TopicListViewModel(app: Application) : AndroidViewModel(app) {
    //state for page.
    val state = MutableLiveData<PageStatusEnum>().also { it.value = PageStatusEnum.LOADING }
    //Loading
    val loading = MutableLiveData<PageLoadMoreStatusEnum>().also { it.value = PageLoadMoreStatusEnum.NONE }
    //data for UI.
    val data = MutableLiveData<ArrayList<TopicInfo>>()

    fun init() {
        refresh()
    }

    fun refresh() {
        state.postValue(PageStatusEnum.LOADING)
        thread {
            try {
                HotFeedTopicRequest2(getApplication(), 20).req(object : RequestCallback {
                    override fun onError(request: BaseRequest?, errCode: Int) {
                        state.postValue(PageStatusEnum.ERROR)
                    }

                    override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                        state.postValue(PageStatusEnum.CONTENT)
                        result?.getJSONArray("list")?.let { jsonArray ->
                            val retList = Gson().fromJson<ArrayList<TopicInfo>>(jsonArray.toString(), object : TypeToken<ArrayList<TopicInfo>>() {}.type)
                            data.postValue(retList)
                        }
                    }
                })
            } catch (e: Exception) {
                state.postValue(PageStatusEnum.ERROR)
            }
        }
    }

}

class TopicListHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title = view.topic_item_title
    val number = view.topic_item_number
    val content = view.topic_item_content
    val posts = view.topic_item_posts
    val views = view.topic_item_views
}

class TopicListAdapter(val inflater: LayoutInflater, val click: (TopicInfo) -> Unit, val data: ArrayList<TopicInfo> = ArrayList()) : RecyclerView.Adapter<TopicListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicListHolder {
        return TopicListHolder(inflater.inflate(R.layout.topic_list_item, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TopicListHolder, position: Int) {
        data[position].let { info ->
            holder.title.text = info.topicName
            holder.number.text = "${position + 1}"
            holder.content.text = info.description
            holder.posts.text = "${info.postsCount} posts"
            holder.views.text = "${info.usersCount} views"
            holder.number.visibility = if (position < 10) {
                View.VISIBLE
            } else {
                View.GONE
            }
            holder.content.visibility = if (info.description.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
            holder.itemView.setOnClickListener { click(info) }
        }
    }


    fun setData(news: ArrayList<TopicInfo>) {
        if (data.isEmpty()) {
            data.addAll(news)
            notifyDataSetChanged()
        }
    }
}

open class TopicInfo(
        @SerializedName("id")
        var id: String?,

        @SerializedName("topicName")
        var topicName: String?,

        @SerializedName("bannerUrl")
        var bannerUrl: String?,

        @SerializedName("emceeUid")
        var emceeUid: String?,

        @SerializedName("postsCount")
        var postsCount: Int?,

        @SerializedName("usersCount")
        var usersCount: Int?,

        @SerializedName("created")
        var created: Long?,

        @SerializedName("description")
        var description: String?
)

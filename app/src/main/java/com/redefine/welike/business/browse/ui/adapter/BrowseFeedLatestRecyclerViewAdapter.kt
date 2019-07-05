package com.redefine.welike.business.browse.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.media.mediasdk.OSCore.T
import com.redefine.commonui.fresco.loader.HeadUrlLoader

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder
import com.redefine.commonui.share.request.ShareCountReportManager
import com.redefine.commonui.widget.StickHeaderItemDecoration
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.R
import com.redefine.welike.business.browse.management.constant.BrowseConstant
import com.redefine.welike.business.browse.management.constant.FeedViewHolderHelper
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener
import com.redefine.welike.business.feeds.management.UserVoteManager
import com.redefine.welike.business.feeds.management.bean.*
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener
import com.redefine.welike.business.feeds.ui.listener.IVoteChoiceListener
import com.redefine.welike.business.feeds.ui.viewholder.*
import com.redefine.welike.statistical.EventLog1

/**
 * create by honlin 20180621
 */

class BrowseFeedLatestRecyclerViewAdapter<H>(source: String) : FeedRecyclerViewAdapter<H>(null, source), IFeedOperationListener, ShareCountReportManager.ShareCountCallback, StickHeaderItemDecoration.StickyHeaderInterface, UserVoteManager.UserVoteCallback, IVoteChoiceListener {


    private val FEED_CARD_TYPE_HEADER = -1000

    private var iBrowseClickListener: IBrowseClickListener? = null

    private var gpScorePost: GPScorePost? = null

    var headerClick: (data: PostBase) -> Unit = {}

    //    var magicView: SimpleDraweeView? = null
    private var canShowStickHeader = false

//    var source: String
//        get() = mFeedSource
//        set(source) {
//            this.mFeedSource = source
//        }

    init {
        ShareCountReportManager.getInstance().register(this)
    }

    override fun onCreateFooterViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*>? {
        return FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false))
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.context)
        }
        val viewHolder: BaseRecyclerViewHolder<*>
        when (viewType) {
            FeedViewHolderHelper.FEED_CARD_TYPE_TEXT -> viewHolder = TextFeedViewHolder(mInflater.inflate(R.layout.text_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_VOTE_PIC -> viewHolder = VotePicViewHolder(mInflater.inflate(R.layout.vote_pic_feed_item, parent, false), this, this)
            FeedViewHolderHelper.FEED_CARD_TYPE_PIC -> viewHolder = PicFeedViewHolder(mInflater.inflate(R.layout.pic_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_VIDEO -> viewHolder = VideoFeedViewHolder(mInflater.inflate(R.layout.video_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_LINK -> viewHolder = LinkFeedViewHolder(mInflater.inflate(R.layout.link_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_LINK -> viewHolder = ForwardFeedLinkViewHolder(mInflater.inflate(R.layout.forward_link_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_TEXT -> viewHolder = ForwardFeedTextViewHolder(mInflater.inflate(R.layout.forward_text_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_PIC -> viewHolder = ForwardFeedPicViewHolder(mInflater.inflate(R.layout.forward_pic_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_VIDEO -> viewHolder = ForwardFeedVideoViewHolder(mInflater.inflate(R.layout.forward_video_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_DELETE -> viewHolder = ForwardFeedDeleteViewHolder(mInflater.inflate(R.layout.forward_delete_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_VOTE_PIC -> viewHolder = ForwardFeedVotePicViewHolder(mInflater.inflate(R.layout.forward_vote_pic_feed_item, parent, false), this, this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_VOTE_TEXT -> viewHolder = ForwardFeedVoteTextViewHolder(mInflater.inflate(R.layout.forward_vote_text_feed_item, parent, false), this, this)

            FeedViewHolderHelper.FEED_CARD_TYPE_VOTE_TEXT -> viewHolder = VoteTextFeedViewHolder(mInflater.inflate(R.layout.vote_feed_text_layout, parent, false), this, this)

            FeedViewHolderHelper.FEED_CARD_TYPE_INTEREST -> viewHolder = FeedInterestViewHolder(mInflater.inflate(R.layout.item_feed_interest_layout, parent, false))

            FeedViewHolderHelper.FEED_CARD_TYPE_ALL_VIDEO -> viewHolder = VideoFeedSpecialViewHolder(mInflater.inflate(R.layout.video_feed_special_layout, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_ART -> viewHolder = ArtTextFeedViewHolder(mInflater.inflate(R.layout.art_text_feed_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_FORWARD_ART -> viewHolder = ForwordTextArtViewHolder(mInflater.inflate(R.layout.forward_art_text_item, parent, false), this)
            FeedViewHolderHelper.FEED_CARD_TYPE_GP_SCORE -> viewHolder = GPScoreViewHolder(mInflater.inflate(R.layout.item_feed_gp_score_layout, parent, false))
            FEED_CARD_TYPE_HEADER -> viewHolder = DiscoverStickyHeaderHolder(mInflater.inflate(R.layout.item_discover_header, parent, false))
            FeedViewHolderHelper.FEED_CARD_TYPE_UNKNOWN -> viewHolder = TextFeedViewHolder(mInflater.inflate(R.layout.text_feed_item, parent, false), this)
            else -> viewHolder = TextFeedViewHolder(mInflater.inflate(R.layout.text_feed_item, parent, false), this)
        }
        return viewHolder
    }

    override fun onBindItemViewHolder(holder: BaseRecyclerViewHolder<*>, position: Int) {

        super.onBindItemViewHolder(holder, position)
        val marginLayoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.topMargin = 0
        holder.itemView.layoutParams = marginLayoutParams

        if (holder is BaseFeedViewHolder) {
            holder.dismissDivider(position == mPostBaseList.size - 1)
            iBrowseClickListener?.let { holder.setBrowseClickListener(it) }
//            holder.setBrowseClickListener(iBrowseClickListener)
        } else if (holder is VideoFeedSpecialViewHolder) {
            iBrowseClickListener?.let { holder.setBrowseClickListener(it) }
//            holder.setBrowseClickListener(iBrowseClickListener)
        } else if (holder is GPScoreViewHolder) {
            holder.bindViews(this, mPostBaseList[position])
            holder.setChangeListener(object : GPScoreViewHolder.GPDataChangeListener {
                override fun dataChanged(data: GPScorePost) {
                    (mPostBaseList[position] as GPScorePost).currentType = data.currentType
                    (mPostBaseList[position] as GPScorePost).currentSelect = data.currentSelect
                    notifyItemChanged(holder.getAdapterPosition())
                }

                override fun dissmiss() {
                    removeScoreItem(position)
                }
            })
        } else if (holder is DiscoverStickyHeaderHolder) {
            holder.bindViews(this, mPostBaseList[position])
            holder.itemView.setOnClickListener { headerClick(mPostBaseList[position]) }
        }
        if (holder is BaseFeedViewHolder) {
            holder.showHotFlog(false)
            holder.showTopFlog(false)
        }
    }

    override fun getRealItemViewType(position: Int): Int {
        val postBase = mPostBaseList[position]
        return if (postBase.type == DiscoverHeader.TYPE) {
            FEED_CARD_TYPE_HEADER
        } else FeedViewHolderHelper.getFeedViewHolderType(postBase)
    }

    fun addHisData(feeds: ArrayList<PostBase>) {
        if (CollectionUtil.isEmpty(feeds)) {
            return
        }
        filterForBackground(feeds)
        mPostBaseList.addAll(feeds)
        notifyDataSetChanged()
    }

    fun addNewData(feeds: ArrayList<PostBase>) {
        mPostBaseList.clear()
        if (!CollectionUtil.isEmpty(feeds)) {
            filterForBackground(feeds)
            mPostBaseList.addAll(feeds)
        }

        if (interestPost != null) {
            mPostBaseList.add(if (mPostBaseList.size > 0) 1 else 0, interestPost)
        }
        notifyDataSetChanged()
    }

    fun addData(feeds: List<PostBase>) {
        //should use diff util
        if (mPostBaseList.isEmpty()) {
            if (interestPost != null) {
                mPostBaseList.add(interestPost)
            }
        }
        mPostBaseList.addAll(feeds)
        notifyDataSetChanged()
    }

    override fun addScoreData(position: Int) {
        if (gpScorePost == null)
            gpScorePost = GPScorePost()
        mPostBaseList.add(position, gpScorePost)
        notifyItemInserted(position)

    }

    private fun removeScoreItem(position: Int) {
        mPostBaseList.removeAt(position)
        notifyItemRemoved(position)
        gpScorePost = null
        val last = getAdapterItemPosition(mPostBaseList.size - 1)
        this.notifyItemRangeChanged(position, last)
    }


    override fun destroy() {
        destroyVideo()
        ShareCountReportManager.getInstance().unregister(this)
    }

    override fun onMenuBtnClick(context: Context, postBase: PostBase) {
        if (iBrowseClickListener == null) {
            super.onMenuBtnClick(context, postBase)
        }

    }


    fun setBrowseClickListener(iBrowseClickListener: IBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        for (x in itemPosition downTo 0) {
            if (isHeader(x)) {
                return x
            }
        }
        return -1
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.item_discover_header
    }

    override fun getHeaderView(headerPosition: Int, parent: RecyclerView): View {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discover_header, parent, false)
        bindHeaderData(view, headerPosition)
        return view
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        if (mPostBaseList.size < 1) {
            return
        }

        val title: TextView = header.findViewById(R.id.discover_header_title)
        val image: SimpleDraweeView = header.findViewById(R.id.discover_header_image)
//        val image2: ImageView = header.findViewById(R.id.discover_header_image2)
        val desc: TextView = header.findViewById(R.id.discover_header_desc)
        mPostBaseList[headerPosition].let { bean ->
            if (bean is DiscoverHeader) {
                header.tag = bean
                title.text = bean.topicName.trimStart("#"[0])
                desc.text = bean.rmdWords
                header.setBackgroundResource(bean.getBackground())
                header.setOnClickListener { headerClick(bean) }
                if (bean.iconUrl.isNullOrEmpty()) {
                    image.visibility = View.INVISIBLE
                } else {
                    image.setImageURI(bean.iconUrl)
                    image.visibility = View.VISIBLE

                }
//                if (it.iconUrl.isNullOrEmpty()) {
//                    image2.visibility = View.INVISIBLE
//                } else {
//                    image2.visibility = View.VISIBLE
////                    val subscriber = object : BaseBitmapDataSubscriber() {
////                        override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
////
////                        }
////
////                        override fun onNewResultImpl(bitmap: Bitmap?) {
////                            image2.setImageBitmap(bitmap)
////                        }
////                    }
//                    image.setImageURI(it.iconUrl)
//                    Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(it.iconUrl), "")
//                            .subscribe(subscriber, UiThreadImmediateExecutorService.getInstance())
//                }
            }
        }
    }

//    fun bindHeaderData2(header: View, headerPosition: Int) {
//        if (mPostBaseList.size < 1) {
//            return
//        }
//
//        val title: TextView = header.findViewById(R.id.discover_header_title)
//        val image: SimpleDraweeView = header.findViewById(R.id.discover_header_image)
////        val image2: ImageView = header.findViewById(R.id.discover_header_image2)
//        val desc: TextView = header.findViewById(R.id.discover_header_desc)
//        mPostBaseList[headerPosition].let {
//            if (it is DiscoverHeader) {
//                header.tag = it
//                title.text = it.topicName.trimStart("#"[0])
//                desc.text = it.rmdWords
//                image.visibility = View.GONE
//                header.setBackgroundResource(it.getBackground())
//                if (it.iconUrl.isNullOrEmpty()) {
//                    image2.visibility = View.INVISIBLE
//                } else {
//                    image2.visibility = View.VISIBLE
//                    val subscriber = object : BaseBitmapDataSubscriber() {
//                        override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
//
//                        }
//
//                        override fun onNewResultImpl(bitmap: Bitmap?) {
//                            image2.setImageBitmap(bitmap)
//                        }
//                    }
////                    magicView?.setImageURI(it.iconUrl)
//                    Fresco.getImagePipeline().fetchDecodedImage(ImageRequest.fromUri(it.iconUrl), "")
//                            .subscribe(subscriber, UiThreadImmediateExecutorService.getInstance())
//                }
//            }
//        }
//    }

    override fun isHeader(itemPosition: Int): Boolean {
        if (mPostBaseList.size <= itemPosition) {
            return false
        }

        return mPostBaseList[itemPosition].type == DiscoverHeader.TYPE
    }


    fun filterForBackground(feeds: ArrayList<PostBase>) {
        val total = 4
        var flag = 1
        feeds.forEach {
            if (it is DiscoverHeader) {
                it.backgroudAt = flag
                flag++
                if (flag > total) {
                    flag = 1
                }
            }
        }
    }

    override fun onUserVotePosts(result: Any?, errCode: Int) {
        if (result != null && result is PollInfo) {
            try {
                refreshOnVote(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onTextVote(pid: String, pollId: String, choiceIds: java.util.ArrayList<PollItemInfo>, isRepost: Boolean) {
        UserVoteManager.getInstance().tryVote(pid, pollId, choiceIds, isRepost)

    }

    private fun refreshOnVote(pollInfo: PollInfo) {
        var hasVoteChanged = false
        for (postBase in mPostBaseList) {

            if (postBase is PollPost) {
                if (TextUtils.equals(postBase.mPollInfo.pollId, pollInfo.pollId)) {
                    hasVoteChanged = true
                    postBase.mPollInfo = pollInfo
                }
            }
            if (postBase is ForwardPost && !postBase.isForwardDeleted && postBase.rootPost is PollPost) {
                if (TextUtils.equals((postBase.rootPost as PollPost).mPollInfo.pollId, pollInfo.pollId)) {
                    hasVoteChanged = true
                    (postBase.rootPost as PollPost).mPollInfo = pollInfo
                }
            }
        }
        if (hasVoteChanged) {
            notifyDataSetChanged()
        }
    }

    private lateinit var magicHeader: View

    fun showStickHeader(_magicHeader: View, recyclerView: RecyclerView) {
        canShowStickHeader = true
        magicHeader = _magicHeader
        magicHeader.visibility = View.INVISIBLE
        recyclerView.addOnScrollListener(scrollListener)

    }

    fun resetHeader() {
        if (canShowStickHeader) {
            magicHeader.visibility = View.VISIBLE
            mTopChildPosition = RecyclerView.NO_POSITION
            mHeaderPos = -1
        }
    }

    var mHeaderPos = -1
    var mTopChildPosition = RecyclerView.NO_POSITION

    val scrollListener = object : RecyclerView.OnScrollListener() {


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val topChild = recyclerView.getChildAt(0) ?: return
            val transInfoView = recyclerView.findChildViewUnder((magicHeader.measuredWidth / 2).toFloat(), (magicHeader.measuredHeight + 1).toFloat())
                    ?: return
            val topChildPosition = recyclerView.getChildAdapterPosition(topChild) //最顶部 item Position
            if (topChildPosition == RecyclerView.NO_POSITION) {
                return
            }
            //set header by firstChild.

            if (mTopChildPosition != topChildPosition) {
                mTopChildPosition = topChildPosition

                Log.w("DDAI", "mTopChildPosition >> $topChildPosition")
                //获取 header position (mTopChildPosition 的上面 一个 header position)
                val headerPos = getHeaderPositionForItem(mTopChildPosition)
                if (headerPos == -1) {
                    mTopChildPosition = -1
                    return
                }

                if (mHeaderPos != headerPos) { //header 替换了，绘制新的header
                    mHeaderPos = headerPos
                    Log.w("DDAI", "headerPos >> $headerPos")
                    bindHeaderData(magicHeader, headerPos)
                }
            }


            // if transInfoView is headerview
            val holder = recyclerView.getChildViewHolder(transInfoView)
            if (holder is DiscoverStickyHeaderHolder) {
                if (transInfoView.top > 0) {
                    val deltaY = transInfoView.top - magicHeader.measuredHeight
                    magicHeader.translationY = deltaY.toFloat()
                } else {
                    magicHeader.translationY = 0f
                }
            } else {
                magicHeader.translationY = 0f
            }
        }

    }
}

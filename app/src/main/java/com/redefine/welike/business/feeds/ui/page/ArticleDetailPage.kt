package com.redefine.welike.business.feeds.ui.page

import android.graphics.Typeface
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.fresco.loader.HeadUrlLoader
import com.redefine.commonui.fresco.loader.VideoCoverUrlLoader
import com.redefine.commonui.share.SharePackageFactory
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.MessageIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.common.LikeManager
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants
import com.redefine.welike.business.feeds.management.SinglePostManager
import com.redefine.welike.business.feeds.management.bean.ArticleInfo
import com.redefine.welike.business.feeds.management.bean.Comment
import com.redefine.welike.business.feeds.management.bean.ForwardPost
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.feeds.ui.fragment.FeedDetailPageSwitcher
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate
import com.redefine.welike.business.feeds.ui.util.FeedHelper
import com.redefine.welike.business.feeds.ui.util.OnSuperLikeExpCallback
import com.redefine.welike.business.feeds.ui.util.PostRichItemClickHandler
import com.redefine.welike.business.feeds.ui.util.SuperLikeDetailHelper
import com.redefine.welike.business.publisher.ui.activity.PublishCommentStarter
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter
import com.redefine.welike.business.user.management.bean.User
import com.redefine.welike.business.user.ui.page.UserHostPage
import com.redefine.welike.common.NumberUtils
import com.redefine.welike.common.util.DateTimeUtil
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom
import com.redefine.welike.commonui.event.helper.ShareEventHelper
import com.redefine.welike.commonui.event.model.EventModel
import com.redefine.welike.commonui.framework.PageStackManager
import com.redefine.welike.commonui.photoselector.PhotoSelector
import com.redefine.welike.commonui.share.ShareHelper
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.manager.NewShareEventManager
import com.redefine.welike.statistical.manager.PostEventManager
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.article_detail.*
import kotlinx.android.synthetic.main.article_detail_content.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * Name: ArticleDetailPage
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-28 14:15
 *
 */
@Route(path = RouteConstant.PATH_ARTICLE_DETAIL)
class ArticleDetailPage : BaseActivity(), IRefreshDelegate, OnSuperLikeExpCallback {

    private lateinit var mArticle: ArticleInfo
    private lateinit var mPostBase: PostBase
    private lateinit var mFeedDetailPageSwitch: FeedDetailPageSwitcher
    private lateinit var superLikeHelper: SuperLikeDetailHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_detail)
        EventBus.getDefault().register(this)

        mPostBase = intent.extras.getSerializable(FeedConstant.KEY_FEED) as PostBase
        if (mPostBase.articleInfo == null) finish()
        mArticle = mPostBase.articleInfo

        mFeedDetailPageSwitch = FeedDetailPageSwitcher(PageStackManager(this@ArticleDetailPage), mPostBase, this)
        mFeedDetailPageSwitch.setArticle(true)
        common_back_btn.setOnClickListener {
            finish()
        }
        head_layout.setOnClickListener {
            UserHostPage.launch(true, mArticle.user?.uid)
        }
        superLikeHelper = SuperLikeDetailHelper()
        feed_detail_refresh_layout.isEnableLoadMore = false
        feed_detail_refresh_layout.isEnableOverScrollBounce = false
        feed_detail_refresh_layout.setOnRefreshListener(OnRefreshListener { mFeedDetailPageSwitch.startRefresh() })

        if (mArticle.user != null) {
            HeadUrlLoader.getInstance().loadHeaderUrl2(title_user_header, mArticle.user.headUrl)
            common_feed_name.text = mArticle.user.nickName
        }

        common_more_btn.visibility = View.INVISIBLE

        layoutInflater.inflate(R.layout.article_detail_content, feed_detail_top_view, true)

        article_title.text = mArticle.title

        article_time.text = DateTimeUtil.formatArticleDateTime(mArticle.created)

        article_view_count.text = resources.getString(R.string.views) + NumberUtils.getBrowseNum(mPostBase.readCount)

        article_content.richProcessor.setOnRichItemClickListener(PostRichItemClickHandler(this@ArticleDetailPage, mPostBase))

        article_content.richProcessor.setRichContent(mArticle.content, mArticle.richItemList)

        setSelectIndex(FeedConstant.PAGE_COMMENT_INDEX)

        article_share_text.text = "share_to".translate(ResourceTool.ResourceFileEnum.PUBLISH)
        article_bottom_share.text = "share_to".translate(ResourceTool.ResourceFileEnum.PUBLISH)

        feed_detail_bottom_comment.text = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_detail_input_placeholder")
        feed_detail_head_tab_forward_layout.setOnClickListener {
            setSelectIndex(FeedConstant.PAGE_FORWARD_INDEX)
        }

        feed_detail_head_tab_comment_layout.setOnClickListener {
            setSelectIndex(FeedConstant.PAGE_COMMENT_INDEX)
        }

        feed_detail_head_tab_like_layout.setOnClickListener {
            setSelectIndex(FeedConstant.PAGE_LIKE_INDEX)
        }

        detail_quick_share.setOnClickListener {
            doShare(mPostBase)
        }

        feed_detail_bottom_forward.setOnClickListener {
            EventLog1.FeedForment.report2(ShareEventHelper.convertPostType(mPostBase), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, mPostBase.pid,
                    mPostBase.strategy, mPostBase.operationType, mPostBase.language, mPostBase.tags, null, null, FeedHelper.getRootOrPostUid(mPostBase), mPostBase.sequenceId,mPostBase.reclogs)
            PublishForwardStarter.startActivity4PostFromArticle(this@ArticleDetailPage, mPostBase)
        }

        feed_detail_bottom_like.setOnClickListener {
            //            superLikeHelper.bindView(mPageStackManager, feed_detail_bottom_like, mPostBase, this)
            if (!mPostBase.isLike) {
                onSuperLikeExpCallback(true, mPostBase.superLikeExp.toInt() + 1)
            }
        }

        feed_detail_bottom_comment.setOnClickListener {
            EventLog1.FeedForment.report1(ShareEventHelper.convertPostType(mPostBase), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, mPostBase.pid,
                    mPostBase.strategy, mPostBase.operationType, mPostBase.language, mPostBase.tags, null, null, FeedHelper.getRootOrPostUid(mPostBase), mPostBase.sequenceId,mPostBase.reclogs)
            PublishCommentStarter.startActivityFromArticleDetail(this@ArticleDetailPage, mPostBase)
        }

        article_whats_app_share.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.WHATS_APP)
        }

        article_bottom_whats_app_share_layout.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.WHATS_APP)
        }

        article_facebook_share.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.FACEBOOK)
        }

        article_bottom_facebook_share_layout.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.FACEBOOK)
        }

        article_instagram_share.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.INSTAGTRAM)
        }

        article_bottom_instagram_share_layout.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.INSTAGTRAM)
        }

        article_copy_share.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.COPY)
        }

        article_bottom_copy_share_layout.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.COPY)
        }

        article_more_share.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.SYSYTEM)
        }

        article_bottom_more_share_layout.setOnClickListener {
            shareToTarget(SharePackageFactory.SharePackage.SYSYTEM)
        }

        whats_app_text.text = SharePackageFactory.getName(SharePackageFactory.SharePackage.WHATS_APP)
        face_book_text.text = SharePackageFactory.getName(SharePackageFactory.SharePackage.FACEBOOK)
        instagram_text.text = SharePackageFactory.getName(SharePackageFactory.SharePackage.INSTAGTRAM)
        copy_text.text = SharePackageFactory.getName(SharePackageFactory.SharePackage.COPY)
        more_text.text = SharePackageFactory.getName(SharePackageFactory.SharePackage.SYSYTEM)

        if (mArticle.videoInfo != null && !TextUtils.isEmpty(mArticle.videoInfo.videoUrl)) {
            article_video_container.visibility = View.VISIBLE
            VideoCoverUrlLoader.getInstance().loadVideoThumbUrl(article_video_container, article_video_thumb
                    , mArticle.videoInfo.coverUrl
                    , mArticle.videoInfo.width
                    , mArticle.videoInfo.height, 0)
            article_video_thumb.setOnClickListener {
                PhotoSelector.launchPlayPostVideo(this@ArticleDetailPage, mArticle.videoInfo.videoUrl, mArticle.videoInfo.videoSite)
            }
        }

        updateFeedCount()
    }

    private var mIndex: Int = FeedConstant.PAGE_COMMENT_INDEX

    private fun setSelectIndex(index: Int) {
        feed_detail_head_tab_forward_layout.isSelected = index == FeedConstant.PAGE_FORWARD_INDEX
        feed_detail_head_tab_comment_layout.isSelected = index == FeedConstant.PAGE_COMMENT_INDEX
        feed_detail_head_tab_like_layout.isSelected = index == FeedConstant.PAGE_LIKE_INDEX
        changeTextViewBold(feed_detail_head_tab_forward, index == FeedConstant.PAGE_FORWARD_INDEX)
        changeTextViewBold(feed_detail_head_tab_comment, index == FeedConstant.PAGE_COMMENT_INDEX)
        changeTextViewBold(feed_detail_head_tab_like, index == FeedConstant.PAGE_LIKE_INDEX)

        mIndex = index
        mFeedDetailPageSwitch.setCurrentItem(feed_detail_view_container, mIndex)
    }

    private fun doReport(post: PostBase) {
        val bundle = Bundle()
        bundle.putSerializable(FeedbackConstants.FEEDBACK_KEY_POST, post)
        EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_REPOER_PAGE, bundle))
    }


    private fun doShare(post: PostBase?) {
        //        ShareModel shareModel = new ShareModel();
        //        shareModel.setH5Link("http://s.welike.in/share/index.html");
        //        shareModel.setTitle(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_feed_detail_share_content"));
//        TrackerUtil.getEventTracker().send(HitBuilders.EventBuilder()
//                .setCategory(TrackerConstant.EVENT_EV_CT)
//                .setAction(TrackerConstant.EVENT_SHARE_SOURCE)
//                .setLabel("postDetail").build())
        if (post == null) {
            ToastUtils.showLong(ErrorCode.showErrCodeText(ErrorCode.ERROR_NETWORK_INVALID))
            return
        }
        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_ARTICAL)
        val eventModel = EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                null, EventLog1.Share.ShareFrom.ARTICLE, EventLog1.Share.PopPage.POST_DETAIL, mPostBase.strategy, mPostBase.operationType, null, mPostBase.pid, mPostBase.language, mPostBase.tags, mPostBase.uid,
                null, null, null, null, null, mPostBase.reclogs)
        ShareHelper.sharePost(this, mPostBase, false, eventModel)
    }

    private fun shareToTarget(sharePackage: SharePackageFactory.SharePackage) {
        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_ARTICAL)

        val eventModel = EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                null, EventLog1.Share.ShareFrom.ARTICLE, EventLog1.Share.PopPage.POST_DETAIL, mPostBase.strategy, mPostBase.operationType, null, mPostBase.pid, mPostBase.language, mPostBase.tags, mPostBase.uid,
                null, null, null, null, null,mPostBase.reclogs)
        ShareHelper.sharePostToChannel(this, mPostBase, false, sharePackage, eventModel)
    }

    /**
     * 设置字体是否为粗体
     */
    private fun changeTextViewBold(textView: TextView, isNeedBold: Boolean) {

        if (isNeedBold)
            textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        else
            textView.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }

    private fun updateFeedCount() {
        updateLikeCount(mPostBase.superLikeExp.toInt())
        updateForwardCount()
        updateCommentCount()
    }

    private fun updateForwardCount() {
        val forwardCount = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "repost_count")
        if (mPostBase.forwardCount > 0) {
            val count = FeedHelper.getForwardCount(mPostBase.forwardCount.toLong())
            feed_detail_head_tab_forward.text = String.format(forwardCount, count)
        } else {
            feed_detail_head_tab_forward.text = String.format(forwardCount, "")
        }
    }

    private fun updateLikeCount(exp: Int) {
        val drawable = ResourceTool.getBoundDrawable(resources, LikeManager.getFeedImage(exp), ScreenUtils.dip2Px(24f), ScreenUtils.dip2Px(24f))
        feed_detail_bottom_like.setImageDrawable(drawable)

        val likeCount = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "like_count")
        if (mPostBase.likeCount > 0) {
            val count = FeedHelper.getLikeCount(mPostBase.likeCount)
            feed_detail_head_tab_like.text = String.format(likeCount, count)
        } else {
            feed_detail_head_tab_like.text = String.format(likeCount, "")
        }
    }

    private fun updateCommentCount() {
        val commentCount = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_count")
        if (mPostBase.commentCount > 0) {
            val count = FeedHelper.getCommentCount(mPostBase.commentCount.toLong())
            feed_detail_head_tab_comment.text = String.format(commentCount, count)
        } else {
            feed_detail_head_tab_comment.text = String.format(commentCount, "")
        }
    }

    override fun startRefresh() {
        mFeedDetailPageSwitch.startRefresh()
    }

    override fun stopRefresh() {
        if (feed_detail_refresh_layout != null) {
            feed_detail_refresh_layout.finishRefresh()
        }
    }

    override fun setRefreshEnable(isEnable: Boolean) {
        feed_detail_refresh_layout.isEnableRefresh = isEnable
    }

    override fun onSuperLikeExpCallback(isLast: Boolean, exp: Int) {
        if (isLast) {
            EventLog.Feed.report5(mPostBase.pid, mPostBase.superLikeExp, (if (exp > 99) 100 else exp).toLong(), 15, PostEventManager.getPostType(mPostBase), mPostBase.strategy, mPostBase.sequenceId)
            SinglePostManager.superLike(mPostBase, exp.toLong(), false)
            updateLikeCount(mPostBase.superLikeExp.toInt())

            mFeedDetailPageSwitch.onNewLike(User.convertFromAccount(AccountManager.getInstance().account!!), mPostBase.superLikeExp)
        } else {
            updateLikeCount(exp)
        }
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_LIKE)
        EventLog1.FeedLike.report1(ShareEventHelper.convertPostType(mPostBase), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, mPostBase.pid, mPostBase.uid, mPostBase.strategy,
                mPostBase.operationType, mPostBase.language, mPostBase.tags, FeedHelper.getRootOrPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.sequenceId,mPostBase.reclogs)
    }

    override fun onDestroy() {
        mFeedDetailPageSwitch.destroy(feed_detail_view_container)
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessage(message: Message?) {
        if (message == null) {
            return
        }

        if (message.what == MessageIdConstant.MESSAGE_COMMENT_PUBLISH) {
            val comment = message.obj as Comment
            if (!TextUtils.equals(comment.pid, mPostBase.pid)) {
                return
            }
            mFeedDetailPageSwitch.onNewComment(comment)
            mPostBase.commentCount = mPostBase.commentCount + 1
            updateCommentCount()
        } else if (message.what == MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH) {
            val postBase = message.obj as PostBase
            if (TextUtils.isEmpty(mPostBase.pid) || postBase !is ForwardPost) {
                return
            }
            // 其实无法知道此post是转发的我
            mFeedDetailPageSwitch.onNewForward(postBase)
            mPostBase.forwardCount = mPostBase.forwardCount + 1
            updateForwardCount()
        }
    }

}
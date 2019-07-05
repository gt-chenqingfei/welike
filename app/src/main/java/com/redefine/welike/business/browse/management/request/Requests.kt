package com.redefine.welike.business.browse.management.request

import android.Manifest
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.redefine.foundation.http.BaseHttpReq
import com.redefine.foundation.utils.ChannelHelper
import com.redefine.foundation.utils.CommonHelper
import com.redefine.im.threadTry
import com.redefine.welike.MyApplication
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.LanguageSupportManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.feedback.management.bean.ReportModel
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean
import com.redefine.welike.statistical.utils.GoogleUtil
import org.json.JSONArray
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions

class ForwardedPostsRequest(pid: String, cursor: String?) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, MyApplication.getAppContext()) {

    init {
        setHost("feed/post/$pid/forwarded-posts", true)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor?.let {
            setParam("cursor", it)
        }
        putUserInfo("fid", pid)
    }
}

class ForwardedPostsRequest2(pid: String, cursor: String?) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, MyApplication.getAppContext()) {

    init {
        setHost("feed/post/h5/$pid/forwarded-posts", false)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor?.let {
            setParam("cursor", it)
        }
        putUserInfo("fid", pid)
    }
}

class FeedsRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {

    @Throws(Exception::class)
    fun tryFeeds(cursor: String?, interes: ArrayList<String>, callback: RequestCallback) {
        threadTry {
            setHost("leaderboard/skip/rising", false)
            setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
            val interests = ArrayList<String>()
            if (interes.isNotEmpty()) {
                interests.addAll(interes)
            }
            val tags = ChannelHelper.getTags(MyApplication.getAppContext())
            if (tags.size > 0) {
                interests.add("\"" + tags[0] + "\"")
            }
            setParam("interests", interests)
            setParam("userType", CommonHelper.getChannel(MyApplication.getAppContext()))
            setParam("gid", GoogleUtil.forceGAID())
            ChannelHelper.tagGroup?.let {
                setParam("retpid", it)
            }
            cursor?.let {
                setParam("cursor", it)
            }
            req(callback)
        }
    }
}

class DiscoverSkipTopicsRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {
    @Throws(Exception::class)
    fun tryFeeds(status: Int, page: Int, callback: RequestCallback) {
        threadTry {
            setHost("conplay/topic/hat/fe/skip/page", false)
            setParam("status", status)
            setParam("pageNum", page)
            setParam("pageSize", GlobalConfig.DISCOVER_TOPICS)
            req(callback)
        }
    }
}

class DiscoverTopicsRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, true) {
    @Throws(Exception::class)
    fun tryFeeds(status: Int, page: Int, callback: RequestCallback) {
        threadTry {
            setHost("conplay/topic/hat/fe/page", true)
            setParam("status", status)
            setParam("pageNum", page)
            setParam("pageSize", GlobalConfig.DISCOVER_TOPICS)
            req(callback)
        }
    }
}

class Hot24hFeedRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {

    @Throws(Exception::class)
    fun request(cursor: String?, interes: ArrayList<String>, callback: RequestCallback) {
        threadTry {
            setHost("leaderboard/post/h5/24h", false)
            setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
            var interests = ArrayList<String>()

            if (interes != null && interes.isNotEmpty()) {
                interests.addAll(interes)
            }
            val tags = ChannelHelper.getTags(MyApplication.getAppContext())
            if (tags.size > 0) {
                interests.add("\"" + tags[0] + "\"")
            }
            setParam("interests", interests)
            setParam("userType", CommonHelper.getChannel(context))
            setParam("gid", GoogleUtil.forceGAID())
            ChannelHelper.tagGroup?.let {
                setParam("retpid", it)
            }
            cursor?.let {
                setParam("cursor", it)
            }
            req(callback)
        }
    }

}

class Interest24hFeedRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {

    @Throws(Exception::class)
    fun request(cursor: String?, id: String, callback: RequestCallback) {
        threadTry {
            setHost("leaderboard/skip/interest/24h", false)
            setParam("gid", GoogleUtil.forceGAID())
            setParam("interestId", id)
            setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
            cursor?.let {
                setParam("cursor", it)
            }
            req(callback)
        }
    }

}

class CommentsRequest(pid: String, cursor: String?, sortType: FeedDetailCommentHeadBean.CommentSortType, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/post/$pid/comments", true)
//            setHost("feed/post/h5/$pid/comments", false)
        setParam("count", GlobalConfig.COMMENTS_NUM_ONE_PAGE)
        if (sortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            setParam("order", "hot")
        } else {
            setParam("order", "created")
        }
        cursor?.let {
            setParam("cursor", it)
        }
        putUserInfo("pid", pid)
    }
}

class CommentsRequest2(pid: String, cursor: String?, sortType: FeedDetailCommentHeadBean.CommentSortType, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/post/h5/$pid/comments", false)
        setParam("count", GlobalConfig.COMMENTS_NUM_ONE_PAGE)
        if (sortType == FeedDetailCommentHeadBean.CommentSortType.HOT) {
            setParam("order", "hot")
        } else {
            setParam("order", "created")
        }
        cursor?.let {
            setParam("cursor", it)
        }
        putUserInfo("pid", pid)
    }
}

class UserPostsRequest(uid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/user/$uid/posts", true)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        putUserInfo("uid", uid)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class UserPostsRequest2(uid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/user/h5/$uid/posts", false)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        putUserInfo("uid", uid)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class PostLikeUsersRequest(fid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/post/$fid/liked-users", true)
        setParam("count", GlobalConfig.USERS_NUM_ONE_PAGE)
        putUserInfo("fid", fid)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class PostLikeUsersRequest2(fid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/post/h5/$fid/liked-users", false)
        setParam("count", GlobalConfig.USERS_NUM_ONE_PAGE)
        putUserInfo("fid", fid)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class UserLikePostsRequest(uid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/user/$uid/like-posts", true)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        putUserInfo("uid", uid)
        cursor.let {
            setParam("cursor", it)
        }
    }
}

class UserLikePostsRequest2(uid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/skip/user/$uid/like-posts", false)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        putUserInfo("uid", uid)
        cursor.let {
            setParam("cursor", it)
        }
    }
}

class TopicInfoRequest(topicId: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("conplay/topic/$topicId/detail", true)
    }
}

class TopicInfoRequest2(topicId: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("conplay/topic/h5/$topicId/detail", false)
    }
}

class TopicHotFeedRequest(tid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("leaderboard/topic/24h", true)
        setParam("order", "created")
        setParam("topicId", tid)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class TopicHotFeedRequest2(tid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("leaderboard/topic/h5/24h", false)
        setParam("order", "created")
        setParam("topicId", tid)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class TopicFeedsRequest(tid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/topic/$tid/posts", true)
        setParam("order", "created")
        setParam("topicId", tid)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class TopicFeedsRequest2(tid: String, cursor: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/topic/h5/$tid/posts", false)
        setParam("order", "created")
        setParam("topicId", tid)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor?.let {
            setParam("cursor", it)
        }
    }
}

class TopicRelatedUserRequest(tid: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/topic/$tid/users", true)
        setParam("order", "created")
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
    }
}

class TopicRelatedUserRequest2(tid: String?, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("feed/topic/h5/$tid/users", false)
        setParam("order", "created")
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
    }
}

class UserDetailRequest(uid: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("user/detail/id/$uid", true)
    }
}

class UserDetailRequest2(uid: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("user/detail/id/$uid", false)
//        setParam("source", "web")
    }
}

class DeleteToken(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("push/token/del", true)
//        setParam("source", "web")
    }


}

class InterestRequest(context: Context, referrerId: String?) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("user/interest/list", true)
        referrerId.let {
            setParam("referrerId", referrerId)
        }

    }
}


class InterestRequest2(context: Context, referrerId: String?) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {

    init {
        setHost("user/interest/skip/list", false)
        setParam("referrerId", referrerId)
    }
}

class RefreshPostCacheRequest(context: Context, uid: String?) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {
    init {
        setHost("user/skip/interest/update", false)
        uid.let {
            setParam("userId", uid)
        }
//
    }
}


class RefreshPostCacheWhenLanguageRequest(context: Context, uid: String?) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {
    init {
        setHost("user/skip/lac/update", false)
//        uid.let {
//            setParam("userId", uid)
//        }
    }
}

class RecommendGroupRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("user/recommend/group", true)
    }
}

class RecommendUserRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("user/recommend", true)
    }

    @Throws(Exception::class)
    fun request(pageNum: Int, callback: RequestCallback) {
        setParam("pageSize", 30)
        setParam("pageNum", pageNum)
        req(callback)
    }
}


class RecommendSlideRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("recommend/horizon-slide", true)
    }

    @Throws(Exception::class)
    fun request(pageNum: Int, cursor: String?, callback: RequestCallback) {
        setParam("pageSize", 30)
        setParam("pageNum", pageNum)
        setParam("allowContactFollow", EasyPermissions.hasPermissions(context, Manifest.permission.READ_CONTACTS))
        setParam("cursor", cursor)
        req(callback)
    }

}


class RecommendSlideCloseRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, true) {
    @Throws(Exception::class)
    fun request(uid: String) {

        setHost("recommend/horizon-slide-dislike", true)

        setUrlExtParam("recommendUserId", uid)

        req()

    }

}

class RecommendUserRequest2(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {

    init {
        setHost("recommend/full-screen", true)
    }

    @Throws(Exception::class)
    fun request(pageNum: Int, callback: RequestCallback) {
        setParam("pageSize", 30)
        setParam("pageNum", pageNum)
        req(callback)
    }
}

class SearchSugRequest(keyword: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/query/user_all", true)
        setParam("query", keyword)
        setParam("pageNumber", 0)
        setParam("count", GlobalConfig.SUG_SEARCH_ONE_PAGE_NUM)
    }
}

class SearchSugRequest1(keyword: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/skip/query/user_all", false)
        setParam("query", keyword)
        setParam("pageNumber", 0)
        setParam("count", GlobalConfig.SUG_SEARCH_ONE_PAGE_NUM)
    }
}

class SearchLatestRequest(keyword: String, pageNum: Int, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/top/content", true)
        setParam("query", keyword)
        setParam("pageNumber", pageNum)
        setParam("count", GlobalConfig.SEARCH_POSTS_NUMBER_ONE_PAGE)
    }
}

class SearchLatestRequest1(keyword: String, pageNum: Int, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/skip/top/content", false)
        setParam("query", keyword)
        setParam("pageNumber", pageNum)
        setParam("count", GlobalConfig.SEARCH_POSTS_NUMBER_ONE_PAGE)
    }
}

class SearchPostRequest(keyword: String, pageNum: Int, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/post/content", true)
        setParam("content", keyword)
        setParam("pageNumber", pageNum)
        setParam("count", GlobalConfig.SEARCH_POSTS_NUMBER_ONE_PAGE)
    }
}

class SearchPostRequest1(keyword: String, pageNum: Int, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/skip/post/content", false)
        setParam("content", keyword)
        setParam("pageNumber", pageNum)
        setParam("count", GlobalConfig.SEARCH_POSTS_NUMBER_ONE_PAGE)
    }
}

class SearchUserRequest(keyword: String, pageNum: Int, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/user/nickName", true)
        setParam("nickName", keyword)
        setParam("pageNumber", pageNum)
        setParam("count", GlobalConfig.SEARCH_USERS_NUMBER_ONE_PAGE)
    }
}

class SearchUserRequest1(keyword: String, pageNum: Int, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/skip/user/nickName", false)
        setParam("nickName", keyword)
        setParam("pageNumber", pageNum)
        setParam("count", GlobalConfig.SEARCH_USERS_NUMBER_ONE_PAGE)
    }
}

class SearchMovieRequest(keyword: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/movie/info", true)
        setParam("query", keyword)
        setParam("pageNumber", 0)
        setParam("count", 1)
    }
}

class SearchMovieRequest1(keyword: String, context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("search/movie/skip/info", true)
        setParam("query", keyword)
        setParam("pageNumber", 0)
        setParam("count", 1)
    }
}

class HotFeedTopicRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("conplay/topic/resident/topics", true)
    }
}

class HotFeedTopicRequest1(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("conplay/topic/resident/skip/topics/" + GlobalConfig.SEARCH_HOT_TOPIC_NUM, false)
    }
}

class HotwordRequest(context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("conplay/hotwards/skip/list", false)
    }
}

class HotFeedTopicRequest2(context: Context, count: Int) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("conplay/topic/resident/skip/topics/" + count, false)
    }
}

class VideoFeedRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {
    @Throws(Exception::class)
    fun request(cursor: String?, interes: ArrayList<String>, callback: RequestCallback) {
        setHost("leaderboard/skip/post/video/24h", false)
        setParam("pageSize", 10)
        cursor.let {
            setParam("cursor", cursor)
        }

        val interests = ArrayList<String>()

        if (interes.isNotEmpty()) {
            interests.addAll(interes)
        }
//        interests.addAll(ChannelHelper.getTags(context))
        val tags = ChannelHelper.getTags(MyApplication.getAppContext())
        if (tags.size > 0) {
            interests.add("\"" + tags[0] + "\"")
        }
        setParam("interests", interests)
        setParam("userType", CommonHelper.getChannel(context))
        setParam("postType", "video")
        setParam("language", LanguageSupportManager.getInstance().currentMenuLanguageType)
        setParam("gid", GoogleUtil.gaid)

        req(callback)
    }
}

class VideoFeedRequest2(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    @Throws(Exception::class)
    fun request(cursor: String?, interes: ArrayList<String>, callback: RequestCallback) {
        setHost("leaderboard/post/video/24h", true)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor.let {
            setParam("cursor", cursor)
        }

        val interests = ArrayList<String>()

        if (interes.isNotEmpty()) {
            interests.addAll(interes)
        }
        val tags = ChannelHelper.getTags(MyApplication.getAppContext())
        if (tags.size > 0) {
            interests.add("\"" + tags[0] + "\"")
        }
        setParam("interests", interests)
        setParam("userType", CommonHelper.getChannel(context))
        setParam("postType", "video")
        setParam("language", LanguageSupportManager.getInstance().currentMenuLanguageType)
        setParam("gid", GoogleUtil.gaid)

        req(callback)
    }
}


class RecommendCloseRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, true) {
    @Throws(Exception::class)
    fun request(uid: String) {

        setHost("tag/recommended/close", true)

        val js = Gson()

        setBodyData(js.toJson(uid.toLong()))

        req()

    }

}


class TrendingPeopleRequest1(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {
    @Throws(Exception::class)
    fun request(callback: RequestCallback) {
        setHost("discovery/skip/user/top", false)
        setParam("page", 0)
        setParam("pageSize", GlobalConfig.POSTS_NUM_ONE_PAGE)
        req(callback)

    }
}

class TrendingPeopleRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, true) {
    @Throws(Exception::class)
    fun request(callback: RequestCallback) {
        setHost("discovery/user/top", true)
        setParam("page", 0)
        setParam("pageSize", GlobalConfig.POSTS_NUM_ONE_PAGE)
        req(callback)

    }
}

class TrendingTopicRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {
    @Throws(Exception::class)
    fun request(cursor: String?, isPost: Boolean, callback: RequestCallback) {
        setHost("leaderboard/skip/topic/hot", false)
        cursor.let {
            setParam("cursor", cursor)
        }

        setParam("topPosts", isPost)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        req(callback)
    }
}

class LatestCampaignRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {
    @Throws(Exception::class)
    fun request(callback: RequestCallback) {
        setHost("conplay/topic/resident/skip/topics/2/30", false)
        req(callback)

    }
}


class TrendingSearchWordRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {

    @Throws(Exception::class)
    fun request(callback: RequestCallback) {
        setHost("conplay/hotwards/skip/4", false)
        req(callback)
    }

}

class SimilarVideoRequest(val context: Context, val cursor: String?, val postId: String?, val interes: ArrayList<String>) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("leaderboard/post/video/similar", true)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor.let {
            setParam("cursor", cursor)
        }
        postId.let {
            setParam("post", postId)
        }

        val interests = ArrayList<String>()
        if (interes.isNotEmpty()) {
            interests.addAll(interes)
        }
        val tags = ChannelHelper.getTags(MyApplication.getAppContext())
        if (tags.size > 0) {
            interests.add("\"" + tags[0] + "\"")
        }
        setParam("interests", interests)
        setParam("userType", CommonHelper.getChannel(context))
        setParam("language", LanguageSupportManager.getInstance().currentMenuLanguageType)
        setParam("gid", GoogleUtil.gaid)
    }

}

class SimilarVideoRequest1(val context: Context, val cursor: String?, val postId: String?, val interes: ArrayList<String>) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("leaderboard/skip/post/video/similar", false)
        setParam("count", GlobalConfig.POSTS_NUM_ONE_PAGE)
        cursor.let {
            setParam("cursor", cursor)
        }
        postId.let {
            setParam("post", postId)
        }

        val interests = ArrayList<String>()
        if (interes.isNotEmpty()) {
            interests.addAll(interes)
        }
        val tags = ChannelHelper.getTags(MyApplication.getAppContext())
        if (tags.size > 0) {
            interests.add("\"" + tags[0] + "\"")
        }
        setParam("interests", interests)
        setParam("userType", CommonHelper.getChannel(context))
        setParam("language", LanguageSupportManager.getInstance().currentMenuLanguageType)
        setParam("gid", GoogleUtil.gaid)
    }

}


class GPCloseRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, true) {
    @Throws(Exception::class)
    fun request() {

        setHost("user/gpscore/toast/hide", true)

        var jo = JSONObject()

        jo.put("hide", true)

        setBodyData(jo.toString())

        req()

    }
}

class GPStatusRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context, false) {
    @Throws(Exception::class)
    fun request(callback: RequestCallback) {

        setHost("user/gpscore/toast/status", false)

        req(callback)

    }
}

class GPScoreStatusRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {
    @Throws(Exception::class)
    fun request(score: Int) {

        setHost("user/gpscore/skip/add", false)

        var jo = JSONObject()

        jo.put("score", score)

        jo.put("comment", "")

        setBodyData(jo.toString())

        req()

    }
}

class BrowseLikeRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context, false) {
    @Throws(Exception::class)
    fun request(postId: String) {

        setHost("feed/skip/user/superlike/post/$postId/with/1", false)

        req()

    }
}

class ReportRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {
    init {
        AccountManager.getInstance().account?.let {
            setHost("report/report/" + it.uid + "/report", true)
        }
    }

    fun request(postId: String, postOwnerId: String, reason: CharSequence, callback: RequestCallback) {
        val bodyData = JSONObject()
        bodyData.put("reportId", postId)
        bodyData.put("postUserId", postOwnerId)
        bodyData.put("reason", reason)
        setBodyData(bodyData.toString())
        req(callback)
    }
}

class ReportRequest1(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {

    fun request(postId: String, postOwnerId: String, reason: CharSequence, callback: RequestCallback) {
        threadTry {
            setHost("report/skip/report/" + GoogleUtil.forceGAID() + "/report", true)
            val bodyData = JSONObject()
            bodyData.put("reportId", postId)
            bodyData.put("postUserId", postOwnerId)
            bodyData.put("reason", reason)
            setBodyData(bodyData.toString())
            req(callback)
        }
    }
}

class NewReportRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {
    init {
        AccountManager.getInstance().account?.let {
            setHost("report/report/" + it.uid + "/report", true)
        }
    }

    fun request(reportModel: ReportModel, callback: RequestCallback) {
        val bodyData = com.alibaba.fastjson.JSONObject()
        bodyData.put("description", reportModel.description)
        bodyData.put("images", reportModel.images)
        bodyData.put("reportId", reportModel.reportId)
        bodyData.put("userId", reportModel.userId)
        bodyData.put("postUserId", reportModel.postUserId)
        bodyData.put("reason", reportModel.id)
        setBodyData(bodyData.toString())
        req(callback)
    }
}

class NewReportRequest1(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_POST, context) {

    fun request(reportModel: ReportModel, callback: RequestCallback) {
        threadTry {
            setHost("report/skip/report/" + GoogleUtil.forceGAID() + "/report", true)
            val bodyData = com.alibaba.fastjson.JSONObject()
            bodyData.put("description", reportModel.description)
            bodyData.put("images", reportModel.images)
            bodyData.put("reportId", reportModel.reportId)
            bodyData.put("userId", GoogleUtil.forceGAID())
            bodyData.put("postUserId", reportModel.postUserId)
            bodyData.put("reason", reportModel.id)
            setBodyData(bodyData.toString())
            req(callback)
        }
    }
}

class ProfilePhotoRequest(val uid: String, val cursor: String?, val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("feed/user/" + uid + "/image-attachments", true)
        setParam("user", uid)
        setParam("count", GlobalConfig.PROFILE_PHOTO_NUMBER_ONE_PAGE)
        cursor.let { setParam("cursor", cursor) }
    }

}

class ProfilePhotoRequest1(val uid: String, val cursor: String?, val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("feed/skip/user/" + uid + "/image-attachments", false)
        setParam("user", uid)
        setParam("count", GlobalConfig.PROFILE_PHOTO_NUMBER_ONE_PAGE)
        cursor.let { setParam("cursor", cursor) }
    }

}

class ProfileVideoRequest(val uid: String, val cursor: String?, val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("feed/user/" + uid + "/video-attachments", true)
        setParam("user", uid)
        setParam("count", GlobalConfig.SEARCH_USERS_NUMBER_ONE_PAGE)
        cursor.let { setParam("cursor", cursor) }
    }

}

class ProfileVideoRequest1(val uid: String, var cursor: String?, val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("feed/skip/user/" + uid + "/video-attachments", false)
        setParam("user", uid)
        setParam("count", GlobalConfig.SEARCH_USERS_NUMBER_ONE_PAGE)
        cursor.let { setParam("cursor", cursor) }
    }

}


class RecommendFeedsRequest(val uid: String, var cursor: String?, val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("feed/user/" + uid + "/recommend/feeds", true)
        setParam("count", GlobalConfig.SEARCH_USERS_NUMBER_ONE_PAGE)
        cursor.let { setParam("cursor", cursor) }
    }

}

class DigitaNumRequest(val context: Context) : BaseRequest(BaseHttpReq.REQUEST_METHOD_GET, context) {
    init {
        setHost("recommend/user/digitalNum", true)
    }
}









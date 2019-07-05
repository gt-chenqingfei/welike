package com.redefine.welike.base.profile

import android.text.TextUtils
import com.redefine.welike.base.dao.welike.Profile
import com.redefine.welike.base.profile.bean.Account
import com.redefine.welike.base.profile.bean.UserBase
import org.json.JSONArray
import org.json.JSONObject

class AccountParserHelper {
    fun profileToAccount(profile: Profile?): Account? {

        profile?.let {
            val account = Account()
            account.uid = it.uid
            account.accessToken = it.atoken
            account.refreshToken = it.rtoken
            account.tokenType = it.ttype
            account.headUrl = it.head
            account.nickName = it.nick
            account.expired = java.lang.Long.parseLong(it.expired)
            account.introduction = it.introduction
            account.postsCount = it.postsCount
            account.followUsersCount = it.followingCount
            account.followedUsersCount = it.followerCount
            account.likedMyPostsCount = it.likedMyPostsCount
            account.myLikedPostsCount = it.myLikedPostsCount
            account.completeLevel = it.completeLevel
            account.isLogin = it.login
            account.vip = it.vip
            account.status = it.status

            if (!it.intrests.isNullOrEmpty()) {
                try {
                    //if it is JSONObject
                    val listJO = JSONObject(it.intrests)
                    listJO.optJSONObject("ex")?.let {
                        account.parserEx(it)
                    }
                    listJO.optJSONArray("interest")?.let {
                        account.intrests = parserInterests(it)
                    }
                } catch (e: Exception) {
                    try {
                        val listJA = JSONArray(it.intrests)
                        account.intrests = parserInterests(listJA)
                    } catch (e: Exception) {

                    }
                }
            }
            return account
        }
        return null
    }

    /**
     * set ex params:
     * int curLevel ,int changeNameCount ,List<Link> mLinks
     */
    private fun Account.parserEx(jo: JSONObject) {
        curLevel = jo.optInt("curLevel")
        changeNameCount = jo.optInt("changeNameCount")
        links = ArrayList<UserBase.Link>()
        val list = ArrayList<UserBase.Link>()
        val ja = jo.optJSONArray("links")
        for (x in 0..ja.length()) {
            ja.optJSONObject(x)?.let {
                list.add(parserLink(it))
            }
        }
    }

    private fun parserInterests(ja: JSONArray): List<UserBase.Intrest> {
        val list = ArrayList<UserBase.Intrest>()
        for (x in 0..ja.length()) {
            val jo = ja.optJSONObject(x)
            jo?.let {
                list.add(parserInterest(it))
            }
        }
        return list
    }

    private fun parserInterest(jo: JSONObject): UserBase.Intrest {
        return UserBase.Intrest().apply {
            iid = jo.optString("id")
            label = jo.optString("name")
            icon = jo.optString("icon")
        }
    }

    private fun parserLink(jo: JSONObject): UserBase.Link {
        return UserBase.Link().apply {
            linkId = jo.optLong("linkId")
            linkType = jo.optInt("linkType")
            link = jo.optString("link")
        }
    }

    fun accountToProfile(account: Account?): Profile? {
        if (account == null) return null

        val profile = Profile()
        profile.uid = account.uid
        profile.nick = account.nickName
        profile.head = account.headUrl
        profile.expired = account.expired.toString()
        profile.atoken = account.accessToken
        profile.rtoken = account.refreshToken
        profile.ttype = account.tokenType
        profile.introduction = account.introduction
        profile.postsCount = account.postsCount
        profile.followingCount = account.followUsersCount
        profile.followerCount = account.followedUsersCount
        profile.likedMyPostsCount = account.likedMyPostsCount
        profile.myLikedPostsCount = account.myLikedPostsCount
        profile.completeLevel = account.completeLevel
        profile.login = account.isLogin
        profile.vip = account.vip
        profile.status = account.status
        val paramsJO = JSONObject()
        val exJO = JSONObject()
        val interestJA = JSONArray()
        val linkJA = JSONArray()

        account.curLevel?.let {
            exJO.put("curLevel", it)
        }
        account.changeNameCount?.let {
            exJO.put("changeNameCount", it)
        }
        account.intrests?.forEach {
            interestJA.put(JSONObject().apply {
                put("id", it.iid)
                put("name", it.label)
                put("icon", it.icon)
            })
        }
        account.links?.forEach {
            linkJA.put(JSONObject().apply {
                put("linkId", it.linkId)
                put("linkType", it.linkType)
                put("link", it.link)
            })
        }
        exJO.put("links", linkJA)
        paramsJO.put("ex", exJO)
        paramsJO.put("interest", interestJA)
        profile.intrests = paramsJO.toString()
        return profile
    }


}
package com.redefine.welike.business.publisher.management.cache

import android.arch.lifecycle.LiveData
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.redefine.foundation.utils.CommonHelper
import com.redefine.im.threadTry
import com.redefine.welike.MyApplication
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.bean.*
import com.redefine.welike.business.publisher.room.DatabaseCenter
import com.redefine.welike.business.publisher.room.Draft
import com.redefine.welike.business.publisher.room.DraftCount
import com.redefine.welike.business.publisher.room.DraftDao
import java.util.*

/**
 * Copyright (C) 2018 redefine , Inc.
 * @author qingfei.chen
 */
class DraftCacheWrapper {
    private lateinit var draftDao: DraftDao

    fun init() {
        DatabaseCenter.init(MyApplication.getAppContext())
        DatabaseCenter.database?.let {
            draftDao = it.draftDao()
            reset()
        } ?: e("DraftCacheWrapper", "init error,because database == null")
    }

    companion object {
        fun getInstance() = Holder.INSTANCE

        fun convertDraftFromLocal(draft: Draft): DraftBase? {
            val gson = GsonBuilder()
                    .registerTypeAdapter(PostBase::class.java, PostBaseJsonDeserializer())
                    .setLenient()
                    .create()

            var retDraft: DraftBase? = null
            when (draft.type) {
                DraftCategory.POST ->
                    retDraft = gson.fromJson<DraftPost>(draft.content, DraftPost::class.java)

                DraftCategory.COMMENT ->
                    retDraft = gson.fromJson<DraftComment>(draft.content, DraftComment::class.java)

                DraftCategory.REPLY ->
                    retDraft = gson.fromJson<DraftReply>(draft.content, DraftReply::class.java)

                DraftCategory.FORWARD -> {
                    retDraft = gson.fromJson<DraftForwardPost>(draft.content, DraftForwardPost::class.java)
                }

                DraftCategory.COMMENT_REPLY_BACK ->
                    retDraft = gson.fromJson<DraftReplyBack>(draft.content, DraftReplyBack::class.java)

            }

            if (retDraft != null) {
                retDraft.draftId = draft.did
            }

            return retDraft

        }
    }

    private object Holder {
        val INSTANCE = DraftCacheWrapper()
    }

    fun getUid(): String? {
        if (AccountManager.getInstance().account == null) {
            return null
        }
        return AccountManager.getInstance().account?.uid
    }

    fun insertOrUpdate(draftBase: DraftBase) {
        val json = Gson().toJson(draftBase, draftBase.javaClass)
        getUid()?.let {

            var draftId = draftBase.draftId
            if (TextUtils.isEmpty(draftId)) {
                draftId = CommonHelper.generateUUID()
            }
            val draftSave = Draft(draftId, it, draftBase.isShow, Date().time, json, draftBase.type)
            threadTry {
                draftDao.insertOrReplace(draftSave)
            }
        } ?: e("DraftCacheWrapper", "insertOrUpdate error,because uid = null")
    }

    fun getAllDrafts(): LiveData<List<Draft>>? {

        getUid()?.let {


            return draftDao.getAll(it, true, GlobalConfig.DRAFT_MAX_COUNT)
//                val returnList = ArrayList<DraftBase>()
//                draftList.forEach {
//                    val draftItem = convertDraftFromLocal(it)
//                    if (draftItem != null) {
//                        returnList.add(draftItem)
//                    }
//                }

//            return draftList

        } ?: e("DraftCacheWrapper", "getAllDrafts error,because uid = null")

        return null

    }

    fun getDraftsCount(countCallback: IDraftCountCallback) {
        getUid()?.let {
            threadTry {
                val count = draftDao.getDraftCount(it, true)
                countCallback.onDraftCountCallback(count.total)
            }
        } ?: e("DraftCacheWrapper", "getDraftsCount error,because uid = null")
    }

    fun delete(draftBase: DraftBase) {
        threadTry {
            draftDao.delete(draftBase.draftId)
        }
    }

    fun clear() {
        threadTry {
            val uid: String = AccountManager.getInstance().account?.uid!!
            getUid()?.let {
                draftDao.deleteAll(it)
            } ?: e("DraftCacheWrapper", "clear error,because uid = null")

        }
    }


    private fun reset() {
        getUid()?.let {
            threadTry {

                val dbDraftList: List<Draft>? = draftDao.getAll(it)

                if (dbDraftList != null && dbDraftList.isNotEmpty()) {
                    for (i in dbDraftList.indices) {
                        val dbDraft = dbDraftList[i]
                        if (i < GlobalConfig.DRAFT_MAX_COUNT) {
                            dbDraft.visibility = true
                            draftDao.update(dbDraft)
                        } else {
                            draftDao.delete(dbDraft)
                        }
                    }
                }
            }
        } ?: e("DraftCacheWrapper", "reset error,because uid = null")
    }

    fun getDraftCount1(): LiveData<DraftCount>? {
        val uid = getUid() ?: return null
        return draftDao.getDraftCount1(uid, true)
    }

    fun getUploadingDraft(): LiveData<List<Draft>>? {
        val uid = getUid() ?: return null
        return draftDao.getUploadingDraft(uid, DraftCategory.POST)
    }
}
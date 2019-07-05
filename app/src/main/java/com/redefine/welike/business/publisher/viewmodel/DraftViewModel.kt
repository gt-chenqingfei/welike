package com.redefine.welike.business.publisher.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.redefine.im.threadTry
import com.redefine.im.threadUITry
import com.redefine.welike.business.publisher.management.DraftManager
import com.redefine.welike.business.publisher.management.cache.DraftCacheWrapper
import com.redefine.welike.business.publisher.room.Draft
import com.redefine.welike.business.publisher.room.DraftCount

class DraftViewModel(application: Application) : AndroidViewModel(application) {

    fun getDraftCount(t: (LiveData<DraftCount>) -> Unit) {
        threadTry {
            DraftManager.getInstance()?.getDraftCount1()?.let {
                threadUITry {
                    t.invoke(it)
                }
            }
        }
    }

    fun getDraftList(t: (LiveData<List<Draft>>) -> Unit) {
        threadTry {
            DraftCacheWrapper.getInstance().getAllDrafts()?.let {
                t.invoke(it)
            }
        }
    }


    fun getUploadingDraft(t: (LiveData<List<Draft>>) -> Unit) {
        threadTry {
            DraftCacheWrapper.getInstance().getUploadingDraft()?.let {
                threadUITry {
                    t.invoke(it)
                }
            }
        }
    }


}
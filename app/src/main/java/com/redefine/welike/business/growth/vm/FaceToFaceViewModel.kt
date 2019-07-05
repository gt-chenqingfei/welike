package com.redefine.welike.business.growth.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.user.management.bean.RecommendUser
import com.redefine.welike.net.RetrofitManager
import com.redefine.welike.net.subscribeExt
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.RandomUtils

/**
 *
 * Name: FaceToFaceViewModel
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-01-14 22:11
 *
 */

class FaceToFaceViewModel(application: Application) : AndroidViewModel(application) {

    val recommendUsers = MutableLiveData<List<RecommendUser>>()

    private val service = RetrofitManager.getInstance().retrofit.create(FaceToFaceService::class.java)

    fun getRecommendUsers(location: Location) {
        AccountManager.getInstance().account?.let { it ->
            service.getFaceToFaceRecommendUsers(it.uid, location.longitude, location.latitude)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread()).subscribeExt({
                        if (it.code == ErrorCode.ERROR_NETWORK_SUCCESS) {
                            recommendUsers.postValue(it.result?.list)
                        }
                    },{

                    })
        }
    }
}
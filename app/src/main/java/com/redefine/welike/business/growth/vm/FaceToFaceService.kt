package com.redefine.welike.business.growth.vm

import com.redefine.welike.business.user.management.bean.FaceToFaceRecommendUserList
import com.redefine.welike.net.Result
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * Name: FaceToFaceService
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-01-14 22:16
 *
 */
interface FaceToFaceService {

    @GET("lbs/f2f")
    fun getFaceToFaceRecommendUsers(@Query("userId") userId: String
                                    , @Query("lng") lng: Double
                                    , @Query("lat") lat: Double): Observable<Result<FaceToFaceRecommendUserList>>
}
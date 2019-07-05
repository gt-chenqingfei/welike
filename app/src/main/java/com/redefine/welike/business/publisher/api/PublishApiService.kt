package com.redefine.welike.business.publisher.api

import com.redefine.welike.business.publisher.api.bean.*
import com.redefine.welike.net.Result
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 *
 * Name: PublishApiService
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-21 17:48
 *
 */

interface PublishApiService {
    @POST("feed/user/{user_id}/contents")
    fun post(@Path("user_id") uid: String, @Body publishPostBean: RequestPostBean): Observable<Result<ResponsePostBean>>

    @POST("feed/user/{user_id}/contents")
    fun comment(@Path("user_id") uid: String, @Body publishCommentBean: RequestCommentBean): Observable<Result<ResponseCommentBean>>

    @POST("feed/user/{user_id}/contents")
    fun reply(@Path("user_id") uid: String, @Body publishCommentBean: RequestReplyBean): Observable<Result<ResponseReplyBean>>

    @POST("feed/user/{user_id}/contents")
    fun forward(@Path("user_id") uid: String, @Body publishCommentBean: RequestForwardBean): Observable<Result<ResponseForwardBean>>
}
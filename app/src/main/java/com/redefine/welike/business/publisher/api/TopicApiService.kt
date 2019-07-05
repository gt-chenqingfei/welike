package com.redefine.welike.business.publisher.api

import com.redefine.welike.business.publisher.api.bean.Topic
import com.redefine.welike.business.publisher.api.bean.TopicCategory
import com.redefine.welike.net.Result
import io.reactivex.Observable
import retrofit2.http.GET
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

interface TopicApiService {

    @GET("conplay/label/topic/{labelId}/topics")
    fun getTopic(@Path("labelId") labelId: String): Observable<Result<List<Topic>>>

    @GET("conplay/label/topic/labels")
    fun getTopicCategories(): Observable<Result<List<TopicCategory>>>

    @GET("conplay/skip/label/topic/{labelId}/topics")
    fun getSkipTopic(@Path("labelId") labelId: String): Observable<Result<List<Topic>>>

    @GET("conplay/skip/label/topic/labels")
    fun getSkipTopicCategories(): Observable<Result<List<TopicCategory>>>
}
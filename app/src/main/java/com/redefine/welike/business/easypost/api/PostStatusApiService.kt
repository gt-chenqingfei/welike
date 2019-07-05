package com.redefine.welike.business.easypost.api

import com.redefine.welike.business.easypost.api.bean.PostStatusList
import com.redefine.welike.net.Result
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Copyright (C) 2018 redefine , Inc.
 *
 * @author qingfei.chen
 * @date 2018-10-17 17:50:02
 */
interface PostStatusApiService {

    @GET("conplay/post/status/all")
    fun getAllStatus(): Observable<Result<PostStatusList>>

}
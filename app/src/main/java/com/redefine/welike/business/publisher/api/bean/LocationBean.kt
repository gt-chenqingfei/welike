package com.redefine.welike.business.publisher.api.bean

import com.google.gson.annotations.SerializedName

/**
 *
 * Name: LocationBean
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-29 16:44
 *
 */

data class LocationBean(
        @SerializedName("lon") val lon: String? = null,
        @SerializedName("lat") val lat: String? = null,
        @SerializedName("placeId") val placeId: String? = null,
        @SerializedName("placeName") val placeName: String? = null)

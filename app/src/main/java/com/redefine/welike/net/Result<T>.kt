package com.redefine.welike.net

/**
 *
 * Name: `HisMessagesResult<T>`
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-10 12:33
 *
 */
data class Result<T> (var code: Int, var resultMsg : String?, var result : T ?)
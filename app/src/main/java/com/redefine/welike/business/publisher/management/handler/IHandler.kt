package com.redefine.welike.business.publisher.management.handler


/**
 *
 * Name: IHandler
 * Author: liwenbo
 * Email:
 * Comment: //责任链的每个责任的抽象接口
 * Date: 2018-07-10 18:04
 *
 */
interface IHandler {

    fun start(callback: (IHandler, PublishMessage) -> Unit): Boolean
    fun isLast(): Boolean
    fun cancel()
}
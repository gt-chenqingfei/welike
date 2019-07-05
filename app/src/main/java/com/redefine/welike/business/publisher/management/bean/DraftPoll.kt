package com.redefine.welike.business.publisher.management.bean

import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.business.feeds.management.bean.PollItemInfo

/**
 *
 * Name: DraftPoll
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-23 17:19
 *
 */

class DraftPoll(val mPollItemInfos: List<PollItemInfo> = ArrayList(), val expiredTime: Long = GlobalConfig.DEFAULT_POLL_EXPIRED_TIME)


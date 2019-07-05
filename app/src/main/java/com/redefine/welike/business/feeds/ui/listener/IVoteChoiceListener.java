package com.redefine.welike.business.feeds.ui.listener;

import com.redefine.welike.business.feeds.management.bean.PollItemInfo;

import java.util.ArrayList;

/**
 * Created by liwenbo on 2018/3/4.
 */

public interface IVoteChoiceListener {

    void onTextVote(String pid, String pollId, ArrayList<PollItemInfo> choiceIds, boolean isRepost);

}

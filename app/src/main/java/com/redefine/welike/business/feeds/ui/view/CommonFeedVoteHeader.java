package com.redefine.welike.business.feeds.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.PollParticipants;
import com.redefine.welike.business.feeds.management.bean.PollPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.business.user.ui.page.UserHostPage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengnan on 2018/6/20.
 **/
public class CommonFeedVoteHeader extends ConstraintLayout implements  View.OnClickListener {
    private View mRootView;
    private IPageStackManager mPageStackManager;
    private PostBase mPostBase;
    private TextView mVotePeople;
    private TextView mVoteDec;
    private TextView mVoteMore;

    public CommonFeedVoteHeader(Context context) {
        super(context);
        init(context);
    }

    public CommonFeedVoteHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonFeedVoteHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_feed_poll_header, this);

        mVotePeople = findViewById(R.id.votePeople);
        mVoteDec=findViewById(R.id.voteDec);
        mVoteMore = findViewById(R.id.voteMore);
    }
    public void initViews(PostBase postBase) {

        mPostBase = postBase;
        mVotePeople.setText("");
        mVoteDec.setText("");
        if (null!=mPostBase&&mPostBase.isHotPoll()) {
           //null!=mPostBase&&mPostBase.isHotPoll()
            setVisibility(View.VISIBLE);
            mVoteMore.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "more_poll"));
            mVoteMore.setOnClickListener(this);
            //((PollPost)postBase).mPollInfo.totalCount=1110;
            if(((PollPost)postBase).mPollInfo.totalCount>0){
                List<PollParticipants>list=mPostBase.getPollParticipants();
               // List<PollParticipants>list=makeTestData();
                if(null!=list&&list.size()>0){
                    mVotePeople.setOnClickListener(this);
                    if(list.size()==1){
                        String text= ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "voted");
                        mVotePeople.setText(list.get(0).name);
                        mVoteDec.setText(text);
                    }else {
                        String text= ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "follower_vote_numbers");
                        mVotePeople.setText(list.get(0).name);
                        mVoteDec.setText(String.format(text, ((PollPost)postBase).mPollInfo.totalCount));
                    }

                }else {
                    String text= ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "vote_numbers");
                    mVotePeople.setText("");
                    mVoteDec.setText(String.format(text, ((PollPost)postBase).mPollInfo.totalCount));
                }
            }else {
                String text= ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "vote_now");
                mVotePeople.setText("");
                mVoteDec.setText(text);
            }



        } else {
            setVisibility(View.GONE);
        }

    }
    private List<PollParticipants> makeTestData(){
        List<PollParticipants>list=new ArrayList<>();
        for(int i=0;i<5;i++){
            PollParticipants te=new PollParticipants();
            te.name="abcihjljjkjkllkjjjjkmkklkkkjjkkjjk";
            te.id="150176";

            list.add(te);
        }
        return list;

    }

    @Override
    public void onClick(View v) {
        if(v==mVoteMore){
            Bundle bundle = new Bundle();
            TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
            bean.name = "#poll";
            bean.id = "POLL";
            bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
        }else if(v==mVotePeople){
            List<PollParticipants>list=mPostBase.getPollParticipants();
            if(null!=list&&list.size()>0){
                UserHostPage.launch(true,list.get(0).id);

            }
        }

    }
}

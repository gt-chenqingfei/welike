package com.redefine.welike.business.user.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.androidkun.xtablayout.XTabLayout;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.ui.adapter.UserHostFollowPagerAdapter;
import com.redefine.welike.business.user.ui.fragment.UserFollowerFragment;
import com.redefine.welike.business.user.ui.fragment.UserFollowingFragment;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.redefine.welike.business.user.ui.constant.UserConstant.FOLLOWING;
import static com.redefine.welike.business.user.ui.constant.UserConstant.NICK_NAME;
import static com.redefine.welike.business.user.ui.constant.UserConstant.TAB_NAME;
import static com.redefine.welike.business.user.ui.constant.UserConstant.UID;

/**
 * @author gongguan
 * @time 2018/1/12 上午11:33
 */
@Route(path = RouteConstant.FOLLOW_ROUTE_PATH)
public class UserFollowActivity extends BaseActivity {
    private String tabName = "";
    private String uId = "";

    private ViewPager mVp;
    private XTabLayout pageTab;
    private String nickName;

    private List<Fragment> mPages = new ArrayList<>(2);

    public static void launch(String tabName, String uId, String nickName) {
        Bundle bundle = new Bundle();
        bundle.putString(TAB_NAME, tabName);
        bundle.putString(NICK_NAME, nickName);
        bundle.putString(UID, uId);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_USER_FOLLOW_EVENT, bundle));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        parseBundle();
        initView();
        initData();
    }

    private void initView() {
        mVp = findViewById(R.id.vp_user_host_follow);
        pageTab = findViewById(R.id.page_tab);
        TextView title = findViewById(R.id.tv_common_title);
        ImageView iv_back = findViewById(R.id.iv_common_back);
        ImageView iv_next = findViewById(R.id.iv_common_next);
        Account account = AccountManager.getInstance().getAccount();
        if (TextUtils.equals(nickName, account.getNickName()))
            title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follow_page_title"));
        else
            title.setText(nickName + "\t" + ResourceTool.getString(ResourceTool.ResourceFileEnum.USER,"user_follow_page_title"));

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.FOLLOW);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
            }
        });
    }

    private void parseBundle() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        uId = extras.getString(UID);
        nickName = extras.getString(NICK_NAME);
        tabName = extras.getString(TAB_NAME);
    }

    private void initData() {
        UserHostFollowPagerAdapter adapter = new UserHostFollowPagerAdapter(getSupportFragmentManager());

        mPages.clear();
        boolean followerShow = false;
        boolean followingShow = false;
        if (FOLLOWING.equalsIgnoreCase(tabName)) {
            followingShow = true;
        } else {
            followerShow = true;
        }
        UserFollowerFragment userFollowerFragment = UserFollowerFragment.create(uId, followerShow);
        UserFollowingFragment userFollowingFragment = UserFollowingFragment.create(uId, followingShow);

        String[] mPageTitle = new String[]{ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_following_num_text"),
                ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text")};
        mPages.add(userFollowingFragment);
        mPages.add(userFollowerFragment);
        adapter.setTitle(mPageTitle);
        adapter.setPages(mPages);

        mVp.setAdapter(adapter);
        if (FOLLOWING.equalsIgnoreCase(tabName)) {
            mVp.setCurrentItem(0);
        } else {
            mVp.setCurrentItem(1);
        }
        pageTab.setupWithViewPager(mVp);
    }

}

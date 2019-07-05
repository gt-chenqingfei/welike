package com.redefine.welike.business.user.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;

import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.ui.fragment.ProfileLikeFragment;
import com.redefine.welike.business.user.ui.fragment.ProfilePhotoFragment;
import com.redefine.welike.business.user.ui.fragment.ProfilePostFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/8/21
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
    private String slidingTitle[];
    private String mTabPost = MyApplication.getApp().getString(R.string.user_host_page_posts_list_btn);
    private String mTabLike = MyApplication.getApp().getString(R.string.user_host_page_albums_list_btn);
    private String mTabAlbum = MyApplication.getApp().getString(R.string.album);
    private List<Fragment> mFragments;
    private String mUid;

    public ProfilePagerAdapter(FragmentManager fm, String uid) {
        super(fm);
        this.mUid = uid;
        mFragments = new ArrayList<>();
        mFragments.add(ProfilePostFragment.create(mUid));
        if (AccountManager.getInstance().isLogin() &&AccountManager.getInstance().isSelf(mUid)) {
            mFragments.add(ProfileLikeFragment.create(mUid));
            slidingTitle = new String[]{mTabPost, mTabLike};
        } else {
            mFragments.add(ProfilePhotoFragment.create(mUid));
            slidingTitle = new String[]{mTabPost, mTabAlbum};
        }
    }

    public void setPostAndLikeCount(int postCount, long likeCount) {
        slidingTitle[0] = postCount + mTabPost;
        if (slidingTitle[1].startsWith(mTabLike)) {
            slidingTitle[1] = likeCount + mTabLike;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return slidingTitle[position];
    }
}

package com.redefine.welike.business.common;

import android.support.annotation.DrawableRes;

import com.redefine.welike.R;

/**
 * Created by daining on 2018/4/24.
 */

public class LikeManager {

    public static final int SUPPER_LIKE = 5;

    private static boolean isTest = false;//配置超级赞的灰度发布

    public static int convertSuperLikeLevel(long exp) {
        if (exp == 0) return 1;
        if (exp >= 1 && exp < 30) return 2;
        if (exp >= 30 && exp < 50) return 3;
        if (exp >= 50 && exp < 100) return 4;
        if (exp >= 100) return SUPPER_LIKE;
        return 1;
    }

    @DrawableRes
    public static int getImage(int exp) {
        int level = LikeManager.convertSuperLikeLevel(exp);
        int id = R.drawable.feed_detail_like1;
        switch (level) {
            case 1:
                id = R.drawable.feed_detail_like1;
                break;
            case 2:
//                id = (R.drawable.feed_detail_like2);
//                break;
            case 3:
//                id = (R.drawable.feed_detail_like3);
//                break;
            case 4:
//                id = (R.drawable.feed_detail_like4);
//                break;
            case 5:
                id = (R.drawable.feed_detail_like5);
                break;
        }
        return id;
    }


    @DrawableRes
    public static int getFeedImage(int exp) {
        int level = LikeManager.convertSuperLikeLevel(exp);
        int id = R.drawable.feed_detail_like2;
        switch (level) {
            case 1:
                id = R.drawable.feed_detail_like1;
                break;
            case 2:
//                id = (R.drawable.feed_detail_like2);
//                break;
            case 3:
//                id = (R.drawable.feed_detail_like3);
//                break;
            case 4:
//                id = (R.drawable.feed_detail_like4);
//                break;
            case 5:
                id = (R.drawable.feed_detail_like5);
                break;
        }
        return id;
    }

    @DrawableRes
    public static int getLikelevelImage(int exp) {
        int id = R.drawable.common_feed_like_level_1;
        switch (exp) {
            case 2:
                id = (R.drawable.common_feed_like_level_1);
                break;
            case 3:
                id = (R.drawable.common_feed_like_level_2);
                break;
            case 4:
                id = (R.drawable.common_feed_like_level_3);
                break;
            case 5:
                id = (R.drawable.common_feed_like_level_4);
                break;
        }
        return id;
    }

    @DrawableRes
    public static int getLikeNumver(int exp) {
        int id = R.drawable.common_feed_like_num_2;
        switch (exp) {
            case 0:
                id = R.drawable.common_feed_like_num_2;
                break;
            case 1:
                id = (R.drawable.common_feed_like_num_3);
                break;
            case 2:
                id = (R.drawable.common_feed_like_num_4);
                break;
            case 3:
                id = (R.drawable.common_feed_like_num_5);
                break;
            case 4:
                id = (R.drawable.common_feed_like_num_6);
                break;
            case 5:
                id = (R.drawable.common_feed_like_num_7);
                break;
            case 6:
                id = (R.drawable.common_feed_like_num_8);
                break;
            case 7:
                id = (R.drawable.common_feed_like_num_9);
                break;
            case 8:
                id = (R.drawable.common_feed_like_num_10);
                break;
            case 9:
                id = (R.drawable.common_feed_like_num_11);
                break;
            case -1:
                id = (R.drawable.common_feed_like_num_1);
                break;
        }
        return id;
    }
}

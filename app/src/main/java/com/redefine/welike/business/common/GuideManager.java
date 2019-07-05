package com.redefine.welike.business.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.StringDef;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.common.Life;
import com.redefine.welike.common.LifeListener;
import com.redefine.welike.commonui.widget.ArrowTextView;
import com.redefine.welike.guide.Overlay;
import com.redefine.welike.guide.ToolTip;
import com.redefine.welike.guide.TourGuide;
import com.redefine.welike.statistical.EventLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by daining on 2018/4/19.
 */

public enum GuideManager {

    INSTANCE;

    public static final String HOME_DISCOVERY = "HOME_DISCOVERY_";
    public static final String DISCOVERY_FOLLOW = "DISCOVERY_FOLLOW_";
    public static final String HOME_SUPER_LIKE = "HOME_SUPER_LIKE_";
    public static final String ME_POST = "ME_POST_";
    public static final String POST_ADD_IMAGE = "POST_ADD_IMAGE_";
    public static final String POLL_ADD = "POLL_ADD";
    public static final String EDITOR_SHOW = "EDITOR_SHOW";

    @StringDef({HOME_DISCOVERY, DISCOVERY_FOLLOW, HOME_SUPER_LIKE, ME_POST, POST_ADD_IMAGE, POLL_ADD,EDITOR_SHOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GuideType {
    }

    private SharedPreferences sp;
    Animation animation;

    public void init(Context context) {
        sp = context.getSharedPreferences("GUIDE", Context.MODE_PRIVATE);
        Life.regListener(new LifeListener() {
            @Override
            public void onFire(int event) {
                if (event == Life.LIFE_REGISTER_FINISH) {
                    reset();
                }
            }
        });

        animation = new TranslateAnimation(0f, 0f, 0f, 0f);
        animation.setFillAfter(true);
    }

    /**
     * 重制 Guide 的显示时机。
     */
    private void reset() {
        mePostFlag = true;
        sp.edit().putInt(HOME_DISCOVERY, 1)
                .putInt(DISCOVERY_FOLLOW, 1)
                .putInt(HOME_SUPER_LIKE, 1)
                .putInt(ME_POST, 1)
                .putInt(POST_ADD_IMAGE, 1)
                .apply();
    }

    private boolean mePostFlag = true;
    private boolean showHomeDiscovery = false;

    public boolean check(@GuideType String type) {
        int v = 0;
//        if (type.equalsIgnoreCase(HOME_DISCOVERY)) {
//            v = sp.getInt(type, 0);
//        } else if (type.equalsIgnoreCase(HOME_SUPER_LIKE)) {
//            //先判断 HOME_DISCOVERY 是否已经显示。
//            if (!showHomeDiscovery) {
//                v = sp.getInt(type, 0);
//                sp.edit().putInt(type, 0).apply();
//            }
//        } else if (type.equalsIgnoreCase(DISCOVERY_FOLLOW)) {
//            v = sp.getInt(type, 0);
//            sp.edit().putInt(type, 0).apply();
//        } else if (type.equalsIgnoreCase(ME_POST)) {
//            Account account = AccountManager.getInstance().getAccount();
//            if (account != null) {
//                if (account.getPostsCount() < 1) {
//                    if (mePostFlag) {
//                        mePostFlag = false;
//                        return true;
//                    }
//                }
//            }
//            return false;
//        } else if (type.equalsIgnoreCase(POST_ADD_IMAGE)) {
//            v = sp.getInt(type, 0);
//        } else if (type.equalsIgnoreCase(POLL_ADD)) {
//            v = sp.getInt(type, 0);
//        }
        if (type.equalsIgnoreCase(ME_POST)) {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                if (account.getPostsCount() < 1) {
                    if (mePostFlag) {
                        mePostFlag = false;
                        return true;
                    }
                }
            }
            return false;
        }else{
            return false;
        }
    }

    public void show(@GuideType String type, ArrowTextView guide, View target) {
//        if (type.equalsIgnoreCase(HOME_SUPER_LIKE)) {
//            guide.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setVisibility(View.GONE);
//                }
//            });
//            guide.setVisibility(View.VISIBLE);
//            guide.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_3"));
//            guide.playBottom(target);
//        } else if (type.equalsIgnoreCase(DISCOVERY_FOLLOW)) {
//            guide.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.setVisibility(View.GONE);
//                }
//            });
//            guide.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_2"));
//            guide.setVisibility(View.VISIBLE);
//            guide.playTop(target);
//        }
//        sp.edit().putInt(type, 0).apply();
    }

    public TourGuide show(@GuideType final String type, Activity activity) {
        final TourGuide tourGuide = TourGuide.init(activity).with(TourGuide.Technique.CLICK);
        if (type.equalsIgnoreCase(ME_POST)){
            tourGuide.setToolTip(new ToolTip()
                    .setGravity(Gravity.TOP)
                    .setShadow(true)
                    .setEnterAnimation(animation)
                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_4")))
                    .setOverlay(new Overlay()
                            .setBackgroundColor(Color.parseColor("#26000000"))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tourGuide.cleanUp();
                                    EventLog.Guide.report(type);
                                }
                            })
                    );
        }
//        if (type.equalsIgnoreCase(HOME_DISCOVERY)) {
//            showHomeDiscovery = true;
//            tourGuide.setToolTip(new ToolTip()
//                    .setGravity(Gravity.TOP)
//                    .setEnterAnimation(animation)
//                    .setShadow(true)
//                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_1")))
//                    .setOverlay(new Overlay()
//                            .setBackgroundColor(Color.parseColor("#26000000"))
//                            .setHoleRadius(DensityUtil.dp2px(35))
//                            .setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    tourGuide.cleanUp();
//                                    EventLog.Guide.report(type);
//                                }
//                            })
//                    );
//        } else if (type.equalsIgnoreCase(ME_POST)) {
//
//        } else if (type.equalsIgnoreCase(POST_ADD_IMAGE)) {
//            tourGuide.setToolTip(new ToolTip()
//                    .setGravity(Gravity.TOP)
//                    .setEnterAnimation(animation)
//                    .setShadow(true)
//                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_5")))
//                    .setOverlay(new Overlay()
//                            .setBackgroundColor(Color.parseColor("#26000000"))
//                            .setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    tourGuide.cleanUp();
//                                    EventLog.Guide.report(type);
//                                }
//                            })
//                    );
//        } else if (type.equalsIgnoreCase(HOME_SUPER_LIKE)) {
//            tourGuide.setToolTip(new ToolTip()
//                    .setGravity(Gravity.TOP)
//                    .setEnterAnimation(animation)
//                    .setShadow(true)
//                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_3")))
//                    .setOverlay(new Overlay()
//                            .setBackgroundColor(Color.parseColor("#26000000"))
//                            .setHoleRadius(DensityUtil.dp2px(25))
//                            .setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    tourGuide.cleanUp();
//                                    EventLog.Guide.report(type);
//                                }
//                            })
//                    );
//        } else if (type.equalsIgnoreCase(DISCOVERY_FOLLOW)) {
//            tourGuide.setToolTip(new ToolTip()
//                    .setGravity(Gravity.TOP)
//                    .setEnterAnimation(animation)
//                    .setShadow(true)
//                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_2")))
//                    .setOverlay(new Overlay()
//                            .setBackgroundColor(Color.parseColor("#26000000"))
//                            .setStyle(Overlay.Style.ROUNDED_RECTANGLE)
//                            .setHolePadding(4)
//                            .setRoundedCornerRadius(20)
//                            .setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    tourGuide.cleanUp();
//                                    EventLog.Guide.report(type);
//                                }
//                            })
//                    );
//        } else if (type.equalsIgnoreCase(POLL_ADD)) {
//            tourGuide.setToolTip(new ToolTip()
//                    .setGravity(Gravity.TOP)
//                    .setEnterAnimation(animation)
//                    .setShadow(true)
//                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_6")))
//                    .setOverlay(new Overlay()
//                            .setBackgroundColor(Color.parseColor("#26000000"))
//                            .setStyle(Overlay.Style.ROUNDED_RECTANGLE)
//                            .setHolePadding(4)
//                            .setRoundedCornerRadius(20)
//                            .setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    tourGuide.cleanUp();
//                                    EventLog.Guide.report(type);
//                                }
//                            })
//                    );
//        }
//        sp.edit().putInt(type, 0).apply();
        return tourGuide;
    }


}

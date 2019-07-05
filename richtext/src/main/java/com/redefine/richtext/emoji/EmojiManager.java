package com.redefine.richtext.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;

import com.redefine.richtext.emoji.bean.EmojiBean;
import com.redefine.richtext.emoji.googlecompat.EmojiSupportList;
import com.redefine.richtext.emoji.googlecompat.EmojiSupportList4LessL;
import com.redefine.richtext.emoji.googlecompat.GoogleCompatEmojiProvider;
import com.redefine.richtext.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by MR on 2018/1/23.
 */

public class EmojiManager {
    private static final Pattern mDefaultEmojiPattern = Pattern.compile("\\[[a-zA-Z0-9_]+\\]");
    private final Map<String, EmojiBean> mDefaultEmojiMap = new LinkedHashMap<String, EmojiBean>();
    private final Map<String, EmojiBean> mShowEmojiMap = new LinkedHashMap<String, EmojiBean>();

    private EmojiManager() {
        initDefauleEmoji();
        initShowEmoji();
    }

    public void init(Context context) {

        String[] emojiSupportList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            emojiSupportList = EmojiSupportList.supportEmojiData;
        } else {
            emojiSupportList = EmojiSupportList4LessL.supportEmojiData;
        }

        com.welike.emoji.EmojiManager.install(new GoogleCompatEmojiProvider(emojiSupportList), context);
    }

    private void initShowEmoji() {
        mShowEmojiMap.putAll(mDefaultEmojiMap);
        mShowEmojiMap.remove("[music]");
        mShowEmojiMap.remove("[poo]");
        mShowEmojiMap.remove("[pill]");
        mShowEmojiMap.remove("[home]");
    }

    private void initDefauleEmoji() {
        mDefaultEmojiMap.put("[smile]", new EmojiBean("[smile]", R.drawable.smile));
        mDefaultEmojiMap.put("[laugh]", new EmojiBean("[laugh]", R.drawable.laugh));
        mDefaultEmojiMap.put("[grin]", new EmojiBean("[grin]", R.drawable.grin));
        mDefaultEmojiMap.put("[awkward]", new EmojiBean("[awkward]", R.drawable.awkward));
        mDefaultEmojiMap.put("[tears_of_joy]", new EmojiBean("[tears_of_joy]", R.drawable.tears_of_joy));
        mDefaultEmojiMap.put("[naughty]", new EmojiBean("[naughty]", R.drawable.naughty));
        mDefaultEmojiMap.put("[cool]", new EmojiBean("[cool]", R.drawable.cool));
        mDefaultEmojiMap.put("[delicious]", new EmojiBean("[delicious]", R.drawable.delicious));
        mDefaultEmojiMap.put("[disappointed]", new EmojiBean("[disappointed]", R.drawable.disappointed));
        mDefaultEmojiMap.put("[confounded]", new EmojiBean("[confounded]", R.drawable.confounded));
        mDefaultEmojiMap.put("[crying]", new EmojiBean("[crying]", R.drawable.crying));
        mDefaultEmojiMap.put("[steaming]", new EmojiBean("[steaming]", R.drawable.steaming));
        mDefaultEmojiMap.put("[scream]", new EmojiBean("[scream]", R.drawable.scream));
        mDefaultEmojiMap.put("[pout]", new EmojiBean("[pout]", R.drawable.pout));
        mDefaultEmojiMap.put("[cold_sweat]", new EmojiBean("[cold_sweat]", R.drawable.cold_sweat));
        mDefaultEmojiMap.put("[sleep]", new EmojiBean("[sleep]", R.drawable.sleep));
        mDefaultEmojiMap.put("[sick]", new EmojiBean("[sick]", R.drawable.sick));
        mDefaultEmojiMap.put("[love_eye]", new EmojiBean("[love_eye]", R.drawable.love_eye));
        mDefaultEmojiMap.put("[blowkiss]", new EmojiBean("[blowkiss]", R.drawable.blowkiss));
        mDefaultEmojiMap.put("[kiss]", new EmojiBean("[kiss]", R.drawable.kiss));

        // add at 1.6.1  start
        mDefaultEmojiMap.put("[follow_me]", new EmojiBean("[follow_me]", R.drawable.follow_me));
        mDefaultEmojiMap.put("[look_right]", new EmojiBean("[look_right]", R.drawable.look_right));
        mDefaultEmojiMap.put("[point_right]", new EmojiBean("[point_right]", R.drawable.point_right));
        mDefaultEmojiMap.put("[point_down]", new EmojiBean("[point_down]", R.drawable.point_down));
        // add at 1.6.1  end

        mDefaultEmojiMap.put("[hah]", new EmojiBean("[hah]", R.drawable.hah));
        mDefaultEmojiMap.put("[wink]", new EmojiBean("[wink]", R.drawable.wink));
        mDefaultEmojiMap.put("[roar]", new EmojiBean("[roar]", R.drawable.roar));
        mDefaultEmojiMap.put("[angry]", new EmojiBean("[angry]", R.drawable.angry));
        mDefaultEmojiMap.put("[thinking]", new EmojiBean("[thinking]", R.drawable.thinking));
        mDefaultEmojiMap.put("[smirk]", new EmojiBean("[smirk]", R.drawable.smirk));
        mDefaultEmojiMap.put("[fearful]", new EmojiBean("[fearful]", R.drawable.fearful));
        mDefaultEmojiMap.put("[silence]", new EmojiBean("[silence]", R.drawable.silence));
        mDefaultEmojiMap.put("[woo]", new EmojiBean("[woo]", R.drawable.woo));
        mDefaultEmojiMap.put("[dizzy]", new EmojiBean("[dizzy]", R.drawable.dizzy));
        mDefaultEmojiMap.put("[devil]", new EmojiBean("[devil]", R.drawable.devil));
        mDefaultEmojiMap.put("[angel]", new EmojiBean("[angel]", R.drawable.angel));
        mDefaultEmojiMap.put("[ghost]", new EmojiBean("[ghost]", R.drawable.ghost));
        mDefaultEmojiMap.put("[rose]", new EmojiBean("[rose]", R.drawable.rose));
        mDefaultEmojiMap.put("[good]", new EmojiBean("[good]", R.drawable.good));
        mDefaultEmojiMap.put("[okhand]", new EmojiBean("[okhand]", R.drawable.okhand));
        mDefaultEmojiMap.put("[bad]", new EmojiBean("[bad]", R.drawable.bad));
        mDefaultEmojiMap.put("[pray]", new EmojiBean("[pray]", R.drawable.pray));
        mDefaultEmojiMap.put("[like]", new EmojiBean("[like]", R.drawable.like));
        mDefaultEmojiMap.put("[heart_broken]", new EmojiBean("[heart_broken]", R.drawable.heart_broken));
        mDefaultEmojiMap.put("[cloud]", new EmojiBean("[cloud]", R.drawable.cloud));
        mDefaultEmojiMap.put("[lightning]", new EmojiBean("[lightning]", R.drawable.lightning));
        mDefaultEmojiMap.put("[rain]", new EmojiBean("[rain]", R.drawable.rain));
        mDefaultEmojiMap.put("[rainbow]", new EmojiBean("[rainbow]", R.drawable.rainbow));
        mDefaultEmojiMap.put("[sun]", new EmojiBean("[sun]", R.drawable.sun));
        mDefaultEmojiMap.put("[moon]", new EmojiBean("[moon]", R.drawable.moon));
        mDefaultEmojiMap.put("[fire]", new EmojiBean("[fire]", R.drawable.fire));
        mDefaultEmojiMap.put("[tiger]", new EmojiBean("[tiger]", R.drawable.tiger));
        mDefaultEmojiMap.put("[elephant]", new EmojiBean("[elephant]", R.drawable.elephant));
        mDefaultEmojiMap.put("[railway]", new EmojiBean("[railway]", R.drawable.railway));
        mDefaultEmojiMap.put("[boat]", new EmojiBean("[boat]", R.drawable.boat));
        mDefaultEmojiMap.put("[rocket]", new EmojiBean("[rocket]", R.drawable.rocket));
        mDefaultEmojiMap.put("[car]", new EmojiBean("[car]", R.drawable.car));
        mDefaultEmojiMap.put("[petrol]", new EmojiBean("[petrol]", R.drawable.petrol));
        mDefaultEmojiMap.put("[hamburger]", new EmojiBean("[hamburger]", R.drawable.hamburger));
        mDefaultEmojiMap.put("[curry]", new EmojiBean("[curry]", R.drawable.curry));
        mDefaultEmojiMap.put("[coffee]", new EmojiBean("[coffee]", R.drawable.coffee));
        mDefaultEmojiMap.put("[tea]", new EmojiBean("[tea]", R.drawable.tea));
        mDefaultEmojiMap.put("[cookie]", new EmojiBean("[cookie]", R.drawable.cookie));
        mDefaultEmojiMap.put("[apple]", new EmojiBean("[apple]", R.drawable.apple));
        mDefaultEmojiMap.put("[balloon]", new EmojiBean("[balloon]", R.drawable.balloon));
        mDefaultEmojiMap.put("[gift]", new EmojiBean("[gift]", R.drawable.gift));
        mDefaultEmojiMap.put("[cake]", new EmojiBean("[cake]", R.drawable.cake));
        mDefaultEmojiMap.put("[wine]", new EmojiBean("[wine]", R.drawable.wine));
        mDefaultEmojiMap.put("[beer]", new EmojiBean("[beer]", R.drawable.beer));
        mDefaultEmojiMap.put("[cheers]", new EmojiBean("[cheers]", R.drawable.cheers));
        mDefaultEmojiMap.put("[party]", new EmojiBean("[party]", R.drawable.party));
        mDefaultEmojiMap.put("[india]", new EmojiBean("[india]", R.drawable.india));
        mDefaultEmojiMap.put("[cricket]", new EmojiBean("[cricket]", R.drawable.cricket));
        mDefaultEmojiMap.put("[star]", new EmojiBean("[star]", R.drawable.star));
        mDefaultEmojiMap.put("[crystal_ball]", new EmojiBean("[crystal_ball]", R.drawable.crystal_ball));
        mDefaultEmojiMap.put("[lipstick]", new EmojiBean("[lipstick]", R.drawable.lipstick));
        mDefaultEmojiMap.put("[trophy]", new EmojiBean("[trophy]", R.drawable.trophy));
        mDefaultEmojiMap.put("[clapping]", new EmojiBean("[clapping]", R.drawable.clapping));
        mDefaultEmojiMap.put("[music]", new EmojiBean("[music]", R.drawable.music));
        mDefaultEmojiMap.put("[sing]", new EmojiBean("[sing]", R.drawable.sing));
        mDefaultEmojiMap.put("[movie]", new EmojiBean("[movie]", R.drawable.movie));
        mDefaultEmojiMap.put("[game]", new EmojiBean("[game]", R.drawable.game));
        mDefaultEmojiMap.put("[graduation]", new EmojiBean("[graduation]", R.drawable.graduation));
        mDefaultEmojiMap.put("[eyes]", new EmojiBean("[eyes]", R.drawable.eyes));
        mDefaultEmojiMap.put("[ok]", new EmojiBean("[ok]", R.drawable.ok));
        mDefaultEmojiMap.put("[vs]", new EmojiBean("[vs]", R.drawable.vs));
        mDefaultEmojiMap.put("[poo]", new EmojiBean("[poo]", R.drawable.poo));
        mDefaultEmojiMap.put("[love]", new EmojiBean("[love]", R.drawable.love));
        mDefaultEmojiMap.put("[alien]", new EmojiBean("[alien]", R.drawable.alien));
        mDefaultEmojiMap.put("[chick]", new EmojiBean("[chick]", R.drawable.chick));
        mDefaultEmojiMap.put("[pill]", new EmojiBean("[pill]", R.drawable.pill));
        mDefaultEmojiMap.put("[phone]", new EmojiBean("[phone]", R.drawable.phone));
        mDefaultEmojiMap.put("[crown]", new EmojiBean("[crown]", R.drawable.crown));
        mDefaultEmojiMap.put("[ring]", new EmojiBean("[ring]", R.drawable.ring));
        mDefaultEmojiMap.put("[jewel]", new EmojiBean("[jewel]", R.drawable.jewel));
        mDefaultEmojiMap.put("[bomb]", new EmojiBean("[bomb]", R.drawable.bomb));
        mDefaultEmojiMap.put("[xmas_tree]", new EmojiBean("[xmas_tree]", R.drawable.xmas_tree));
        mDefaultEmojiMap.put("[police]", new EmojiBean("[police]", R.drawable.police));
        mDefaultEmojiMap.put("[home]", new EmojiBean("[home]", R.drawable.home));
        mDefaultEmojiMap.put("[school]", new EmojiBean("[school]", R.drawable.school));
        mDefaultEmojiMap.put("[boy]", new EmojiBean("[boy]", R.drawable.boy));
        mDefaultEmojiMap.put("[girl]", new EmojiBean("[girl]", R.drawable.girl));
        mDefaultEmojiMap.put("[man]", new EmojiBean("[man]", R.drawable.man));
        mDefaultEmojiMap.put("[woman]", new EmojiBean("[woman]", R.drawable.woman));
    }

    private static class EmojiManagerHodler {
        private static final EmojiManager INSTANCE = new EmojiManager();

        private EmojiManagerHodler() {
        }
    }

    public static EmojiManager getInstance() {
        return EmojiManagerHodler.INSTANCE;
    }

    public static Pattern getDefaultEmojiPattern() {
        return mDefaultEmojiPattern;
    }

    public Drawable getEmojiDrawable(Context context, String emoji) {
        if (TextUtils.isEmpty(emoji)) {
            return null;
        }
        try {
            return context.getResources().getDrawable(mDefaultEmojiMap.get(emoji).resource);
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    public EmojiBean getEmojiBean(Context context, String emoji) {
        if (TextUtils.isEmpty(emoji)) {
            return null;
        }
        return mDefaultEmojiMap.get(emoji);
    }


}

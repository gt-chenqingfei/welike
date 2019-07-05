package com.redefine.richtext.emoji.googlecompat;

import android.os.Build;
import android.support.annotation.NonNull;

import com.welike.emoji.EmojiProvider;
import com.welike.emoji.emoji.EmojiCategory;
import com.redefine.richtext.emoji.googlecompat.category.ActivityCategory;
import com.redefine.richtext.emoji.googlecompat.category.FlagsCategory;
import com.redefine.richtext.emoji.googlecompat.category.FoodCategory;
import com.redefine.richtext.emoji.googlecompat.category.NatureCategory;
import com.redefine.richtext.emoji.googlecompat.category.ObjectsCategory;
import com.redefine.richtext.emoji.googlecompat.category.PeopleCategory;
import com.redefine.richtext.emoji.googlecompat.category.SymbolsCategory;
import com.redefine.richtext.emoji.googlecompat.category.TravelCategory;

public final class GoogleCompatEmojiProvider implements EmojiProvider {
    private String[] supportList;

    public GoogleCompatEmojiProvider(String[] supportList) {
        this.supportList = supportList;
    }

    @Override
    @NonNull
    public EmojiCategory[] getCategories() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getAllCategories();
        } else {
            return getCategoriesFilterFlag();
        }
    }

    @Override
    public String[] getSupportEmojiWhiteList() {
        return supportList;
    }


    private EmojiCategory[] getAllCategories() {
        return new EmojiCategory[]{
                new PeopleCategory(),
                new NatureCategory(),
                new FoodCategory(),
                new ActivityCategory(),
                new TravelCategory(),
                new ObjectsCategory(),
                new SymbolsCategory(),
                new FlagsCategory()};
    }

    private EmojiCategory[] getCategoriesFilterFlag() {
        return new EmojiCategory[]{
                new PeopleCategory(),
                new NatureCategory(),
                new FoodCategory(),
                new ActivityCategory(),
                new TravelCategory(),
                new ObjectsCategory(),
                new SymbolsCategory()};
    }

}

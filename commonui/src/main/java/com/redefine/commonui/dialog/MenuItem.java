package com.redefine.commonui.dialog;

/**
 * Created by liwenbo on 2018/3/4.
 */

public class MenuItem {

    public MenuItem(int menuId, CharSequence menuText) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.isClickable = true;
    }

    public MenuItem(int menuId, CharSequence menuText, boolean isClickable) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.isClickable = isClickable;
    }

    public MenuItem(int menuId, CharSequence menuText, boolean isClickable, int textColor, int textSize) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.isClickable = isClickable;
        this.menuTextColor = textColor;
        this.menuTextSize = textSize;
    }

    public MenuItem(int menuId, CharSequence menuText, boolean isClickable, int textColor) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.isClickable = isClickable;
        this.menuTextColor = textColor;
    }

    public MenuItem(int menuId, CharSequence menuText, int textColor, int textSize) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.menuTextColor = textColor;
        this.menuTextSize = textSize;
    }

    public MenuItem(int menuId, CharSequence menuText, int textColor) {
        this.menuId = menuId;
        this.menuText = menuText;
        this.menuTextColor = textColor;
    }


    public int menuId;
    public CharSequence menuText;
    public boolean isClickable = true;
    public int menuTextColor;
    public int menuTextSize;
}

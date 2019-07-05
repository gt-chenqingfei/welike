package com.redefine.commonui.dialog;

public class MenuItemIdConstant {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    public static final int MENU_ITEM_DELETE = generateID();
    public static final int MENU_ITEM_ORIGINAL = generateID();
    public static final int MENU_ITEM_REPLY = generateID();
    public static final int MENU_ITEM_FORWARD = generateID();
    public static final int MENU_ITEM_FEED_BACK = generateID();
    public static final int MENU_ITEM_DISCARD = generateID();
    public static final int MENU_ITEM_CANCEL = generateID();
    public static final int MENU_ITEM_SAVE = generateID();
    public static final int MENU_ITEM_UNLIKE = generateID();
    public static final int MENU_ITEM_SHARE = generateID();
    public static final int MENU_ITEM_REPORT = generateID();
    public static final int MENU_ITEM_COPY = generateID();
    public static final int MENU_ITEM_TITLE = generateID();
    public static final int MENU_ITEM_BLOCK = generateID();
    public static final int MENU_ITEM_UNBLOCK = generateID();
    public static final int MENU_ITEM_NICK_NAME = generateID();

    public static final int MENU_ITEM_ONE_DAY = generateID();
    public static final int MENU_ITEM_THREE_DAY = generateID();
    public static final int MENU_ITEM_SEVEN_DAY = generateID();
    public static final int MENU_ITEM_NO_LIMIT = generateID();
    public static final int MENU_ITEM_ONE_MONTH = generateID();



    public static final int MENU_ITEM_SORT_HOT = generateID();
    public static final int MENU_ITEM_SORT_LATEST = generateID();

    public static final int MENU_ITEM_REPORT_PORN = generateID();
    public static final int MENU_ITEM_REPORT_VIOLATION = generateID();
    public static final int MENU_ITEM_REPORT_ANNOYING = generateID();
    public static final int MENU_ITEM_REPORT_INFRINGEMENT = generateID();
    public static final int MENU_ITEM_REPORT_COPYRIGHT = generateID();

    public static final int MENU_ITEM_TOP = generateID();
    public static final int MENU_ITEM_UNTOP = generateID();



}

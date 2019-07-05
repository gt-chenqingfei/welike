package com.redefine.welike.base.constant;

/**
 * Created by MR on 2018/1/17.
 */

public class CommonRequestCode {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return id_base++;
    }

    public static final int EDITOR_LAUNCH_CONTACT_CODE = generateID();
    public static final int EDITOR_CHOOSE_PIC_CODE = generateID();
    public static final int EDITOR_ARTICLE_CHOOSE_PIC_CODE = generateID();
    public static final int USER_HOST_CHOOSE_PIC_CODE = generateID();
    public static final int REGISTER_CHOOSE_HEADER_PIC_CODE = generateID();
    public static final int CHAT_SELECT_PIC_CODE = generateID();
    public static final int LOCATION_SELECT_REQUEST_CODE = generateID();
    public static final int CHOICE_TOPIC_CODE = generateID();
    public static final int RECORDER_VIDEO_CODE = generateID();
    public static final int EDITOR_POLL_CHOOSE_PIC_CODE = generateID();
    public static final int REQUEST_IMAGE_PICK_VIDEO_RECORD = generateID();
    public static final int REQUEST_IMAGE_PICK_SNAPSHOT = generateID();
    public static final int SHORT_CUT_IMAGE_PICK = generateID();
    public static final int SHORT_CUT_CHOICE_TOPIC_CODE = generateID();
    public static final int IMAGE_PICK_PREVIEW = generateID();
    public static final int PUBLISH_ARTICLE_PREVIEW = generateID();
    public static final int CHOSEN_SUPER_TOPIC = generateID();
    public static final int CHOSEN_REPORT_PIC_CODE = generateID();
    public static final int WEB_CHOOSE_PIC_CODE = generateID();

}

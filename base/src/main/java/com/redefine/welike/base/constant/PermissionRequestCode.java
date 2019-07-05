package com.redefine.welike.base.constant;

public class PermissionRequestCode {

    private static int id_base = 0x00000000;

    private static int generateID() {
        return ++id_base;
    }

    public static final int VIDEO_RECORD_PERMISSION_REQUEST_CODE = generateID();
    public static final int CAMERA_SNAPSHOT_PERMISSION_REQUEST_CODE = generateID();
    public static final int IMAGE_PICK_PERMISSION_REQUEST_CODE = generateID();
    public static final int LOCATION_PERMISSION_REQUEST = generateID();
    public static final int READ_CONTACT_PERMISSION = generateID();
    public static final int DOWNLOAD_VIDEO_PERMISSION = generateID();
    public static final int POST_STATUS_IMAGE_SAVE_CODE = generateID();

}

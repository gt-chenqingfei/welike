package com.redefine.sunny;

/**
 * Created by n.d on 2017/7/12.
 */

public class Result {
    public Result(int code, String toast, String message) {
        this.code = code;
        this.toast = toast;
        this.message = message;
    }

    public int code;
    public String toast;
    public String message;
}

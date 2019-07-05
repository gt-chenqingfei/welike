package com.redefine.welike.statistical;

/**
 * Created by nianguowang on 2018/5/3
 */
public interface Config {

    String GATHER_API = "collection/app";
    String REQUEST_PARAM_NAME = "name";
    String REQUEST_PARAM_COMMON = "common";
    String REQUEST_PARAM_DATA = "data";

    //缓存上报线程轮训时间间隔
    int UPLOAD_DURATION_CACHE = 15 * 1000;

    //批量上报线程轮训时间间隔
    int UPLOAD_DURATION_AMOUNT = 5 * 1000;
    int UPLOAD_DURATION_AMOUNT_WIFI = 2 * 1000;


    //批量上报，每次上报的数量
    int UPLOAD_COUNT_AMOUNT = 1000;
    //缓存上报，每次上报的数量
    int UPLOAD_COUNT_CACHE = 1000;


    //批量上报规避条件，CPU占用大于60%
    float CONDITION_CPU = 0.6f;

    //批量上报规避条件，内存占用大于90%
    float CONDITION_MEMORY = 0.9f;

    //批量上报规避条件，电池电量低于20%
    int CONDITION_BATTERY = 20;
}

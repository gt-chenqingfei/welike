package com.redefine.welike.business.startup.management;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/4/23
 */
public class PhoneNumberConveter {

    private static List<String> mNationCodes = new ArrayList<>();

    static {
        mNationCodes.add("+86");//中国
        mNationCodes.add("+91");//印度
//        mNationCodes.add("+66");//泰国
    }

    /**
     * 将带区号带的手机号解析出区号和手机号，例如：号码为"+8615090910963"，解析成区号86和手机号15090910963。
     * @param phone 带区号带手机号码
     * @return 长度为2的字符数组，index为0的是区号，为1的是电话号码
     */
    public static String[] convertPhone(String phone) {
        String[] phoneNumber = new String[2];
        for (String nationCode : mNationCodes) {
            if(phone.contains(nationCode)) {
                phoneNumber[0] = nationCode.substring(1, nationCode.length());
                phoneNumber[1] = phone.replace(nationCode, "");
                return phoneNumber;
            }
        }

        //TODO 如果电话号码不是前面带区号格式，则默认是印度区号，以后优化。
        phoneNumber[0] = "91";
        phoneNumber[1] = phone;
        return phoneNumber;
    }
}

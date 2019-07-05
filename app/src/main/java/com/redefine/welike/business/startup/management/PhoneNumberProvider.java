package com.redefine.welike.business.startup.management;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 获取手机号码，支持：
 * 1，从系统获取，如果系统版本大于M，支持双卡。
 * 2，从缓存获取，获取用户上一次登陆的手机号。
 */
public class PhoneNumberProvider {

    private static final String SP_MOBILE_NAME = "welike_mobile_list";
    private static final String SP_KEY_MOBILE = "mobile";

    private static final int SHOW_MOBILE_SIZE_MAX = 2;
    /**
     * 1、显示上一次登录的手机号码
     * 2、显示曾经登录过其他手机号码
     * 3、显示当前手机号码
     * 备选框中最多显示{@link #SHOW_MOBILE_SIZE_MAX}个备选手机号，优先级从1到3。
     * @param context
     * @return
     */
    public static String[] getShownPhoneNumber(Context context) {
        List<String> phoneNumberFromCache = getPhoneNumberFromCache(context);
        List<String> phoneNumberFromSystem = null;
        boolean hasPhonePermission = EasyPermissions.hasPermissions(context, Manifest.permission.READ_PHONE_STATE);
        if (hasPhonePermission) {
            phoneNumberFromSystem= getPhoneNumberFromSystem(context);
        }

        String[] shownPhone = new String[0];
        if(CollectionUtil.isEmpty(phoneNumberFromCache)) {
            if(CollectionUtil.isEmpty(phoneNumberFromSystem)) {
                return null;
            }
            return phoneNumberFromSystem.toArray(shownPhone);
        }

        if(phoneNumberFromCache.size() == SHOW_MOBILE_SIZE_MAX) {//直接返回全部手机号
            return phoneNumberFromCache.toArray(shownPhone);
        } else if(phoneNumberFromCache.size() > SHOW_MOBILE_SIZE_MAX) {//则直接返回前SHOW_MOBILE_SIZE_MAX个手机号
            String[] numberArray = new String[SHOW_MOBILE_SIZE_MAX];
            for (int i = 0; i < SHOW_MOBILE_SIZE_MAX; i++) {
                numberArray[i] = phoneNumberFromCache.get(i);
            }
            return numberArray;
        } else {//从系统获取手机号，并与phoneNumberFromCache合并之后取前SHOW_MOBILE_SIZE_MAX个手机号
            if(CollectionUtil.isEmpty(phoneNumberFromSystem)) {
                return phoneNumberFromCache.toArray(shownPhone);
            }
            for (String phone : phoneNumberFromSystem) {
                if(!phoneNumberFromCache.contains(phone)) {
                    phoneNumberFromCache.add(phone);
                }
            }
            if(phoneNumberFromCache.size() > SHOW_MOBILE_SIZE_MAX) {
                String[] numberArray = new String[SHOW_MOBILE_SIZE_MAX];
                for (int i = 0; i < SHOW_MOBILE_SIZE_MAX; i++) {
                    numberArray[i] = phoneNumberFromCache.get(i);
                }
                return numberArray;
            } else {
                return phoneNumberFromCache.toArray(shownPhone);
            }
        }
    }

    /**
     * 从系统获取手机号，如果系统版本大于M，支持获取双卡。
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    private static List<String> getPhoneNumberFromSystem(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getPhoneNumberForM(context);
        }
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (phoneMgr == null) {
            return null;
        }
        String line1Number = phoneMgr.getLine1Number();
        List<String> phoneList = new ArrayList<>();
        if(validPhoneNumber(line1Number)) {
            phoneList.add(line1Number);
        }
        StartEventManager.getInstance().setPhone_load(phoneList);
        return phoneList;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static List<String> getPhoneNumberForM(Context context) {
        List<SubscriptionInfo> subscription = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if(subscription == null) {
            return null;
        }

        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < subscription.size(); i++) {
            SubscriptionInfo info = subscription.get(i);
            String number = info.getNumber();
            if(validPhoneNumber(number)) {
                numbers.add(number);
            }
        }
        StartEventManager.getInstance().setPhone_load(numbers);
        return numbers;
    }

    private static boolean validPhoneNumber(String phoneNumber) {
        if(TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        if(phoneNumber.length() < 10) {
            return false;
        }
        return true;
    }

    /**
     * 获取历史登录过的手机号
     * @param context
     * @return
     */
    private static List<String> getPhoneNumberFromCache(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_MOBILE_NAME, Context.MODE_PRIVATE);
        String mobiles = sharedPreferences.getString(SP_KEY_MOBILE, null);
        if(TextUtils.isEmpty(mobiles)) {
            return null;
        }
        String[] mobileArray = mobiles.split(",");
        return new ArrayList<>(Arrays.asList(mobileArray));
    }

    public static void saveLoginPhone(Context context, String phone) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_MOBILE_NAME, Context.MODE_PRIVATE);
        String mobiles = sharedPreferences.getString(SP_KEY_MOBILE, null);
        StringBuilder newMobiles = new StringBuilder();
        if(!TextUtils.isEmpty(mobiles)) {
            String[] mobileArray = mobiles.split(",");
            List<String> mobileList = new ArrayList<>(Arrays.asList(mobileArray));
            if(mobileList.contains(phone)) {
                mobileList.remove(phone);
            }
            mobileList.add(0, phone);
            for (String mobile : mobileList) {
                newMobiles.append(mobile)
                        .append(",");
            }
        } else {
            newMobiles.append(phone).append(",");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SP_KEY_MOBILE, newMobiles.toString());
        editor.apply();
    }
}

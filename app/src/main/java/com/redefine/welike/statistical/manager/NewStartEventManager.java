package com.redefine.welike.statistical.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/7/28
 */
public enum  NewStartEventManager {
    INSTANCE;

    private static final String INSTALL_APP_SP = "install_manager";
    private static final String IS_LOGIN = "is_login";

    private boolean isFromLogin;
    private int login_source;
    private int return_result = -1;
    private int isNewUser = -1;
    private int language;
    private String phone_number;
    private int SMS_check;
    private int SMS_send = 1;
    private String nickname;
    private int nickname_check;
    private int verify_type;
    private int isLogoff;
    private int page_status;

    public void setLogin_source(int login_source) {
        this.login_source = login_source;
    }

    public void setReturn_result(int return_result) {
        this.return_result = return_result;
    }

    public void setIsNewUser(int isNewUser) {
        this.isNewUser = isNewUser;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void addSMS_check() {
        this.SMS_check++;
    }

    public void addSMS_send() {
        this.SMS_send++;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addNickname_check() {
        this.nickname_check++;
    }

    public void setVerify_type(int verify_type) {
        this.verify_type = verify_type;
    }

    public void setPage_status(int page_status) {
        this.page_status = page_status;
    }

    public void reset() {
        login_source = 0;
        return_result = -1;
        isNewUser = -1;
        language = 1;
        phone_number = null;
        SMS_check = 0;
        SMS_send = 1;
        nickname = null;
        nickname_check = 0;
        verify_type = 0;
        isLogoff = 0;
        page_status = 0;
    }

    private void initLogoff() {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(INSTALL_APP_SP, Context.MODE_PRIVATE);
        isLogoff = sp.getBoolean(IS_LOGIN, false) ? 1 : 0;
    }

    private void updateLogoff() {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(INSTALL_APP_SP, Context.MODE_PRIVATE);
        sp.edit().putBoolean(IS_LOGIN, true).apply();
    }

    private void initLanguage() {
        String language = LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
        this.language = language.equals(LocalizationManager.LANGUAGE_TYPE_ENG) ? EventConstants.LOGIN_LANGUAGE_EN : EventConstants.LOGIN_LANGUAGE_HI;
    }

    public void report1() {
        initLogoff();
        initLanguage();
        EventLog.NewLogin.report1(isLogoff, language);
    }
    public void report2() {
        EventLog.NewLogin.report2(login_source);
    }
    public void report3() {
        EventLog.NewLogin.report3(login_source, return_result);
    }
    public void report4(int count) {
        EventLog.NewLogin.report4(count);
    }
    public void report5() {
        initLogoff();
        EventLog.NewLogin.report5(isLogoff);
    }
    public void report6() {
        EventLog.NewLogin.report6(phone_number);
    }
    public void report7() {
        EventLog.NewLogin.report7(login_source);
    }
    public void report8() {
        EventLog.NewLogin.report8(login_source, return_result);
    }
    public void report9() {
        initLogoff();
        EventLog.NewLogin.report9(isNewUser, isLogoff, page_status);
    }
    public void report10() {
        EventLog.NewLogin.report10(login_source, page_status);
    }
    public void report11() {
        EventLog.NewLogin.report11(login_source, return_result, page_status);
    }
    public void report12() {
        EventLog.NewLogin.report12(isNewUser, phone_number, SMS_send, page_status);
    }
    public void report13() {
        EventLog.NewLogin.report13(isNewUser, phone_number, SMS_send, page_status);
    }
    public void report14() {
        EventLog.NewLogin.report14(isNewUser, phone_number, SMS_send, page_status, verify_type);
    }
    public void report15() {
        initLogoff();
        EventLog.NewLogin.report15(phone_number, SMS_send, page_status, isLogoff);
    }
    public void report16() {
        EventLog.NewLogin.report16(phone_number, SMS_send, verify_type);
    }
    public void report17() {
        EventLog.NewLogin.report17(nickname, nickname_check, phone_number, verify_type, SMS_send);
    }
    public void report18() {
        isFromLogin = true;
        initLogoff();
        initLanguage();
        EventLog.NewLogin.report18(login_source, return_result, isNewUser, phone_number, SMS_send, SMS_check,
                nickname, nickname_check, verify_type, isLogoff, page_status, this.language);
        updateLogoff();
    }
    public void report19() {
        if (isFromLogin) {
            initLogoff();
            initLanguage();
            EventLog.NewLogin.report19(login_source, return_result, isNewUser, phone_number, SMS_send, SMS_check,
                    nickname, nickname_check, verify_type, isLogoff, page_status, this.language);
            updateLogoff();
        }
    }

}

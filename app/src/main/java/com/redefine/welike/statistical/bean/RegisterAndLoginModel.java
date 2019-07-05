package com.redefine.welike.statistical.bean;

import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.statistical.EventLog;

import java.io.Serializable;

/**
 * Created by nianguowang on 2018/11/6
 */
public class RegisterAndLoginModel implements Serializable {

    public int smsSend;
    public int smsCheck;
    public int nickNameCheck;
    public int stayTime;
    public String nickName;
    public String language;
    public String phoneNumber;
    public EventLog.RegisterAndLogin.PageSource pageSource;
    public EventLog.RegisterAndLogin.TcInstalled tcInstalled;
    public EventLog.RegisterAndLogin.LoginVerifyType loginVerifyType;
    public EventLog.RegisterAndLogin.AccountStatus accountStatus;
    public EventLog.RegisterAndLogin.PageType pageType;
    public EventLog.RegisterAndLogin.LoginSource loginSource;
    public EventLog.RegisterAndLogin.ReturnResult returnResult;
    public EventLog.RegisterAndLogin.NewUser newUser;
    public EventLog.RegisterAndLogin.VerifyType verifyType;
    public EventLog.RegisterAndLogin.PageStatus pageStatus;
    public EventLog.RegisterAndLogin.InputWay inputWay;
    public EventLog.RegisterAndLogin.RequestWay requestWay;
    public EventLog.RegisterAndLogin.FromPage fromPage;
    public EventLog.RegisterAndLogin.VerifySource verifySource;

    public RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource pageSource) {
        this.pageSource = pageSource;
        this.language = LocalizationManager.getInstance().getCurrentLanguage();
        returnResult = EventLog.RegisterAndLogin.ReturnResult.INVALID;
        newUser = EventLog.RegisterAndLogin.NewUser.INVALID;
        pageType = EventLog.RegisterAndLogin.PageType.FULL_SCREEN;
        verifySource = EventLog.RegisterAndLogin.VerifySource.INVALID;
    }
}

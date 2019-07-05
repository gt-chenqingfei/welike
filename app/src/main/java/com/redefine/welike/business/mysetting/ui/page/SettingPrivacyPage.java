package com.redefine.welike.business.mysetting.ui.page;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.commonui.widget.switchbtn.SwitchButton;
import com.redefine.welike.R;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.mysetting.ui.vm.AccountOptionViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by gongguan on 2018/2/23.
 */
@Route(path = RouteConstant.SETTING_PRIVACY_ROUTE_PATH)
public class SettingPrivacyPage extends BaseActivity {


    private TextView tvContactTitle, tvSettingFindContactsModel, tvSettingFollowModel, settingPrivacyItem,
            tvMobileTitle, settingFollowItem, tvSettingMobileModel, tvTitle, settingPhoneItem, tvPrivacyInfo;

    private ImageView ivBack;

    private SwitchButton settingMobileContactsSwitch, settingFollowSwitch, settingMobileSwitch;

    private AccountOptionViewModel accountOptionViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_setting_privacy);

        initView();
        accountOptionViewModel = ViewModelProviders.of(this).get(AccountOptionViewModel.class);
        setEvent();

        accountOptionViewModel.getOption();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_common_back);
        tvTitle = findViewById(R.id.tv_common_title);
        tvContactTitle = findViewById(R.id.tv_contact_title);
        tvSettingFindContactsModel = findViewById(R.id.tv_setting_find_contacts_model);
        settingPrivacyItem = findViewById(R.id.setting_privacy_item);
        tvSettingFollowModel = findViewById(R.id.tv_setting_follow_model);
        tvMobileTitle = findViewById(R.id.tv_mobile_title);
        settingFollowItem = findViewById(R.id.setting_follow_item);
        tvSettingMobileModel = findViewById(R.id.tv_setting_mobile_model);
        settingPhoneItem = findViewById(R.id.setting_phone_item);
        tvPrivacyInfo = findViewById(R.id.tv_privacy_info);

        settingMobileContactsSwitch = findViewById(R.id.setting_mobile_contacts_switch);
        settingFollowSwitch = findViewById(R.id.setting_follow_switch);
        settingMobileSwitch = findViewById(R.id.setting_mobile_switch);

        tvTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_privacy"));

        tvContactTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_contact"));

        tvSettingFindContactsModel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_find_contacts"));
        settingPrivacyItem.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_find_contacts_info"));
        tvSettingFollowModel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_follow_by_phone"));
        tvMobileTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_mobile_model"));
        settingFollowItem.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_follow_by_phone_info"));
        tvSettingMobileModel.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_display_mobile_model"));
        settingPhoneItem.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_display_mobile_model_info"));
        tvPrivacyInfo.setText(highKeyword(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_privacy_info"),
                ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_privacy_match")));

        boolean isSwitched = SpManager.Setting.getCurrentMobileModelSetting(this);
        settingMobileSwitch.setChecked(!isSwitched);

        boolean isContactsSwitched = SpManager.Setting.getSettingMobileContactsModel(this);
        settingMobileContactsSwitch.setChecked(isContactsSwitched);

        boolean isFollowSwitched = SpManager.Setting.getSettingFollowByContactsModel(this);
        settingFollowSwitch.setChecked(isFollowSwitched);
    }


    private void setEvent() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvPrivacyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.luanch(SettingPrivacyPage.this, GlobalConfig.USER_SERVICE, R.color.white);
            }
        });

        settingMobileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SpManager.Setting.setCurrentMobileModelSetting(!b, SettingPrivacyPage.this);
            }
        });
        settingFollowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accountOptionViewModel.changeFollowOption(b);

            }
        });
        settingMobileContactsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                accountOptionViewModel.changeContactsOption(b);
            }
        });


        accountOptionViewModel.getAccountFollowStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) return;
                boolean isFollowSwitched = SpManager.Setting.getSettingFollowByContactsModel(SettingPrivacyPage.this);
                if (aBoolean != isFollowSwitched) {
                    settingFollowSwitch.setChecked(aBoolean);
                    SpManager.Setting.setFollowByContactsModel(aBoolean, SettingPrivacyPage.this);
                }
            }
        });

        accountOptionViewModel.getAccountContactsStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) return;
                boolean isContactsSwitched = SpManager.Setting.getSettingMobileContactsModel(SettingPrivacyPage.this);
                if (aBoolean != isContactsSwitched) {
                    settingMobileContactsSwitch.setChecked(aBoolean);
                    SpManager.Setting.setMobileContactsModel(aBoolean, SettingPrivacyPage.this);
                }
            }
        });

    }

    /**
     * 高亮某个关键字，如果有多个则全部高亮
     */
    private SpannableString highKeyword(String str, String key) {

        SpannableString sp = new SpannableString(str);

        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(str);

        while (m.find()) {  //通过正则查找，逐个高亮
            int start = m.start();
            int end = m.end();
            sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.common_menu_share_text_color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

}

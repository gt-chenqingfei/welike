package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.business.search.ui.viewholder.BaseSearchSugViewHolder;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gongguan
 * @time 2018/1/17 下午5:12
 */

public class ContactListViewHolder extends BaseSearchSugViewHolder<User> {
    protected TextView nameTx;
    protected VipAvatar headView;

    public ContactListViewHolder(View itemView) {
        super(itemView);
        nameTx = itemView.findViewById(R.id.tv_contacts_list_name);
        headView = itemView.findViewById(R.id.simple_contacts_list_headView);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, User data) {
        super.bindViews(adapter, data);

        VipUtil.set(headView, data.getHeadUrl(), data.getVip());
        VipUtil.setNickName(nameTx, data.getCurLevel(), data.getNickName());
    }

    public void bindBean(final User bean, String inputStr) {
//        HeadUrlLoader.getInstance().loadHeaderUrl(headView, bean.getHeadUrl());
        VipUtil.set(headView, bean.getHeadUrl(), bean.getVip());
        VipUtil.setNickName(nameTx, bean.getCurLevel(), bean.getNickName());

        nameTx.setText(getLightName(bean.getNickName(),  inputStr));
    }

    private SpannableString getLightName(String nickname, String inputStr) {
        if (nickname == null) {
            nickname = "";
        }
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(inputStr)) {
            return new SpannableString(nickname);
        }
        Pattern pattern = Pattern.compile(inputStr.toLowerCase());
        Matcher matcher = pattern.matcher(nickname.toLowerCase());
        SpannableString spanString = new SpannableString(nickname);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            spanString.setSpan(new ForegroundColorSpan(itemView.getResources().getColor(R.color.main)),
                    start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spanString;
    }

}

package com.redefine.welike.business.user.ui.view.state;


import android.view.View;
import android.widget.TextView;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nianguowang on 2018/6/28
 */
public abstract class AbstractProfileState implements IProfileState {

    protected User mUser;
    public AbstractProfileState(User user) {
        mUser = user;
    }

    @Override
    public void setInfluence(View influenceContainer) {
        if(mUser == null) {
            return;
        }
        List<UserBase.Intrest> influences = mUser.getInfluences();
        if(CollectionUtil.isEmpty(influences)) {
            influenceContainer.setVisibility(View.GONE);
        } else {
            influenceContainer.setVisibility(View.VISIBLE);
            TextView influence = influenceContainer.findViewById(R.id.tv_user_host_influence);
            TextView influenceReal = influenceContainer.findViewById(R.id.tv_user_host_influence_real);
            influence.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_influence_ex"));
            influenceReal.setText(parseInfluenceToString(influences));
        }
    }

    protected void sortInterest(List<UserBase.Intrest> intrests) {
        Collections.sort(intrests, new Comparator<UserBase.Intrest>() {
            @Override
            public int compare(UserBase.Intrest o1, UserBase.Intrest o2) {
                int o1Id, o2Id;
                try {
                    o1Id = Integer.parseInt(o1.getIid());
                    o2Id = Integer.parseInt(o2.getIid());
                } catch (Exception e) {
                    e.printStackTrace();
                    o1Id = 0;
                    o2Id = 0;
                }

                return o1Id - o2Id;
            }
        });
    }

    protected String parseInfluenceToString(List<UserBase.Intrest> influences) {
        StringBuilder influenceStr = new StringBuilder();
        for (UserBase.Intrest influence : influences) {
            influenceStr.append(influence.getLabel()).append(" ");
            List<UserBase.Intrest> subInfluence = influence.getSubInterest();
            if(!CollectionUtil.isEmpty(subInfluence)) {
                String s = parseInfluenceToString(subInfluence);
                influenceStr.append(s).append(" ");
            }
        }
        return influenceStr.toString();
    }

    protected String parseInterestToString(List<UserBase.Intrest> interests) {
        StringBuilder interestStr = new StringBuilder();
        for (UserBase.Intrest interest : interests) {
            List<UserBase.Intrest> subInterests = interest.getSubInterest();
            if(CollectionUtil.isEmpty(subInterests)) {
                interestStr.append(interest.getLabel()).append("  ");
            } else {
//                for (UserBase.Intrest subInterest : subInterests) {
//                    interestStr.append(subInterest.getLabel()).append(" ");
//                }
                sortInterest(subInterests);
                String sub = parseInterestToString(subInterests);
                interestStr.append(sub).append(" ");
            }
        }
        return interestStr.toString();
    }
}

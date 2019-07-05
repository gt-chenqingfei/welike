package com.redefine.welike.business.feeds.management.provider;

import com.redefine.welike.base.profile.bean.UserBase;

import java.util.ArrayList;
import java.util.List;

public class InterestCategoryLocalStrategy implements IInterestCategoryStrategy {
    @Override
    public void provide(IInterestCategoryCallback callback) {
        callback.onInterestCategorySuccess(getInterestCategoryList());
    }

    private List<UserBase.Intrest> getInterestCategoryList() {
        List<UserBase.Intrest> list = new ArrayList<>();
        UserBase.Intrest bsInterest = new UserBase.Intrest();
//        bsInterest.setIcon();
        bsInterest.setLabel("B&S");
        list.add(bsInterest);

        UserBase.Intrest cricketInterest = new UserBase.Intrest();
//        cricketInterest.setIcon();
        cricketInterest.setLabel("cricket");
        list.add(cricketInterest);

        UserBase.Intrest entInterest = new UserBase.Intrest();
//        entInterest.setIcon();
        entInterest.setLabel("Ent");
        list.add(entInterest);

        UserBase.Intrest eduInterest = new UserBase.Intrest();
//        eduInterest.setIcon();
        eduInterest.setLabel("Edu");
        list.add(eduInterest);

        UserBase.Intrest comedyInterest = new UserBase.Intrest();
//        comedyInterest.setIcon();
        comedyInterest.setLabel("Comedy");
        list.add(comedyInterest);

        UserBase.Intrest lifesyleInterest = new UserBase.Intrest();
//        lifesyleInterest.setIcon();
        lifesyleInterest.setLabel("Lifestyle");
        list.add(lifesyleInterest);

        UserBase.Intrest newsInterest = new UserBase.Intrest();
//        newsInterest.setIcon();
        newsInterest.setLabel("News");
        list.add(newsInterest);

        UserBase.Intrest techInterest = new UserBase.Intrest();
//        techInterest.setIcon();
        techInterest.setLabel("Tech");
        list.add(techInterest);

        return list;
    }
}

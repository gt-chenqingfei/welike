package com.redefine.welike.business.feeds.management.provider;

public class InterestCategoryProvider {

    private IInterestCategoryStrategy mStrategory;

    public InterestCategoryProvider() {
        mStrategory = new InterestCategoryRemoteStrategy();
    }

    public void setStragety(IInterestCategoryStrategy stragety) {
        mStrategory = stragety;
    }

    public void provider(IInterestCategoryCallback callback) {
        mStrategory.provide(callback);
    }
}

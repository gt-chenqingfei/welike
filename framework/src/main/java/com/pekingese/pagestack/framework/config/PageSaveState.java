/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/12/30
 * <p>
 * Description : 用于保存单个Page的View的状态类
 * <p>
 * Creation    : 17/12/30
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/12/30, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.config;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PageSaveState implements Parcelable {

    public Bundle mSaveState;

    public PageSaveState(Bundle saveState) {
        mSaveState = saveState;
    }

    public PageSaveState() {
        mSaveState = new Bundle();
    }

    protected PageSaveState(Parcel in) {
        mSaveState = in.readBundle(getClass().getClassLoader());
    }

    public static final Parcelable.Creator<PageSaveState> CREATOR = new Parcelable.Creator<PageSaveState>() {
        @Override
        public PageSaveState createFromParcel(Parcel in) {
            return new PageSaveState(in);
        }

        @Override
        public PageSaveState[] newArray(int size) {
            return new PageSaveState[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(mSaveState);
    }
}

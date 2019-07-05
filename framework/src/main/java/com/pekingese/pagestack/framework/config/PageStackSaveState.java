/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/9/21
 * <p>
 * Description : description
 * <p>
 * Creation    : 2017/9/21
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/9/21, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.pekingese.pagestack.framework.util.CollectionUtil;

import java.util.Stack;

public class PageStackSaveState implements Parcelable {

    public Parcelable[] mSaveState;

    public PageStackSaveState(Stack<PageConfig> saveState) {
        if (CollectionUtil.isArrayEmpty(saveState)) {
            mSaveState = new Parcelable[]{};
        } else {
            mSaveState = new Parcelable[saveState.size()];
            saveState.toArray(mSaveState);
        }

    }

    protected PageStackSaveState(Parcel in) {
        mSaveState = in.readParcelableArray(getClass().getClassLoader());
    }

    public static final Creator<PageStackSaveState> CREATOR = new Creator<PageStackSaveState>() {
        @Override
        public PageStackSaveState createFromParcel(Parcel in) {
            return new PageStackSaveState(in);
        }

        @Override
        public PageStackSaveState[] newArray(int size) {
            return new PageStackSaveState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelableArray(mSaveState, 0);
    }
}

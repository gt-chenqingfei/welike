/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/11/8
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/11/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/11/8, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.state;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@link Parcelable} implementation that should be used by inheritance
 * hierarchies to ensure the state of all classes along the chain is saved.
 */
public abstract class AbsSavedState implements Parcelable {
    public static final AbsSavedState EMPTY_STATE = new AbsSavedState() {};

    private final Parcelable mSuperState;

    /**
     * Constructor used to make the EMPTY_STATE singleton
     */
    private AbsSavedState() {
        mSuperState = null;
    }

    /**
     * Constructor called by derived classes when creating their SavedState objects
     *
     * @param superState The state of the superclass of this view
     */
    protected AbsSavedState(Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        mSuperState = superState != EMPTY_STATE ? superState : null;
    }

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     *
     * @param source parcel to read from
     */
    protected AbsSavedState(Parcel source) {
        this(source, null);
    }

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     *
     * @param source parcel to read from
     * @param loader ClassLoader to use for reading
     */
    protected AbsSavedState(Parcel source, ClassLoader loader) {
        Parcelable superState = source.readParcelable(loader);
        mSuperState = superState != null ? superState : EMPTY_STATE;
    }

    public final Parcelable getSuperState() {
        return mSuperState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSuperState, flags);
    }

    public static final Parcelable.Creator<AbsSavedState> CREATOR = new Creator<AbsSavedState>() {
        @Override
        public AbsSavedState createFromParcel(Parcel in) {
            Parcelable superState = in.readParcelable(getClass().getClassLoader());
            if (superState != null) {
                throw new IllegalStateException("superState must be null");
            }
            return EMPTY_STATE;
        }

        @Override
        public AbsSavedState[] newArray(int size) {
            return new AbsSavedState[size];
        }
    };

}

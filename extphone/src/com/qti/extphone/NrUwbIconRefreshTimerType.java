/*
 * Copyright (c) 2022 Qualcomm Innovation Center, Inc. All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause-Clear
 */

package com.qti.extphone;

import android.os.Parcel;
import android.os.Parcelable;

public class NrUwbIconRefreshTimerType implements Parcelable {

    private static final String TAG = "NrUwbIconRefreshTimerType";

    public static final int SCG_TO_MCG = 0;
    public static final int IDLE_TO_CONNECT = 1;
    public static final int IDLE = 2;

    private int mNrUwbIconRefreshTimerType;

    public NrUwbIconRefreshTimerType(int type) {
        mNrUwbIconRefreshTimerType = type;
    }

    public NrUwbIconRefreshTimerType(Parcel in) {
        mNrUwbIconRefreshTimerType = in.readInt();
    }

    public int get() {
        return mNrUwbIconRefreshTimerType;
    }

    public static boolean isValid(int mode) {
        switch (mode) {
            case SCG_TO_MCG:
            case IDLE_TO_CONNECT:
            case IDLE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mNrUwbIconRefreshTimerType);
    }

    public static final Parcelable.Creator<NrUwbIconRefreshTimerType> CREATOR =
            new Parcelable.Creator() {
        public NrUwbIconRefreshTimerType createFromParcel(Parcel in) {
            return new NrUwbIconRefreshTimerType(in);
        }

        public NrUwbIconRefreshTimerType[] newArray(int size) {
            return new NrUwbIconRefreshTimerType[size];
        }
    };

    public void readFromParcel(Parcel in) {
        mNrUwbIconRefreshTimerType = in.readInt();
    }

    @Override
    public String toString() {
        return TAG + ": " + get();
    }
}

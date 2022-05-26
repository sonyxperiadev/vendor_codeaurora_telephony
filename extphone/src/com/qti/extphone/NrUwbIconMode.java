/*
 * Copyright (c) 2022 Qualcomm Innovation Center, Inc. All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause-Clear
 */

package com.qti.extphone;

import android.os.Parcel;
import android.os.Parcelable;

public class NrUwbIconMode implements Parcelable {

    private static final String TAG = "NrUwbIconMode";

    public static final int NONE = 0;
    public static final int CONNECTED = 1;
    public static final int IDLE = 2;
    public static final int CONNECTED_AND_IDLE = 3;

    private int mNrUwbIconMode;

    public NrUwbIconMode(int mode) {
        mNrUwbIconMode = mode;
    }

    public NrUwbIconMode(Parcel in) {
        mNrUwbIconMode = in.readInt();
    }

    public int get() {
        return mNrUwbIconMode;
    }

    public static boolean isValid(int mode) {
        switch (mode) {
            case NONE:
            case CONNECTED:
            case IDLE:
            case CONNECTED_AND_IDLE:
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
        out.writeInt(mNrUwbIconMode);
    }

    public static final Parcelable.Creator<NrUwbIconMode> CREATOR = new Parcelable.Creator() {
        public NrUwbIconMode createFromParcel(Parcel in) {
            return new NrUwbIconMode(in);
        }

        public NrUwbIconMode[] newArray(int size) {
            return new NrUwbIconMode[size];
        }
    };

    public void readFromParcel(Parcel in) {
        mNrUwbIconMode = in.readInt();
    }

    @Override
    public String toString() {
        return TAG + ": " + get();
    }
}

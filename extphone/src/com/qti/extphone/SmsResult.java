/*
 * Copyright (c) 2021, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of The Linux Foundation nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qti.extphone;

import android.os.Parcel;
import android.os.Parcelable;

public class SmsResult implements Parcelable{
    private static final String TAG = "SmsResult";

    private int mMessageRef;
    private String mAckPDU;
    private int mErrorCode;

    public SmsResult(int messageRef, String ackPDU, int errorCode) {
        mMessageRef = messageRef;
        mAckPDU = ackPDU;
        mErrorCode = errorCode;
    }

    public SmsResult(Parcel in) {
        mMessageRef = in.readInt();
        mAckPDU = in.readString();
        mErrorCode = in.readInt();
    }

    public int getMessageRef() {
        return mMessageRef;
    }

    public String getAckPDU() {
        return mAckPDU;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mMessageRef);
        out.writeString(mAckPDU);
        out.writeInt(mErrorCode);
    }

    public static final Parcelable.Creator<SmsResult> CREATOR = new Parcelable.Creator() {
        public SmsResult createFromParcel(Parcel in) {
            return new SmsResult(in);
        }

        public SmsResult[] newArray(int size) {
            return new SmsResult[size];
        }
    };

    public void readFromParcel(Parcel in) {
        mMessageRef = in.readInt();
        mAckPDU = in.readString();
        mErrorCode = in.readInt();
    }

    @Override
    public String toString() {
        return "SmsResult{" + "mMessageRef=" + getMessageRef() + ", mErrorCode=" + getErrorCode() +
                ", mAckPDU='" +  getAckPDU() + '\'' +  "}";
    }
}

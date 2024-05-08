/*
 * Copyright (c) 2020-2021, The Linux Foundation. All rights reserved.
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

import android.util.Log;
import android.os.Message;
import android.content.Context;
import android.os.IBinder;
import android.os.Handler;
import android.os.RemoteException;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;

/**
* ExtTelephonyManager class provides ExtTelephonyService interface to
* the clients. Clients needs to instantiate this class to use APIs from
* IExtPhone.aidl. ExtTelephonyManager class has the logic to connect
* or disconnect to the bound service.
*/
public class ExtTelephonyManager {

    private static final String LOG_TAG = "ExtTelephonyManager";
    private static final boolean DBG = true;
    private static Context mContext;
    private Boolean mServiceConnected;
    private ExtTelephonyServiceConnection mConnection;
    private IExtPhone mExtTelephonyService = null;
    private Handler mServiceConnectionStatusHandler = null;
    private int mServiceConnectionStatusId;
    private int INVALID = -1;
    private static ExtTelephonyManager mInstance;
    private ServiceCallback mServiceCb = null;
    private static int mClientCount = 0;

    /**
    * Constructor
    * @param context context in which the bindService will be
    *                initiated.
    */
    public ExtTelephonyManager(Context context){
        this.mContext = context;
        mServiceConnected = false;
        log("ExtTelephonyManager() ...");
    }

    /**
    * This method returns the singleton instance of ExtTelephonyManager object
    */
    public static synchronized ExtTelephonyManager getInstance(Context context) {
        synchronized (ExtTelephonyManager.class) {
            if (mInstance == null) {
                mInstance = new ExtTelephonyManager(context);
            }
            return mInstance;
        }
    }

    /**
    * To check if the service is connected or not
    * @return boolean true if service is connected, false oterwise
    */
    public boolean isServiceConnected() {
        return mServiceConnected;
    }

    /**
    * Initiate connection with the service.
    *
    * @param serviceCallback {@link ServiceCallback} to receive
    *                        service-level callbacks.
    *
    * @return boolean Immediate result of the operation. true if
    *                 successful.
    *                 NOTE: This does not garuntee a successful
    *                 connection. The client needs to use handler
    *                 to listen to the Result.
    */
    public boolean connectService(ServiceCallback cb){
        mServiceCb = cb;
        mClientCount += 1;
        log("Creating ExtTelephonyService. If not started yet, start ...");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.qti.phone",
                                              "com.qti.phone.ExtTelephonyService"));
        mConnection = new ExtTelephonyServiceConnection();
        boolean success = mContext.bindService(intent, mConnection,
                                              Context.BIND_AUTO_CREATE);
        log("bind Service result: " + success);
        return success;
    }

    /**
    * Disconnect the connection with the Service.
    *
    */
    public void disconnectService() {
        log( "disconnectService() mClientCount="+mClientCount);
        if (mClientCount > 0) mClientCount -= 1;
        if (mClientCount <= 0 && mConnection != null) {
            mContext.unbindService(mConnection);
            mConnection = null;
        }
    }

    /**
    *
    * Internal helper functions/variables
    */
    private class ExtTelephonyServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mExtTelephonyService = IExtPhone.Stub.asInterface((IBinder) boundService);
            if (mExtTelephonyService == null) {
                log("ExtTelephonyService Connect Failed (onServiceConnected)... ");
            } else {
                log("ExtTelephonyService connected ... ");
            }
            mServiceConnected = true;
            if (mServiceCb != null) {
                mServiceCb.onConnected();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            log("The connection to the service got disconnected!");
            mExtTelephonyService = null;
            mServiceConnected = false;
            if (mServiceCb != null) {
                mServiceCb.onDisconnected();
            }
        }
    }

    /**
    * Get value assigned to vendor property
    * @param - property name
    * @param - default value of property
    * @return - integer value assigned
    */
    public int getPropertyValueInt(String property, int def) {
        int ret = INVALID;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        log("getPropertyValueInt: property=" + property);
        try {
            ret = mExtTelephonyService.getPropertyValueInt(property, def);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "getPropertyValueInt, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Get value assigned to vendor property
    * @param - property name
    * @param - default value of property
    * @return - boolean value assigned
    */
    public boolean getPropertyValueBool(String property, boolean def) {
        boolean ret = def;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        log("getPropertyValueBool: property=" + property);
        try {
            ret = mExtTelephonyService.getPropertyValueBool(property, def);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "getPropertyValueBool, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Get value assigned to vendor property
    * @param - property name
    * @param - default value of property
    * @return - string value assigned
    */
    public String getPropertyValueString(String property, String def) {
        String ret = def;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        log("getPropertyValueString: property=" + property);
        try {
            ret = mExtTelephonyService.getPropertyValueString(property, def);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "getPropertyValueString, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Check if slotId has PrimaryCarrier SIM card present or not.
    * @param - slotId
    * @return true or false
    */
    public boolean isPrimaryCarrierSlotId(int slotId) {
        boolean ret = false;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        try {
            ret = mExtTelephonyService.isPrimaryCarrierSlotId(slotId);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "isPrimaryCarrierSlotId, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Get current primary card slot Id.
    * @param - void
    * @return slot index
    */
    public int getCurrentPrimaryCardSlotId() {
        int ret = INVALID;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        try {
            ret = mExtTelephonyService.getCurrentPrimaryCardSlotId();
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "getCurrentPrimaryCardSlotId, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Returns ID of the slot in which PrimaryCarrier SIM card is present.
    * If none of the slots contains PrimaryCarrier SIM, this would return '-1'
    * Supported values: 0, 1, -1
    */
    public int getPrimaryCarrierSlotId() {
        int ret = INVALID;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        try {
            ret = mExtTelephonyService.getPrimaryCarrierSlotId();
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "getPrimaryCarrierSlotId, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Set Primary card on given slot.
    * @param - slotId to be set as Primary Card.
    * @return void
    */
    public void setPrimaryCardOnSlot(int slotId) {
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return;
        }
        try {
            mExtTelephonyService.setPrimaryCardOnSlot(slotId);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "setPrimaryCardOnSlot, remote exception");
            e.printStackTrace();
        }
    }

    /**
    * Perform incremental scan using QCRIL hooks.
    * @param - slotId
    *          Range: 0 <= slotId < {@link TelephonyManager#getActiveModemCount()}
    * @return true if the request has successfully been sent to the modem, false otherwise.
    * Requires permission: android.Manifest.permission.MODIFY_PHONE_STATE
    */
    public boolean performIncrementalScan(int slotId) {
        boolean ret = false;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        try {
            ret = mExtTelephonyService.performIncrementalScan(slotId);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "performIncrementalScan, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Abort incremental scan using QCRIL hooks.
    * @param - slotId
    *          Range: 0 <= slotId < {@link TelephonyManager#getActiveModemCount()}
    * @return true if the request has successfully been sent to the modem, false otherwise.
    * Requires permission: android.Manifest.permission.MODIFY_PHONE_STATE
    */
    public boolean abortIncrementalScan(int slotId) {
        boolean ret = false;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        try {
            ret = mExtTelephonyService.abortIncrementalScan(slotId);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "abortIncrementalScan, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Check for Sms Prompt is Enabled or Not.
    * @return
    *        true - Sms Prompt is Enabled
    *        false - Sms prompt is Disabled
    * Requires Permission: android.Manifest.permission.READ_PHONE_STATE
    */
    public boolean isSMSPromptEnabled() {
        boolean ret = false;
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return ret;
        }
        try {
            ret = mExtTelephonyService.isSMSPromptEnabled();
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "isSMSPromptEnabled, remote exception");
            e.printStackTrace();
        }
        return ret;
    }

    /**
    * Enable/Disable Sms prompt option.
    * @param - enabled
    *        true - to enable Sms prompt
    *        false - to disable Sms prompt
    * Requires Permission: android.Manifest.permission.MODIFY_PHONE_STATE
    */
    public void setSMSPromptEnabled(boolean enabled) {
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return;
        }
        try {
            mExtTelephonyService.setSMSPromptEnabled(enabled);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "setSMSPromptEnabled, remote exception");
            e.printStackTrace();
        }
    }

    /**
    * supply pin to unlock sim locked on network.
    * @param - netpin - network pin to unlock the sim.
    * @param - type - PersoSubState for which the sim is locked onto.
    * @param - callback - callback to notify UI, whether the request was success or failure.
    * @param - phoneId - slot id on which the pin request is sent.
    * @return void
    */
    public void supplyIccDepersonalization(String netpin, String type,
            IDepersoResCallback callback, int phoneId) {
        if (!mServiceConnected) {
            Log.e(LOG_TAG, "service not connected!");
            return;
        }
        try {
            mExtTelephonyService.supplyIccDepersonalization(netpin,
                    type, callback, phoneId);
        } catch(RemoteException e) {
            Log.e(LOG_TAG, "supplyIccDepersonalization, remote exception");
            e.printStackTrace();
        }
    }

    private void log(String str) {
        if (DBG) {
            Log.d(LOG_TAG, str);
        }
    }
}

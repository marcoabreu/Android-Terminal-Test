package com.marcoabreu.att.device;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.marcoabreu.att.script.DeviceRuntimeContainer;

/**
 * Created by AbreuM on 05.08.2016.
 */
public class Informations {
    public static String getPhoneNumber(DeviceRuntimeContainer runtime) {
        if(true) return "1234567890123456";
        TelephonyManager tMgr = (TelephonyManager)runtime.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber; //Not all devices supply this information
    }
}

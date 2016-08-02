package com.marcoabreu.att.aaascripts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.marcoabreu.att.helper.WaitHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

/**
 * Created by AbreuM on 14.07.2016.
 */
public class CallScript extends ScriptBase {
    public void callNumber(Activity appActivity, String phoneNumber) {
        String url = "tel:" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        if (ActivityCompat.checkSelfPermission(appActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("No permission - you forgot to grant them on the start");
        }
        appActivity.startActivity(intent);

        //Wait while idle
        WaitHelper.waitWhile(() -> getCallState(appActivity) == CallState.CALL_STATE_IDLE);
    }

    public void endCall(Activity appActivity) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        //Taken from http://stackoverflow.com/questions/20965702/end-incoming-call-programmatically
        String serviceManagerName = "android.os.ServiceManager";
        String serviceManagerNativeName = "android.os.ServiceManagerNative";
        String telephonyName = "com.android.internal.telephony.ITelephony";
        Class<?> telephonyClass;
        Class<?> telephonyStubClass;
        Class<?> serviceManagerClass;
        Class<?> serviceManagerNativeClass;
        Method telephonyEndCall;
        Object telephonyObject;
        Object serviceManagerObject;
        telephonyClass = Class.forName(telephonyName);
        telephonyStubClass = telephonyClass.getClasses()[0];
        serviceManagerClass = Class.forName(serviceManagerName);
        serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
        Method getService = // getDefaults[29];
                serviceManagerClass.getMethod("getService", String.class);
        Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
        Binder tmpBinder = new Binder();
        tmpBinder.attachInterface(null, "fake");
        serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
        IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
        Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
        telephonyObject = serviceMethod.invoke(null, retbinder);
        telephonyEndCall = telephonyClass.getMethod("endCall");
        telephonyEndCall.invoke(telephonyObject);

        //Wait till IDLE
        WaitHelper.waitWhile(() -> getCallState(appActivity) != CallState.CALL_STATE_IDLE);
    }

    public CallState getCallState(Activity appActivity) {
        TelephonyManager tm = (TelephonyManager) appActivity.getSystemService(android.content.Context.TELEPHONY_SERVICE);

        int callStateRaw = tm.getCallState();

        CallState callState = CallState.fromInteger(callStateRaw);

        Log.i("CallScript", callState.toString());

        return callState;
    }

    enum CallState{
        CALL_STATE_IDLE(TelephonyManager.CALL_STATE_IDLE),
        CALL_STATE_RINGING(TelephonyManager.CALL_STATE_RINGING),
        CALL_STATE_OFFHOOK(TelephonyManager.CALL_STATE_OFFHOOK);

        private final int value;

        private CallState(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static CallState fromInteger(int value) {
            for(CallState state : CallState.values()) {
                if(state.getValue() == value) {
                    return state;
                }
            }

            throw new NoSuchElementException("Undefined " + value);
        }
    }
}

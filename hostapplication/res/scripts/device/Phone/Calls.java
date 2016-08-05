package com.marcoabreu.att.listener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.marcoabreu.att.script.DeviceRuntimeContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by AbreuM on 01.08.2016.
 */
public class Calls {
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


    /**
     * Created by AbreuM on 01.08.2016.
     */
    public static void startCall(DeviceRuntimeContainer runtime) throws InterruptedException {
        System.out.println("startCall");

        for (Map.Entry entry : runtime.getParameters().entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        String url = "tel:" + runtime.getParameters().get("phoneNumber");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        if (ActivityCompat.checkSelfPermission(runtime.getAppContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("No permission - you forgot to grant them on the start");
        }
        runtime.getAppContext().startActivity(intent);

        //Wait while idle
        while(getCallState(runtime.getAppContext()) == CallState.CALL_STATE_IDLE) {
            Thread.sleep(100);
        }
    }

    private static CallState getCallState(Context appContext) {
        TelephonyManager tm = (TelephonyManager) appContext.getSystemService(android.content.Context.TELEPHONY_SERVICE);

        int callStateRaw = tm.getCallState();

        CallState callState = CallState.fromInteger(callStateRaw);

        return callState;
    }

    public static void endCall(DeviceRuntimeContainer runtime) throws InterruptedException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
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

        //Wait till idle
        while(getCallState(runtime.getAppContext()) != CallState.CALL_STATE_IDLE) {
            Thread.sleep(100);
        }
    }


}
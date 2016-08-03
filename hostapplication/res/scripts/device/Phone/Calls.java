package com.marcoabreu.att.aaascripts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.marcoabreu.att.helper.WaitHelper;
import com.marcoabreu.att.script.DeviceRuntimeContainer;

import java.util.Map;
import java.util.NoSuchElementException;

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
public static void startCall(DeviceRuntimeContainer runtime) {
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
        //WaitHelper.waitWhile(() -> getCallState(runtime.getAppContext()) == CallState.CALL_STATE_IDLE);
        }

private static CallState getCallState(Context appContext) {
        TelephonyManager tm = (TelephonyManager) appContext.getSystemService(android.content.Context.TELEPHONY_SERVICE);

        int callStateRaw = tm.getCallState();

        CallState callState = CallState.fromInteger(callStateRaw);

        return callState;
        }


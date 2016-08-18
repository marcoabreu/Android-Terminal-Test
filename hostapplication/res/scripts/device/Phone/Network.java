package com.marcoabreu.att;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.marcoabreu.att.script.DeviceRuntimeContainer;

import java.util.HashMap;
import java.util.Map;

import static android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT;
import static android.telephony.TelephonyManager.NETWORK_TYPE_CDMA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EHRPD;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_IDEN;
import static android.telephony.TelephonyManager.NETWORK_TYPE_LTE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_UMTS;

/**
 * Created by AbreuM on 18.08.2016.
 */
public class Network {
    private static final String TAG = Network.class.toString();
    private static final Map<Integer, NetworkType> networkTypeMapping;

    static {
        networkTypeMapping = new HashMap<>();
        networkTypeMapping.put(NETWORK_TYPE_CDMA, NetworkType.TwoG);
        networkTypeMapping.put(NETWORK_TYPE_EDGE, NetworkType.TwoG);
        networkTypeMapping.put(NETWORK_TYPE_GPRS, NetworkType.TwoG);
        networkTypeMapping.put(NETWORK_TYPE_1xRTT, NetworkType.TwoG);
        networkTypeMapping.put(NETWORK_TYPE_IDEN, NetworkType.TwoG);

        networkTypeMapping.put(NETWORK_TYPE_UMTS, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_EVDO_0, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_EVDO_A, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_HSDPA, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_HSUPA, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_HSPA, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_EVDO_B, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_EHRPD, NetworkType.ThreeG);
        networkTypeMapping.put(NETWORK_TYPE_HSPAP, NetworkType.ThreeG);

        networkTypeMapping.put(NETWORK_TYPE_LTE, NetworkType.FourG);

    }

    private static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    public static void awaitPhoneConnectivity(DeviceRuntimeContainer runtime) throws InterruptedException {
        String connectionType = (String) runtime.getParameters().get("type");
        String cellId = (String) runtime.getParameters().get("cell");

        while(!getNetworkClass(runtime.getAppContext()).equals(connectionType)) {
            Log.d(TAG, "Currently connected to " + getNetworkClass(runtime.getAppContext()) + " and waiting for " + connectionType);
            Thread.sleep(200);
        }
    }

    public static void awaitDataConnectivity(DeviceRuntimeContainer runtime) throws InterruptedException {

    }



    public static void awaitConnectivity(DeviceRuntimeContainer runtime) throws InterruptedException {
        if(true)throw new RuntimeException("do not use");
        String connectionType = (String) runtime.getParameters().get("type");
        String cellId = (String) runtime.getParameters().get("cell");

        NetworkType expectedNetworkType = null;
        switch(connectionType) {
            case "2G":
                expectedNetworkType = NetworkType.TwoG;
                break;
            case "3G":
                expectedNetworkType = NetworkType.ThreeG;
                break;
            case "4G":
                expectedNetworkType = NetworkType.FourG;
                break;
            default:
                throw new IllegalArgumentException("Unknown connectionType " + connectionType);
        }

        Log.i(TAG, "Waiting for connection to " + expectedNetworkType.toString());

        ConnectivityManager cm = (ConnectivityManager) runtime.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        while(true) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int networkSubtype = networkInfo.getSubtype();
                NetworkType networkType = networkTypeMapping.get(networkSubtype);
                if(networkType == null) {
                    Log.e(TAG, "Unmapped network subtype " + networkSubtype);
                } else {
                    if(networkType == expectedNetworkType) {
                        break;
                    } else {
                        Log.d(TAG, "Currently connected to " + networkType + " via " + networkInfo.getSubtypeName());
                    }
                }
            }

            Thread.sleep(250);
        }
        /*
        2G:
        NETWORK_TYPE_CDMA
        NETWORK_TYPE_EDGE
        NETWORK_TYPE_GPRS


        3G:
        NETWORK_TYPE_HSDPA
        NETWORK_TYPE_HSPA
        NETWORK_TYPE_HSUPA
        NETWORK_TYPE_UMTS


        4G:
        NETWORK_TYPE_LTE

        Unknown:
        1xRTT
        NETWORK_TYPE_EVDO_0
        NETWORK_TYPE_EVDO_A
        NETWORK_TYPE_EHRPD
        NETWORK_TYPE_EVDO_B
        NETWORK_TYPE_HSPAP
        NETWORK_TYPE_IDEN
        NETWORK_TYPE_UNKNOWN
         */
    }

    enum NetworkType {
        TwoG,
        ThreeG,
        FourG
    }
}
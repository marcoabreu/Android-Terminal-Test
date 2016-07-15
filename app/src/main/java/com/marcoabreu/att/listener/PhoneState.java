package com.marcoabreu.att.listener;

import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by AbreuM on 14.07.2016.
 */
@Deprecated
public class PhoneState extends PhoneStateListener {
    //Do not use, it was just a test!


    private static ServiceState lastServiceState;
    private static CellLocation lastCellLocation;
    private static SignalStrength lastSignalStrength;
    private static CallState lastCallState;
    private static String lastCalledNumber;
    private static DataActivity lastDataActivity;
    private static DataState lastDataState;
    //private static NetworkType lastDataStateNetworkType;


    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        PhoneState.lastServiceState = serviceState;
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        super.onMessageWaitingIndicatorChanged(mwi);
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        super.onCallForwardingIndicatorChanged(cfi);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        PhoneState.lastCellLocation = location;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        PhoneState.lastCallState = CallState.fromInteger(state);
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        PhoneState.lastDataState = DataState.fromInteger(state);
        //lastDataStateNetworkType = NetworkType.fromInteger(networkType);
    }

    @Override
    public void onDataActivity(int direction) {
        super.onDataActivity(direction);
        PhoneState.lastDataActivity = DataActivity.fromInteger(direction);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        PhoneState.lastSignalStrength = signalStrength;
    }

    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
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

    enum DataActivity {
        DATA_ACTIVITY_NONE(TelephonyManager.DATA_ACTIVITY_NONE),
        DATA_ACTIVITY_IN(TelephonyManager.DATA_ACTIVITY_IN),
        DATA_ACTIVITY_OUT(TelephonyManager.DATA_ACTIVITY_OUT),
        DATA_ACTIVITY_INOUT(TelephonyManager.DATA_ACTIVITY_INOUT),
        DATA_ACTIVITY_DORMANT(TelephonyManager.DATA_ACTIVITY_DORMANT);

        private final int value;

        private DataActivity(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static DataActivity fromInteger(int value) {
            for (DataActivity state : DataActivity.values()) {
                if (state.getValue() == value) {
                    return state;
                }
            }

            throw new NoSuchElementException("Undefined " + value);
        }


    }

    enum DataState{
        DATA_DISCONNECTED(TelephonyManager.DATA_DISCONNECTED),
        DATA_CONNECTING(TelephonyManager.DATA_CONNECTING),
        DATA_CONNECTED(TelephonyManager.DATA_CONNECTED),
        DATA_SUSPENDED(TelephonyManager.DATA_SUSPENDED);

        private final int value;

        private DataState(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static DataState fromInteger(int value) {
            for(DataState state : DataState.values()) {
                if(state.getValue() == value) {
                    return state;
                }
            }

            throw new NoSuchElementException("Undefined " + value);
        }
    }
}

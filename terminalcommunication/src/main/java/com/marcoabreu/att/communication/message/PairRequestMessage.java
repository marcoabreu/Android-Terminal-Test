package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Message sent by the device to request pairing with the host
 * Created by AbreuM on 02.08.2016.
 */
public class PairRequestMessage extends BaseMessage{
    private String serialString;
    private String deviceModel;

    public PairRequestMessage(String serialString, String deviceModel) {
        super(Opcode.PAIR_REQUEST);
        this.serialString = serialString;
        this.deviceModel = deviceModel;
    }

    public String getSerialString() {
        return serialString;
    }

    public void setSerialString(String serialString) {
        this.serialString = serialString;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
}

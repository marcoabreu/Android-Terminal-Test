package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Message sent by the device to request pairing with the host
 * Created by AbreuM on 02.08.2016.
 */
public class PairRequestMessage extends BaseMessage{

    /**
     * Some randomly generated string shown on the device to identify it - basically a helper to know which device is trying connect
     */
    private String serialString;

    public PairRequestMessage(String serialString) {
        super(Opcode.PAIR_REQUEST);
        this.serialString = serialString;
    }

    public String getSerialString() {
        return serialString;
    }

    public void setSerialString(String serialString) {
        this.serialString = serialString;
    }
}

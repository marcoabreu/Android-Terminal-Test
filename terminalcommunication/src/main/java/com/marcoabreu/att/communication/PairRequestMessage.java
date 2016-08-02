package com.marcoabreu.att.communication;

/**
 * Message sent by the device to request pairing with the host
 * Created by AbreuM on 02.08.2016.
 */
public class PairRequestMessage extends BaseMessage {

    /**
     * Some randomly generated string shown on the device to identify it - basically a helper to know which device is trying connect
     */
    private String identifyingString;

    public PairRequestMessage(String identifyingString) {
        super(Opcode.PAIR_REQUEST);
        this.identifyingString = identifyingString;
    }
}

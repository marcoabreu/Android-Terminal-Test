package com.marcoabreu.att.communication;

/**
 * Answer of the host to a pairing request
 * Created by AbreuM on 02.08.2016.
 */
public class PairResponseMessage extends BaseMessage{
    public PairResponseMessage(PairRequestMessage pairRequestMessage) {
        super(Opcode.PAIR_RESPONSE, pairRequestMessage);
    }
}

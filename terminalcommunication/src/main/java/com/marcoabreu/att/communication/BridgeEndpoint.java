package com.marcoabreu.att.communication;

/**
 * Created by AbreuM on 03.08.2016.
 */
public interface BridgeEndpoint {
    void registerMessageListener(BridgeMessageListener listener);
    void removeMessageListener(BridgeMessageListener listener);
}

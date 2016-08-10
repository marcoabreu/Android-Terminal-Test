package com.marcoabreu.att.bridge;

/**
 * Listener for bridge events
 * Created by AbreuM on 10.08.2016.
 */
public interface BridgeEventListener {
    void onHostPaired(PairedHost host);

    void onHostUnpaired(PairedHost host);


}

package com.marcoabreu.att.communication;

/**
 * Listener for device messages
 * Created by AbreuM on 02.08.2016.
 */
public interface BridgeMessageListener {
    void onMessage(PhysicalDevice device, com.marcoabreu.att.communication.message.BaseMessage message);
}

package com.marcoabreu.att.communication;

import java.io.IOException;

/**
 * Listener for device messages
 * Created by AbreuM on 02.08.2016.
 */
public interface BridgeMessageListener {
    void onMessage(PhysicalDevice device, com.marcoabreu.att.communication.message.BaseMessage message) throws IOException;
}

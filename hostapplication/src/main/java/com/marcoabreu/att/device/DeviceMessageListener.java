package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.BaseMessage;

/**
 * Listener for device messages
 * Created by AbreuM on 02.08.2016.
 */
public interface DeviceMessageListener {
    public void onMessage(PairedDevice device, BaseMessage message, DeviceServer server);
}

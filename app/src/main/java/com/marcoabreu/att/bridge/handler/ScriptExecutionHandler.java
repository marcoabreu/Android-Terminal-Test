package com.marcoabreu.att.bridge.handler;

import com.marcoabreu.att.bridge.PairedHost;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.ExecuteActionMessage;
import com.marcoabreu.att.communication.message.ExecuteActionResponse;
import com.marcoabreu.att.script.DeviceInterpreter;

/**
 * Handle requests to execute a dynamic script
 * Created by AbreuM on 03.08.2016.
 */
public class ScriptExecutionHandler implements BridgeMessageListener {
    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) {
        if(message instanceof ExecuteActionMessage) {
            final PairedHost host = (PairedHost) device;
            ExecuteActionMessage actionMessage = (ExecuteActionMessage)message;

            ExecuteActionResponse response = new ExecuteActionResponse(actionMessage);

            try {
                DeviceInterpreter interpreter = new DeviceInterpreter(actionMessage.getMethodName(), actionMessage.getScriptContent(), actionMessage.getPath());

                if(actionMessage.isReadReturnValue()) {
                    response.setReturnValue(interpreter.executeReturn(host, actionMessage.getParameters()));
                } else {
                    interpreter.executeVoid(host, actionMessage.getParameters());
                }
            } catch (Exception ex) {
                response.setOccuredException(ex);
            }

            host.sendResponse(response);
        }
    }
}

package com.marcoabreu.att.bridge.handler;

import android.util.Log;

import com.marcoabreu.att.bridge.PairedHost;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.ExecuteActionMessage;
import com.marcoabreu.att.communication.message.ExecuteActionResponse;
import com.marcoabreu.att.script.DeviceInterpreter;

import java.io.IOException;

/**
 * Handle requests to execute a dynamic script
 * Created by AbreuM on 03.08.2016.
 */
@Deprecated
public class ScriptExecutionHandlerOld implements BridgeMessageListener {
    private static String TAG = ScriptExecutionHandlerOld.class.toString();
    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) throws IOException {
        if(message instanceof ExecuteActionMessage) {
            Log.d(TAG, "Received ExecuteActionMessage");
            final PairedHost host = (PairedHost) device;
            ExecuteActionMessage actionMessage = (ExecuteActionMessage)message;

            ExecuteActionResponse response = new ExecuteActionResponse(actionMessage);

            try {
                DeviceInterpreter interpreter = new DeviceInterpreter(actionMessage.getMethodName(), actionMessage.getClassName(), actionMessage.getScriptContent(), actionMessage.getPath());
                Log.d(TAG, "Executing action");
                if(actionMessage.isReadReturnValue()) {
                    response.setReturnValue(interpreter.executeReturn(host, actionMessage.getParameters()));
                } else {
                    interpreter.executeVoid(host, actionMessage.getParameters());
                }
                Log.d(TAG, "Action executed successfully");
            } catch (Exception ex) {
                response.setOccuredException(ex);
                Log.e(TAG, "Action execution failed", ex);
            }

            host.sendResponse(response);
        }
    }
}

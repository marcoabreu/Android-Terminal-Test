package com.marcoabreu.att.bridge.handler;

import android.util.Log;

import com.marcoabreu.att.bridge.PairedHost;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.AbortActionMessage;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.ExecuteActionMessage;
import com.marcoabreu.att.communication.message.ExecuteActionResponse;
import com.marcoabreu.att.script.DexInterpreter;

import java.io.IOException;

/**
 * Handle requests to execute a dynamic script
 * Created by AbreuM on 03.08.2016.
 */
public class ScriptExecutionHandler implements BridgeMessageListener {
    private static String TAG = ScriptExecutionHandler.class.toString();

    private static Thread lastInterpreterThread = null;

    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) throws IOException {
        if(message instanceof ExecuteActionMessage) {
            Log.d(TAG, "Received ExecuteActionMessage");
            final PairedHost host = (PairedHost) device;
            ExecuteActionMessage actionMessage = (ExecuteActionMessage)message;

            ExecuteActionResponse response = new ExecuteActionResponse(actionMessage);

            lastInterpreterThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DexInterpreter interpreter = new DexInterpreter(actionMessage.getMethodName(), actionMessage.getPath());
                        Log.d(TAG, "Executing action");
                        if(actionMessage.isReadReturnValue()) {
                            response.setReturnValue(interpreter.executeReturn(host, actionMessage.getParameters()));
                        } else {
                            interpreter.executeVoid(host, actionMessage.getParameters());
                        }
                        Log.d(TAG, "Action executed successfully");
                    } catch (Exception ex) {
                        response.setOccurredException(ex);
                        Log.e(TAG, "Action execution failed", ex);
                    }

                    try {
                        host.sendResponse(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            lastInterpreterThread.setDaemon(true);
            lastInterpreterThread.start();
        } else if(message instanceof AbortActionMessage) {
            Log.d(TAG, "Received AbortActionMessage");
            AbortActionMessage actionMessage = (AbortActionMessage)message;

            if(lastInterpreterThread != null) {
                lastInterpreterThread.interrupt();
            }
        }
    }
}

package com.marcoabreu.att.bridge.handler;

import android.util.Log;

import com.marcoabreu.att.MainActivity;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.PairResponseMessage;
import com.marcoabreu.att.script.DexInterpreter;

import java.io.IOException;

/**
 * Handle the pair response
 * Created by AbreuM on 04.08.2016.
 */
public class PairResponseHandler implements BridgeMessageListener {
    private static final String TAG = PairResponseHandler.class.toString();
    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) throws IOException {
        if(message instanceof PairResponseMessage) {
            PairResponseMessage responseMessage = (PairResponseMessage)message;

            try {
                DexInterpreter.init(MainActivity.getAppContext(), responseMessage.getDexFileContent(), responseMessage.getClasspathMapping());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error during loading of dynamic scripts", e);
            }

            //TODO: Fire connected event (show we've successfully connected)
            Log.d(TAG, "Pairing successful");
        }

    }
}

package com.marcoabreu.att;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.marcoabreu.att.bridge.DeviceClient;
import com.marcoabreu.att.bridge.handler.ScriptExecutionHandler;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.TestMessage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();
    private static Context context;

    TextView info, infoip, msg;
    Button compilerTestButton;
    String message = "";
    DeviceClient deviceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.context = this;

        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.textViewInfo);
        infoip = (TextView) findViewById(R.id.textViewInfoIp);
        msg = (TextView) findViewById(R.id.textViewMsg);
        compilerTestButton = (Button) findViewById(R.id.button);

        compilerTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compilerTest();
            }
        });

        //Thread socketServerThread = new Thread(new SocketServerThread());
        //socketServerThread.start();


        try {
            deviceClient = new DeviceClient(12022);
            deviceClient.start();

            //Handler for dynamic script execution
            deviceClient.registerMessageListener(new ScriptExecutionHandler());

            deviceClient.registerMessageListener(new BridgeMessageListener() {
                @Override
                public void onMessage(PhysicalDevice device, BaseMessage message) {
                    if(message instanceof TestMessage) {
                        Log.d(TAG, "Received test message: " + ((TestMessage)message).getMessage());
                    } else {
                        Log.d(TAG, "Received unknown message");
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compilerTest() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(deviceClient != null) {
            try {
                deviceClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }
}

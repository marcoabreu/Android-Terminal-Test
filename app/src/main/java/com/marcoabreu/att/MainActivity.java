package com.marcoabreu.att;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.marcoabreu.att.bridge.BridgeEventListener;
import com.marcoabreu.att.bridge.DeviceClient;
import com.marcoabreu.att.bridge.PairedHost;
import com.marcoabreu.att.bridge.handler.PairResponseHandler;
import com.marcoabreu.att.bridge.handler.ScriptExecutionHandler;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();
    private static Context context;

    TextView info;
    TextView status;
    Button compilerTestButton;
    DeviceClient deviceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.context = this;

        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.textViewInfo);
        status = (TextView) findViewById(R.id.textViewStatus);
        compilerTestButton = (Button) findViewById(R.id.button);

        compilerTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compilerTest();
            }
        });

        info.setText("Serial: " + Build.SERIAL);
        status.setText("Unpaired");

        try {
            deviceClient = new DeviceClient(12022);

            //Handler for dynamic script execution
            deviceClient.registerMessageListener(new ScriptExecutionHandler());
            deviceClient.registerMessageListener(new PairResponseHandler());

            deviceClient.registerBridgeListener(new BridgeEventListener() {
                @Override
                public void onHostPaired(PairedHost host) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            status.setText("Paired");
                        }
                    };
                    new Handler(Looper.getMainLooper()).post(runnable);
                }

                @Override
                public void onHostUnpaired(PairedHost host) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            status.setText("Unpaired");
                        }
                    };
                    new Handler(Looper.getMainLooper()).post(runnable);
                }
            });

            deviceClient.start();
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

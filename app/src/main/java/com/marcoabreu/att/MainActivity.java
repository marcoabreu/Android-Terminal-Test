package com.marcoabreu.att;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
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

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    TextView info;
    TextView status;
    TextView networkType;
    DeviceClient deviceClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.context = this;

        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.textViewInfo);
        status = (TextView) findViewById(R.id.textViewStatus);
        networkType = (TextView) findViewById(R.id.textViewNetworkType);

        info.setText("Serial: " + Build.SERIAL);
        status.setText("Unpaired");
        networkType.setText("Waiting...");

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

        mHandler = new Handler();
        startRepeatingTask();
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

        stopRepeatingTask();
    }

    private void updateStatus() {
        //ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        //networkType.setText("Network: " + networkInfo.getSubtypeName());
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    public static Context getAppContext() {
        return MainActivity.context;
    }
}

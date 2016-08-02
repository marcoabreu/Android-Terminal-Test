package com.marcoabreu.att;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.marcoabreu.att.compiler.BeanCompiler;
import com.marcoabreu.att.compiler.CompilerException;
import com.marcoabreu.att.aaascripts.CallScript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

import bsh.EvalError;

public class MainActivity extends AppCompatActivity {
    TextView info, infoip, msg;
    Button compilerTestButton;
    String message = "";
    ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();


    }

    private void compilerTest() {
        /*final String code = "import android.content.Intent;\n" +
                "import android.net.Uri;" +
                "    String url = \"tel:\" + phonenumber;\n" +
                "    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));" +
                "    myapp.startActivity(intent);";*/


        final String code = "import com.marcoabreu.att.scripts.CallScript;\n" +
                "        CallScript script = new CallScript();\n" +
                "        script.callNumber(AppActivity, \"3344444555\");";

        final String code2 = "import com.marcoabreu.att.scripts.CallScript;\n" +
                "        CallScript script = new CallScript();\n" +
                "        script.endCall(AppActivity);";

        BeanCompiler compiler = new BeanCompiler();

        //compiler.loadSource("C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\app\\src\\main\\java\\com\\marcoabreu\\att\\scripts\\CallScript.java");

        compiler.loadParameter("AppActivity", this);
        //compiler.loadParameter("phonenumber", "4393949394399");



        compiler.loadCode(code);

        try {
            compiler.execute();
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        } catch (CompilerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 50; i++) {
            new CallScript().getCallState(this);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




        compiler.clear();
        compiler.loadParameter("AppActivity", this);
        compiler.loadCode(code2);
        try {
            compiler.execute();
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        } catch (CompilerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 10; i++) {
            new CallScript().getCallState(this);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 12022;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                    }
                });

//                while (true) {
//                    Socket socket = serverSocket.accept();
//                    count++;
//                    message += "#" + count + " from " + socket.getInetAddress()
//                            + ":" + socket.getPort() + "\n";
//
//                    MainActivity.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            msg.setText(message);
//                        }
//                    });
//
//                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
//                            socket, count);
//                    socketServerReplyThread.run();
//
//                }

                while(true) {
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            infoip.setText("Waiting for connection");
                            msg.setText("No connection");
                        }
                    });

                    Socket socket = serverSocket.accept();

                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            infoip.setText("Connected at " + new Date().toString());
                            msg.setText("Waiting for messages...");
                        }
                    });

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                        while (!socket.isClosed()) {
                            final String curMsg = in.readLine();

                            if(curMsg == null) { //Connection closed
                                break;
                            }

                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    msg.setText("Received message: '" + curMsg + "' at " + new Date().toString());
                                }
                            });
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Android, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    msg.setText(message);
                }
            });
        }

    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}

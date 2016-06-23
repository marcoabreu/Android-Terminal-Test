package com.marcoabreu.att.host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import se.vidstige.jadb.AdbServerLauncher;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.Transport;
import se.vidstige.jadb.managers.Package;
import se.vidstige.jadb.managers.PackageManager;

public class HostApp {
    public static void main(String args[]) {
        System.out.println("Test");

        //establish connection and start the app
        try {
            new AdbServerLauncher().launch();
            JadbConnection jadb = new JadbConnection();
            List<JadbDevice> devices = jadb.getDevices();

            for(JadbDevice device : devices) {
                System.out.println(device.toString());
            }

            JadbDevice curDevice = devices.get(0);


            Transport transport = jadb.createTransport();
            transport.send(String.format("host-serial:%s:forward:tcp:%d;tcp:%d", curDevice.getSerial(), 12022, 12022));
            transport.verifyResponse();

            //Launch app
            PackageManager pm = new PackageManager(curDevice);
            pm.launch(new Package("com.marcoabreu.att"));
            Thread.sleep(5000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JadbException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try (
                Socket echoSocket = new Socket("127.0.0.1", 12022);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ){
            System.out.println("Enter your message:");
            String curMsg = "";

            while((curMsg = stdIn.readLine()).compareToIgnoreCase("exit") != 0) {
                out.write(curMsg + "\r\n");
                out.flush();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

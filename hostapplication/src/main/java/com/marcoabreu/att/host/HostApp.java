package com.marcoabreu.att.host;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Executor;
import com.marcoabreu.att.engine.RunStatus;
import com.marcoabreu.att.engine.Sequence;

public class HostApp {
    /*
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
            //TODO PackageManager install app if not installed and execute
            //TODO: adb shell pm grant com.your.package android.permission.WRITE_EXTERNAL_STORAGE to grant all permissions
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
    }*/

    /*public static void main(String args[]) {
        Composite sequence = new Sequence(
                new Action(() -> {
                    System.out.println("Action1");
                    try { Thread.sleep(200); } catch(Exception ex) { return false; }
                    return true;
                }),
                new Action(() -> {
                    System.out.println("Action2");
                    try { Thread.sleep(200); } catch(Exception ex) { return false; }
                    return true;
                }),
                new IfStatement(() -> true,
                    new Action(() -> {
                        System.out.println("Hello from the IfStatement");
                        return true;
                    })),
                new Action(() -> {
                    System.out.println("Action3");
                    try { Thread.sleep(200); } catch(Exception ex) { return false; }
                    return true;
                }),
                new IfStatement(() -> false,
                    new Action(() -> {
                        System.out.println("This should not be called");
                        return true;
                    }),
                    new Action(() -> {
                        System.out.println("Hello from the else-case of the IfStatement");
                        return true;
                    })
                ),
                new Action(() -> {
                    System.out.println("Action4");
                    try { Thread.sleep(200); } catch(Exception ex) { return false; }
                    return true;
                })
            );

        Composite repeat = new Repeat(5,
            new Action(() -> {
                System.out.println("Repeat this 5 times, Part 1");
                return true;
            }),
            new Action(() -> {
                System.out.println("Repeat this 5 times, Part 2");
                return true;
            })
        );

        Date end = new Date(new Date().getTime() + 5000);
        Composite repeat2 = new Repeat(() -> new Date().after(end),
            new Action(() -> {
                System.out.println("It's currently " + new Date().toString());
                try { Thread.sleep(200); } catch(Exception ex) { return false; }
                return true;
            })
        );

        Composite compositeToExecute = new Sequence(
            sequence,
            repeat,
            repeat2
        );

        try(Executor ex = new Executor(compositeToExecute)) {
            ex.start();
            while(ex.execute(100) == RunStatus.RUNNING) {
            }
        } catch(Exception e) {

        }
    }*/

    public static void main(String args[]) {
        Composite compositeToExecute = new Sequence(
        );

        try(Executor ex = new Executor(compositeToExecute)) {
            ex.start();
            while(ex.execute(100) == RunStatus.RUNNING) {
            }
        } catch(Exception e) {

        }
    }
}

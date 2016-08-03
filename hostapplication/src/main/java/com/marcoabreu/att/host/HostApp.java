package com.marcoabreu.att.host;

import com.marcoabreu.att.communication.message.TestMessage;
import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.host.handler.DataStorageGetHandler;
import com.marcoabreu.att.host.handler.DataStorageSaveHandler;

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

    /*public static void main(String args[]) {
        Composite compositeToExecute = new Sequence(
        );

        try(Executor ex = new Executor(compositeToExecute)) {
            ex.start();
            while(ex.execute(100) == RunStatus.RUNNING) {
            }
        } catch(Exception e) {

        }
    }*/

    /*public static void main(String args[]) {
        AttProfile profile = new AttProfile();
        profile.setDescription("Testdescription");
        profile.setIdentifier("TestId");
        profile.setName("Testname");

        AttActionHost action1 = new AttActionHost();
        action1.setName("Set up Koppelfeld");
        action1.setMethod("setSignals");
        action1.setPath("Peripherals/Koppelfeld");
        action1.setTimeoutMs(60000);
        action1.addParameter(new AttParameterText("3G_1", "20"));
        action1.addParameter(new AttParameterText("3G_2", "20"));
        action1.addParameter(new AttParameterText("3G_3", "20"));
        action1.addParameter(new AttParameterText("3G_4", "40"));
        action1.addParameter(new AttParameterText("2G_1", "0"));
        action1.addParameter(new AttParameterText("noise1", "93"));
        profile.addChild(action1);

        AttActionDevice action2 = new AttActionDevice();
        action2.setTargetDevice("device1");
        action2.setName("Await 3G");
        action2.setMethod("AwaitConnectivity");
        action2.setPath("Network/Connectivity");
        action2.setTimeoutMs(60000);
        action2.addParameter(new AttParameterText("networkType", "3G"));
        profile.addChild(action2);

        AttActionDevice action3 = new AttActionDevice();
        action3.setTargetDevice("device1");
        action3.setName("Call device2");
        action3.setMethod("StartCall");
        action3.setPath("Phone/Calls");
        action3.setTimeoutMs(60000);
        profile.addChild(action3);

        AttParameterScriptDevice deviceActionParam1 = new AttParameterScriptDevice();
        deviceActionParam1.setKey("number");
        deviceActionParam1.setTargetDevice("device2");
        deviceActionParam1.setMethod("GetPhoneNumber");
        deviceActionParam1.setPath("Phone/Informations");
        deviceActionParam1.setTimeoutMs(60000);
        action3.addParameter(deviceActionParam1);

        AttActionDevice action4 = new AttActionDevice();
        action4.setTargetDevice("device2");
        action4.setName("Pickup call");
        action4.setMethod("PickupCall");
        action4.setPath("Phone/Calls");
        action4.setTimeoutMs(60000);
        profile.addChild(action4);

        AttSleep sleep1 = new AttSleep(10000);
        sleep1.setName("Wait 10s");
        profile.addChild(sleep1);

        AttActionHost action5 = new AttActionHost();
        action5.setName("Disable 3G");
        action5.setMethod("setSignals");
        action5.setPath("Peripherals/Koppelfeld");
        action5.setTimeoutMs(60000);
        action5.addParameter(new AttParameterText("3G_1", "93"));
        action5.addParameter(new AttParameterText("3G_2", "93"));
        action5.addParameter(new AttParameterText("3G_3", "93"));
        action5.addParameter(new AttParameterText("3G_4", "93"));
        profile.addChild(action5);

        AttActionDevice action6 = new AttActionDevice();
        action6.setTargetDevice("device1");
        action6.setName("Await 2G");
        action6.setMethod("AwaitConnectivity");
        action6.setPath("Network/Connectivity");
        action6.setTimeoutMs(60000);
        action6.addParameter(new AttParameterText("networkType", "2G"));
        profile.addChild(action6);

        try {
            ProfileMarshaller.saveProfile(profile, "");
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        ProfileExecutor pe = new ProfileExecutor(profile);

        pe.start();
    }*/

    /*public static void main(String args[]) {
        AttProfile profile = new AttProfile();
        profile.setDescription("Testdescription");
        profile.setIdentifier("TestId");
        profile.setName("Testname");

        AttActionHost action1 = new AttActionHost();
        action1.setName("Set up Koppelfeld");
        action1.setMethod("setSignals");
        action1.setPath("Peripherals/Koppelfeld");
        action1.setTimeoutMs(60000);
        action1.addParameter(new AttParameterText("3G_1", "20"));
        action1.addParameter(new AttParameterText("3G_2", "20"));
        action1.addParameter(new AttParameterText("3G_3", "20"));
        action1.addParameter(new AttParameterText("3G_4", "40"));
        action1.addParameter(new AttParameterText("2G_1", "0"));
        action1.addParameter(new AttParameterText("noise1", "93"));
        profile.addChild(action1);

        AttSleep sleep1 = new AttSleep(10000);
        sleep1.setName("Wait 10s");
        profile.addChild(sleep1);

        AttActionHost action5 = new AttActionHost();
        action5.setName("Disable 3G");
        action5.setMethod("setSignals");
        action5.setPath("Peripherals/Koppelfeld");
        action5.setTimeoutMs(60000);
        action5.addParameter(new AttParameterText("3G_1", "93"));
        action5.addParameter(new AttParameterText("3G_2", "93"));
        action5.addParameter(new AttParameterText("3G_3", "93"));
        action5.addParameter(new AttParameterText("3G_4", "93"));
        profile.addChild(action5);

        AttParameterScriptHost deviceHostParam1 = new AttParameterScriptHost();
        deviceHostParam1.setKey("random");
        deviceHostParam1.setMethod("generateRandom");
        deviceHostParam1.setPath("Peripherals/Koppelfeld");
        deviceHostParam1.setTimeoutMs(60000);
        action5.addParameter(deviceHostParam1);

        try {
            ProfileMarshaller.saveProfile(profile, "");
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        ProfileExecutor pe = new ProfileExecutor(profile);

        pe.start();

        while(true) {
            RunStatus runState = pe.getRunState();
            System.out.println(runState);
            if(runState != RunStatus.RUNNING) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

    public static void main(String args[]) {
        try {
            DeviceManager deviceManager = new DeviceManager();

            //register listeners
            deviceManager.getDeviceServer().registerMessageListener(new DataStorageGetHandler());
            deviceManager.getDeviceServer().registerMessageListener(new DataStorageSaveHandler());

            deviceManager.start();

            deviceManager.getConnectedDevices().forEach(System.out::println);

            deviceManager.startPairing(deviceManager.getConnectedDevices().get(0));

            while(true) {
                Thread.sleep(1000);
                try {
                    deviceManager.getPairedDevices().iterator().next().sendMessage(new TestMessage("Hello"));
                } catch(Exception ex) {

                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

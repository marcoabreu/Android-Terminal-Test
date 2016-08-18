package com.marcoabreu.att;

import com.marcoabreu.att.profile.ScriptRuntimeContainer;
import com.marcoabreu.att.storage.StorageScope;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by AbreuM on 01.08.2016.
 */
public class Koppelfeld {
    private static final String STORAGE_CONNECTED_CELLS = "Peripherals.Koppelfeld.ConnectedCells";
    private static final String STORAGE_SERIAL_PORT = "Peripherals.Koppelfeld.SerialPort";

    private static final int KOPPELFELD_NBCHANNELS = 6;


    /**
     * Ask the user to connect the specified cells if required and reset all signals
     */
    public static void connectCells(ScriptRuntimeContainer runtime) throws SerialPortException {
        resetSignals(runtime);

        //Read required cells from the profile and look if they are already connected
        Map<String, Integer> usedCells = new HashMap<>();
        Map<String, Integer> connectedCells = new HashMap<>(getConnectedCells(runtime));
        Set<String> missingCells = new HashSet<>();

        for(Map.Entry<String, Object> param : runtime.getParameters().entrySet()) {
            if(connectedCells.containsKey(param.getKey())) {
                usedCells.put(param.getKey(), connectedCells.remove(param.getKey()));
            } else {
                missingCells.add(param.getKey());
            }
        }

        Map<String, Integer> obsoleteCells = connectedCells; //All cells left in this map are not used by this profile and thereby obsolete
        requestCellConnect(getConnectedCells(runtime), missingCells, obsoleteCells); //Ask user to connect cells if there are some missing
    }

    private static void requestCellConnect(Map<String, Integer> connectedCells, Set<String> missingCells, Map<String, Integer> freeCells) {
        if(missingCells.size() > 0) {
            if(missingCells.size() > freeCells.size()) {
                throw new IllegalArgumentException("Not enough free cells available");
            }

            //Show dialog to show user free cells and ask them to connect the missing cells
            StringBuilder sb = new StringBuilder();

            Map<String, Integer> newMapping = new HashMap<>();
            Iterator<Map.Entry<String, Integer>> freeCellIterator = freeCells.entrySet().iterator();
            for(String cell : missingCells) {
                Map.Entry<String, Integer> currentFreeCell = freeCellIterator.next();
                newMapping.put(cell, currentFreeCell.getValue());
                sb.append("\tReplace " + currentFreeCell.getKey() + " with " + cell + " on channel " + currentFreeCell.getValue() + "\n");
            }

            com.marcoabreu.att.Message.showMessageImpl("The Koppefeld needs some new connections: \n" + sb.toString() + "\nConfirm when you have connected the cells.");

            for(Map.Entry<String, Integer> cell : newMapping.entrySet()) {
                connectedCells.put(cell.getKey(), cell.getValue());
                System.out.println(String.format("Reassigned Channel %d to %s", cell.getValue(), cell.getKey()));
            }
        }
    }

    private static Map<String, Integer> getConnectedCells(ScriptRuntimeContainer runtime) {
        Map<String, Integer> connectedCells = runtime.getDataStorage().getData(STORAGE_CONNECTED_CELLS);

        //Open connection if required
        if(connectedCells == null) {
            connectedCells = new HashMap<>();

            //Add dummy channels
            for(int i = 1; i <= KOPPELFELD_NBCHANNELS; i++) {
                connectedCells.put("Unknown" + i, i);
            }

            runtime.getDataStorage().saveData(STORAGE_CONNECTED_CELLS, StorageScope.APPLICATION, connectedCells);
        }

        return connectedCells;
    }

    /**
     * Change the signals of the connected cells to the passed value
     */
    public static void setSignals(ScriptRuntimeContainer runtime) throws SerialPortException {
        Map<String, Integer> connectedCells = getConnectedCells(runtime);

        //Channel -> SignalStrength
        Map<Integer, Integer> signalStrengths = new HashMap<>();

        //We allow multiple signals to be set at the same time, so map the keys to the actual cell channels
        for(Map.Entry<String, Object> parameter : runtime.getParameters().entrySet()) {
            Integer channel = connectedCells.get(parameter.getKey());
            if(channel == null) {
                throw new IllegalStateException("Profile error: Cell " + parameter.getKey() + " was used even though it has not been initialized");
            }

            signalStrengths.put(channel, Integer.valueOf((String)parameter.getValue()));
        }

        setSignalsImpl(runtime, signalStrengths);

    }

    private static void setSignalsImpl(ScriptRuntimeContainer runtime, Map<Integer, Integer> signalStrengths) throws SerialPortException {
        for(Map.Entry<Integer, Integer> signal : signalStrengths.entrySet()) {
            sendMessage(runtime, "R" + signal.getKey() + "P" + signal.getValue());
        }
    }

    /**
     * Reset all signals to 93db
     */
    private static void resetSignals(ScriptRuntimeContainer runtime) throws SerialPortException {
        sendMessage(runtime, "C");
    }

    private static void sendMessage(ScriptRuntimeContainer runtime, String message) throws SerialPortException {
        //We open and close connection with every message for the simple sake of not leaving an open port - we can't ensure garbage collection

        SerialPort serial = getSerialPort(runtime);
        try {
            serial.openPort();
            serial.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            serial.writeByte((byte) 0x02); //Start command: STX = 0x02H
            try {
                serial.writeString(message, "US-ASCII");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            serial.writeByte((byte) 0x03); //End command: ETX = 0x03H
        } finally {
            if(serial != null) {
                serial.closePort();
            }
        }
    }

    private static SerialPort getSerialPort(ScriptRuntimeContainer runtime) {
        SerialPort serialPort = runtime.getDataStorage().getData(STORAGE_SERIAL_PORT);

        if(serialPort == null) {
            //Ask user to plug out cable, record connected interfaces, ask him to plug it back in and calculate the difference
            com.marcoabreu.att.Message.showMessageImpl("Please make sure the usb serial cable is not plugged in");
            HashSet<String> previousConnectedPorts = new HashSet<>(Arrays.asList(SerialPortList.getPortNames()));

            com.marcoabreu.att.Message.showMessageImpl("Please plug in the usb serial cable");
            HashSet<String> currentlyConnectedPorts = new HashSet<>(Arrays.asList(SerialPortList.getPortNames()));

            currentlyConnectedPorts.removeAll(previousConnectedPorts);

            if(currentlyConnectedPorts.size() == 0) {
                com.marcoabreu.att.Message.showMessageImpl("No newly plugged in device has been detected");
            } else if(currentlyConnectedPorts.size() > 1) {
                com.marcoabreu.att.Message.showMessageImpl("More than one serial device has been connected in the meantime");
            } else {
                runtime.getDataStorage().saveData(STORAGE_SERIAL_PORT, StorageScope.APPLICATION, new SerialPort(currentlyConnectedPorts.iterator().next()));
            }

            serialPort = getSerialPort(runtime); //recursive call to handle errors properly
        }

        return serialPort;
    }
}
package com.marcoabreu.att.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.marcoabreu.att.device.CompilerException;
import com.marcoabreu.att.device.DeviceConnectionListener;
import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.device.PairedDevice;
import com.marcoabreu.att.host.JavaInterpreter;
import com.marcoabreu.att.host.handler.DataStorageGetHandler;
import com.marcoabreu.att.host.handler.DataStorageSaveHandler;
import com.marcoabreu.att.host.handler.PairRequestHandler;
import org.apache.commons.lang3.tuple.Pair;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.marcoabreu.att.ui.MainForm.ConnectionStatus.*;

/**
 * Created by AbreuM on 08.08.2016.
 */
public class MainForm {
    private final JFrame mainFrame;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton pairButton;
    private JButton unassignButton;
    private JButton assignButton;
    private JTable devicesTable;
    private JButton pauseButton;
    private JButton startButton;
    private JButton stopButton;
    private JButton loadProfileButton;
    private JTree profileTree;

    public MainForm() {
        mainFrame = new JFrame("Android Terminal Test");
        mainFrame.setContentPane(this.panel1);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();

        this.pairButton.setEnabled(false);
        this.assignButton.setEnabled(false);
        this.unassignButton.setEnabled(false);

        loadProfileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                loadProfileHandler();
            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                startProfileHandler();
            }
        });
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                stopProfileHandler();
            }
        });
        pauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                pauseProfileHandler();
            }
        });
        pairButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                pairDeviceHandler();
            }
        });
        assignButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                assignDeviceHandler();
            }
        });
        unassignButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                unassignDeviceHandler();
            }
        });
        devicesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectDeviceHandler();
            }
        });

        devicesTable.setModel(new DeviceTableItemModel());

        try {
            init();
        } catch (Exception e) {
            //TODO handle exception
            e.printStackTrace();
        }

    }

    private void init() throws InterruptedException, ExecutionException, CompilerException, IOException {
        JavaInterpreter.init();

        DeviceManager deviceManager = DeviceManager.getInstance();

        //register listeners
        deviceManager.getDeviceServer().registerMessageListener(new DataStorageGetHandler());
        deviceManager.getDeviceServer().registerMessageListener(new DataStorageSaveHandler());
        deviceManager.getDeviceServer().registerMessageListener(new PairRequestHandler());
        deviceManager.addDevicePairedListener(new DeviceConnectionListener() {
            @Override
            public void onDeviceConnected(JadbDevice device) {
                SwingUtilities.invokeLater(() -> deviceConnectedHandler(device));
            }

            @Override
            public void onDeviceDisconnected(JadbDevice device) {
                SwingUtilities.invokeLater(() -> deviceDisconnectedHandler(device));
            }

            @Override
            public void onDeviceNeedPermission(JadbDevice device) {
                SwingUtilities.invokeLater(() -> deviceNeedPermissionHandler(device));
            }

            @Override
            public void onDevicePaired(PairedDevice device) {
                SwingUtilities.invokeLater(() -> devicePairedHandler(device));
            }

            @Override
            public void onDeviceUnpaired(PairedDevice device) {
                SwingUtilities.invokeLater(() -> deviceUnpairedHandler(device));
            }
        });

        deviceManager.start();
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    private Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> getSelectedDevice() {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = model.getDeviceAt(devicesTable.getSelectedRow());
        return deviceData;
    }

    private void loadProfileHandler() {
        //TODO Load profile dialog
    }

    private void startProfileHandler() {
        //TODO Start profile engine
    }

    private void stopProfileHandler() {
        //TODO Stop profile engine
    }

    private void pauseProfileHandler() {
        //TODO Pause profile engine
    }

    private void pairDeviceHandler() {
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = getSelectedDevice();
        try {
            DeviceManager.getInstance().startPairing(deviceData.getLeft());
        } catch (IOException e) { //TODO proper exception handling
            e.printStackTrace();
        } catch (JadbException e) {
            e.printStackTrace();
        }
    }

    private void assignDeviceHandler() {
        //TODO assign device dialog
    }

    private void unassignDeviceHandler() {
        //TODO unassign device
    }

    private void selectDeviceHandler() {
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = getSelectedDevice();

        if (deviceData != null) {
            this.pairButton.setEnabled(deviceData.getRight().getConnectionStatus() == UNPAIRED);
            this.assignButton.setEnabled(deviceData.getRight().getConnectionStatus() == PAIRED);
            this.unassignButton.setEnabled(deviceData.getRight().getConnectionStatus() == ASSIGNED);
        } else {
            this.pairButton.setEnabled(false);
            this.assignButton.setEnabled(false);
            this.unassignButton.setEnabled(false);
        }
    }

    private void devicePairedHandler(PairedDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device.getJadbDevice());
        data.setConnectionStatus(PAIRED);
        model.fireTableDataChanged();
    }

    private void deviceUnpairedHandler(PairedDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device.getJadbDevice());
        data.setConnectionStatus(UNPAIRED);
        model.fireTableDataChanged();
    }

    private void deviceNeedPermissionHandler(JadbDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device);
        data.setConnectionStatus(PERMISSION_REQUIRED);
        model.fireTableDataChanged();
    }

    private void deviceConnectedHandler(JadbDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        model.addDevice(device);
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device);
        data.setConnectionStatus(UNPAIRED);
        model.fireTableDataChanged();
    }

    private void deviceDisconnectedHandler(JadbDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        model.removeDevice(device);
        model.fireTableDataChanged();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1 = new JTabbedPane();
        panel1.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Devices", panel2);
        assignButton = new JButton();
        assignButton.setText("Assign");
        panel2.add(assignButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        unassignButton = new JButton();
        unassignButton.setText("Unassign");
        panel2.add(unassignButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pairButton = new JButton();
        pairButton.setText("Pair");
        panel2.add(pairButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        devicesTable = new JTable();
        scrollPane1.setViewportView(devicesTable);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        tabbedPane1.addTab("Profile", panel3);
        startButton = new JButton();
        startButton.setText("Start");
        panel3.add(startButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadProfileButton = new JButton();
        loadProfileButton.setText("Load profile");
        panel3.add(loadProfileButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        profileTree = new JTree();
        panel3.add(profileTree, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        pauseButton = new JButton();
        pauseButton.setText("Pause");
        panel3.add(pauseButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setText("Stop");
        panel3.add(stopButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private class DeviceTableItemModel extends AbstractTableModel {
        private List<JadbDevice> devices;
        private Map<JadbDevice, DeviceTableData> deviceData;

        public DeviceTableItemModel() {
            devices = new ArrayList<>();
            deviceData = new HashMap<>();
        }

        @Override
        public int getRowCount() {
            return devices.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Pair<JadbDevice, DeviceTableData> device = getDeviceAt(rowIndex);
            DeviceTableData deviceData = device.getRight();
            switch (columnIndex) {
                case 0:
                    return deviceData.getId();
                case 1:
                    return deviceData.getDeviceType();
                case 2:
                    return deviceData.getConnectionStatus();
                case 3:
                    return deviceData.getAlias();
                default:
                    throw new RuntimeException("Column not supported: " + columnIndex);
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Id";
                case 1:
                    return "Device Type";
                case 2:
                    return "Connection status";
                case 3:
                    return "Alias";
                default:
                    throw new RuntimeException("Not supported column " + column);
            }

        }

        public Pair<JadbDevice, DeviceTableData> getDeviceAt(int rowIndex) {
            if (rowIndex == -1) {
                return null;
            }

            JadbDevice jadbDevice = devices.get(rowIndex);
            DeviceTableData deviceData = getDeviceData(jadbDevice);
            return Pair.of(jadbDevice, deviceData);
        }

        public void addDevice(JadbDevice jadbDevice) {
            devices.add(jadbDevice);
            deviceData.put(jadbDevice, new DeviceTableData(jadbDevice));
        }

        public void removeDevice(JadbDevice jadbDevice) {
            devices.remove(jadbDevice);
            deviceData.remove(jadbDevice);
        }

        public DeviceTableData getDeviceData(JadbDevice jadbDevice) {
            return deviceData.get(jadbDevice);
        }

        public class DeviceTableData {
            private final JadbDevice jadbDevice;
            private ConnectionStatus connectionStatus = UNPAIRED;
            private String alias = "-";

            public DeviceTableData(JadbDevice jadbDevice) {
                this.jadbDevice = jadbDevice;
            }

            public String getId() {
                return jadbDevice.getSerial();
            }

            public String getDeviceType() {
                return "TODO"; //TODO
            }

            public ConnectionStatus getConnectionStatus() {
                return connectionStatus;
            }

            public void setConnectionStatus(ConnectionStatus connectionStatus) {
                this.connectionStatus = connectionStatus;
            }

            public String getAlias() {
                return alias;
            }

            public void setAlias(String alias) {
                this.alias = alias;
            }
        }
    }

    enum ConnectionStatus {
        UNPAIRED,
        PAIRED,
        ASSIGNED,
        PERMISSION_REQUIRED
    }

}

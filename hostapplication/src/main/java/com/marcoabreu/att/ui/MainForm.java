package com.marcoabreu.att.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.marcoabreu.att.device.CompilerException;
import com.marcoabreu.att.device.DeviceConnectionListener;
import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.device.PairedDevice;
import com.marcoabreu.att.engine.RunStatus;
import com.marcoabreu.att.host.JavaInterpreter;
import com.marcoabreu.att.host.handler.DataStorageGetHandler;
import com.marcoabreu.att.host.handler.DataStorageSaveHandler;
import com.marcoabreu.att.host.handler.PairRequestHandler;
import com.marcoabreu.att.profile.ProfileExecutor;
import com.marcoabreu.att.profile.ProfileMarshaller;
import com.marcoabreu.att.profile.data.AttComposite;
import com.marcoabreu.att.profile.data.AttGroupContainer;
import com.marcoabreu.att.profile.data.AttProfile;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
    private static final Logger LOG = LogManager.getLogger();
    private static final int PROFILE_WATCHER_UPDATE_MS = 200;
    private final JFrame mainFrame;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton pairButton;
    private JButton unassignButton;
    private JTable devicesTable;
    private JButton pauseButton;
    private JButton startButton;
    private JButton stopButton;
    private JButton loadProfileButton;
    private JTree profileTree;

    private ProfileExecutor profileExecutor;
    private AttProfile loadedProfile;
    private Thread profileExecutorWatcherThread;

    public MainForm() {
        mainFrame = new JFrame("Android Terminal Test");
        mainFrame.setContentPane(this.panel1);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();

        this.pairButton.setEnabled(false);
        this.unassignButton.setEnabled(false);

        profileExecutorWatcherThread = new Thread(new ProfileExecutorWatcherThread());
        profileExecutorWatcherThread.setDaemon(true);
        profileExecutorWatcherThread.start();

        loadProfileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                loadProfileButtonHandler();
            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                startProfileButtonHandler();
            }
        });
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                stopProfileButtonHandler();
            }
        });
        pauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                pauseProfileButtonHandler();
            }
        });
        pairButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                pairDeviceButtonHandler();
            }
        });
        unassignButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                unassignDeviceButtonHandler();
            }
        });
        devicesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedDeviceChangeHandler();
            }
        });

        devicesTable.setModel(new DeviceTableItemModel());

        try {
            init();
        } catch (Exception e) {
            showMessage("Error during initialization", e);
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

            @Override
            public void onDeviceAssigned(PairedDevice device, String alias) {
                SwingUtilities.invokeLater(() -> deviceAssignedHandler(device, alias));
            }

            @Override
            public void onDeviceUnassigned(PairedDevice device) {
                SwingUtilities.invokeLater(() -> deviceUnassignedHandler(device));
            }
        });

        deviceManager.start();
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    private void showMessage(String message) {
        JOptionPane op = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = op.createDialog("Android Terminal Test");
        dialog.setAlwaysOnTop(false);
        dialog.setModal(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private void showMessage(String message, Throwable throwable) {
        StringBuilder resultingMessage = new StringBuilder();
        resultingMessage.append(message + "\n");
        resultingMessage.append("Exception:\n");

        Throwable nestedThrowable = throwable;

        while (nestedThrowable != null) {
            resultingMessage.append("\t" + nestedThrowable.toString() + "\n");
            nestedThrowable = nestedThrowable.getCause();
        }

        showMessage(resultingMessage.toString());
    }

    private Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> getSelectedDevice() {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = model.getDeviceAt(devicesTable.getSelectedRow());
        return deviceData;
    }

    private void loadProfileButtonHandler() {
        JFileChooser fileChooser = new JFileChooser();
        int retVal = fileChooser.showOpenDialog(null);

        if (retVal == JFileChooser.APPROVE_OPTION) {
            File selectedProfile = fileChooser.getSelectedFile();

            try {
                loadedProfile = ProfileMarshaller.readProfile(selectedProfile);

                DefaultMutableTreeNode headNode = new DefaultMutableTreeNode("Profile " + loadedProfile.getIdentifier());
                for (AttComposite attCompositeChild : loadedProfile.getComposites()) {
                    generateProfileTreeElement(headNode, attCompositeChild);
                }

                DefaultTreeModel model = (DefaultTreeModel) profileTree.getModel();
                model.setRoot(headNode);

            } catch (JAXBException e) {
                showMessage("Unable to read profile", e);
            }


        }
    }

    private void generateProfileTreeElement(DefaultMutableTreeNode parent, AttComposite attComposite) {
        DefaultMutableTreeNode nodeRoot = new DefaultMutableTreeNode(attComposite.toString());
        parent.add(nodeRoot);

        if (attComposite instanceof AttGroupContainer) {
            AttGroupContainer attGroupContainer = (AttGroupContainer) attComposite;
            for (AttComposite attChild : attGroupContainer.getComposites()) {
                generateProfileTreeElement(nodeRoot, attChild);
            }
        }
    }

    private void startProfileButtonHandler() {
        if (loadedProfile == null) {
            showMessage("Select a profile first");
            return;
        }

        if (profileExecutor != null && profileExecutor.getRunState() == RunStatus.RUNNING) {
            showMessage("Profile execution is already running, please stop it first");
            return;
        }

        profileExecutor = new ProfileExecutor(loadedProfile);
        try {
            profileExecutor.start();
        } catch (Exception e) {
            showMessage("Error while starting profile", e);
        }
    }

    private void stopProfileButtonHandler() {
        if (profileExecutor != null) {
            profileExecutor.stop();
        }
    }

    private void pauseProfileButtonHandler() {
        if (profileExecutor != null) {
            profileExecutor.pause();
        }
    }

    private void pairDeviceButtonHandler() {
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = getSelectedDevice();
        try {
            DeviceManager.getInstance().startPairing(deviceData.getLeft());
        } catch (IOException e) {
            showMessage("Error communicating to device over adb", e);
            e.printStackTrace();
        } catch (JadbException e) {
            showMessage("Error connecting to device over adb", e);
        }
    }

    private void unassignDeviceButtonHandler() {
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = getSelectedDevice();

        if (deviceData != null) {
            DeviceManager.getInstance().removeDeviceAssignment(deviceData.getRight().getAlias());
        }
    }

    private void selectedDeviceChangeHandler() {
        Pair<JadbDevice, DeviceTableItemModel.DeviceTableData> deviceData = getSelectedDevice();

        if (deviceData != null) {
            this.pairButton.setEnabled(deviceData.getRight().getConnectionStatus() == UNPAIRED);
            this.unassignButton.setEnabled(deviceData.getRight().getConnectionStatus() == ASSIGNED);
        } else {
            this.pairButton.setEnabled(false);
            this.unassignButton.setEnabled(false);
        }
    }

    private void devicePairedHandler(PairedDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device.getJadbDevice());
        data.setDeviceModel(device.getDeviceModel());
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

    private void deviceAssignedHandler(PairedDevice device, String alias) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device.getJadbDevice());
        data.setConnectionStatus(ASSIGNED);
        data.setAlias(alias);
        model.fireTableDataChanged();
    }

    private void deviceUnassignedHandler(PairedDevice device) {
        DeviceTableItemModel model = (DeviceTableItemModel) devicesTable.getModel();
        DeviceTableItemModel.DeviceTableData data = model.getDeviceData(device.getJadbDevice());
        data.setConnectionStatus(PAIRED);
        data.setAlias("-");
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
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Devices", panel2);
        unassignButton = new JButton();
        unassignButton.setText("Unassign");
        panel2.add(unassignButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pairButton = new JButton();
        pairButton.setText("Pair");
        panel2.add(pairButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        pauseButton = new JButton();
        pauseButton.setText("Pause");
        panel3.add(pauseButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setText("Stop");
        panel3.add(stopButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        profileTree = new JTree();
        scrollPane2.setViewportView(profileTree);
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
            private String deviceModel = "unspecified";

            public DeviceTableData(JadbDevice jadbDevice) {
                this.jadbDevice = jadbDevice;
            }

            public String getId() {
                return jadbDevice.getSerial();
            }

            public String getDeviceType() {
                return this.deviceModel;
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

            public String getDeviceModel() {
                return deviceModel;
            }

            public void setDeviceModel(String deviceModel) {
                this.deviceModel = deviceModel;
            }
        }
    }

    enum ConnectionStatus {
        UNPAIRED,
        PAIRED,
        ASSIGNED,
        PERMISSION_REQUIRED
    }

    private class ProfileExecutorWatcherThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (profileExecutor != null) {
                    try {
                        RunStatus runStatus = profileExecutor.getRunState();

                        if (profileExecutor.getLastExecutionException() != null) {
                            LOG.error("Error during profile execution", profileExecutor.getLastExecutionException());
                            showMessage("Error during profile execution", profileExecutor.getLastExecutionException());
                            profileExecutor.stop();
                            profileExecutor = null;
                        }
                    } catch (Exception ex) {
                        showMessage("Unknown error in profile watcher", ex);
                    }
                }

                try {
                    Thread.sleep(PROFILE_WATCHER_UPDATE_MS);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}

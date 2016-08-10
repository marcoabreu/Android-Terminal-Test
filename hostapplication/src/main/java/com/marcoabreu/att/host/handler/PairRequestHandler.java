package com.marcoabreu.att.host.handler;

import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.PairRequestMessage;
import com.marcoabreu.att.communication.message.PairResponseMessage;
import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.device.PairedDevice;
import com.marcoabreu.att.device.RuntimeCompiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AbreuM on 04.08.2016.
 */
public class PairRequestHandler implements BridgeMessageListener{
    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) throws IOException {
        if(message instanceof PairRequestMessage) {
            //Compile dynamic script
            PairRequestMessage request = (PairRequestMessage) message;
            PairResponseMessage response = new PairResponseMessage(request);

            PairedDevice pairedDevice = (PairedDevice)device;

            try {
                pairedDevice.setSerial(request.getSerialString());
                pairedDevice.setDeviceModel(request.getDeviceModel());
                pairedDevice.setJadbDevice(DeviceManager.getInstance().getConnectedDevices().stream().filter(jadbDevice -> jadbDevice.getSerial().equals(pairedDevice.getSerial())).findFirst().get());

                //TODO remove local path
                String basePath = "C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\res\\scripts\\device";
                String libPath = "C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\res\\scripts\\device\\libs";
                RuntimeCompiler compiler = new RuntimeCompiler(basePath);
                Pair<File, Map<String, String>> compiledDex = compiler.convertDex(new File(libPath));
                response.setDexFileContent(FileUtils.readFileToByteArray(compiledDex.getLeft()));

                Map<String, String> classpathMapping = new HashMap<>();
                for(Map.Entry<String, String> entry : compiledDex.getRight().entrySet()) {
                    String replaced = entry.getKey().replace(basePath, "").replace(".java", "").replace("\\", "/");
                    replaced = replaced.substring(1); //Remove starting slash
                    classpathMapping.put(replaced, entry.getValue());
                }

                response.setClasspathMapping(classpathMapping);

                DeviceManager.getInstance().registerPairedDevice((PairedDevice) device);

            } catch (Exception ex) {
                response.setOccuredException(ex);
            }

            device.sendResponse(response);
        }
     }
}

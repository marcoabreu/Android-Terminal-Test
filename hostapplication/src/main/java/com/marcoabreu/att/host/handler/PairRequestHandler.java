package com.marcoabreu.att.host.handler;

import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.PairRequestMessage;
import com.marcoabreu.att.communication.message.PairResponseMessage;
import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.device.PairedDevice;
import com.marcoabreu.att.device.RuntimeDexCompiler;

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

            try {
                //TODO remove local path
                String basePath = "C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\res\\scripts\\device";
                RuntimeDexCompiler compiler = new RuntimeDexCompiler(basePath);
                Pair<File, Map<String, String>> compiledDex = compiler.convert();
                response.setDexFileContent(FileUtils.readFileToByteArray(compiledDex.getLeft()));

                Map<String, String> classpathMapping = new HashMap<>();
                for(Map.Entry<String, String> entry : compiledDex.getRight().entrySet()) {
                    String replaced = entry.getKey().replace(basePath, "").replace(".java", "").replace("\\", "/");
                    replaced = replaced.substring(1); //Remove starting slash
                    classpathMapping.put(replaced, entry.getValue());
                }

                response.setClasspathMapping(classpathMapping);

                DeviceManager.getInstance().addPairedDevice((PairedDevice) device);
                //TODO: Notify a device has been paired

            } catch (Exception ex) {
                response.setOccuredException(ex);
            }

            device.sendResponse(response);
        }
     }
}

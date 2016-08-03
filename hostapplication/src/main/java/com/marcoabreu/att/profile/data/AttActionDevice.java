package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.host.handler.DeviceActionCompiler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "ActionDevice")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttActionDevice extends AttAction {

    @XmlAttribute(name = "targetDevice", required = true)
    private String targetDevice;

    /**
     * Retrieve the target device from the device manager
     * @return Instance of the physical device
     */
    private PhysicalDevice retrievePhysicalDevice() {
        return DeviceManager.getInstance().getPairedDeviceBySynonym(targetDevice);
    }

    @Override
    public Composite convertLogic() {
        final DeviceActionCompiler compiler;
        try {
            compiler = new DeviceActionCompiler(retrievePhysicalDevice(), this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return new Action(() -> {
            try {
                compiler.executeVoid();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            return true;
        });
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }
}

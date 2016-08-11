package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.host.DeviceActionCompiler;
import com.marcoabreu.att.profile.ProfileExecutor;

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

    @Override
    public Composite convertLogic(ProfileExecutor profileExecutor) {
        final DeviceActionCompiler compiler;
        try {
            compiler = new DeviceActionCompiler(DeviceManager.getInstance().getPairedDeviceByAlias(targetDevice), this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return profileExecutor.registerComposite(this, new Action(() -> {
            try {
                compiler.executeVoid();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            return true;
        }));
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.targetDevice, this.getName());
    }
}

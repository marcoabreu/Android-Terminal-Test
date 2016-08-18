package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.device.DeviceManager;
import com.marcoabreu.att.host.DeviceActionCompiler;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "ParameterScriptDevice")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttParameterScriptDevice extends AttParameterScript {
    private DeviceActionCompiler compiler;

    @XmlAttribute(name = "targetDevice", required = true)
    private String targetDevice;

    @Override
    public Serializable getValue() {
        try {
            return compiler.executeReturn();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void init() {
        try {
            compiler = new DeviceActionCompiler(DeviceManager.getInstance().getPairedDeviceByAlias(targetDevice), this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                compiler.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }
}

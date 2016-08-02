package com.marcoabreu.att.profile.data;

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
    @XmlAttribute(name = "targetDevice", required = true)
    private String targetDevice;

    @Override
    public Object getValue() {
        //Load file content

        //Send to device compiler

        //Execute method

        throw new RuntimeException();
    }

    @Override
    public void init() {
        //TODO
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }
}

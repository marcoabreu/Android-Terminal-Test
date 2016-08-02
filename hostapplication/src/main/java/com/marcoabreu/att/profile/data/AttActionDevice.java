package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;

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
    public Composite convertLogic() {
        return new Action(() -> {
            //Load file content

            //Send to device compiler

            //Execute method

            throw new RuntimeException();
        });
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }
}

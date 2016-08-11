package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.profile.ProfileExecutor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AttComposite {
    @XmlAttribute(name = "name")
    private String name;

    public abstract Composite convertLogic(ProfileExecutor profileExecutor);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

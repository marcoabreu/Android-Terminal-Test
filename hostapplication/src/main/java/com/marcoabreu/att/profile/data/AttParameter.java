package com.marcoabreu.att.profile.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AttParameter {

    @XmlAttribute(name = "key", required = true)
    private String key;

    public AttParameter() {
    }

    public abstract Serializable getValue();

    public abstract void init();

    public AttParameter(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

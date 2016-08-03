package com.marcoabreu.att.profile.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "TextParameter")
public class AttParameterText extends AttParameter {
    @XmlAttribute(name = "value")
    private String textValue;

    public AttParameterText() {
        super();
    }

    public AttParameterText(String key, String textValue) {
        super(key);
        this.textValue = textValue;
    }

    @Override
    public void init() {
    }

    @Override
    public Serializable getValue() {
        return this.textValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}

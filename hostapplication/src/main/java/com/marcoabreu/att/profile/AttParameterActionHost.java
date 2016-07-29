package com.marcoabreu.att.profile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "ParameterActionHost")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttParameterActionHost extends AttParameterAction {
    @Override
    public Object getValue() {
        //Load file content

        //Send to host compiler

        //Execute method

        throw new RuntimeException();
    }
}

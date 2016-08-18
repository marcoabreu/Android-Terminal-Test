package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.host.HostActionCompiler;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "ParameterScriptHost")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttParameterScriptHost extends AttParameterScript {
    HostActionCompiler compiler;

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
            compiler = new HostActionCompiler(this);
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
}

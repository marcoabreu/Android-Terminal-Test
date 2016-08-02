package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.host.ActionCompiler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import bsh.EvalError;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "ParameterScriptHost")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttParameterScriptHost extends AttParameterScript {
    ActionCompiler compiler;

    @Override
    public Object getValue() {
        try {
            return compiler.executeReturn();
        } catch (EvalError evalError) {
            throw new RuntimeException(evalError);
        }
    }

    @Override
    public void init() {
        try {
            compiler = new ActionCompiler(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

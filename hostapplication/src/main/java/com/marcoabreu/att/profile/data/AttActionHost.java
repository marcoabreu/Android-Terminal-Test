package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.host.ActionCompiler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import bsh.EvalError;

/**
 * An Action to execute a script on the host computer
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "ActionHost")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttActionHost extends AttAction {

    public AttActionHost() {
        super();
    }

    @Override
    public Composite convertLogic() {
        final ActionCompiler compiler;
        try {
            compiler = new ActionCompiler(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return new Action(() -> {
            try {
                compiler.executeVoid();
            } catch (EvalError evalError) {
                throw new RuntimeException(evalError);
            }

            return true;
        });
    }
}
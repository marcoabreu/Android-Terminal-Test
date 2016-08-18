package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.host.HostActionCompiler;
import com.marcoabreu.att.profile.ProfileExecutor;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
    public Composite convertLogic(ProfileExecutor profileExecutor) {
        final HostActionCompiler compiler;
        try {
            compiler = new HostActionCompiler(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return profileExecutor.registerComposite(this, new Action(() -> {
            try {
                compiler.executeVoid();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                try {
                    compiler.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }));
    }

    @Override
    public String toString() {
        return String.format("Host: %s", this.getName());
    }
}

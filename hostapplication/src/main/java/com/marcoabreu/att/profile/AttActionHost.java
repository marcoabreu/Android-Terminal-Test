package com.marcoabreu.att.profile;

import com.marcoabreu.att.engine.Composite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
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
        throw new RuntimeException();
    }
}

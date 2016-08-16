package com.marcoabreu.att.profile.data.conditional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 16.08.2016.
 */
@XmlRootElement(name = "Or")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttOr extends ConditionContainer {

    public AttOr() {
        super();
    }

    @Override
    public boolean evaluate() {
        return this.conditions.stream().anyMatch(condition -> condition.evaluate());
    }
}

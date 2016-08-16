package com.marcoabreu.att.profile.data.conditional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Inverse a condition
 * Created by AbreuM on 16.08.2016.
 */
@XmlRootElement(name = "Inverse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttInverse extends Condition {
    @XmlElementRef(name = "condition")
    private Condition condition;

    @Override
    public boolean evaluate() {
        return !condition.evaluate();
    }
}

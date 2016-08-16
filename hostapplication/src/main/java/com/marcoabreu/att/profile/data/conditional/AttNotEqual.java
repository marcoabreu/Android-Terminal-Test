package com.marcoabreu.att.profile.data.conditional;

import org.apache.commons.lang3.tuple.Pair;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by AbreuM on 16.08.2016.
 */
@XmlRootElement(name = "NotEqual")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttNotEqual extends ValueComparator {
    @Override
    public boolean evaluate() {
        Pair<Serializable, Serializable> values = this.evaluateValues();

        return !values.getLeft().equals(values.getRight());
    }
}

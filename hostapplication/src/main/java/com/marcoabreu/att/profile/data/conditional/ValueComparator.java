package com.marcoabreu.att.profile.data.conditional;

import com.marcoabreu.att.profile.data.AttParameter;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import java.io.Serializable;

/**
 * Base class holding two value suppliers and offering the possibility to compare them
 * Created by AbreuM on 16.08.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ValueComparator extends Condition {
    @XmlElementRef
    protected AttParameter value1;

    @XmlElementRef
    protected AttParameter value2;

    protected Pair<Serializable, Serializable> evaluateValues() {
        Serializable val1 = value1.getValue();
        Serializable val2 = value2.getValue();

        if(!val1.getClass().equals(val2.getClass())) {
            throw new ClassCastException(String.format("Type mismatch for comparison. %s vs %s", value1.getClass(), value2.getClass()));
        }

        return Pair.of(val1, val2);
    }

    public AttParameter getValue1() {
        return value1;
    }

    public void setValue1(AttParameter value1) {
        this.value1 = value1;
    }

    public AttParameter getValue2() {
        return value2;
    }

    public void setValue2(AttParameter value2) {
        this.value2 = value2;
    }
}

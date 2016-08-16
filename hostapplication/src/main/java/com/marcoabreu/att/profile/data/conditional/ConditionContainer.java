package com.marcoabreu.att.profile.data.conditional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class containing multiple conditions for comparison
 * Created by AbreuM on 16.08.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( { AttAnd.class, AttEquals.class, AttInverse.class, AttNotEqual.class, AttOr.class, ValueComparator.class})
public abstract class ConditionContainer extends Condition {
    @XmlElementRef
    protected List<Condition> conditions;

    public ConditionContainer() {
        this.conditions = new ArrayList<>();
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    public void removeCondition(Condition condition) {
        this.conditions.remove(condition);
    }
}

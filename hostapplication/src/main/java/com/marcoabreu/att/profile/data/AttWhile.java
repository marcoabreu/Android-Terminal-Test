package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Repeat;
import com.marcoabreu.att.profile.ProfileExecutor;
import com.marcoabreu.att.profile.data.conditional.*;

import javax.xml.bind.annotation.*;
import java.util.stream.Collectors;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "While")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( { AttAnd.class, AttEquals.class, AttInverse.class, AttNotEqual.class, AttOr.class, ValueComparator.class})
public class AttWhile extends AttGroupContainer {
    @XmlElementRef
    private Condition condition;

    public AttWhile() {
    }



    @Override
    public Composite convertLogic(ProfileExecutor profileExecutor) {
        return profileExecutor.registerComposite(this, new Repeat(composites.stream().map(composite -> composite.convertLogic(profileExecutor)).collect(Collectors.toList())));
    }

    @Override
    public String toString() {
        return String.format("While: %s", this.getName());
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}

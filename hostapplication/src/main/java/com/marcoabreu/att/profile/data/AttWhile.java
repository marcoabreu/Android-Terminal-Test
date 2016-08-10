package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Repeat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.stream.Collectors;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "While")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttWhile extends AttGroupContainer {
    //TODO implement
    @Override
    public Composite convertLogic() {
        return new Repeat(composites.stream().map(composite -> composite.convertLogic()).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        return String.format("While: %s", this.getName());
    }
}

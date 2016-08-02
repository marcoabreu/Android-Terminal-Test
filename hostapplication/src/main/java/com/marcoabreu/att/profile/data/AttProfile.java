package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Sequence;

import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "AttProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttProfile extends AttGroupContainer {
    public AttProfile() {
        super();
    }

    @XmlAttribute(name = "id")
    private String identifier;

    @XmlAttribute(name = "description")
    private String description;

    @Override
    public Composite convertLogic() {
        return new Sequence(composites.stream().map(composite -> composite.convertLogic()).collect(Collectors.toList()));
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

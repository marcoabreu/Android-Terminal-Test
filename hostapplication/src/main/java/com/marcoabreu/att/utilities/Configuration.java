package com.marcoabreu.att.utilities;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 17.08.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="AttConfig")
public class Configuration {
    private static Configuration instance;

    @XmlElement(name="Jdk7Path")
    private String jdk7path = "";

    @XmlElement(name="AndroidSdkPath")
    private String androidSdkPath = "";

    public static Configuration getInstance() {
        if(instance == null) {
            throw new NullPointerException("Configuration not loaded");
        }
        return instance;
    }

    public static void loadConfiguration(File configFile) throws JAXBException {
        if(!configFile.exists()) {
            instance = new Configuration();
        } else {
            JAXBContext jc = JAXBContext.newInstance(Configuration.class);
            Unmarshaller u = jc.createUnmarshaller();
            instance = (Configuration) u.unmarshal(configFile);
        }
    }

    public static void saveConfiguration(File configFile) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance( Configuration.class );
        Marshaller m = jc.createMarshaller();
        m.marshal(instance,configFile);
    }

    public String getJdk7path() {
        return jdk7path;
    }

    public void setJdk7path(String jdk7path) {
        this.jdk7path = jdk7path;
    }

    public String getAndroidSdkPath() {
        return androidSdkPath;
    }

    public void setAndroidSdkPath(String androidSdkPath) {
        this.androidSdkPath = androidSdkPath;
    }
}

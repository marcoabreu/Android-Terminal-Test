package com.marcoabreu.att.communication;

/**
 * Class representing a physical device like a phone
 * Created by AbreuM on 03.08.2016.
 */
public abstract class PhysicalDevice {
    private final String id;

    public PhysicalDevice(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}

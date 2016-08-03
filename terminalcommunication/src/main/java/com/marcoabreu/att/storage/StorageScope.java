package com.marcoabreu.att.storage;

/**
 * Scope of stored data. Decides about the time to live and when the data will be purged
 * Created by AbreuM on 01.08.2016.
 */
public enum StorageScope {
    /**
     * Singleton, data is only purged by closing the application
     */
    APPLICATION,

    /**
     * Data is saved for every device and purged upon disconnecting
     */
    DEVICE,

    /**
     * Combination of DEVICE and PROFILE. Data will be purged after finishing the profile, but is kept separately for every device
     */
    DEVICE_PROFILE,

    /**
     * Data is saved during the execution of the current profile and will be purged upon finishing the profile
     */
    PROFILE,
}

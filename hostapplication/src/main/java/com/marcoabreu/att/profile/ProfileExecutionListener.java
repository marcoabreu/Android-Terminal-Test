package com.marcoabreu.att.profile;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.profile.data.AttComposite;

/**
 * Listener to monitor profile execution
 * Created by AbreuM on 11.08.2016.
 */
public interface ProfileExecutionListener {
    /**
     * Event fired before starting execution of a composite
     * @param profileComposite
     * @param engineComposite
     */
    void onStartComposite(AttComposite profileComposite, Composite engineComposite);

    /**
     * Event fired after finishing execution of a composite
     * @param profileComposite
     * @param engineComposite
     */
    void onEndComposite(AttComposite profileComposite, Composite engineComposite);
}

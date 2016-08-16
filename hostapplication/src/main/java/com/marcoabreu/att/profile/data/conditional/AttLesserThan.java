package com.marcoabreu.att.profile.data.conditional;

import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;

/**
 * Created by AbreuM on 16.08.2016.
 */
@Deprecated //TODO: A generic numeric comparison needs quite some effort and extends available time
public class AttLesserThan  extends ValueComparator {
    @Override
    public boolean evaluate() {
        Pair<Serializable, Serializable> values = this.evaluateValues();

        throw new RuntimeException("Not implemented");
    }
}
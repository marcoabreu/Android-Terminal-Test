package com.marcoabreu.att.profile.data;

import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by AbreuM on 29.07.2016.
 */
@XmlRootElement(name = "Sleep")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttSleep extends AttComposite {
    @XmlAttribute(name = "durationMs")
    private long durationMs;

    public AttSleep() {
    }

    public AttSleep(long durationMs) {
        this.durationMs = durationMs;
    }

    @Override
    public Composite convertLogic() {
        final long timeToSleep = durationMs;
        return new Action(() -> {
            try {
                Thread.sleep(timeToSleep);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    @Override
    public String toString() {
        return String.format("Sleep %ds: %s", this.getDurationMs() / 1000, this.getName());
    }
}

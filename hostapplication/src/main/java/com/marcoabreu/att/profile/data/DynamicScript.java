package com.marcoabreu.att.profile.data;

import java.util.List;

/**
 * Created by AbreuM on 02.08.2016.
 */
public interface DynamicScript {
    public String getPath();
    public String getMethod();
    public long getTimeoutMs();
    public List<AttParameter> getParameters();
}

package com.marcoabreu.att.communication.message;


import com.marcoabreu.att.communication.Opcode;

import java.util.Map;

/**
 * Answer of the host to a pairing request
 * Created by AbreuM on 02.08.2016.
 */
public class PairResponseMessage extends BaseMessage {
    //Dynamic script content
    private byte[] dexFileContent;
    private Map<String, String> classpathMapping;

    public PairResponseMessage(PairRequestMessage pairRequestMessage) {
        super(Opcode.PAIR_RESPONSE, pairRequestMessage);
    }

    public byte[] getDexFileContent() {
        return dexFileContent;
    }

    public void setDexFileContent(byte[] dexFileContent) {
        this.dexFileContent = dexFileContent;
    }

    public Map<String, String> getClasspathMapping() {
        return classpathMapping;
    }

    public void setClasspathMapping(Map<String, String> classpathMapping) {
        this.classpathMapping = classpathMapping;
    }
}

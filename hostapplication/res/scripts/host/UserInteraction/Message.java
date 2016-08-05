package com.marcoabreu.att.device;

import com.marcoabreu.att.profile.ScriptRuntimeContainer;

import javax.swing.JOptionPane;

/**
 * Created by AbreuM on 05.08.2016.
 */
public class Message {
    public static void showMessage(ScriptRuntimeContainer runtime) {
        String message = runtime.getParameter("message");

        JOptionPane.showMessageDialog(null, message);
    }
}

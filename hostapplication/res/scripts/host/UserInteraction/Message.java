package com.marcoabreu.att;

import com.marcoabreu.att.profile.ScriptRuntimeContainer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by AbreuM on 05.08.2016.
 */
public class Message {
    public static void showMessage(ScriptRuntimeContainer runtime) {
        String message = runtime.getParameter("message");

        showMessageImpl(message);
    }

    public static void showMessageImpl(String message) {
        JOptionPane op = new JOptionPane(message,JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = op.createDialog("Android Terminal Test");
        dialog.setAlwaysOnTop(true);
        dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        //dialog.setModal(true); //blocks execution
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}

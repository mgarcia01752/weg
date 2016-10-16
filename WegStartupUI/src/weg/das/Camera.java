/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weg.ui.WegMainUI;

/**
 *
 * @author Maurice
 */
public class Camera implements Runnable {

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void start() {
        try {
            Runtime.getRuntime().exec("sudo raspivid -t 0 -p 220,280,700,175");
        } catch (IOException ex) {
            Logger.getLogger(WegMainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

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

    public final static int FULL_SCREEN = 0;
    public final static int PREVIEW_SCREEN = 1;
    
    private int iCameraView = PREVIEW_SCREEN;
    private Process procCamera = null;
    
    public Camera() {this.iCameraView = PREVIEW_SCREEN;}
    
    public Camera(int iCameraView) {this.iCameraView = iCameraView;}
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void start() {
        
        if (iCameraView == FULL_SCREEN) {
            try {
                this.procCamera = Runtime.getRuntime().exec("sudo raspivid -t 0 -f");
            } catch (IOException ex) {
                Logger.getLogger(WegMainUI.class.getName()).log(Level.SEVERE, null, ex);
            }       
        } else if (iCameraView == PREVIEW_SCREEN) {
            try {
                this.procCamera = Runtime.getRuntime().exec("sudo raspivid -t 0 -p 225,280,700,175");
            } catch (IOException ex) {
                Logger.getLogger(WegMainUI.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }

    }
             
    /**
     * 
     */
    public void killProcess() {
        this.procCamera.destroy();
    }
    
}

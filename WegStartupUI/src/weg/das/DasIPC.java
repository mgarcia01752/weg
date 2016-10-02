/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maurice
 */
public class DasIPC implements Runnable {

    private Process pIPC = null; 
    
    @Override
    public void run() {
       
    }
    
    public void start() {
        
        if (pIPC == null) {
           try {
               this.pIPC = Runtime.getRuntime().exec("sudo DamCommSocket -d > log.txt");
           } catch (IOException ex) {
               Logger.getLogger(DasIPC.class.getName()).log(Level.SEVERE, null, ex);
           }           
        }
    }
    
    /**
     * 
     */
    public void killProcess() {
        if (this.pIPC != null) {
           this.pIPC.destroy();
           this.pIPC = null;
        }
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maurice
 */
public class WegStartupUI {

        private static class DataAqusitionSystem implements Runnable {
        
        private WegMainUI wmu;
        
        public DataAqusitionSystem(WegMainUI wmu) {
            this.wmu = wmu;
        }
        
        /**
         * 
         */
        public void run () {
            
            while (true) {
                
                /* Start IPC connection to PIC-DAS */
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WegMainUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /* Start of GUI */
        WegMainUI wmu = new WegMainUI();
        wmu.setVisible(true);
     
        Thread threadDAS = new Thread(new DataAqusitionSystem(wmu));
        threadDAS.start();
        
    }
    
}

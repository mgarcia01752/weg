/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weg.das.DasCommands;
import weg.das.DasConnection;
import weg.das.Gps;

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
            
            try {
                DasConnection dc;
                dc = new DasConnection(InetAddress.getByName("10.1.10.16"),DasConnection.IPC_PORT);
                
                Gps gps = new Gps();
                
                while (true) {
                    
                    String sCommandResponse = dc.get(DasCommands.GPS);
                    
                    if (!gps.parse(sCommandResponse)) {
                        continue;
                    }
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WegMainUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    
                    System.out.println("GPS Raw -> " + sCommandResponse);
                    System.out.println("GPS ToStrig -> " + gps.getCurrentGpsData());
                    System.out.println("GPS Lat -> " + gps.getLatitude());
                    System.out.println("GPS Lng -> " + gps.getLongitude());
                    
                    this.wmu.updateGPS(gps);
                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(WegStartupUI.class.getName()).log(Level.SEVERE, null, ex);
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

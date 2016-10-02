/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.ui;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weg.das.Barometer;
import weg.das.DasCommands;
import weg.das.DasConnection;
import weg.das.Gps;
import weg.das.Temperature;
import weg.das.UV;

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
            
            Gps gps = new Gps();
            
            while(true) {
                
                wmu.updateSystemTime();
                
                /*Wait till Connect Button is selected */
                while (!this.wmu.isDasConnectSelected()) {
                    
                    wmu.updateSystemTime();
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WegStartupUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    System.out.println("Connect Button Toogled - Disconnected from DAS.....");
                    
                }
                
                System.out.println("Connect Button Toogled - Connected to DAS.....");
                
                DasConnection dc;
                
                try {
                  
                  /* if useing in remote mode get IP Addresses */
                  if (this.wmu.isRemoteDasSelected()) {
                    dc = new DasConnection(this.wmu.getRemoteDasInetAddress(),DasConnection.IPC_PORT);  
                  } else {
                     dc = new DasConnection(this.wmu.getLocalDasInetAddress(),DasConnection.IPC_PORT); 
                  }
              
                  while (true) {
                      
                      wmu.updateSystemTime();

                      String sCommandResponse = dc.get(DasCommands.GPS);

                      if (gps.parse(sCommandResponse)) {              
                          System.out.println("GPS Raw -> " + sCommandResponse);
                          System.out.println("GPS ToStrig -> " + gps.getCurrentGpsData());
                          System.out.println("GPS Lat -> " + gps.getLatitude());
                          System.out.println("GPS Lng -> " + gps.getLongitude());

                          this.wmu.updateGPS(gps);
                      }

                      sCommandResponse = dc.get(DasCommands.TEMP_F);

                      this.wmu.updateTemperture(Temperature.getTemp(sCommandResponse));

                      sCommandResponse = dc.get(DasCommands.BaROMETER);

                      this.wmu.updateBarometer(Barometer.getBarometer(sCommandResponse).toString());
                      
                      sCommandResponse = dc.get(DasCommands.UV);

                      this.wmu.updateUV(UV.getUV(sCommandResponse).toString());
                      
                      /* When Connect is not selcted exit Loop and wait till it is selected again */
                      if (!this.wmu.isDasConnectSelected())break;

                  }
              } catch (UnknownHostException ex) {
                  Logger.getLogger(WegStartupUI.class.getName()).log(Level.SEVERE, null, ex);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.ui;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
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
                      
                      System.out.println("+-----------------------------DAS QUERY---------------------------------------------+");
                      
                      wmu.updateSystemTime();

                      String sCommandResponse = dc.get(DasCommands.FULL_SENSOR_DUMP);
                      
                      if (sCommandResponse == null) {
                          System.out.println("sCommandResponse NULL RESPONSE FOUND");
                          continue;
                      }
                      
                      System.out.println("CMD-RSP: " + sCommandResponse);
                      
                      /* UVindex,Tdata,Pdata,nmea */
                      List<String> lsFullCommand = Arrays.asList(sCommandResponse.split("\\|"));
                      
                      if (lsFullCommand.size()<4) {
                          System.out.println("+-----------------------------ERROR---------------------------------------------+");
                          System.out.println("lsFullCommand size is less than 4 - Actual: " + lsFullCommand.size());
                          System.out.println("CMD-ERROR: " + lsFullCommand.toString());
                          continue;
                      } else if(!lsFullCommand.get(0).contentEquals("600:")) {
                          System.out.println("+-----------------------------NO 600 CODE HEADER---------------------------------------------+");
                          System.out.println("CMD-ERROR: " + lsFullCommand.toString());
                          continue;
                      }
                  
                      if (gps.parse(lsFullCommand.get(4))) {
                          System.out.println("+-----------------------------GPS---------------------------------------------+");
                          System.out.println("GPS Raw -> " + sCommandResponse);
                          System.out.println("GPS ToStrig -> " + gps.getCurrentGpsData());
                          System.out.println("GPS Lat -> " + gps.getLatitude());
                          System.out.println("GPS Lng -> " + gps.getLongitude());

                          this.wmu.updateGPS(gps);
                          
                          this.wmu.updateCompass();
                          
                      }

                      //sCommandResponse = dc.get(DasCommands.TEMP_F);
                      System.out.println("Temp: " + lsFullCommand.get(2));
                      this.wmu.updateTemperture(Temperature.getTemp(lsFullCommand.get(2)));

                      //sCommandResponse = dc.get(DasCommands.BaROMETER);
                      System.out.println("Baro: " + lsFullCommand.get(3));
                      this.wmu.updateBarometer(Barometer.getBarometer(lsFullCommand.get(3)).toString());
                      
                      //sCommandResponse = dc.get(DasCommands.UV);
                      System.out.println("UV: " + lsFullCommand.get(1));
                      this.wmu.updateUV(UV.getUV(lsFullCommand.get(1)).toString());
                    
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

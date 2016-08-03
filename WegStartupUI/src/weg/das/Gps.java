/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * 150:$GPGGA,070024.000,4009.3988,N,07452.0023,W,1,07,1.26,65.1,M,-34.0,M,,*67 
 * 
 * @author Maurice
 */
public class Gps {
    
    private String sGPSDataFormat;
    private List<String> lsGpsDataRMCFormat;
    private List<String> lsGpsDataGGAFormat;
    private List<String> lsGpsDataFormat;
    
    private final Integer   GPS_FORMAT =        0;
    private final Integer   UTC =               1;
    private final Integer   LATITUDE =          2;
    private final Integer   NS_INDICATOR =      3;
    private final Integer   LONGITUDE =         4;
    private final Integer   EW_INDICATOR =      5;
    private final Integer   POSITION_FIX =      6;
    private final Integer   SATELLITE_NUM =     7;
    private final int       GPSGGA_LENGTH =     15;
    
    private final Integer   RMC_LATITUDE =      3;
    private final Integer   RMC_NS_INDICATOR =  4;
    private final Integer   RMC_LONGITUDE =     5;
    private final Integer   RMC_EW_INDICATOR =  6;
    private final Integer   RMC_DATE =          9;
    private final int       RMC_GPS_LENGTH =   13;
    
    private final String GPS_MODE_GPRMC = "150:$GPRMC";
    private final String GPS_MODE_GPGGA = "150:$GPGGA";
    
    /**
     * Format: 
     * @param sGPSDataFormat 
     */
    public boolean parse(String sGPSDataFormat) {
        this.sGPSDataFormat = sGPSDataFormat;
        return processGpsData();
    }
    
    /**
     * 
     * @return 
     */
    public String getUTC() {
        return lsGpsDataGGAFormat.get(UTC);
    }
    
    /**
     * 
     * @return 
     */
    public String getLatitude() {
        return lsGpsDataGGAFormat.get(LATITUDE);
    }
    
    /**
     * 
     * @return 
     */
    public String getLongitude() {
        return lsGpsDataGGAFormat.get(LONGITUDE);
    }
    
    /**
     * 
     * @return 
     */
    public String getNSIndicator() {
        return lsGpsDataGGAFormat.get(NS_INDICATOR); 
    }
    
    /**
     * 
     * @return 
     */
    public String getEWIndicator() {
        return lsGpsDataGGAFormat.get(EW_INDICATOR); 
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumSatellites() {
        return Integer.parseInt(lsGpsDataGGAFormat.get(SATELLITE_NUM));
    }
 
   /**
     * 
     * @return 0 Fix not available; 1 GPS fix; 2 Differential GPS fix
     * 
     */
    public Integer getFixIndicator() {
       return Integer.parseInt(lsGpsDataGGAFormat.get(POSITION_FIX)); 
    }
    
    /**
     * 
     * @return 
     */
    public List<String> getCurrentGpsData() {
        return this.lsGpsDataFormat;
    }
        
    /**
     * $GPGGA,064951.000,2307.1256,N,12016.4438,E,1,8,0.95,39.9,M,17.8,M,,*65
     * $GPRMC,070325.000,A,4009.3977,N,07452.0034,W,0.22,129.43,020816,,,A*7B
     */
    private boolean processGpsData() {
        
        boolean boolParseStatus = false;
        
        if (this.sGPSDataFormat == null) {
            return boolParseStatus;
        }
        
        this.lsGpsDataFormat = Arrays.asList(this.sGPSDataFormat.split(","));
        
        System.out.println("GPS-DATA-LENGTH: " + this.lsGpsDataFormat.size());
        
        if (this.lsGpsDataFormat.get(GPS_FORMAT).equalsIgnoreCase(GPS_MODE_GPRMC)) {
          
            System.out.println("GPS-RMC-DATA: " + this.lsGpsDataFormat);
            
            this.lsGpsDataRMCFormat = new ArrayList<String>();
            this.lsGpsDataRMCFormat.addAll(this.lsGpsDataFormat);
            
            if (this.lsGpsDataGGAFormat != null) {       
                this.lsGpsDataGGAFormat.set(UTC,this.lsGpsDataFormat.get(UTC));
                this.lsGpsDataGGAFormat.set(LATITUDE,this.lsGpsDataFormat.get(RMC_LATITUDE));
                this.lsGpsDataGGAFormat.set(LONGITUDE,this.lsGpsDataFormat.get(RMC_LONGITUDE));
            }
        
        } else if (this.lsGpsDataFormat.get(GPS_FORMAT).equalsIgnoreCase(GPS_MODE_GPGGA)) {
            
            if (this.lsGpsDataFormat.size() == GPSGGA_LENGTH) {           
                this.lsGpsDataGGAFormat = new ArrayList<String>();
                this.lsGpsDataGGAFormat.addAll(this.lsGpsDataFormat);
                boolParseStatus = true;
            } else {
                System.out.println("GPS-GGA-DATA-ERROR: " + this.lsGpsDataFormat);     
            }
            
        } else {
            System.out.println("GPS-DATA: " + this.lsGpsDataFormat);
        }
        
       return boolParseStatus; 
    }
    
    
    
}

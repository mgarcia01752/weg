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
    private List<String> lsGpsDataFormat;
    
    private final Integer UTC =            1;
    private final Integer LATITUDE =       2;
    private final Integer NS_INDICATOR =   3;
    private final Integer LONGITUDE =      4;
    private final Integer EW_INDICATOR =   5;
    private final Integer POSITION_FIX =   6;
    private final Integer SATELLITE_NUM =  7;
    
    private final String GPS_MODE_GPRMC = "150:$GPRMC";
    private final String GPS_MODE_GPGGA = "150:$GPGGA";
    
    /**
     * Format: 
     * @param sGPSDataFormat 
     */
    public Gps(String sGPSDataFormat) {
        this.sGPSDataFormat = sGPSDataFormat;
        processGpsData();
    }
    
    /**
     * 
     * @return 
     */
    public String getUTC() {
        return lsGpsDataFormat.get(UTC);
    }
    
    /**
     * 
     * @return 
     */
    public String getLatitude() {
        return lsGpsDataFormat.get(LATITUDE);
    }
    
    /**
     * 
     * @return 
     */
    public String getLongitude() {
        return lsGpsDataFormat.get(LONGITUDE);
    }
    
    /**
     * 
     * @return 
     */
    public String getNSIndicator() {
        return lsGpsDataFormat.get(NS_INDICATOR); 
    }
    
    /**
     * 
     * @return 
     */
    public String getEWIndicator() {
        return lsGpsDataFormat.get(EW_INDICATOR); 
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumSatellites() {
        return Integer.parseInt(lsGpsDataFormat.get(SATELLITE_NUM));
    }
 
   /**
     * 
     * @return 0 Fix not available; 1 GPS fix; 2 Differential GPS fix
     * 
     */
    public Integer getFixIndicator() {
       return Integer.parseInt(lsGpsDataFormat.get(POSITION_FIX)); 
    }
    
    /**
     * 
     * @return 
     */
    public List<String> getGpsData() {
        return this.lsGpsDataFormat;
    }
        
    /**
     * $GPGGA,064951.000,2307.1256,N,12016.4438,E,1,8,0.95,39.9,M,17.8,M,,*65 
     */
    private void processGpsData() {
       this.lsGpsDataFormat = new ArrayList<String>(Arrays.asList(this.sGPSDataFormat.split(","))); 
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.util.Arrays;
import java.util.List;

/**
 *
 * $GPGGA,064951.000,2307.1256,N,12016.4438,E,1,8,0.95,39.9,M,17.8,M,,*65 
 * 
 * @author Maurice
 */
public class Gps {
    
    private String sGPSDataFormat;
    private List<String> lsGpsDataFormat;
    
    private static Integer UTC =            1;
    private static Integer LATITUDE =       2;
    private static Integer NS_INDICATOR =   3;
    private static Integer LONGITUDE =      4;
    private static Integer EW_INDICATOR =   5;
    private static Integer POSITION_FIX =   6;
    private static Integer SATELLITE_NUM =  7;
    
    
    /**
     * Format: 
     * @param sGPSDataFormat 
     */
    public Gps(String sGPSDataFormat) {
        this.sGPSDataFormat = sGPSDataFormat;
    }
    
    /**
     * 
     * @return 
     */
    public String getUTC() {
        return lsGpsDataFormat.get(1);
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
     * @return 
     */
    public List<String> getGpsData() {
        return this.lsGpsDataFormat;
    }
    
    /**
     * $GPGGA,064951.000,2307.1256,N,12016.4438,E,1,8,0.95,39.9,M,17.8,M,,*65 
     */
    private void processGpsData() {
       
       this.lsGpsDataFormat = Arrays.asList(this.sGPSDataFormat.split(",")); 
       
       
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    private final Integer   LONGITUDE =         2;
    private final Integer   NS_INDICATOR =      3;
    private final Integer   LATITUDE =          4; 
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
    
    private final String GPS_MODE_GPRMC = "$GPRMC";
    private final String GPS_MODE_GPGGA = "$GPGGA";
    
    private String sLastLatGpsDMS = "W 40° 9' 24.4";
    private String sLastLongGpsDMS = "N 74° 52' 0.7";
    
    private long lLastPaceTime = System.currentTimeMillis();
    private final int PACE_INTERVAL_CHECK = 10;
    
    private double dPaceLastLong = 0;
    private double dPaceLastLat = 0;
    
    /**
     * Format: 
     * @param sGPSDataFormat 
     */
    public boolean parse(String sGPSDataFormat) {
        this.sGPSDataFormat = sGPSDataFormat.replace("\\s+", "");
        return processGpsData();
    }
    
    /**
     * 
     * @return 
     */
    public String getUTC() {
        return utcToClock(lsGpsDataGGAFormat.get(UTC));
    }
    
    /**
     * 
     * @return 
     */
    public String getLatitude() {
        
        Pattern pattern = Pattern.compile("(\\d\\d)(\\d\\d)\\.(\\d+)");
        
        Matcher matcher = pattern.matcher(lsGpsDataGGAFormat.get(LATITUDE));
        if (matcher.find()) {
            System.out.println("MATCH: " + matcher.group(0)); //prints /{item}/
        } else {
            System.out.println("Match not found");
        }
        
        String sDMSDec = "0.0";
        
        if (!lsGpsDataGGAFormat.get(LATITUDE).isEmpty()) {
            sDMSDec = lsGpsDataGGAFormat.get(LATITUDE);
        }
        
        String sLastLtGpsDMS =   lsGpsDataGGAFormat.get(EW_INDICATOR) + " " +
                                decimalToDMS(Double.parseDouble(sDMSDec));
        
        return sLastLtGpsDMS;
    }
    
    /**
     * 
     * @return 
     */
    public String getLongitude() {
        
        String sDMSDec = "0.0";
        
        if (!lsGpsDataGGAFormat.get(LONGITUDE).isEmpty()) {
            sDMSDec = lsGpsDataGGAFormat.get(LONGITUDE);
        }
        
        String sLastLogGpsDMS =   lsGpsDataGGAFormat.get(NS_INDICATOR) + " " +
                                decimalToDMS(Double.parseDouble(sDMSDec));
        
        return sLastLogGpsDMS;
    }
    
    /**
     * 
     * @return 
     */
    public double getLongitudeDecimal() {
        
        double dDMSDec = 0.0;
        
        if (!lsGpsDataGGAFormat.get(LONGITUDE).isEmpty()) {
            dDMSDec = Double.parseDouble(lsGpsDataGGAFormat.get(LONGITUDE));
        } 
        
        return dDMSDec;
    }
    
    /**
     * 
     * @return 
     */
    public double getLatitudeDecimal() {
        
        double dDMSDec = 0.0;
        
        if (!lsGpsDataGGAFormat.get(LONGITUDE).isEmpty()) {
            dDMSDec = Double.parseDouble(lsGpsDataGGAFormat.get(LONGITUDE));
        } 
        
        return dDMSDec;
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
            System.out.println("GPS-STRING-NULL");
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
            
            System.out.println("GPS-GGA-DATA: " + this.lsGpsDataFormat);
            
            if (this.lsGpsDataFormat.size() == GPSGGA_LENGTH) {           
                this.lsGpsDataGGAFormat = new ArrayList<String>();
                this.lsGpsDataGGAFormat.addAll(this.lsGpsDataFormat);
                boolParseStatus = true;
            } else {
                System.out.println("GPS-GGA-DATA-ERROR: " + this.lsGpsDataFormat);     
            }
            
        } else {
            System.out.println("GPS-DATA-ELSE: " + this.lsGpsDataFormat);
        }
        
       return boolParseStatus; 
    }
        
    /**
     * https://community.oracle.com/thread/3619431
     * 
     * @param value
     * @return 
     */
    public static String decimalToDMS(double value) {
        String result = null;
        double degValue = value / 100;
        int degrees = (int) degValue;
        double decMinutesSeconds = ((degValue - degrees)) / .60;
        double minuteValue = decMinutesSeconds * 60;
        int minutes = (int) minuteValue;
        double secsValue = (minuteValue - minutes) * 60;
        result = degrees + "\u00B0" + " " + minutes + "' " + String.format("%.1f", secsValue) + "\" ";
        return result;
    }
    
    /**
     * 
     * UTC Time 064951.000 hhmmss.sss 
     * 
     * @param sUTC
     * @return 
     */
    public static String utcToClock(String sUTC) {
        
        Pattern pattern = Pattern.compile("(\\d\\d)(\\d\\d)(\\d\\d)\\.(\\d+)");
        
        String sUtcClockFormat = "";
        
        Matcher matcher = pattern.matcher(sUTC);
        if (matcher.find()) {
            System.out.println("MATCH: " + matcher.group(0)); //prints /{item}/
            
            sUtcClockFormat = matcher.group(1)+ ":" + matcher.group(2) + ":" + matcher.group(3); 
            
        } else {
            System.out.println("Match not found");
        }
        
        return sUtcClockFormat;
    }
    
    /**
     * 
     * @param sLatLast
     * @param sLongLast
     * @param sLatNew
     * @param sLongNew
     * @return 
     */
    public String compassUpdate(String sLatNew, String sLongNew) {
        
        String sCompassDirection = "--";
        
        String sNewLatDir = "";
        String sNewLongDir = "";        
        
        String sGetLat = this.getLatitude();
        String sGetLong = getLongitude();
        
        double dLatLastSec = getGpsSecond(this.sLastLatGpsDMS);
        double dLatNewSec = getGpsSecond(sLatNew);
        double dLongLastSec = getGpsSecond(this.sLastLongGpsDMS);
        double dLongNewSec = getGpsSecond(sLongNew);
       
        if (dLatNewSec > dLatLastSec) {
            sNewLatDir = "N";
        } else if (dLatNewSec < dLatLastSec) {
            sNewLatDir = "S";
        } else {
            sNewLatDir = "";
        }
        
        if (dLongNewSec > dLongLastSec) {
            sNewLongDir = "W";
        } else if (dLongNewSec < dLongLastSec) {
            sNewLongDir = "E";
        } else {
            sNewLongDir = "";
        }
               
        sCompassDirection = (sNewLatDir+sNewLongDir);
        
        this.sLastLatGpsDMS = sGetLat;
        this.sLastLongGpsDMS = sGetLong;
        
        return sCompassDirection;
        
    }
    
    /**
     * 
     * @param sGpsDMS
     * @return 
     */
    private double getGpsSecond (String sGpsDMS) {
        
        /* N 74° 52' 0.7 */
        Pattern pattern = Pattern.compile(".*\\d+.\\s+\\d+\\'\\s+(\\d+\\.\\d+)");
        
        double dGpsDmsSecond = 0.0;
        
        Matcher matcher = pattern.matcher(sGpsDMS);
        
        if (matcher.find()) {
           dGpsDmsSecond = Double.parseDouble(matcher.group(1));
        }
        
        return dGpsDmsSecond;
    }
    
    /**
     * 
     * @param dUpdateLat
     * @param dUpdateLong
     * @return 
     */
    public Double updatePace(double dUpdateLat, double dUpdateLong) {
        
        Double dPace = 0.0;
        
        /* update inital value */
        if (this.dPaceLastLat == 0) {
            this.dPaceLastLat = dUpdateLat;
            this.dPaceLastLong = dUpdateLong;
            return dPace;
        }
        
        long lDeltaSeconds = (System.currentTimeMillis() - this.lLastPaceTime)/1000;
        
        System.out.println("DELTA: " + lDeltaSeconds);
      
        if (lDeltaSeconds >= this.PACE_INTERVAL_CHECK) {
            
            double dDistance = distance(this.dPaceLastLat, this.dPaceLastLong, dUpdateLat, dUpdateLong, "M");
            
            System.out.println("DISTACE: : " + dDistance);
            
            dPace = (dDistance/lDeltaSeconds) * 3600.0;
            
            this.lLastPaceTime = System.currentTimeMillis();
            this.dPaceLastLat = dUpdateLat;
            this.dPaceLastLong = dUpdateLong;
        }
        
        /* Pace */
        return dPace;
        
    }
    
    /**
     * 
     * http://www.geodatasource.com/developers/java
     * 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit M = Miles, K = Kilometer, N = Nautical Miles
     * @return 
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
            double theta = lon1 - lon2;
            double dist =   Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + 
                            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                    dist = dist * 1.609344;
            } else if (unit == "N") {
                    dist = dist * 0.8684;
            }

            return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians
    http://www.geodatasource.com/developers/java
    :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees
    http://www.geodatasource.com/developers/java
    :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
    }
    
    
}

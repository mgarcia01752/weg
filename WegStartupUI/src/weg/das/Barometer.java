/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

/**
 *
 * @author Maurice
 */
public class Barometer {
    
    private static final String PRESSURE_TREND_STATUS = "Initial Reading";
    private static int iLastPressureReading = -1;
    private static int iCurrentPressureReading = 0;
    
    /**
     * 
     * @param sBarometer
     * @return 
     */
    public static Integer getBarometer(String sBarometer) {
        
        Integer iDefaultRtn = 0;
        
        if (sBarometer == null) {
            return iDefaultRtn;
        }
        
        if (sBarometer.matches("\\d\\d\\d\\:\\d+")) {
        
            if (iLastPressureReading > 0 )
                iLastPressureReading = iCurrentPressureReading;
            
            iCurrentPressureReading = Integer.parseInt(sBarometer.split(":")[1]);
            
            return iCurrentPressureReading;
        
        } else  {
            return iDefaultRtn;
        }
    }
    
    /**
     * 
     * @return 
     */
    public static String getPressureTrendStatus() {
        
        if (iLastPressureReading < 0) {
            return PRESSURE_TREND_STATUS;
        }
        
        if (iCurrentPressureReading > iLastPressureReading) {
            return "Rising";
        } else  if (iCurrentPressureReading < iLastPressureReading) {
            return "Falling";
        } else {
            return "Steady";
        } 
        
    }
    
}

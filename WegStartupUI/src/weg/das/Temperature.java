/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.text.DecimalFormat;

/**
 *
 * @author Maurice
 */
public class Temperature {
    
    private static String sLastTempValue = "---.- -";
    
    public static double  LOW_TEMP = -999.0;
    public static double  HIGH_TEMP = -999.0;
    
    public static String getTemp(String sTemp) {
        
        String sDefaultRtn = "---.- -";
        String sLastTempValue = "";
         
        if (sTemp == null) {
            return sDefaultRtn;
        }
        
        if (sTemp.matches("\\d+\\.\\d")) { 
            sLastTempValue = sTemp;
            updateTemp(sTemp);
            return sLastTempValue;  
        } else  {
            updateTemp(sTemp);
            return sLastTempValue;
        }
          
    }
    
    /**
     * 
     * @param sTemp
     * @return 
     */
    public static String FahrenheitToCelsius (String sTemp) {
        
        float fTemp = Float.parseFloat(sTemp);
        
        System.out.print("F-C: "  + fTemp);
                
        fTemp = ((fTemp - 32)*5)/9;
        
        System.out.print("F-C: "  + fTemp);
        
        String pattern = "##.#";
        DecimalFormat df = new DecimalFormat(pattern);
        
        return df.format(fTemp);
          
    }
    
    private static void updateTemp(String sTemp) {
        double dTemp = Double.parseDouble(sTemp);
        
        if (LOW_TEMP == -999.0) {
            LOW_TEMP = dTemp;
            HIGH_TEMP = dTemp;
        }
        
        if (dTemp < LOW_TEMP) {HIGH_TEMP = dTemp;}
        if (dTemp > HIGH_TEMP) {HIGH_TEMP = dTemp;}
        
    }
    
}

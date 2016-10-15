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
    
    public static String getTemp(String sTemp) {
        
        String sDefaultRtn = "---.- -";
         
        if (sTemp == null) {
            return sDefaultRtn;
        }
        
        if (sTemp.matches("\\d+\\.\\d")) { 
            sLastTempValue = sTemp;
            return sLastTempValue;  
        } else  {
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
}

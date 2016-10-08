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
public class Temperature {
    
    private static String sLastTempValue = "---.- -";
    
    public static String getTemp(String sTemp) {
        
        String sDefaultRtn = "---.- -";
         
        if (sTemp == null) {
            return sDefaultRtn;
        }
        
        if (sTemp.matches("\\d+\\.\\d")) { 
            sLastTempValue = sTemp;
            return sLastTempValue + "\u00b0";  
        } else  {
            return sLastTempValue + "\u00b0";
        }
          
    }
}

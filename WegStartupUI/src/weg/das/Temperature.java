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
    
    public static String getTemp(String sTemp) {
        
        if (sTemp.matches("\\d\\d\\d\\:\\d+\\.\\d\\s\\D")) {
          return sTemp.split(":")[1];  
        } else  {
            return "---.- -";
        }
        
        
    }
}

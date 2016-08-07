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
public class UV {
    
    public static Integer getUV(String sUV) {
        
        Integer sDefaultRtn = 0;

        if (sUV == null) {
            return sDefaultRtn;
        }

        if (sUV.matches("\\d\\d\\d\\:\\d+")) {
          return Integer.parseInt(sUV.split(":")[1]);  
        } else  {
            return sDefaultRtn;
        }
    }
}

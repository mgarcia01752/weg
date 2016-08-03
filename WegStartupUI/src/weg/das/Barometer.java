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
    
    public static String getBarometer(String sBarometer) {
        return sBarometer.split(":")[1];
    }
    
}

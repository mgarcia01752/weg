/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.das;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maurice Garcia
 */
public class DasConnection {
    
    public final int IPC_PORT = 5000;  
    private InetAddress iaDAS = null;
    private int iPort = 0;
    
    
    /**
     * 
     * @param iaDAS
     * @param iPort 
     */
    public DasConnection (InetAddress iaDAS , int iPort) {
        this.iaDAS = iaDAS;
        this.iPort = iPort;
    }
       
    /**
     * 
     */
    public DasConnection() {
        
        try {
            this.iaDAS = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.iPort = IPC_PORT;
    }
    
    /**
     * 
     * @return 
     */
    public InetAddress getDasInetAddress() {
        return iaDAS;
    }
    
    /**
     * 
     * @param iDasCommand
     * @return 
     */
    public String get(int iDasCommand) {
        return TxRxCommand(iDasCommand);
    }
    
    private String TxRxCommand(Integer iDasCommand){
        
        Socket socket = null;
        
        try {
            socket = new Socket(this.iaDAS, this.iPort);
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*********************************************************************** 
                                    Send DAS Message 
        ***********************************************************************/
        BufferedWriter bw = null;
        
        try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            bw.write(iDasCommand.toString());
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*********************************************************************** 
                                    Receive DAS Message 
        ***********************************************************************/
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(DasConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return reader.toString();
    }

}

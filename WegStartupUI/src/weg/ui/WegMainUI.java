/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.ui;

import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import weg.das.Gps;
import weg.das.Temperature;

/**
 *
 * @author Maurice
 */
public class WegMainUI extends javax.swing.JFrame {

    private Gps gps = null;
    
    /**
     * Creates new form WegMainUI
     */
    public WegMainUI() {
        initComponents();
               
    }

    public void updateUV(String sUvData) {
        
    }
    
    
    public void updateGPS(Gps gps) {
        
        this.gps = gps;
        
        this.jLabelLatiData.setText(gps.getLatitude());
        this.jLabelLongData.setText(gps.getLongitude());
        this.jLabelUTCData.setText(gps.getUTC());
        this.jLabelNumSatData.setText(gps.getNumSatellites().toString());
        
        updateSatelliteFixStatus(gps.getFixIndicator());
        
    }
    
    /**
     * 
     * @param sTemp 
     */
    public void updateTemperture(String sTemp) {
        this.jLabelTemerature.setText(sTemp);
    }
    
    /**
     * 
     * @param sBarometer 
     */
    public void updateBarometer(String sBarometer) {
        this.jLabelBarometer.setText(sBarometer);
    }
    
    /**
     * 
     * @param iFixStatus 
     */
    public void updateSatelliteFixStatus(int iFixStatus) {
        if (iFixStatus == 1) {
            this.jLabelGpsFixIndicator.setForeground(Color.green);
        } else if (iFixStatus == 1) {
           this.jLabelGpsFixIndicator.setForeground(Color.red); 
        } else {
           this.jLabelGpsFixIndicator.setForeground(Color.yellow);  
        }
    }
    
    /**
     * 
     * @param sPresTrend 
     */
    public void updatePressureTrend(String sPresTrend) {
        jLabelBaroChangeStatus.setText(sPresTrend);
    }
    
    /**
     * 
     * @return 
     */
    public boolean isRemoteDasSelected() {
        return jRB_RemoteDAS.isSelected();
    }
   
    /**
     * 
     * @return 
     */
    public boolean isLocalDasSelected() {
        return jRB_LocalDAS.isSelected();
    }    
    
    /**
     * 
     * @return
     * @throws UnknownHostException 
     */
    public InetAddress getRemoteDasInetAddress() throws UnknownHostException {
        return InetAddress.getByName(jTextFieldInetAddress.getText());
    }
    
    /**
     * 
     * @return
     * @throws UnknownHostException 
     */
    public InetAddress getLocalDasInetAddress() throws UnknownHostException {
        return InetAddress.getByName("127.0.0.1");
    }
    
    /**
     * 
     * @return 
     */
    public boolean isDasConnectSelected() {
        return jTB_ConnectToDAS.isSelected();
    }
    
       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanelStatus = new javax.swing.JPanel();
        jTB_ConnectToDAS = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jRB_RemoteDAS = new javax.swing.JRadioButton();
        jTextFieldInetAddress = new javax.swing.JTextField();
        jTextFieldDASPort = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRB_LocalDAS = new javax.swing.JRadioButton();
        jPanelGPS = new javax.swing.JPanel();
        jLabelLatitude = new javax.swing.JLabel();
        jLabelLongitude = new javax.swing.JLabel();
        jLabelGPSFix = new javax.swing.JLabel();
        jLabelGpsFixIndicator = new javax.swing.JLabel();
        jLabelNumSatData = new javax.swing.JLabel();
        jLabelLatiData = new javax.swing.JLabel();
        jLabelLongData = new javax.swing.JLabel();
        jLabelUTC = new javax.swing.JLabel();
        jLabelUTCData = new javax.swing.JLabel();
        jLabelLocalTime = new javax.swing.JLabel();
        jLabelLocalTimeData = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4TempBaro = new javax.swing.JPanel();
        jLabelTemerature = new javax.swing.JLabel();
        jPanelBarometer = new javax.swing.JPanel();
        jLabelBarometer = new javax.swing.JLabel();
        jLabelBaroChangeStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPB_UvIndex = new javax.swing.JProgressBar();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(800, 400));
        setResizable(false);

        jPanelStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings"));
        jPanelStatus.setPreferredSize(new java.awt.Dimension(400, 200));

        jTB_ConnectToDAS.setBackground(new java.awt.Color(255, 0, 0));
        jTB_ConnectToDAS.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTB_ConnectToDAS.setText("CONNECT");
        jTB_ConnectToDAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTB_ConnectToDASActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("DAS Access Control"));

        jRB_RemoteDAS.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRB_RemoteDAS.setSelected(true);
        jRB_RemoteDAS.setText("Remote DAS");
        jRB_RemoteDAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_RemoteDASActionPerformed(evt);
            }
        });

        jTextFieldInetAddress.setEditable(false);
        jTextFieldInetAddress.setText("10.1.10.16");
        jTextFieldInetAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldInetAddressActionPerformed(evt);
            }
        });

        jTextFieldDASPort.setEditable(false);
        jTextFieldDASPort.setText("5000");
        jTextFieldDASPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldDASPortActionPerformed(evt);
            }
        });

        jLabel2.setText("InetAdress");

        jLabel3.setText("Port");

        jRB_LocalDAS.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRB_LocalDAS.setText("Local DAS");
        jRB_LocalDAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_LocalDASActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRB_RemoteDAS)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldInetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldDASPort, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRB_LocalDAS)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRB_RemoteDAS)
                    .addComponent(jTextFieldInetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDASPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(jRB_LocalDAS)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelStatusLayout = new javax.swing.GroupLayout(jPanelStatus);
        jPanelStatus.setLayout(jPanelStatusLayout);
        jPanelStatusLayout.setHorizontalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStatusLayout.createSequentialGroup()
                .addGroup(jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelStatusLayout.createSequentialGroup()
                        .addGap(262, 262, 262)
                        .addComponent(jTB_ConnectToDAS))
                    .addGroup(jPanelStatusLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelStatusLayout.setVerticalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStatusLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTB_ConnectToDAS))
        );

        jPanelGPS.setBorder(javax.swing.BorderFactory.createTitledBorder("GPS"));

        jLabelLatitude.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLatitude.setText("Latitude:");

        jLabelLongitude.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLongitude.setText("Longitude: ");

        jLabelGPSFix.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelGPSFix.setText("GPS Fixed");

        jLabelGpsFixIndicator.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelGpsFixIndicator.setForeground(new java.awt.Color(255, 0, 0));
        jLabelGpsFixIndicator.setText("•");

        jLabelNumSatData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelNumSatData.setText("--");

        jLabelLatiData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLatiData.setText("N ----.----");

        jLabelLongData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLongData.setText("E -----.----");

        jLabelUTC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelUTC.setText("UTC:");

        jLabelUTCData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelUTCData.setText("------.---");

        jLabelLocalTime.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLocalTime.setText("Local Time: ");

        jLabelLocalTimeData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLocalTimeData.setText("Tue Oct 15 12:19:40 ");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/weg/ui/small_sat.jpg"))); // NOI18N

        javax.swing.GroupLayout jPanelGPSLayout = new javax.swing.GroupLayout(jPanelGPS);
        jPanelGPS.setLayout(jPanelGPSLayout);
        jPanelGPSLayout.setHorizontalGroup(
            jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGPSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGPSLayout.createSequentialGroup()
                                .addComponent(jLabelUTC)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelUTCData))
                            .addGroup(jPanelGPSLayout.createSequentialGroup()
                                .addComponent(jLabelLocalTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelLocalTimeData)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGPSLayout.createSequentialGroup()
                                .addComponent(jLabelLongitude)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelLongData))
                            .addGroup(jPanelGPSLayout.createSequentialGroup()
                                .addComponent(jLabelLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelLatiData, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelGPSLayout.createSequentialGroup()
                                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelGPSLayout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabelNumSatData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabelGPSFix))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelGpsFixIndicator)))
                        .addContainerGap(87, Short.MAX_VALUE))))
        );
        jPanelGPSLayout.setVerticalGroup(
            jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGPSLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelLatitude)
                    .addComponent(jLabelLatiData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelLongitude)
                    .addComponent(jLabelLongData))
                .addGap(18, 18, 18)
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelGPSFix)
                    .addComponent(jLabelGpsFixIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelNumSatData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUTC)
                    .addComponent(jLabelUTCData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelLocalTime)
                    .addComponent(jLabelLocalTimeData)))
        );

        jPanel4TempBaro.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Temperature"), "Temperature"));
        jPanel4TempBaro.setPreferredSize(new java.awt.Dimension(350, 100));

        jLabelTemerature.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabelTemerature.setText("--- F");

        javax.swing.GroupLayout jPanel4TempBaroLayout = new javax.swing.GroupLayout(jPanel4TempBaro);
        jPanel4TempBaro.setLayout(jPanel4TempBaroLayout);
        jPanel4TempBaroLayout.setHorizontalGroup(
            jPanel4TempBaroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4TempBaroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTemerature)
                .addContainerGap(73, Short.MAX_VALUE))
        );
        jPanel4TempBaroLayout.setVerticalGroup(
            jPanel4TempBaroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4TempBaroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelTemerature, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelBarometer.setBorder(javax.swing.BorderFactory.createTitledBorder("Barometer"));

        jLabelBarometer.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabelBarometer.setText("---- mB");

        jLabelBaroChangeStatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelBaroChangeStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelBaroChangeStatus.setText("STEADY");

        javax.swing.GroupLayout jPanelBarometerLayout = new javax.swing.GroupLayout(jPanelBarometer);
        jPanelBarometer.setLayout(jPanelBarometerLayout);
        jPanelBarometerLayout.setHorizontalGroup(
            jPanelBarometerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBarometerLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabelBaroChangeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelBarometerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelBarometer, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelBarometerLayout.setVerticalGroup(
            jPanelBarometerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBarometerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelBarometer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelBaroChangeStatus)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ultraviolet", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jPB_UvIndex.setMaximum(11);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPB_UvIndex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPB_UvIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelGPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4TempBaro, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                                .addComponent(jPanelBarometer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4TempBaro, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(jPanelBarometer, javax.swing.GroupLayout.PREFERRED_SIZE, 106, Short.MAX_VALUE)))
                    .addComponent(jPanelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 182, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelGPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRB_LocalDASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_LocalDASActionPerformed
        jTextFieldInetAddress.setEditable(false);
        jTextFieldDASPort.setEditable(false);
        jRB_RemoteDAS.setSelected(false);
    }//GEN-LAST:event_jRB_LocalDASActionPerformed

    private void jTextFieldInetAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldInetAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldInetAddressActionPerformed

    private void jTextFieldDASPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDASPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDASPortActionPerformed

    private void jRB_RemoteDASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_RemoteDASActionPerformed
 
        jTextFieldInetAddress.setEditable(true);
        jTextFieldDASPort.setEditable(true);
        jRB_LocalDAS.setSelected(false);
    }//GEN-LAST:event_jRB_RemoteDASActionPerformed

    private void jTB_ConnectToDASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTB_ConnectToDASActionPerformed
        // TODO add your handling code here:
        
        if (jTB_ConnectToDAS.isSelected()) {
            jTB_ConnectToDAS.setBackground(Color.green);
            
            jTextFieldInetAddress.setEditable(false);
            jTextFieldDASPort.setEditable(false);
        } else {
            jTB_ConnectToDAS.setBackground(Color.red);
            
            jTextFieldInetAddress.setEditable(false);
            jTextFieldDASPort.setEditable(false);
        }
        
    }//GEN-LAST:event_jTB_ConnectToDASActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WegMainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WegMainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WegMainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WegMainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WegMainUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelBaroChangeStatus;
    private javax.swing.JLabel jLabelBarometer;
    private javax.swing.JLabel jLabelGPSFix;
    private javax.swing.JLabel jLabelGpsFixIndicator;
    private javax.swing.JLabel jLabelLatiData;
    private javax.swing.JLabel jLabelLatitude;
    private javax.swing.JLabel jLabelLocalTime;
    private javax.swing.JLabel jLabelLocalTimeData;
    private javax.swing.JLabel jLabelLongData;
    private javax.swing.JLabel jLabelLongitude;
    private javax.swing.JLabel jLabelNumSatData;
    private javax.swing.JLabel jLabelTemerature;
    private javax.swing.JLabel jLabelUTC;
    private javax.swing.JLabel jLabelUTCData;
    private javax.swing.JProgressBar jPB_UvIndex;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4TempBaro;
    private javax.swing.JPanel jPanelBarometer;
    private javax.swing.JPanel jPanelGPS;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JRadioButton jRB_LocalDAS;
    private javax.swing.JRadioButton jRB_RemoteDAS;
    private javax.swing.JToggleButton jTB_ConnectToDAS;
    private javax.swing.JTextField jTextFieldDASPort;
    private javax.swing.JTextField jTextFieldInetAddress;
    // End of variables declaration//GEN-END:variables
}

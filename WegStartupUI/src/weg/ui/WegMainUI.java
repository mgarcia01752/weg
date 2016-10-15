/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weg.ui;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import weg.das.Gps;
import weg.das.Temperature;

/**
 *
 * @author Maurice
 */
public class WegMainUI extends javax.swing.JFrame {

    private Gps gps = null;
    private DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    
    private boolean FARN = Boolean.TRUE;
    private boolean CELS = Boolean.FALSE;
    private boolean boolTempScale = FARN;
    private String sTempInScope;
    private int iInitalBarometerSetting = -1;
    private int iUpdateBarometerSetting = -1;
    
    /**
     * Creates new form WegMainUI
     */
    public WegMainUI() {
        initComponents();          
    }

    public void updateUV(String sUvData) {
        
        int iUvIndex = Integer.parseInt(sUvData);
        
        jLUvIndex.setText(sUvData);
                  
        if (iUvIndex <= 2 ) {
          jLUvIndex.setForeground(new java.awt.Color(0, 91, 9));
          jLUvIdxStatus.setText("Low");
        } else if ((iUvIndex >= 3)&&(iUvIndex <= 5)) {
          jLUvIndex.setForeground(Color.YELLOW);
          jLUvIdxStatus.setText("Moderate");
        } else if ((iUvIndex >= 6)&&(iUvIndex <= 7)) {
          jLUvIndex.setForeground(Color.ORANGE);
          jLUvIdxStatus.setText("High");
        } else if ((iUvIndex >= 8)&&(iUvIndex <= 10)) {
          jLUvIndex.setForeground(Color.RED);
          jLUvIdxStatus.setText("Very High");
        } else {
          jLUvIndex.setForeground(Color.blue);
          jLUvIdxStatus.setText("Extream");
        }        
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
        
        this.sTempInScope = sTemp;
        
        if (boolTempScale) {
            this.jLabelTemerature.setText(sTemp + "\u00b0" + "F");
        } else {
            this.jLabelTemerature.setText(Temperature.FahrenheitToCelsius(sTemp) + "\u00b0" + "C");
        }
    }
    
    /**
     * 
     * @param sBarometer 
     */
    public void updateBarometer(String sBarometer) {
        
        if (this.iInitalBarometerSetting == -1) {
          this.iInitalBarometerSetting =  Integer.parseInt(sBarometer); 
        }      
        this.jLabelBarometer.setText(sBarometer+" mB");
        
        updateBarometerTrend(sBarometer);
    }
    
    /**
     * 
     * @param sBarometer 
     */
    public void updateBarometerTrend(String sBarometer) {
       
        int iBaroSet = Integer.parseInt(sBarometer);
        
        if (iInitalBarometerSetting > iBaroSet) {
            jLabelBaroChangeStatus.setText("FALLING");
        } else if (iInitalBarometerSetting < iBaroSet) {
            jLabelBaroChangeStatus.setText("RISING");
        } else {
            jLabelBaroChangeStatus.setText("STEADY");
        }
        
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
     * 
     * @return 
     */
    public void updateSystemTime() {
        jLSystemTime.setText(dateFormat.format(Calendar.getInstance().getTime()));
    }
    
    public void updateCompass() {
        jLCompassDirection.setText(gps.compassUpdate(gps.getLatitude(), gps.getLongitude()));
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
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLCompassDirection = new javax.swing.JLabel();
        jPanel4TempBaro = new javax.swing.JPanel();
        jLabelTemerature = new javax.swing.JLabel();
        jPanelBarometer = new javax.swing.JPanel();
        jLabelBarometer = new javax.swing.JLabel();
        jLabelBaroChangeStatus = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLUvIndex = new javax.swing.JLabel();
        jLUvIdxStatus = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabelUTC = new javax.swing.JLabel();
        jLabelUTCData = new javax.swing.JLabel();
        jLSystemTime = new javax.swing.JLabel();
        jpCamera = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

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
        setPreferredSize(new java.awt.Dimension(800, 450));

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
        jTextFieldInetAddress.setText("10.1.10.27");
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
                .addComponent(jRB_RemoteDAS)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldDASPort, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldInetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jRB_LocalDAS)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(128, 128, 128)
                .addComponent(jTB_ConnectToDAS)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelStatusLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 43, Short.MAX_VALUE))
        );
        jPanelStatusLayout.setVerticalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStatusLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTB_ConnectToDAS)
                .addGap(12, 12, 12))
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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/weg/ui/small_sat.jpg"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Satellites Locked");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Direction");

        jLCompassDirection.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLCompassDirection.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLCompassDirection.setText("--");

        javax.swing.GroupLayout jPanelGPSLayout = new javax.swing.GroupLayout(jPanelGPS);
        jPanelGPS.setLayout(jPanelGPSLayout);
        jPanelGPSLayout.setHorizontalGroup(
            jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGPSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addComponent(jLabelLongitude)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelLongData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addComponent(jLabelLatitude, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelLatiData, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelGPSLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelNumSatData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabelGPSFix))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelGpsFixIndicator)
                            .addComponent(jLabel4))))
                .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))
                    .addGroup(jPanelGPSLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLCompassDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                    .addGroup(jPanelGPSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelNumSatData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)))
                .addGap(58, 58, 58))
            .addGroup(jPanelGPSLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLCompassDirection, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4TempBaro.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Temperature"), "Temperature"));
        jPanel4TempBaro.setPreferredSize(new java.awt.Dimension(350, 100));
        jPanel4TempBaro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel4TempBaroMouseClicked(evt);
            }
        });

        jLabelTemerature.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabelTemerature.setText("00.0 F");

        javax.swing.GroupLayout jPanel4TempBaroLayout = new javax.swing.GroupLayout(jPanel4TempBaro);
        jPanel4TempBaro.setLayout(jPanel4TempBaroLayout);
        jPanel4TempBaroLayout.setHorizontalGroup(
            jPanel4TempBaroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTemerature, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        jLabelBarometer.setText("0000 mB");

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
                .addComponent(jLabelBarometer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ultraviolet Index", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLUvIndex.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLUvIndex.setForeground(new java.awt.Color(0, 91, 9));
        jLUvIndex.setText("0");

        jLUvIdxStatus.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLUvIdxStatus.setText("Low");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLUvIndex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jLUvIdxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLUvIndex)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLUvIdxStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Time"));

        jLabelUTC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelUTC.setText("UTC:");

        jLabelUTCData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelUTCData.setText("00:00:00");

        jLSystemTime.setToolTipText("");
        jLSystemTime.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLSystemTimePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLSystemTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabelUTC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelUTCData)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelUTC)
                    .addComponent(jLabelUTCData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLSystemTime)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jpCamera.setBorder(javax.swing.BorderFactory.createTitledBorder("Camera Preview"));

        javax.swing.GroupLayout jpCameraLayout = new javax.swing.GroupLayout(jpCamera);
        jpCamera.setLayout(jpCameraLayout);
        jpCameraLayout.setHorizontalGroup(
            jpCameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
        );
        jpCameraLayout.setVerticalGroup(
            jpCameraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setText("Reset");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4TempBaro, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanelBarometer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jpCamera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4TempBaro, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(jPanelBarometer, javax.swing.GroupLayout.PREFERRED_SIZE, 106, Short.MAX_VALUE)))
                    .addComponent(jPanelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpCamera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelGPS, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(0, 5, Short.MAX_VALUE)))
                .addContainerGap())
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

    private void jLSystemTimePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLSystemTimePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jLSystemTimePropertyChange

    private void jPanel4TempBaroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4TempBaroMouseClicked
        
        if (boolTempScale) {
            boolTempScale = false;
        } else {
           boolTempScale = true; 
        }
        
        this.updateTemperture(sTempInScope);
    }//GEN-LAST:event_jPanel4TempBaroMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Process p = Runtime.getRuntime().exec("sudo reboot");
        } catch (IOException ex) {
            Logger.getLogger(WegMainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLCompassDirection;
    private javax.swing.JLabel jLSystemTime;
    private javax.swing.JLabel jLUvIdxStatus;
    private javax.swing.JLabel jLUvIndex;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelBaroChangeStatus;
    private javax.swing.JLabel jLabelBarometer;
    private javax.swing.JLabel jLabelGPSFix;
    private javax.swing.JLabel jLabelGpsFixIndicator;
    private javax.swing.JLabel jLabelLatiData;
    private javax.swing.JLabel jLabelLatitude;
    private javax.swing.JLabel jLabelLongData;
    private javax.swing.JLabel jLabelLongitude;
    private javax.swing.JLabel jLabelNumSatData;
    private javax.swing.JLabel jLabelTemerature;
    private javax.swing.JLabel jLabelUTC;
    private javax.swing.JLabel jLabelUTCData;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel4TempBaro;
    private javax.swing.JPanel jPanelBarometer;
    private javax.swing.JPanel jPanelGPS;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JRadioButton jRB_LocalDAS;
    private javax.swing.JRadioButton jRB_RemoteDAS;
    private javax.swing.JToggleButton jTB_ConnectToDAS;
    private javax.swing.JTextField jTextFieldDASPort;
    private javax.swing.JTextField jTextFieldInetAddress;
    private javax.swing.JPanel jpCamera;
    // End of variables declaration//GEN-END:variables
}

# WEG - Wearable Excursion Gear

## Getting Started

DO NOT USE 5" Display until you get to Java Oracle Hotspot Step

### Remote Setup Instructions

Configure the Raspberry PI to connect to your local WiFI or Ethernet Connection

[Download Putty for Remote IP Connection](http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html)

Obtain IPv4 address of the RPI

Start Putty and login into RPI using SSH

**User:** pi

**Password:** raspberry

### Obtain Raspbian OS

[Download Raspbian - Jessie - Build Data: 2016-03-18](http://downloads.raspberrypi.org/raspbian/images/raspbian-2016-03-18/)

##  Installing Raspbian 

[OS Installation](https://www.raspberrypi.org/documentation/installation/installing-images/README.md)

##  Download and Install WEG

	git clone https://github.com/mgarcia01752/weg.git
	
	cd weg

**Backup and Configure for WEG**

	sudo su  
	
	mv /boot/config.txt /boot/config.txt~
	
	mv /boot/cmdline.txt /boot/cmdline.txt~
	
	cp /home/pi/weg/RaspberryPiConfig/* /boot
	
	init 6

## Disable Serial Port

Raspberry PI Configuration -> Interface -> Disable Serial

## Java Oracle Hotspot

	sudo apt-get --purge remove openjdk-7-jre 
	
	sudo apt-get --purge remove openjdk-7-jdk
	
	sudo apt-get install oracle-java7-jdk

**Check Installation**

	java -version

**Response**

java version "1.8.0_65"

Java(TM) SE Runtime Environment (build 1.8.0_65-b17)

Java HotSpot(TM) Client VM (build 25.65-b01, mixed mode)

## Set RPI for Expanded File System

Raspberry PI Configuration -> Expanded File System

##  Download and Install Tangram-es

	cd /home/pi/weg
	
	sudo apt-get install cmake
	
	sudo apt-get update
	
	sudo apt-get install cmake g++-4.7 libcurl4-openssl-dev
	
	git clone https://github.com/tangrams/tangram-es.git
	
	export CXX=/usr/bin/g++-4.9
	
	cd tangram-es
	
	git submodule init && git submodule update
	
	git submodule update --init --recursive
	
**At this point, the build will take ~30 minutes. You can Skip to the next step and come back later**
	
	make rpi


##  Download and Install WiringPi

	cd /home/pi/weg
	
	git clone git://git.drogon.net/wiringPi
	
	cd wiringPi
	
	git pull origin
	
	./build

##  Install UART Serial IPC

	cd /home/pi/weg/uart-serial-ipc
	
	make DamCommSocket

## Test Communication Between PI and DAS

	sudo ./DamCommSocket -h

**Response**

	Data Acquisition Module IPC Ver: 1.0-pre
	Options are:
	        -b: Set BaudRate <9600|115200>
	        -i: Serial Input <command>
	        -l: Loop Input option <Number of Loops for option i>
	        -r: Send Reset to PIC via GPIO 23
	        -v: Version
	        -d: Enable Debug
	        -G: GPS Data
	        -U: UltraViolet Data
	        -T: Temperature Data
	        -B: Barometer Data
	        -S: Solar Power Voltage Data
	        -h: Usage an Exit

**Example: Check GPS**

	sudo ./DamCommSocket -G

**Response**
	
	Get GPS Data
	Input -> 101 Output -> 150:$GPRMC,191927.000,A,4009.3964,N,07452.0041,W,0.17,298.73,300716,,,A*7F

**Example from PI terminal (optional)**
	
	sudo apt-get install telnet
	
	telnet 127.0.0.1 5000
	101

**Response**

	150:$GPRMC,190826.000,A,4009.4024,N,07452.0107,W,0.06,357.50,300716,,,A*74
	
## Update WEG from GitHub

**Update from Master**

	cd /home/pi/weg
	
	git pull
	
	git merge origin

**Update from Branch, Example v0.2**

	cd /home/pi/weg
	
	git pull
	
	git merge origin/v0.2
	
## Starting WEG Manually

**Start DAS IPC**	
	
	cd /home/pi/weg/uart-serial-ipc/
	
	sudo ./DamCommSocket -d
	
**Start WEG GUI**	

	cd /home/pi/weg/WegStartupUI/dist
	
	java -jar WegStartupUI.jar


## Kiosk Screen Instructions

This is needed so not to use the RPI Desktop enviroment.  These steps puts it in Kiosk Mode.

[Online Instructions](https://www.danpurdy.co.uk/web-development/raspberry-pi-kiosk-screen-tutorial/)

These commands will update the package list and upgrade any packages on your device.
Next we install the chromium browser, x11 server utilities and unclutter(removes the cursor from the screen).
To do this type in:

	sudo apt-get install chromium x11-xserver-utils unclutter



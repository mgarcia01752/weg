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

* Raspberry PI Configuration -> Expanded File System


##  Download and Install WiringPi

	cd /home/pi/weg
	git clone git://git.drogon.net/wiringPi
	cd wiringPi
	git pull origin
	
	./build
	

##  Install UART Serial IPC

	cd /home/pi/weg/uart-serial-ipc
	make DamCommSocket
	
	
## Install rc.local

	sudo mv /etc/rc.local /etc/rc.local~
	sudo cp /home/pi/weg/startup/rc.local.txt /etc/rc.local
	sudo chmod 777 /etc/rc.local


## Install WEG.sh for Desktop
	
	sudo cp /home/pi/weg/startup/WEG.sh /home/pi/Desktop
	sudo chmod 777 /home/pi/Desktop/WEG.sh
		
			
## Update WEG from GitHub

**Update from Master**

	cd /home/pi/weg
	
	git pull
	
	git merge origin

	
## Starting WEG Manually

**Start DAS IPC**	

	sudo /home/pi/weg/uart-serial-ipc/DamCommSocket -d
	
**Start WEG GUI**	
	
	sudo java -jar /home/pi/weg/WegStartupUI/dist/WegStartupUI.jar


## PIC Reset Via GPIO via wiringPi

	sudo gpio mode 4 out
	
	sudo gpio write 4 0
	
Wait 3 seconds
	
	sudo gpio write 4 1




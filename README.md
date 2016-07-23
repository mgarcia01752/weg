# WEG - Wearable Excursion Gear

## Getting Started

Before we start, you will need to do the following steps in the order laid out to ensure a clean installation of WEG

[Download Raspbian - Jessie - Build Data: 2016-03-18](http://downloads.raspberrypi.org/raspbian/images/raspbian-2016-03-18/)

##  Raspbian Version

[OS Installation](https://www.raspberrypi.org/documentation/installation/installing-images/README.md)

**Just in case to get the latest Updates**

`sudo apt-get update`

`sudo apt-get upgrade`

##  Starting Point

**Force Baud Rate to 115200**

`sudo su ` 

`echo force_turbo=1 >> /boot/config.txt`

`exit`

**Create WEG Directory**

`cd /home/pi`

`mkdir weg`

`cd weg`

##  Download WEG

`git clone https://github.com/mgarcia01752/weg.git`

## Java Oracle Hotspot

`sudo apt-get --purge remove openjdk-7-jre` 

`sudo apt-get --purge remove openjdk-7-jdk`

`sudo apt-get install oracle-java7-jdk`

**Check Installation**

`java -version`

**Response**

java version "1.7.0_60"

Java(TM) SE Runtime Environment (build 1.7.0_60-b19)

Java HotSpot(TM) Client VM (build 24.60-b09, mixed mode)

##  Tangram-es

`cd /home/pi/weg`

`sudo apt-get install cmake`

`sudo apt-get update`

`sudo apt-get install cmake g++-4.7 libcurl4-openssl-dev`

`cd ~`

`git clone https://github.com/tangrams/tangram-es.git`

`export CXX=/usr/bin/g++-4.9`

`cd tangram-es`

`git submodule init && git submodule update`

`git submodule update --init --recursive`

`make rpi`


##  WiringPi

`cd /home/pi/weg`

`git clone git://git.drogon.net/wiringPi`

`cd wiringPi`

`git pull origin`

`./build`

##  UART Serial IPC

`cd /home/pi/weg/weg/uart-serial-ipc`

`make DamCommSocket`

`cd /home/pi/weg/weg`



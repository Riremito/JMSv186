# JMS Private Server
## Readme
+ [日本語](./Readme.md)
+ [English](./Readme_EN.md)

## Development Environment
+ OS
    + Windows
+ Java
    + [JDK 25 : jdk-25_windows-x64_bin.exe](https://www.oracle.com/jp/java/technologies/downloads/#jdk25-windows)
+ IDE
    + [NetBeans 29 : Apache-NetBeans-29.exe](https://netbeans.apache.org/front/main/download/nb29/)
+ Database
    + [Wampserver : wampserver3.4.0_x64.exe](https://wampserver.aviatechno.net/)
        + MySQL 8.4.7
        + Apache 2.4.65
        + PHP 8.3.28
        + phpMyAdmin 5.2.3

## Execution Environment
+ OS
    + Windows or Linux
+ Java
    + JDK 25
+ Database
    + MySQL 8

## Setup
## Server source
+ clone this repo.
    + develop branch is recommended to use.
+ do following steps, `JMSv186` is name of source code folder.
### wz_xml
+ download and uncompress xml from this repo version you want to use. https://github.com/Riremito/wz_xml
+ in case of JMS147, you should put folder likes this. `JMSv186/wz_xml/xml_JMS_v147/`
### scripts
+ download script from this repo. https://github.com/Riremito/jms_scripts
+ you should put folder likes this. `JMSv186/scripts/scripts_jms/`
### Database
+ automatically generated.

## Build.
+ open porject by NetBeans.
+ open property and add all .jar files in `JMSv186/lib` folder to library ClassPath.
+ build this project.

## how to run server.
### Windows
+ choose `.bat` you want to run.
    + you can also run server by passing arguments to `run_any.bat` like this. `run_any.bat JMS 147 0`
### Linux
+ you can run server by passing arguments to `run_any.sh` like this. `bash run_any.sh JMS 147 0`

## server info
### account
+ auto register.
+ how to change account gender.
    + some version of client cannot create other gender of character.
    + `MapleID` and `MapleID_` are same account name in this server.
    + `MapleID` is for male,  `MapleID_` is for female.
### commands
+ TODO

## client
+ you can easily find full clients in ragezone or web archive.
### others
+ [crash bug fix tool](https://github.com/Riremito/iGPUplz)
    + under JMS187 has to use this to bypass crash bugs.
    + if you want to make client launching speed faster, this tool is also recommended to use.
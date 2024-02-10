@echo off
set MS_VERSION=%1
set MS_SUBVERSION=%2
set MS_REGIONCODE=%3
set WZ_XML_PATH=jms_wz\v%MS_VERSION%\
set SCRIPT_PATH=jms_scripts\
@title かえでサーバー v%MS_VERSION%.%MS_SUBVERSION%
set CLASSPATH=.;dist\*
java -server -Driresaba.path.wz=%WZ_XML_PATH% -Driresaba.path.script=%SCRIPT_PATH% -Djavax.net.ssl.keyStore=filename.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=filename.keystore -Djavax.net.ssl.trustStorePassword=passwd server.Start %MS_VERSION% %MS_SUBVERSION% %MS_REGIONCODE%
pause

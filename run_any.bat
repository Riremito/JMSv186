@echo off
set MS_REGION_NAME=%1
set MS_VERSION=%2
set MS_SUBVERSION=%3
set WZ_XML_PATH=wz_xml\xml_%MS_REGION_NAME%_v%MS_VERSION%\
set SCRIPT_PATH=jms_scripts\
@title Kaede Server %MS_REGION_NAME%_v%MS_VERSION%.%MS_SUBVERSION%
set CLASSPATH=.;dist\*
echo Version = %MS_REGION_NAME% %MS_VERSION%.%MS_SUBVERSION%
echo XML Path = %WZ_XML_PATH%
echo script Path = %SCRIPT_PATH%
echo DatabaseName Path = %MS_REGION_NAME%_v%MS_VERSION%
java -server -Driresaba.path.wz=%WZ_XML_PATH% -Driresaba.path.script=%SCRIPT_PATH% -Djavax.net.ssl.keyStore=filename.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=filename.keystore -Djavax.net.ssl.trustStorePassword=passwd server.Start %MS_REGION_NAME% %MS_VERSION% %MS_SUBVERSION%
pause

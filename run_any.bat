@echo off
if "%1" == "" (
	echo "usage : run_any.bat JMS 147 0 (region version minor_version)"
	pause
	exit /b
)
set MS_REGION_NAME=%1
set MS_VERSION=%2
set MS_SUBVERSION=%3
@title Kaede Server %MS_REGION_NAME%_v%MS_VERSION%.%MS_SUBVERSION%
set CLASSPATH=.;dist\*
java -server tacos.Start %MS_REGION_NAME% %MS_VERSION% %MS_SUBVERSION%
pause

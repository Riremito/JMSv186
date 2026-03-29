@echo off
@title Kaede Server
set CLASSPATH=.;dist\*
java -server tacos.Start vs
pause

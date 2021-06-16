@echo off
@title WZStringDumper
set CLASSPATH=.;scripts\java\MapleSEA.jar;scripts\java\libs\*
java -Dnet.sf.odinms.wzpath=extensible\ tools.WZStringDumper strings
pause
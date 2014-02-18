@echo off
setlocal
cd /D %0\..
call mvn eclipse:clean
call mvn eclipse:eclipse
call mvn dependency:sources -DdownloadSources=true
pause

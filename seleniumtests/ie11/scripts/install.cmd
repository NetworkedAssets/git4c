@echo off

echo Downloading Selenium server

if not exist C:\Selenium mkdir C:\Selenium
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://selenium-release.storage.googleapis.com/3.6/selenium-server-standalone-3.6.0.jar', 'C:\Selenium\server.jar')"

echo Selenium server downloaded

echo Downloading IE Driver

powershell -Command "(New-Object Net.WebClient).DownloadFile('https://selenium-release.storage.googleapis.com/3.6/IEDriverServer_Win32_3.6.0.zip', 'C:\Selenium\IEDriverServer.zip')"

echo IE Driver downloaded

FIND /C /I "localconfluence" %WINDIR%\system32\drivers\etc\hosts
IF %ERRORLEVEL% NEQ 0 ECHO %NEWLINE%^10.0.2.2 localconfluence>>%WINDIR%\System32\drivers\etc\hosts

pause

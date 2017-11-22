@echo off

START "" "C:\Program Files\TightVNC\tvnviewer.exe" 127.0.0.1::3000 -password=secret
START "" "C:\Program Files\TightVNC\tvnviewer.exe" 127.0.0.1::3001 -password=secret

atlas-mvn surefire:test -Dtest=SeleniumSuite
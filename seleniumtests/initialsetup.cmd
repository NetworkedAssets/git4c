@echo off

START "" "C:\Program Files\TightVNC\tvnviewer.exe" 127.0.0.1::3001 -password=secret

#TODO: Check post
ECHO "Waiting for Confluence"

timeout 5

atlas-mvn surefire:test -Dtest=SetupSuite

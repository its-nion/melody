@echo off

:: checking for file argument
SET file=%1

:: finding jar file
IF NOT [%1] == [] goto run
setlocal enabledelayedexpansion
for %%f in (builds\*.jar) do (
    SET fName=%%f
)
SET fDir=%~dp0
SET file="%fDir%%fName%"
echo "Assumed jar file at: %file%"

:: starting melody
:run
echo "Executing java -jar %file%"
java -jar "%file%" --no-gui


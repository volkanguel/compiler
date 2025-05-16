@echo off
if not exist bin mkdir bin
echo Compiling...
javac -cp "lib/*" -d bin src\*.java
echo Done.
pause

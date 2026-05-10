@echo off
echo Compiling Warehouse Management System...
mkdir out 2>nul
javac -d out src\model\*.java src\exception\*.java src\service\*.java src\ui\*.java
if %errorlevel% == 0 (
    echo Compilation successful! Launching application...
    java -cp out ui.App
) else (
    echo Compilation failed. Please check JDK installation (JDK 11+).
    pause
)

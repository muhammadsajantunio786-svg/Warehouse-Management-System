#!/bin/bash
echo "Compiling Warehouse Management System..."
mkdir -p out
javac -d out src/model/*.java src/exception/*.java src/service/*.java src/ui/*.java
if [ $? -eq 0 ]; then
    echo "Compilation successful! Launching application..."
    java -cp out ui.App
else
    echo "Compilation failed. Please ensure JDK 11+ is installed."
fi

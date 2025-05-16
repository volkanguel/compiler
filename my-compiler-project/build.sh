#!/bin/bash
mkdir -p bin
echo "Compiling..."
javac -cp "lib/*" -d bin src/*.java
echo "Done."

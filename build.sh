#! /bin/bash
set -e
VERSION="2.1.2" # Update version here

echo "Compiling..."
javac Main.java

echo "Creating JAR..."
jar cfe BinPacker-${VERSION}.jar Main Main.class sources/*.class routing/*.class parser/jsonFiles/*.class parser/txtFiles/*.class formatter/*.class calculator/*.class

echo "Build complete: BinPacker-${VERSION}.jar"
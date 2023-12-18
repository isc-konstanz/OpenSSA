#!/bin/sh
call ./gradlew framework

echo "Starting OpenMUC framework at http://localhost:8888/"

java -jar bin\felix.jar

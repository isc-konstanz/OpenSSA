::BATCH file for windows
@echo off
call gradlew framework

echo Starting OpenMUC framework at http://localhost:8888/
@echo on

java -jar bin\felix.jar

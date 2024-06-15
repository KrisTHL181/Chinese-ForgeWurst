@echo off
cd "ForgeWurst MC 1.12.2"
rd /s /q .\build\
gradlew clean build
start explorer .\build\libs\

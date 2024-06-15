@echo off
cd "ForgeWurst MC 1.12.2"
echo === CURRENT DIRECTORY: %cd% ===
rd /s /q .\build\
echo === BUILDING... ===
gradlew clean build
echo === BUILD SUCCESS! ===
start explorer .\build\libs\

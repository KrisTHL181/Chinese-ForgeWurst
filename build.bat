@echo off
cd "ForgeWurst MC 1.12.2"
echo === CURRENT DIRECTORY: %cd% ===
echo === REMOVE BUILD DIRECTORY: %cd%\build\ ===
del /f /s /q .\build\ > nul
echo === BUILDING... ===
echo === TARGET DIRECTORY: %cd%\build\libs\===
gradlew build
echo === BUILD SUCCESS! ===

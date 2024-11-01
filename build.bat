@echo off
cd "ForgeWurst MC 1.12.2" 1>nul 2>nul
if %ERRORLEVEL% equ 0 (echo === [+] CHANGE WORKING DIRECTORY [+ ]===)
echo === CURRENT DIRECTORY: %cd% ===
echo === REMOVE BUILD DIRECTORY: %cd%\build\ ===
del /f /s /q .\build\ 1>nul 2>nul nul
if %ERRORLEVEL% equ 0 (echo === [+] CLEANED LAST BUILD [+ ]===)
echo === BUILDING... ===
echo === TARGET DIRECTORY: %cd%\build\libs\===
gradlew build %*
if %ERRORLEVEL% equ 0 (
    ECHO === BUILD SUCCESS! ===
    copy .\build\libs\*.jar ..
    cd ..
) else (
    echo === [-] BUILD FAILED :( ===
    cd ..
)
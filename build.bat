@echo off
@chcp 65001 > nul
@set %GRADLE_EXIT_CONSOLE% = true
cd "ForgeWurst MC 1.12.2" 1>nul 2>nul
if %ERRORLEVEL% equ 0 (echo === [+] CHANGE WORKING DIRECTORY [+]===)
echo === CURRENT DIRECTORY: %cd% ===
del /f /s /q .\build\ 1>nul 2>nul nul
if %ERRORLEVEL% equ 0 (echo === [+] CLEANED LAST BUILD: %cd%\build\ [+] ===)
echo === BUILDING... ===
echo === TARGET DIRECTORY: %cd%\build\libs\===
set buildArg=-Dorg.gradle.jvmargs="--add-opens=java.base/java.lang=ALL-UNNAMED" --offline %*
echo [INFO] Building params: %buildArg%
gradlew build %buildArg%
if %ERRORLEVEL% equ 0 (
    echo === BUILD SUCCESS! ===
    copy .\build\libs\*.jar ..
    cd ..
    explorer .
) else (
    echo === [-] BUILD FAILED :( ===
    cd ..
)
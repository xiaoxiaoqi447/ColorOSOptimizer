@rem Gradle startup script for Windows
@if "%DEBUG%" == "" @echo off
setlocal

set DIRNAME=%~dp0
set APP_HOME=%DIRNAME%
set GRADLE_HOME=%APP_HOME%

@rem Find java.exe
if defined JAVA_HOME (
    set JAVACMD=%JAVA_HOME%\bin\java.exe
) else (
    set JAVACMD=java.exe
)

@rem Execute Gradle
"%JAVACMD%" -version >nul 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo Gradle not found. Please install:
echo   1. JDK 17+
echo   2. Android SDK
echo   3. Gradle 8.4+
echo.
echo Download from: https://services.gradle.org/distributions/gradle-8.4-bin.zip
goto end

:init
call gradle %*

:end
endlocal

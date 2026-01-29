@echo off
set "VIDEO_DIR=E:\Videoupload"
set "SCRIPT_DIR=%~dp0"

if not exist "%VIDEO_DIR%" (
  echo Folder "%VIDEO_DIR%" does not exist. Create it or adjust VIDEO_DIR above.
  pause
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -NoExit -File "%SCRIPT_DIR%upload_videos.ps1" -Folder "%VIDEO_DIR%"

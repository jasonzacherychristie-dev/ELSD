# Deploy ELSD debug APK to a connected Android phone.
# Usage:
#   .\scripts\deploy-debug.ps1
#   .\scripts\deploy-debug.ps1 -Serial ABC123 -Launch
#   .\scripts\deploy-debug.ps1 -ListOnly

param(
    [string]$Serial = "",
    [switch]$Launch,
    [switch]$ListOnly,
    [switch]$UninstallFirst
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path $PSScriptRoot -Parent
if (-not (Test-Path (Join-Path $RepoRoot "gradlew.bat"))) {
    # Repo layout: ELSD/ELSD when scripts live under ELSD/ELSD/scripts
    if (Test-Path (Join-Path $PSScriptRoot "..\gradlew.bat")) {
        $RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
    }
}

$Jbr = "C:\Users\jason\AppData\Local\Programs\Android Studio\jbr"
$Sdk = "D:\Android\Sdk"
if (Test-Path $Jbr) { $env:JAVA_HOME = $Jbr }
if (Test-Path $Sdk) {
    $env:ANDROID_HOME = $Sdk
    $env:ANDROID_SDK_ROOT = $Sdk
    $env:Path = "$Sdk\platform-tools;" + $env:Path
}

$adb = Get-Command adb -ErrorAction SilentlyContinue
if (-not $adb) {
    Write-Error "adb not on PATH. Install platform-tools to D:\Android\Sdk and re-run."
}

Write-Host "=== adb devices ===" -ForegroundColor Cyan
& adb devices -l
if ($ListOnly) { exit 0 }

$deviceArgs = @()
if ($Serial) {
    $deviceArgs = @("-s", $Serial)
    Write-Host "Target serial: $Serial" -ForegroundColor Yellow
} else {
    $lines = & adb devices | Where-Object { $_ -match "`tdevice$" }
    if (-not $lines) {
        Write-Error "No authorized device. Enable USB debugging and accept the RSA prompt."
    }
    if (@($lines).Count -gt 1 -and -not $Serial) {
        Write-Error "Multiple devices connected. Pass -Serial <id> (see adb devices)."
    }
}

if ($UninstallFirst) {
    Write-Host "=== uninstall com.elsd ===" -ForegroundColor Cyan
    & adb @deviceArgs uninstall com.elsd 2>$null
}

Write-Host "=== assembleDebug ===" -ForegroundColor Cyan
Push-Location $RepoRoot
try {
    & .\gradlew.bat :app:assembleDebug --quiet
    if ($LASTEXITCODE -ne 0) { throw "Gradle assembleDebug failed ($LASTEXITCODE)" }
} finally {
    Pop-Location
}

$apk = Join-Path $RepoRoot "app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apk)) {
    Write-Error "APK missing: $apk"
}

Write-Host "=== adb install -r ===" -ForegroundColor Cyan
& adb @deviceArgs install -r $apk
if ($LASTEXITCODE -ne 0) { throw "adb install failed ($LASTEXITCODE)" }

if ($Launch) {
    Write-Host "=== launch SwitchboardActivity ===" -ForegroundColor Cyan
    & adb @deviceArgs shell am start -n com.elsd/.SwitchboardActivity
}

Write-Host "Done." -ForegroundColor Green

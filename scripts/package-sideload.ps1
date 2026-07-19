# Build a friend-friendly sideload APK and copy it to dist/
# Usage: .\scripts\package-sideload.ps1
# Optional: .\scripts\package-sideload.ps1 -PublishRelease  (needs gh auth)

param(
    [switch]$PublishRelease
)

$ErrorActionPreference = "Stop"
$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
Set-Location $RepoRoot

$Jbr = "C:\Users\jason\AppData\Local\Programs\Android Studio\jbr"
$Sdk = "D:\Android\Sdk"
if (Test-Path $Jbr) { $env:JAVA_HOME = $Jbr }
if (Test-Path $Sdk) {
    $env:ANDROID_HOME = $Sdk
    $env:ANDROID_SDK_ROOT = $Sdk
}

Write-Host "=== assembleDebug ===" -ForegroundColor Cyan
& .\gradlew.bat :app:assembleDebug --quiet
if ($LASTEXITCODE -ne 0) { throw "Gradle failed" }

$src = Join-Path $RepoRoot "app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $src)) { throw "APK missing: $src" }

# Version from build.gradle.kts defaultConfig if possible
$ver = "0.1.0-m1"
$gradle = Get-Content (Join-Path $RepoRoot "app\build.gradle.kts") -Raw
if ($gradle -match 'versionName\s*=\s*"([^"]+)"') { $ver = $Matches[1] }

$dist = Join-Path $RepoRoot "dist"
New-Item -ItemType Directory -Force -Path $dist | Out-Null
$name = "ELSD-$ver-sideload.apk"
$dest = Join-Path $dist $name
Copy-Item $src $dest -Force

# Also a stable name for links
Copy-Item $src (Join-Path $dist "ELSD-latest-sideload.apk") -Force

$bytes = (Get-Item $dest).Length
$mb = [math]::Round($bytes / 1MB, 2)
Write-Host "Packaged: $dest ($mb MB)" -ForegroundColor Green
Write-Host "Also:     dist\ELSD-latest-sideload.apk"
Write-Host "Install guide: docs\SHARE_INSTALL.md"

if ($PublishRelease) {
    $tag = "v$ver"
    Write-Host "=== GitHub release $tag ===" -ForegroundColor Cyan
    $notes = @"
## ELSD $ver — sideload build

Friend / family install: see **[SHARE_INSTALL.md](https://github.com/jasonzacherychristie-dev/ELSD/blob/main/docs/SHARE_INSTALL.md)**

- Android 8+ (API 26+)
- Grant **Camera** (required), **Mic** optional for voice
- Switchboard → GO LIVE
- Size: ~$mb MB (under 1.0 budget)

**Praxis Software** · MIT · Jason Z. Christie + Grok (xAI)
"@
    # Create or update release
    $existing = gh release view $tag 2>$null
    if ($LASTEXITCODE -eq 0) {
        gh release upload $tag $dest --clobber
    } else {
        gh release create $tag $dest --title "ELSD $ver sideload" --notes $notes
    }
    Write-Host "Release published." -ForegroundColor Green
    gh release view $tag --json url -q .url
}

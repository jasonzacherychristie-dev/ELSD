# Deploy ELSD to a phone (sideload / adb)

How to **squirt** a debug build from this Windows machine onto a physical Android for early testing.

**Dev phones in the lab:** Pixel 7 · OnePlus 9 (floor) · OnePlus 12 (ship).  
**SDK on this machine:** `D:\Android\Sdk` · **JDK:** Android Studio JBR.

---

## 0. One-time PC setup

```powershell
# Session env (or set permanently in System → Environment Variables)
$env:JAVA_HOME = "C:\Users\jason\AppData\Local\Programs\Android Studio\jbr"
$env:ANDROID_HOME = "D:\Android\Sdk"
$env:ANDROID_SDK_ROOT = "D:\Android\Sdk"
$env:Path = "D:\Android\Sdk\platform-tools;" + $env:Path

adb version
# → Android Debug Bridge version 1.0.41 …
```

Project local config (gitignored):

```
# D:\ELSD\ELSD\local.properties
sdk.dir=D\:\\Android\\Sdk
org.gradle.java.home=C\:\\Users\\jason\\AppData\\Local\\Programs\\Android Studio\\jbr
```

---

## 1. One-time phone setup

Do this on **each** of Pixel 7, OnePlus 9, OnePlus 12.

### Enable Developer options

1. **Settings → About phone**  
2. Tap **Build number** 7 times until it says you’re a developer.

### USB debugging

1. **Settings → System → Developer options** (path varies slightly by OEM)  
2. Turn **ON**:
   - **USB debugging**
   - **Install via USB** (OnePlus / OxygenOS — enable if present)
   - Optional: **Disable permission monitoring** only if install is blocked (prefer leaving off)
3. Unlock the phone; use a **data-capable** USB-C cable (charge-only cables fail silently).

### First plug-in

1. Plug into the PC.  
2. Phone prompt: **Allow USB debugging?** → check **Always allow from this computer** → **Allow**.  
3. On OnePlus you may also get **File transfer / Android Auto** mode — pick **File transfer (MTP)** if adb is empty.

### OEM notes

| Device | Tips |
|--------|------|
| **Pixel 7** | Usually just works with Google USB driver / Windows built-in. Best “clean Android” reference. **6.3"** — great for flat testing; may be snug or not fit some large trays. |
| **OnePlus 9** | **Floor device.** OxygenOS may need **Install via USB**. If `adb devices` is empty: try cable, USB 2.0 port, reboot adb. |
| **OnePlus 12** | **Ship target.** Same as OP9 for USB. Larger tray required in headset. |

Windows: if device shows as unknown, install **Google USB Driver** via Android Studio SDK Manager, or OnePlus USB drivers from the manufacturer if Windows can’t enumerate.

---

## 2. Confirm the phone is seen

```powershell
adb kill-server
adb start-server
adb devices -l
```

Good:

```
List of devices attached
XXXXXXXX    device product:… model:Pixel_7 …
```

Bad:

| Output | Fix |
|--------|-----|
| empty | Cable, USB mode, debugging off, wrong port |
| `unauthorized` | Unlock phone → accept RSA prompt |
| `offline` | Unplug, `adb kill-server`, replug |
| multiple devices | Use `-s SERIAL` on every command |

List serials:

```powershell
adb devices
```

---

## 3. Build + install (recommended path)

From repo root `D:\ELSD\ELSD`:

```powershell
# Env as in §0
.\gradlew.bat :app:installDebug
```

That compiles **and** pushes `app-debug.apk` to the only connected device (or fails if zero/many).

### Target a specific phone (lab with several)

```powershell
# Example serials — use yours from adb devices
$pixel = "SERIAL_PIXEL"
$op9   = "SERIAL_OP9"
$op12  = "SERIAL_OP12"

.\gradlew.bat :app:installDebug -Pandroid.injected.invoked.from.ide=true
# Or pure adb after assemble:
.\gradlew.bat :app:assembleDebug
adb -s $op9 install -r app\build\outputs\apk\debug\app-debug.apk
```

Helper script (optional):

```powershell
.\scripts\deploy-debug.ps1           # default connected device
.\scripts\deploy-debug.ps1 -Serial $op9
.\scripts\deploy-debug.ps1 -Serial $op12 -Launch
```

### Reinstall / replace

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

`-r` = replace existing. Keeps most app data.

### Clean install (fresh prefs)

```powershell
adb uninstall com.elsd
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## 4. Launch + permissions

```powershell
adb shell am start -n com.elsd/.SwitchboardActivity
```

On device, first run:

1. Grant **Camera** (WORLD bus).  
2. Grant **Microphone** when voice is used.  
3. Switchboard → **GO LIVE** → landscape GL view.

Quick smoke:

| Check | Expect |
|-------|--------|
| Switchboard opens | Praxis credit, ADD EFFECT, GO LIVE |
| INFO | About / Praxis splash |
| GO LIVE | Camera feed |
| Voice | Amy lean when speech starts (if mic allowed) |
| CLEAR / random | Board responds |

---

## 5. Logs when it breaks

```powershell
# Clear + follow ELSD only
adb logcat -c
adb logcat -s ELSD:* AndroidRuntime:E libc:F DEBUG:F *:S

# Or broader crash hunt
adb logcat *:E | Select-String -Pattern "elsd|AndroidRuntime|FATAL"
```

Capture a bug report:

```powershell
adb bugreport D:\ELSD\bugreport-op9.zip
```

---

## 6. Device roles (what to test where)

| Device | Role | Priority tests |
|--------|------|----------------|
| **OnePlus 9** | **Floor / gate** | Heavy presets, thermal 10+ min, framerate + drops, mandel_key_trails |
| **OnePlus 12** | **Ship feel** | Same presets should *sizzle*; longer sessions |
| **Pixel 7** | **Clean reference** | API behavior, permissions, camera quirks; flat desk before headset |

**Rule:** a feature is not “done” until OP9 survives it. Pixel proves “not OnePlus-only.” OP12 proves ship quality.

---

## 7. Headset loop (after the viewer arrives)

1. Install APK on OP9 or OP12 via adb **before** seating the phone.  
2. Grant permissions **outside** the shell (easier).  
3. Seat phone in tray (USB-C cutout if charging).  
4. GO LIVE → optional strap adjust → short session.  
5. Pull phone to iterate; use `installDebug` again.

Shopping / fit rules: [`docs/design/TARGET_HARDWARE.md`](design/TARGET_HARDWARE.md).

---

## 8. Common failures

| Symptom | Likely cause |
|---------|----------------|
| `SDK location not found` | Missing `local.properties` / wrong `sdk.dir` |
| `Unsupported class file` / Java errors | Not using JDK 17+ JBR |
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Uninstall old package or same signing key mismatch |
| `INSTALL_FAILED_USER_RESTRICTED` | Enable **Install via USB** (OnePlus) |
| Black GL / no camera | Permission denied; check Settings → Apps → ELSD |
| adb empty on OnePlus | USB mode, cable, RSA dialog behind lock screen |
| Build OK, old UI on phone | Installed to wrong serial; use `-s` |

---

## 9. Minimal daily loop

```powershell
$env:JAVA_HOME = "C:\Users\jason\AppData\Local\Programs\Android Studio\jbr"
$env:ANDROID_HOME = "D:\Android\Sdk"
$env:Path = "D:\Android\Sdk\platform-tools;$env:Path"

cd D:\ELSD\ELSD
adb devices -l
.\gradlew.bat :app:installDebug
adb shell am start -n com.elsd/.SwitchboardActivity
```

That’s the whole squirt.

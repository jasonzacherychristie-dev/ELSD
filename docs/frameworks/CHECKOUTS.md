# Upstream Checkouts

Which repos to clone into a local workspace for a big head start.  
**Default parent:** `D:\ELSD\upstream\` (outside the ELSD app git root to keep history clean).

ELSD app repo: `D:\ELSD\ELSD`  
Upstream clones: `D:\ELSD\upstream\` (recommended, gitignored from app if ever nested)

---

## Required for implementation kickoff

**Status (2026-07-19):** shallow clones present at `D:\ELSD\upstream\`  
(`cardboard`, `vosk-android-demo`, `camera-samples`). Not part of the ELSD git tree.

### 1. Google Cardboard SDK

```powershell
mkdir D:\ELSD\upstream -Force
cd D:\ELSD\upstream
git clone --depth 1 https://github.com/googlevr/cardboard.git
```

**Read first:** `cardboard/README.md`, Android hello VR under samples / sdk.  
**Use as:** submodule *or* Maven/AAR dependency — prefer published artifact if build is painful; keep clone for API examples.  
**Local path:** `D:\ELSD\upstream\cardboard`

### 2. Vosk Android demo

```powershell
cd D:\ELSD\upstream
git clone --depth 1 https://github.com/alphacep/vosk-android-demo.git
```

**Read first:** how models are packed in assets; continuous recognition loop.  
**Use as:** pattern only — do not force their UI into ELSD.  
**Local path:** `D:\ELSD\upstream\vosk-android-demo`

### 3. Camera samples (optional but high value)

```powershell
cd D:\ELSD\upstream
git clone --depth 1 https://github.com/android/camera-samples.git
```

**Hunt for:** OpenGL / SurfaceTexture / analysis pipelines closest to “camera as GL texture.”  
**Local path:** `D:\ELSD\upstream\camera-samples`

---

## Maven-only (no clone required)

```text
androidx.media3:media3-exoplayer
androidx.media3:media3-ui
androidx.media3:media3-exoplayer-hls
androidx.camera:camera-core
androidx.camera:camera-camera2
androidx.camera:camera-lifecycle
androidx.camera:camera-view
```

Exact versions go in `gradle/libs.versions.toml` when the app module is scaffolded.

---

## Not cloning yet

| Repo | When |
|------|------|
| `google/filament` | If GL pipeline becomes unmanageable |
| `google/oboe` | If Visualizer is too weak for mic pulse |
| `cats-oss/android-gpuimage` | Reference for filter chain design only |

---

## Agent note

If the coding agent needs a checkout, run the clones above and confirm:

```powershell
Get-ChildItem D:\ELSD\upstream
```

Do **not** commit upstream trees into `ELSD` unless vendoring a tiny snapshot with license files.

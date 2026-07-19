# ELSD Android Stack — Framework Roles

How open frameworks map onto the **Video Toaster / Electronic LSD** architecture for open-source Android 1.0.

```
  CameraX ─────────────►  A: World texture (OES external)
  Media3 ExoPlayer ────►  B: Media texture (SurfaceTexture / GL)
         │
         ▼
  OpenGL ES 3.x switcher
    KEY (luma / chroma)  →  PULSE (edges + audio)  →  PAINT (style)  →  LSD (FX)
         │
         ▼
  Cardboard SDK ─────────► stereo meshes + lens distortion + head pose
         │
  Vosk / SpeechRecognizer ► voice T-bar (wet/dry, banks, clear)
  Visualizer / Mic FFT ──► BandEnergy → bass/mid/high/beat
         │                      ├──► PULSE + LSD (eyes)
         │                      └──► SpatialHallucinationEngine (ears)
  Gyro / head pose ─────────────► world-lock audio
  (Butterchurn-inspired looks; Resonance Audio later — see VISUALIZERS.md + SPATIAL_AUDIO.md)
```

---

## 1. Google Cardboard SDK

| | |
|--|--|
| **Why** | Open stereo VR: motion tracking, distortion meshes, viewer QR params, button |
| **License** | Apache-2.0 (mark “Google Cardboard” is trademarked — say “compatible with Cardboard”) |
| **Integration** | Android **NDK / C API** is the supported path; JNI from Kotlin activity |
| **ELSD use** | Dual-eye render of our GL compositor; optional viewer button = “next preset” |
| **Local docs** | `raw/cardboard-*.md` |
| **Head start** | Run official hello VR sample first; replace clear-color scene with our texture quad |

**Not using:** Unity XR plugin (we stay pure open Android).  
**Not using:** proprietary Google VR (Daydream) stack.

---

## 2. CameraX (World bus A)

| | |
|--|--|
| **Why** | Lifecycle-safe camera; analysis + preview; works with GL via Surface / ImageReader patterns |
| **License** | Apache-2.0 |
| **ELSD use** | Continuous rear camera → `SurfaceTexture` (GL_TEXTURE_EXTERNAL_OES) as **Bus A** |
| **Key APIs** | `ProcessCameraProvider`, `Preview`, optional `ImageAnalysis` for CPU edge assist |
| **Local docs** | `raw/camerax-overview.md`, `raw/camerax-architecture.md`, OpenGL interop mirror |

**1.0 note:** Prefer GPU path (OES texture) over CPU YUV copies for 30 fps FX.

---

## 3. Media3 / ExoPlayer (Media bus B)

| | |
|--|--|
| **Why** | Local files + **HLS/DASH/progressive** streams; mature, open, Play-friendly |
| **License** | Apache-2.0 |
| **ELSD use** | Decode to `Surface` / `SurfaceTexture` → GL as **Bus B** for keying |
| **Formats 1.0** | MP4/WebM local; HLS (`.m3u8`) stream; still images as looping 1-frame video or bitmap texture |
| **Local docs** | `raw/media3-exoplayer.md`, `raw/media3-hls.md`, hello-world mirror |

**1.0 out of scope:** DRM-heavy YouTube scraping; use direct media URLs or user-picked files (SAF).

---

## 4. OpenGL ES 3.0+ (Toaster heart)

| | |
|--|--|
| **Why** | Real-time multi-pass filters = Video Toaster DVE bank |
| **ELSD use** | Ping-pong FBOs: key → pulse → paint → LSD → stereo submit to Cardboard |
| **Shader languages** | GLSL ES 3.00 for GL path; optional AGSL only for Compose UI previews |
| **Local docs** | `raw/opengl-es-android.md`, `raw/agsl-runtime-shader.md` |

### Effect families (shader modules)

| Family | Module package (planned) | Examples |
|--------|--------------------------|----------|
| `key` | `elsd.fx.key` | Luma key, chroma key, soft edge |
| `pulse` | `elsd.fx.pulse` | Edge breath, bright-plane thump, audio gain |
| `paint` | `elsd.fx.paint` | Gogh, Monet, Noir, Neon, Sketch, Melt |
| `lsd` | `elsd.fx.lsd` | Trail/feedback, hue rot, chroma split, kaleido |

---

## 5. Vosk (Voice T-bar) + SpeechRecognizer fallback

| | |
|--|--|
| **Why** | Offline, open, small models; hands-free while wearing cardboard |
| **License** | Apache-2.0 |
| **ELSD use** | Grammar/spotting for commands: `clear`, `trip`, `gogh`, `key luma`, `city pulse`, … |
| **Model** | Ship smallest English model that fits APK or download-on-first-run (document size) |
| **Fallback** | Platform `SpeechRecognizer` when offline model missing |
| **Local docs** | `raw/vosk-android.md`, `raw/vosk-github.md` |

---

## 6. Audio → Pulse / sound-activated hallucinations

| | |
|--|--|
| **Primary (media)** | `android.media.audiofx.Visualizer` on ExoPlayer audio session id |
| **Primary (mic)** | `AudioRecord` + in-repo FFT (`MicEnergySource`) — MIT |
| **Smoothing** | `elsd.audio.BandEnergy` → `bass` / `mid` / `high` / `beat` |
| **Upgrade** | Oboe (Apache-2.0) if latency is poor |
| **Visual DNA** | Butterchurn (MIT) inspiration; projectM/TarsosDSP = ideas only (LGPL/GPL) |
| **ELSD use** | Uniforms drive PULSE (buildings thump) + LSD amounts + Amy squash |
| **Deep dive** | [`VISUALIZERS.md`](VISUALIZERS.md) |

## 6b. Spatial audio hallucination (ears)

| | |
|--|--|
| **Early** | MIT stereo orbit / rear bias / reverb send (`SpatialHallucinationEngine`) |
| **1.0 target** | Resonance Audio (Apache-2.0) and/or Media3 spatial processors |
| **Pose** | Gyro / Cardboard head pose for world-lock |
| **Safety** | `clear` dries **ears and eyes**; command voice stays intelligible |
| **Deep dive** | [`SPATIAL_AUDIO.md`](SPATIAL_AUDIO.md) |

---

## Dependency policy (open source hygiene)

1. **ELSD code is MIT** (see repo `LICENSE`). Prefer dependencies that are **Apache-2.0 / MIT / BSD**.  
2. Apache-2.0 deps (Cardboard, CameraX, Media3, Vosk, …) stay under Apache — list them in `NOTICE`. MIT app + Apache libs is a standard, compatible combination.  
3. No GPL in the main APK linkage unless dual-license approved.  
4. Vendor third-party notice files in `NOTICE` / `app/src/main/assets/licenses/`.  
5. Cardboard trademark: market as **“Cardboard-compatible”**, not “Google Cardboard app.”  
6. Document every new dependency in this file + `gradle/libs.versions.toml`.  
7. Keep **Grok (xAI)** + **Jason Z. Christie** credit strings in About / boot UI.

---

## What we deliberately skip i
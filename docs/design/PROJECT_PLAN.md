# ELSD — Open Source Android Project Plan

## Goals

1. Ship a **usable 1.0 APK** that demos KEY + PULSE + PAINT + LSD on Cardboard-compatible devices.  
2. Keep the repo **easy to fork**: Apache-2.0, clear modules, no proprietary required runtimes for core path.  
3. Stay faithful to the **Toaster metaphor**: live mix, not a content silo.

## Recommended repo layout

```
ELSD/
  README.md
  LICENSE                 # Apache-2.0
  NOTICE
  CONTRIBUTING.md
  CODE_OF_CONDUCT.md
  settings.gradle.kts
  build.gradle.kts
  gradle/libs.versions.toml
  app/                    # Application shell (Compose + permissions + navigation)
  core-gl/                # GL runtime, FBO graph, effect interfaces
  fx-key/                 # Luma/chroma key shaders + params
  fx-pulse/               # World visualizers + audio uniforms
  fx-paint/               # Painter/style presets
  fx-lsd/                 # Classic trip FX
  media-bus/              # Media3 → GL texture
  camera-bus/             # CameraX → GL texture
  voice/                  # Vosk + command grammar + SpeechRecognizer fallback
  cardboard/              # JNI/NDK bridge to Cardboard SDK
  presets/                # JSON/YAML Toaster "pages"
  docs/                   # Design + framework bookmarks (this tree)
  tools/                  # scripts (shader pack, license check)
```

Gradle can start as a **single `app` module** and split when files hurt; the table above is the *logical* architecture either way.

## Module responsibilities

| Module | Depends on | Owns |
|--------|------------|------|
| `camera-bus` | CameraX | OES world texture, lifecycle |
| `media-bus` | Media3 | File/SAF/HLS → texture, audio session id |
| `core-gl` | GLES 3 | Graph runner, wet/dry, timing |
| `fx-*` | core-gl | Shaders + parameter schemas |
| `voice` | Vosk AAR / platform speech | Intent → parameter bus |
| `cardboard` | Cardboard NDK | Head pose, distortion, stereo submit |
| `app` | all | UX, presets, safety timers, permissions |

## Technical milestones

### M0 — Repo & docs (current)

- [x] Framework bookmarks + local doc mirrors  
- [x] Stack map + checkout guide  
- [x] Vision + plan  
- [ ] LICENSE, README, .gitignore  
- [ ] Optional: clone upstream into `D:\ELSD\upstream`

### M1 — Hello eyes

- [ ] Android app skeleton (Kotlin, min SDK documented)  
- [ ] Fullscreen GL surface, 60/30 fps clear + textured quad  
- [ ] CameraX → OES texture on quad (flat phone preview)  
- [ ] Cardboard hello: stereo + distortion of same texture  

**Exit:** You see the room in Cardboard.

### M2 — Toaster minimum

- [ ] Media3 local video → second texture  
- [ ] Luma key composite (A world, B media)  
- [ ] Global wet/dry  
- [ ] One LSD effect: trail/feedback  

**Exit:** Window TV works in a controlled room.

### M3 — RAD banks

- [ ] Chroma key + color picker (freeze frame)  
- [ ] HLS stream as Bus B  
- [ ] 4+ paint presets, 3+ LSD FX  
- [ ] Pulse: edge + bright + Visualizer bass  

**Exit:** Gogh Walk + City Pulse outdoors.

### M4 — Voice & polish

- [ ] Vosk grammar commands + sober/clear  
- [ ] 8 factory presets  
- [ ] Session timer + soft landing  
- [ ] NOTICE / license screen  
- [ ] CONTRIBUTING + issue templates  

**Exit:** Hands-free demo for a stranger in &lt; 60 seconds.

### M5 — 1.0 release

- [ ] Version tag `v1.0.0`  
- [ ] GitHub Actions: assembleDebug / lint  
- [ ] F-Droid-friendly notes (no proprietary must-haves on core path)  
- [ ] Short demo video + README GIFs  

## Open source process

| Practice | Choice |
|----------|--------|
| Host | GitHub (user/org TBD) |
| Default branch | `main` |
| Versioning | SemVer; presets may version independently |
| CI | GitHub Actions (Android Gradle) |
| Issues | bug / enhancement / shader / docs labels |
| PR rule | one logical change; include before/after note for FX |
| Code of conduct | Contributor Covenant |
| Security | No camera/mic frames uploaded by default; document any future cloud path |

## Risk register

| Risk | Mitigation |
|------|------------|
| Cardboard NDK integration pain | M1 time-box; flat mode ships even if stereo slips |
| Camera + ExoPlayer + GL thread races | Single GL owner thread; explicit EGL context rules |
| Thermal / FPS | Half-res feedback; effect budget (max N passes) |
| Vosk APK size | Download model on first run; optional system speech |
| Trademark | “Cardboard-compatible” wording in store/README |
| Scope creep (Amiga 9000) | Horizon doc only; not 1.0 milestones |

## Decision log (initial)

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Platform | Android first | Cardboard SDK + open ecosystem |
| Engine | OpenGL ES 3 (not Unity) | Open, Toaster-like control |
| Video | Media3 | HLS + local, Apache-2.0 |
| Voice | Vosk primary | Offline OSS |
| License | Apache-2.0 | Aligns with deps |
| Neural style | Post-1.0 | Shader painters first |

## Next actions (human + agent)

1. Commit docs scaffold to `main`.  
2. **Checkout** Cardboard + Vosk demo under `D:\ELSD\upstream` when coding starts (see `../frameworks/CHECKOUTS.md`).  
3. Scaffold Gradle `app` with empty packages matching filter families.  
4. Implement M1 camera quad before any FX glam.

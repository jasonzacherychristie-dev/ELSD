# ELSD — Open Source Android Project Plan

## Goals

1. Ship a **usable 1.0 APK** that demos KEY + PULSE + PAINT + LSD on Cardboard-compatible devices.  
2. Keep the repo **easy to fork**: **MIT** app code, clear modules, no proprietary required runtimes for core path.  
3. Stay faithful to the **TOASTED** metaphor: live mix, wet/dry, world-first — not a content silo.  
4. **Voice only** + **Bounce** cursor (FLAT / SPATIAL / MUTED); see [`SOUL.md`](SOUL.md).  
5. Imply classic desk/demo spirit; never ship forbidden brand names ([`TRADEMARK_SAFE_LANGUAGE.md`](TRADEMARK_SAFE_LANGUAGE.md)).

## Recommended repo layout

```
ELSD/
  README.md
  LICENSE                 # MIT
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
  bounce/                 # Checker cursor + personality lines
  cardboard/              # JNI/NDK bridge to Cardboard SDK
  presets/                # JSON/YAML TOASTED "pages"
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
| `voice` | Vosk AAR / platform speech | **Only** command path → parameter bus |
| `bounce` | core-gl | Checker cursor FLAT / SPATIAL / MUTED + optional quips |
| `cardboard` | Cardboard NDK | Head pose, distortion, stereo submit |
| `app` | all | Legends, presets, safety timers, permissions — **no touch mixer UI** |

## Technical milestones

### M0 — Repo & docs (current)

- [x] Framework bookmarks + local doc mirrors  
- [x] Stack map + checkout guide  
- [x] Vision + plan + soul (Bounce / TOASTED)  
- [x] LICENSE, README, .gitignore  
- [x] Upstream clones under `D:\ELSD\upstream`

### Hardware baseline

- **Target:** OnePlus 12–class phone + large-tray Cardboard-compatible viewer — [`TARGET_HARDWARE.md`](TARGET_HARDWARE.md)

### M1 — Hello eyes

- [x] Android app skeleton (Kotlin, min SDK documented)  
- [x] Fullscreen GL surface, 60/30 fps clear + textured quad  
- [x] CameraX → OES texture on quad (flat phone preview)  
- [x] Amy lean-in on speech (flat)  
- [ ] Cardboard hello: stereo + distortion of same texture (on target viewer)  

**Exit:** You see the room on the reference phone; stereo on target headset when M5/partial stereo lands.

### M2 — TOASTED minimum

- [ ] Media3 local video → second texture  
- [ ] Luma key composite (A world, B media)  
- [ ] Global wet/dry  
- [ ] One LSD effect: trail/feedback  
- [ ] Bounce FLAT placeholder (can be silent)

**Exit:** Window TV works in a controlled room.

### M2.5 — Sound-activated hallucinations (eyes + ears)

- [x] `BandEnergy` + `MediaVisualizerSource` + `MicEnergySource` (skeleton)  
- [x] `SpatialHallucinationEngine` + modes + voice grammar (control math)  
- [ ] Wire bands into renderer uniforms every frame  
- [ ] Wire spatial engine → AudioTrack / Media3 wet chain (actual ears)  
- [ ] Modes eyes: City Pulse, Nerve, Sparkle, Heartbeat  
- [ ] Modes ears: orbit, behind, cathedral, bass crawl, hallucinate ears  
- [ ] Voice: `hallucinate`, `hallucinate ears`, `ears dry`, `city pulse`, `pulse off`  
- [ ] `clear` dries eyes **and** ears  

**Exit:** Bass possesses the world *and* something can move behind your head.  
**Refs:** [`../frameworks/VISUALIZERS.md`](../frameworks/VISUALIZERS.md) · [`../frameworks/SPATIAL_AUDIO.md`](../frameworks/SPATIAL_AUDIO.md)

### M3 — RAD banks

- [ ] Chroma key + color picker (freeze frame)  
- [ ] HLS stream as Bus B  
- [ ] 4+ paint presets, 3+ LSD FX  
- [ ] Spectrum texture optional; Butterchurn-inspired warp (MIT original GLSL)  

**Exit:** Gogh Walk + City Pulse outdoors.

### M4 — Bounce, voice & polish

- [ ] Vosk grammar commands + sober/clear (preempt personality)  
- [ ] Bounce cursor: FLAT + SPATIAL + MUTED  
- [ ] Fourth-wall line budget + `no jokes`  
- [ ] TOASTED chrome: bank lights, wet legend, auto-hide  
- [ ] 8 factory presets  
- [ ] Session timer + soft landing  
- [ ] NOTICE / license screen  
- [ ] CONTRIBUTING + issue templates  

**Exit:** Stranger demo, **voice only**, Bounce optional quiet, &lt; 60 seconds to magic.

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
| License | MIT (app) | Simple for shaders/forks; Apache deps via NOTICE |
| Neural style | Post-1.0 | Shader painters first |

## Next actions (human + agent)

1. Commit docs scaffold to `main`.  
2. **Checkout** Cardboard + Vosk demo under `D:\ELSD\upstream` when coding starts (see `../frameworks/CHECKOUTS.md`).  
3. Scaffold Gradle `app` with empty packages matching filter families.  
4. Implement M1 camera quad before any FX glam.

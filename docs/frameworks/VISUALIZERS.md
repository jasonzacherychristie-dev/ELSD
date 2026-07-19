# Open visualizer code — sound-activated hallucinations

ELSD’s **PULSE** family and parts of **LSD** are not “music player skins.”  
They are **audio → perception drivers**: bass makes buildings thump, mids fray edges, highs sparkle chroma — hallucinations *of the real world*, not a sealed kaleidoscope app.

This doc bookmarks **open** analysis + visualization sources we can learn from, port, or depend on under MIT/Apache-compatible terms.

---

## What we need (signal contract)

| Output | Use in ELSD |
|--------|-------------|
| `uBass` / `uMid` / `uHigh` (0–1 smoothed) | PULSE edge thump, window glow |
| `uRms` / `uBeat` (onset) | Squash Bounce, kick wet flickers |
| `uSpectrum[N]` or 1D spectrum texture | Warp UV, trail length, paint swirl rate |
| Optional: mic **or** media session | World-reactive live room vs plate soundtrack |

**Voice path stays separate** (commands). Visualizer audio is the **music/world** bus, not the T-bar.

---

## Tier A — ship with Android platform (no extra dep)

### 1. `android.media.audiofx.Visualizer`

| | |
|--|--|
| **License** | Platform API (AOSP) |
| **Docs** | https://developer.android.com/reference/android/media/audiofx/Visualizer |
| **Local mirror** | `raw/android-visualizer.md` |
| **ELSD role** | **Primary M2/M3** for plate/media playback |

Hook ExoPlayer `audioSessionId` → `Visualizer(sessionId)` → `getFft()` / waveform callbacks → smooth bands → GL uniforms.

**Pros:** Zero license drama, works with Media3 when session id is valid.  
**Cons:** Capture policy quirks; weak for *mic-only* ambient; some devices return empty FFT if session wrong; not for arbitrary “system mix” without session.

### 2. `AudioRecord` + our FFT (mic world)

| | |
|--|--|
| **License** | Platform + our MIT band code |
| **ELSD role** | Live room / “hallucinate what you hear” without a plate file |

Use short PCM windows → FFT (see Tier B pure-Java FFT) → same band uniforms as Visualizer path.

---

## Tier B — open analysis libraries (preferred for quality)

### 3. **TarsosDSP** (Joren Six et al.)

| | |
|--|--|
| **Repo** | https://github.com/JorenSix/TarsosDSP |
| **Site** | https://0110.be/tags/TarsosDSP |
| **License** | **GPL-3.0** ⚠️ |

Excellent pitch, onset, beat, FFT — but **GPL is a problem** for MIT app distribution if linked into the APK as a hard dependency.  
**Policy:** study algorithms; **do not ship as linked library** unless we dual-license ELSD or isolate as optional GPL plugin. Prefer reimplementation of *ideas* (onset flux, band energy) under MIT.

### 4. **Oboe** (Google)

| | |
|--|--|
| **Repo** | https://github.com/google/oboe |
| **License** | **Apache-2.0** ✅ |
| **Local** | `raw/oboe-audio.md` |
| **ELSD role** | Low-latency mic / playback I/O if Visualizer is insufficient |

Not a visualizer UI — a clean PCM pipe into our FFT.

### 5. **Superpowered** / commercial kits

**Skip for core OSS** (proprietary). Inspiration only.

### 6. Pure FFT (MIT/BSD-friendly)

| Project | License | Notes |
|---------|---------|--------|
| **Kiss FFT** | BSD-style | Tiny C; NDK if we go native |
| **FFTW** | GPL | Avoid in core |
| **Apache Commons Math FastFourierTransformer** | Apache-2.0 | Heavy but pure Java |
| **Minimal Cooley–Tukey in Kotlin** | Our MIT | Best for 256–1024 bins; keep dep graph thin |

**Recommendation:** implement **`elsd.audio.BandEnergy`** (MIT) with a small in-repo FFT or Apache-friendly snippet; feed Visualizer *or* AudioRecord.

---

## Tier C — open visualizer *looks* (port shaders, don’t fork product UI)

These are gold for **sound-activated hallucination** aesthetics. Prefer **shader ideas** over embedding whole apps.

### 7. **projectM** (MilkDrop-compatible)

| | |
|--|--|
| **Repo** | https://github.com/projectM-visualizer/projectm |
| **License** | **LGPL-2.1** ⚠️ |
| **What** | Legendary music visualizer engine (MilkDrop presets) |

**ELSD policy:** LGPL can be used carefully (dynamic link / separate process), but for mobile static link it’s awkward.  
**Use as:** preset *vocabulary* and math references (waveform, spectrum, warp, composite). Re-author simplified GLES fragments under MIT as `fx-pulse` / `fx-lsd` modes (`milk_warp`, `spectrum_tunnel` — original code, not copy-paste GPL/LGPL).

### 8. **Butterchurn** (WebGL MilkDrop)

| | |
|--|--|
| **Repo** | https://github.com/jberg/butterchurn |
| **License** | **MIT** ✅✅ |
| **What** | WebGL MilkDrop-style visualizer |

**Best open visual reference for ELSD.** Study `butterchurn` shaders/presets; port *techniques* into `toasted_composite` / dedicated pass:

- spectrum-driven zoom  
- waveform trails  
- color cycle on bass  

Keep MilkDrop preset files’ licenses separate if we ever bundle them.

### 9. **AVSweb / classic Winamp AVS** lineage

Historical; licenses messy. Prefer Butterchurn/projectM docs over ancient binaries.

### 10. **VLC** audio visualizations

| | |
|--|--|
| **License** | GPL |
| **Policy** | Reference only; do not link libvlc into core ELSD |

### 11. **Three.js audio examples / Web Audio analysers**

| | |
|--|--|
| **License** | MIT |
| **Use** | AnalyserNode patterns → same band smoothing we want on Android |

### 12. **Shadertoy** “audio” / “mic” family

| | |
|--|--|
| **License** | Per-shader (often CC) |
| **Use** | Art direction; only port with clear license / rewrite |

### 13. **Cinder / openFrameworks audio examples**

Good FFT→mesh tutorials; C++. Port ideas to GLES ES 3.

### 14. **Processing Sound / Minim**

Education-grade; Java. License check per version; often used as teaching ports.

### 15. **Android “music visualizer” OSS apps** (examples)

Search periodically for Apache/MIT players with `GLSurfaceView` + `Visualizer`. Treat as pattern sources; don’t absorb random GPL APKs.

---

## ELSD architecture: “Hallucination drive”

```
  Mic (AudioRecord) ──┐
                      ├──► BandEnergy (FFT → bass/mid/high/onset)
  Media3 session ─────┘         │
        Visualizer API ─────────┘
                                ▼
                     uniforms + optional spectrum tex
                                │
         ┌──────────────────────┼──────────────────────┐
         ▼                      ▼                      ▼
      PULSE                  LSD                     Bounce
   (world edges)        (trail/warp amount)      (squash on beat)
         │                      │                      │
         └──────────────────────┴──────────────────────┘
                                ▼
                         TOASTED PGM (camera still underneath)
```

**Sound-activated hallucination** = *modulate the world composite*, not replace it with a black-scope visualizer (unless preset `scope_only` later).

### Named pulse/hallucination modes (product)

| Mode | Audio mapping | Look |
|------|---------------|------|
| **City Pulse** | bass → bright planes / edges | buildings thump |
| **Nerve** | mid → chromatic split amount | frayed reality |
| **Sparkle** | high → noise on speculars | glitter trip |
| **Heartbeat** | onset → wet pulse | kick-synced |
| **Milk Warp** (inspired, MIT-original) | spectrum tex → UV domain warp | classic viz DNA on passthrough |
| **Waveform Vein** | time-domain wave → edge displacement | veins of sound in the scene |

---

## Recommended dependency choices (1.0)

| Need | Choice | License |
|------|--------|---------|
| Media-reactive | Platform **Visualizer** + Media3 session | Platform |
| Mic-reactive | **AudioRecord** + in-repo **BandEnergy** | MIT |
| Low-latency later | **Oboe** | Apache-2.0 |
| Visual inspiration | **Butterchurn** (read, re-author) | MIT upstream |
| Avoid in core APK | TarsosDSP, projectM static link, VLC | GPL/LGPL complexity |

---

## Implementation plan (tie to milestones)

### M2.5 — Audio skeleton

- [ ] `elsd.audio.BandEnergy` — FFT + envelope followers  
- [ ] `elsd.audio.MediaVisualizerSource` — wraps `android.media.audiofx.Visualizer`  
- [ ] `elsd.audio.MicEnergySource` — AudioRecord path  
- [ ] Feed `uBass/uMid/uHigh/uBeat` into existing `toasted_composite.frag`  
- [ ] Voice: `pulse on`, `city pulse`, `heartbeat`, `hallucinate` / `pulse off`

### M3 — Hallucination bank

- [ ] Spectrum as 256×1 `GL_R8` texture optional  
- [ ] Modes: City Pulse, Nerve, Sparkle, Heartbeat  
- [ ] True feedback FBO so trails *eat* audio energy  

### Later

- [ ] Optional Butterchurn-inspired warp pass (MIT original GLSL)  
- [ ] Oboe if latency sucks  
- [ ] Never block `clear` / `sober` even mid-drop  

---

## License watchlist

| Source | Link into ELSD? |
|--------|-----------------|
| Butterchurn (MIT) | Ideas + techniques ✅ |
| Oboe (Apache) | Optional dep ✅ |
| Visualizer API | Yes ✅ |
| projectM (LGPL) | Ideas only / careful dynamic later |
| TarsosDSP (GPL) | Ideas only ❌ link |
| Random Shadertoy | Only if license allows rewrite |

---

## Soul note

Open visualizer culture (MilkDrop, projectM, Butterchurn) is the cousin of Electronic LSD: **music as reality distortion**.  
We keep the **camera as the canvas**. Sound does not replace the world; it **possesses** it.

TOASTED line when it hits: *“Bass has the building.”*

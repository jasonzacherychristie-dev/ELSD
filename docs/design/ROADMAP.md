# ELSD release roadmap — 1.0 · 2.0 · 3.0

**Focus rule:** Build and discuss the **current major** only.  
Wishlist items for later majors stay documented so they don’t sneak into the active milestone list.

| Now | Next | Later |
|-----|------|--------|
| **→ 1.0** ship | **2.0** deepen | **3.0** dream |

Internal sprint tags (M0–M5) roll up into **1.0**. Do not invent M6–M20 for 2.0/3.0 until 1.0 ships.

---

## 1.0 — Perception mixer (ship this)

**Theme:** TOASTED live desk on target hardware. Shader + audio analysis. No heavy neural nets.

### In scope

| Area | Deliverable |
|------|-------------|
| **Hardware** | OnePlus 12–class phone + large-tray Cardboard-compatible viewer ([`TARGET_HARDWARE.md`](TARGET_HARDWARE.md)) |
| **WORLD / PLATE** | CameraX + Media3 (local + HLS); luma/chroma key |
| **FX banks** | KEY · PULSE · PAINT (shader styles) · LSD (trail/hue/split/kaleido/melt) |
| **Eyes** | Sound-activated visual pulse (BandEnergy → uniforms) |
| **Ears** | Spatial hallucination control + wet stereo path (orbit/behind/cathedral…); Resonance optional if it stays light |
| **Amy** | Checker-orb pilot; lean-in on speech; FLAT / SPATIAL / MUTED |
| **Voice** | SpeechRecognizer + grammar; Vosk offline if size OK |
| **Stereo** | Cardboard NDK distortion on target viewer |
| **Safety** | `clear` / `sober` / `stop`; soft landing; session timer; dry eyes **and** ears |
| **License** | MIT app; NOTICE for Apache deps |

### Explicitly out of 1.0

- DeepDream / CNN iterative dream layers  
- On-device diffusion or large style-transfer every frame  
- Cloud vision APIs as required path  
- Amiga 9000 dome hardware  
- iOS  
- Social / accounts / always-on telemetry  

### 1.0 exit (stranger demo)

Voice-only, &lt; 60s to magic: Window TV, Gogh Walk, City Pulse, hallucinate ears, Amy lean-in, **clear** works. Stereo on target headset.

**Internal milestones:** M0–M5 in [`PROJECT_PLAN.md`](PROJECT_PLAN.md).

---

## 2.0 — Deeper mixer (after 1.0)

**Theme:** Richer live desk, better spatial, stronger coupling of senses — still real-time first.

### Planned (not committed until 1.0 ships)

| Area | Direction |
|------|-----------|
| **Feedback graph** | True multi-pass FBO trails; Milk-warp class (MIT original) |
| **Spatial audio** | Full Resonance Audio / world-lock from Cardboard pose; ghost choir quality |
| **Paint** | More painters; optional **lite** one-shot style snapshot (not full DeepDream) |
| **Amy** | Quip packs, captions, serious mode polish |
| **Presets** | User save/load; shareable preset files |
| **Perf** | Thermal profiles per SoC; half-res ladders |
| **A11y** | Captions, high-contrast Amy, seizure-safe profiles |
| **CI / store** | F-Droid notes, release automation |

### Explicitly out of 2.0 (unless research spike)

- Full DeepDream continuous dream  
- Multi-user linked helmets as required  
- Dome / outer-display Amiga 9000 product  

---

## 3.0 — Dream machine (wishlist)

**Theme:** Neural perception layers on the live world — Electronic LSD meets classic deep generative vision — still open, still exit-safe.

### Wishlist (documented, not scheduled)

#### DeepDream mode ⭐

| | |
|--|--|
| **Name** | `deepdream` / voice: `deep dream` / `dream layer` |
| **Family** | New bank or PAINT++ : **DREAM** |
| **Feel** | Iterative CNN “enhance” on WORLD (or PGM): dog-slash-pareidolia, recursive patterns blooming out of streets and faces |
| **Why 3.0** | Real-time or near-real-time DeepDream on a hot flagship is a **research + thermal + model-license** problem. Shipping it as 1.0 would blow focus, APK size, and FPS. |
| **Product rules** | Always under wet/dry; `clear` kills dream layers immediately; session timer stricter; never default-on |
| **Tech candidates (explore later)** | TFLite / ONNX / MediaPipe-class on-device models; lower-res dream FBO at 5–15 fps composited over 30–60 fps passthrough; optional “freeze + dream” still mode for quality |
| **Open-source bar** | Prefer models and code we can redistribute under MIT/Apache-compatible terms; document weights in NOTICE |
| **Couple** | Bass → dream step count or octave layer; Amy: *“I’m seeing dogs in your drywall.”* (jokes mode) |

**DeepDream is not** a substitute for shader PAINT in 1.0 — Gogh/Noir stay GLSL.

#### Other 3.0 wishlist (parked)

| Item | Note |
|------|------|
| Continuous neural style transfer | Related to DeepDream; same release bucket |
| Thought / BCI hooks | Ultimate Reality paper lineage; research only |
| Multi-user shared PGM | Networked TOASTED |
| Amiga 9000–class dome + outer display | Horizon hardware |
| iOS port | After Android 1.x stable |
| Personalized HRTF / SOFA | Spatial ears pro tier |

---

## Focus discipline

| If someone asks… | Answer |
|------------------|--------|
| DeepDream now? | **3.0 wishlist** — tracked here; not a 1.0 ticket |
| New painter shader? | **1.0 / 2.0** — yes if GLSL real-time |
| Cloud dream API? | Only as **optional 3.0** opt-in; never required core |
| Scope creep mid-1.0? | Point at this file; open a `wishlist` issue labeled `3.0` |

### Issue labels (recommended)

- `1.0` · `2.0` · `3.0`  
- `wishlist` · `shader` · `audio` · `neural` · `hardware`  

---

## Version naming

| Tag | Meaning |
|-----|---------|
| `v0.x` | Pre-1.0 public alpha |
| `v1.0.0` | First stranger-demo solid release |
| `v2.0.0` | Deeper mixer (no neural core required) |
| `v3.0.0` | Dream machine — DeepDream mode eligible to ship |

---

## One-line summary

**1.0** ships the open TOASTED desk (shaders, keys, Amy, pulse, spatial ears).  
**2.0** deepens the desk.  
**3.0** may dream — **DeepDream mode** lives there, not in the critical path today.

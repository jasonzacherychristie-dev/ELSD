# ELSD — Electronic LSD

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Status](https://img.shields.io/badge/status-public%20alpha-blue.svg)](https://github.com/jasonzacherychristie-dev/ELSD)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://github.com/jasonzacherychristie-dev/ELSD)

**Open-source Android perception mixer** for Cardboard-compatible viewers.  
**Repository:** https://github.com/jasonzacherychristie-dev/ELSD · **Visibility: public** · **License: MIT**

Live camera world + keyed media (local/stream) + painter styles + real-world pulse visualizers + classic trip FX — mixed **TOASTED** (live desk soul), **voice only**, with **Bounce**: a spirited checker-orb cursor that can go spatial, break the fourth wall, or mute into the background.

> Buildings can throb. Windows can become TVs. Streets can go Van Gogh.  
> You talk. Bounce mixes. Say *clear* when you want the earth back.

## Mission

> Put a live perception mixer in everyone’s hands — open source, voice-first, and honest about wonder and risk.

Full statement: **[MISSION.md](MISSION.md)**  
Conduct: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) · Contributing: [CONTRIBUTING.md](CONTRIBUTING.md)

## Credits

| | |
|--|--|
| **Project lead & research** | [Jason Z. Christie](https://jasonzchristie.blogspot.com/) |
| **Co-implementation** | **Grok (xAI)** via Grok Build |

> ELSD is a Jason Z. Christie project, **co-implemented with Grok by xAI**.

Please keep that credit in forks, About screens, and demos. See [AUTHORS](AUTHORS) and [NOTICE](NOTICE).

## License

**MIT** for ELSD application code, project GLSL, and original docs — see [LICENSE](LICENSE).

Third-party libraries (Cardboard SDK, CameraX, Media3, Vosk, AndroidX, …) remain under **their own licenses** (mostly Apache-2.0). MIT is fully compatible; their notices stay in [NOTICE](NOTICE).

## Status

**Active release: 1.0** — **visuals only** on live camera: ELSD · Art · Cinema · Palette · Mood.  
**2.0:** live video in / stream out / key / ears. **3.0:** DeepDream.

| Release | Theme | Status |
|---------|--------|--------|
| **1.0** | Visual desk — camera + styles + mood + Amy + switchboard | **In progress** |
| **2.0** | Media in/out + key + spatial ears + depth-pop | After 1.0 |
| **3.0** | Dream machine — **DeepDream** | Wishlist |

Full map: **[docs/design/ROADMAP.md](docs/design/ROADMAP.md)** · 1.0 execution: **[docs/design/PROJECT_PLAN.md](docs/design/PROJECT_PLAN.md)**

| 1.0 milestone | Meaning |
|-----------|---------|
| M0 | Docs, stack, soul, public repo ✅ |
| M1 | Camera → GL → Amy lean-in + voice grammar ✅ |
| M2 | Plate keying + trail FBO |
| M2.5 | Eyes + ears hallucination wired to output |
| M3 | Full banks + HLS |
| M4 | Vosk, polish, safety timers |
| M5 | Stereo + **v1.0.0** |

## Switchboard (UI)

One page. Drill down. Same words as voice:

**ADD EFFECT** · **REMOVE EFFECT** · **TOGGLE EFFECT** · **ADD EFFECT TIME** · **REMOVE EFFECT TIME** · **PHASE TIME** · **PHASE ON/OFF** · **SAVE PRESET** · **LOAD PRESET** · **CLEAR BOARD** · **GO LIVE**

Spec: [docs/design/SWITCHBOARD.md](docs/design/SWITCHBOARD.md)

## Target hardware

| | |
|--|--|
| **Phone** | Large Android flagship — **OnePlus 12 class** (~6.8", ~164×76 mm) |
| **Viewer** | Plastic **Cardboard-compatible** headset with tray for **6.7–7.0"** / **≥ ~170×85 mm** |
| **Not required** | Quest / PCVR for 1.0 |

Full spec + shopping QA: **[docs/design/TARGET_HARDWARE.md](docs/design/TARGET_HARDWARE.md)**

## Build

Requirements:

- **Android Studio** (or SDK 35 + build-tools)
- **JDK 17+** (Studio’s JBR is fine)
- Device/emulator with camera + mic (gyro later for stereo)
- Prefer a **large-phone VR viewer** (see target hardware) for real stereo later

```bash
# from repo root
copy local.properties.example local.properties
# edit sdk.dir

# with wrapper (generate once in Android Studio: open project, or:)
# gradle wrapper --gradle-version 8.9

./gradlew :app:assembleDebug
./gradlew :app:test
```

Install `app/build/outputs/apk/debug/app-debug.apk`, grant camera + mic, landscape, talk.

### Voice cheatsheet (M1)

| Say | Effect |
|-----|--------|
| `clear` / `sober` | Dry / sober eyes |
| `toasted` | High wet pride |
| `gogh` / `noir` / `neon` / … | PAINT bank |
| `trails` / `kaleido` / `split` | LSD bank |
| `city pulse` | PULSE on |
| `window tv` / `gogh walk` / `sky cinema` | Presets |
| `mute bounce` / `bounce on` / `bounce 3d` | Cursor |

## Docs map

| Doc | What |
|-----|------|
| [MISSION.md](MISSION.md) | Mission, vision, principles |
| [docs/design/ROADMAP.md](docs/design/ROADMAP.md) | **1.0 / 2.0 / 3.0** |
| [docs/design/VISUAL_SYSTEM.md](docs/design/VISUAL_SYSTEM.md) | ELSD · Art · Cinema · Palette · Mood |
| [docs/design/STILLNESS_MORPH.md](docs/design/STILLNESS_MORPH.md) | Stare longer → morph budget (DeepDream later) |
| [docs/design/SOUL.md](docs/design/SOUL.md) | Look, Amy, TOASTED |
| [docs/design/VISION_1_0.md](docs/design/VISION_1_0.md) | Product vision |
| [docs/design/VOICE_AND_CURSOR.md](docs/design/VOICE_AND_CURSOR.md) | Voice-only + cursor |
| [docs/design/PROJECT_PLAN.md](docs/design/PROJECT_PLAN.md) | Milestones |
| [docs/frameworks/STACK.md](docs/frameworks/STACK.md) | Libraries |
| [docs/frameworks/VISUALIZERS.md](docs/frameworks/VISUALIZERS.md) | Sound → *visual* hallucinations |
| [docs/frameworks/SPATIAL_AUDIO.md](docs/frameworks/SPATIAL_AUDIO.md) | Sound → *spatial ear* hallucinations |
| [docs/bookmarks/FRAMEWORKS.md](docs/bookmarks/FRAMEWORKS.md) | Bookmarks |
| [SECURITY.md](SECURITY.md) | Vulnerability reporting |

## Lineage

- [Toward Ultimate Reality — Electronic LSD and Virtual Displays](https://jasonzchristie.blogspot.com/2017/06/toward-ultimate-reality-electronic-lsd.html)
- [2025 revisit on Medium](https://medium.com/@JasonZChristie/toward-ultimate-reality-revisiting-jason-z-christies-1998-predictions-in-2025-4791c6a8d31d)

## Safety

Immersive and flashing effects can cause discomfort. Always provide a hard **clear/sober** path. Session limits and soft landings expand in M4.

## Trademark note

Describe Cardboard **compatibility** factually. ELSD is not a Google product. Do not use restricted vintage platform/switcher brand names in UI — imply spirit only ([docs/design/TRADEMARK_SAFE_LANGUAGE.md](docs/design/TRADEMARK_SAFE_LANGUAGE.md)).

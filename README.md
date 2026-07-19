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

**M1 codebase landed:** WORLD bus (CameraX → GL), TOASTED composite shader (paint / pulse / LSD / key hooks), Bounce cursor, voice command grammar + SpeechRecognizer loop, mix state + presets.

| Milestone | Meaning |
|-----------|---------|
| M0 | Docs, stack, soul ✅ |
| M1 | Camera → GL → Bounce + voice grammar ✅ (build needs Android SDK) |
| M2 | Plate bus keying + trail FBO + wet polish |
| M3 | HLS, full banks, city pulse audio |
| M4 | Vosk offline, Bounce quips, TOASTED chrome |
| M5 | Stereo Cardboard NDK, v1.0.0 |

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
| [docs/design/SOUL.md](docs/design/SOUL.md) | Look, Bounce, TOASTED |
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

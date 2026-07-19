# ELSD — Electronic LSD

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Status](https://img.shields.io/badge/status-public%20alpha-blue.svg)](https://github.com/jasonzacherychristie-dev/ELSD)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://github.com/jasonzacherychristie-dev/ELSD)

**Open-source Android perception mixer** for a **large-tray Cardboard-compatible phone headset**.  
**Repository:** https://github.com/jasonzacherychristie-dev/ELSD · **Visibility: public** · **License: MIT**  
**House brand (informal):** Praxis Software

Live camera world + painter styles + cinema looks + trip geometry — mixed **TOASTED** (live desk soul), **voice + switchboard**, with **Amy**: a spirited checker-orb pilot that leans in when you talk.

**Hardware we build for:** **OnePlus 9** (floor) · **OnePlus 12** (ship) · large phone tray **6.5–7.0"**. Pixel-class phones welcome for desk testing.

> Buildings can throb. Windows can become TVs. Streets can go Van Gogh.  
> You talk. Bounce mixes. Say *clear* when you want the earth back.

## Mission

> Put a live perception mixer in everyone’s hands — open source, voice-first, and honest about wonder and risk.

Full statement: **[MISSION.md](MISSION.md)**  
Conduct: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) · Contributing: [CONTRIBUTING.md](CONTRIBUTING.md)

## Credits

| | |
|--|--|
| **House brand (informal)** | **Praxis Software** — from Jason Z. Christie’s 2012 fiction |
| **Project lead & research** | [Jason Z. Christie](https://jasonzchristie.blogspot.com/) |
| **Co-implementation** | **Grok (xAI)** via Grok Build |

> ELSD is a **Praxis Software** project by Jason Z. Christie, **co-implemented with Grok by xAI**.

Please keep that credit in forks, About screens, and demos. See [AUTHORS](AUTHORS) and [NOTICE](NOTICE).

## License

**MIT** for ELSD application code, project GLSL, and original docs — see [LICENSE](LICENSE).

Third-party libraries (Cardboard SDK, CameraX, Media3, Vosk, AndroidX, …) remain under **their own licenses** (mostly Apache-2.0). MIT is fully compatible; their notices stay in [NOTICE](NOTICE).

## Status

**Active release: 1.0** — **visuals only** on live camera: ELSD · Art · Cinema · Palette · Mood.  
**2.0** media in/out · **3.0** DeepDream · **4.0** time travel (any era).

| Release | Theme | Status |
|---------|--------|--------|
| **1.0** | Visual desk — camera + styles + mood + Amy + switchboard | **In progress** |
| **2.0** | Media in/out + key + ears + strike morph | After 1.0 |
| **3.0** | Dream machine — **DeepDream** | Wishlist |
| **4.0** | **Time travel** — local world → any era past/future | Horizon |

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

**ADD EFFECT** · **PRESETS** · **SAVE PREFS** · **RANDOM** · **SET FRAMERATE** · **ENABLE DROPPED FRAMES** · **GO LIVE**

Voice prefs: `save prefs walk` · `load prefs noir_night` · `list presets` · `delete preset walk`

Spec: [docs/design/SWITCHBOARD.md](docs/design/SWITCHBOARD.md) · [docs/design/PRESETS.md](docs/design/PRESETS.md) · [docs/design/FRAMERATE.md](docs/design/FRAMERATE.md)

## Target hardware

| | |
|--|--|
| **Headset** | **Large-tray Cardboard-compatible** phone VR viewer — fit **6.5–7.0"** (order for **OP12**; OP9 must fit) |
| **Floor (prove it)** | **OnePlus 9** — Snapdragon 888, 6.55". If it runs here, everything above sizzles. |
| **Ship (sizzle)** | **OnePlus 12** (~6.8") |
| **Desk / API check** | **Pixel 7** and peers — fine for flat testing |
| **Not required for 1.0** | Quest / Pico / PCVR |

Shopping rules + OP9 QA gates: **[docs/design/TARGET_HARDWARE.md](docs/design/TARGET_HARDWARE.md)**

## Build & deploy to a phone

Requirements:

- **Android Studio** (or SDK 35 + build-tools + platform-tools)
- **JDK 17+** (Studio’s JBR is fine)
- USB cable + **Developer options → USB debugging** on the phone
- Optional: large-tray headset (see above) for stereo later

**Full sideload process (Pixel · OP9 · OP12):** **[docs/DEV_DEPLOY.md](docs/DEV_DEPLOY.md)**

```powershell
# Windows — from repo root (ELSD/)
copy local.properties.example local.properties
# set sdk.dir + org.gradle.java.home

# Build debug APK
.\gradlew.bat :app:assembleDebug

# Or build + install on the connected phone
.\gradlew.bat :app:installDebug

# Helper (list devices / install / optional launch)
.\scripts\deploy-debug.ps1 -Launch
.\scripts\deploy-debug.ps1 -Serial YOUR_SERIAL -Launch
```

```bash
# macOS / Linux
./gradlew :app:assembleDebug
./gradlew :app:installDebug
adb shell am start -n com.elsd/.SwitchboardActivity
```

Grant **camera** (+ **mic** for voice). Landscape GO LIVE. Talk.

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
| [docs/design/FEATURES_1_0.md](docs/design/FEATURES_1_0.md) | **1.0 feature set** (Desk · Art · Film · Psychedelic) |
| [docs/design/SHADER_IMPORT.md](docs/design/SHADER_IMPORT.md) | How we import / massage shaders |
| [docs/design/ROADMAP.md](docs/design/ROADMAP.md) | **1.0 → 4.0** |
| [docs/design/VISUAL_SYSTEM.md](docs/design/VISUAL_SYSTEM.md) | Visual banks detail |
| [docs/design/STILLNESS_MORPH.md](docs/design/STILLNESS_MORPH.md) | Stare longer → morph budget |
| [docs/design/TIME_TRAVEL.md](docs/design/TIME_TRAVEL.md) | **4.0** — era transform of the local world |
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

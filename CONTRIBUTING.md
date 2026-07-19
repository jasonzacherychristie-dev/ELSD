# Contributing to ELSD

Thanks for helping build an open **perception mixer** for Android.

## Ground rules

1. **MIT** for ELSD code contributions (see `LICENSE`). Preserve third-party Apache notices.  
2. Keep the **TOASTED** spirit: real-time, composable, world-first, voice-first.  
3. Prefer small PRs: one effect, one bugfix, or one doc topic.  
4. No camera/mic data to the network unless explicitly designed, documented, and opt-in.  
5. Respect trademarks: Cardboard-**compatible**, not “official Google Cardboard app.”  
6. Keep **Grok (xAI)** and **Jason Z. Christie** credits in About / NOTICE when you fork publicly.

## Dev setup (target)

- Android Studio (latest stable)  
- JDK 17+  
- Physical phone for camera tests (lab: OP9 floor, OP12 ship, Pixel OK for desk)  
- Gyroscope recommended for later Cardboard stereo  
- Optional: **large-tray** Cardboard-compatible headset (6.5–7.0") — see `docs/design/TARGET_HARDWARE.md`  
- Deploy steps: `docs/DEV_DEPLOY.md` · `scripts/deploy-debug.ps1`

Upstream reference clones (not required to open a PR): see `docs/frameworks/CHECKOUTS.md`.

## How to propose changes

1. Open an issue describing the effect or bug (screenshots/video welcome).  
2. Fork + branch from `main`.  
3. Match existing Kotlin/GLSL style when present.  
4. For shaders: include parameter defaults and a preset snippet if user-facing.  
5. Ensure debug build still runs **flat preview** if stereo is unaffected.  
6. Update `NOTICE` if you add a dependency.

## Effect contributions

New filters should declare:

- Family: `key` | `pulse` | `paint` | `lsd`  
- Uniforms + ranges  
- Performance note (full-res / half-res / feedback?)  
- Safety: max flash rate / seizure-adjacent warnings if relevant  

## Code of conduct

Be kind. No harassment. Maintainers may reject contributions that weaponize immersive media or ignore consent/safety patterns (`clear` / session limits).

## Questions

Use GitHub Discussions/Issues once the remote is published; until then, coordinate with the project owner.

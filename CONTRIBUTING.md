# Contributing to ELSD

Thanks for helping build an open **perception mixer** for Android.

## Ground rules

1. **Apache-2.0** for code contributions (see `LICENSE`).  
2. Keep the **Toaster spirit**: real-time, composable, world-first.  
3. Prefer small PRs: one effect, one bugfix, or one doc topic.  
4. No camera/mic data to the network unless explicitly designed, documented, and opt-in.  
5. Respect trademarks: Cardboard-**compatible**, not “official Google Cardboard app.”

## Dev setup (target)

- Android Studio (latest stable)  
- JDK 17+  
- Device with gyroscope recommended for Cardboard mode  
- Optional: cardboard viewer  

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

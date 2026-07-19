# ELSD — Electronic LSD

**Open-source Android perception mixer** for Cardboard-compatible viewers.

Live camera world + keyed media (local/stream) + painter styles + real-world pulse visualizers + classic trip FX — mixed **TOASTED** (live desk soul), **voice only**, with **Bounce**: a spirited checker-orb cursor that can go spatial, break the fourth wall, or mute into the background.

> Buildings can throb. Windows can become TVs. Streets can go Van Gogh.  
> You talk. Bounce mixes. Say *clear* when you want the earth back.  
> Built for hobbyists. Inspired by 1998 research toward *Ultimate Reality*.

## Status

**Documentation / planning phase.** Framework bookmarks and local doc mirrors are in-tree. Application code milestones start at **M1** (see plan).

| Milestone | Meaning |
|-----------|---------|
| M0 | Docs, license, stack ✅ (in progress) |
| M1 | Camera → GL → Cardboard eyes |
| M2 | Luma key + media bus + trail |
| M3 | Chroma, paint, pulse, HLS |
| M4 | Voice + presets + safety |
| M5 | v1.0.0 open release |

## Docs map

| Doc | What |
|-----|------|
| [docs/design/VISION_1_0.md](docs/design/VISION_1_0.md) | Product vision |
| [docs/design/SOUL.md](docs/design/SOUL.md) | Look, feel, Bounce, TOASTED |
| [docs/design/VOICE_AND_CURSOR.md](docs/design/VOICE_AND_CURSOR.md) | Voice-only + cursor states |
| [docs/design/PROJECT_PLAN.md](docs/design/PROJECT_PLAN.md) | Modules & milestones |
| [docs/design/SOURCE_PAPERS.md](docs/design/SOURCE_PAPERS.md) | Research lineage |
| [docs/frameworks/STACK.md](docs/frameworks/STACK.md) | How libraries map to busses |
| [docs/frameworks/CHECKOUTS.md](docs/frameworks/CHECKOUTS.md) | Upstream clones |
| [docs/bookmarks/FRAMEWORKS.md](docs/bookmarks/FRAMEWORKS.md) | Link bookmarks |
| [docs/frameworks/raw/](docs/frameworks/raw/) | Fetched third-party doc mirrors |

## Planned open stack

- **Cardboard SDK** (stereo, tracking) — Apache-2.0  
- **CameraX** — world bus  
- **Media3 / ExoPlayer** — media bus (files + HLS)  
- **OpenGL ES 3** — key / pulse / paint / LSD graph  
- **Vosk** — offline voice T-bar  

Details: [docs/frameworks/STACK.md](docs/frameworks/STACK.md).

## License

Code: [Apache License 2.0](LICENSE).  
See [NOTICE](NOTICE) for third-party attribution.

**Trademark:** “Google Cardboard” is a trademark of Google LLC. This project is independent and only aims for viewer compatibility.

## Lineage

- [Toward Ultimate Reality — Electronic LSD and Virtual Displays](https://jasonzchristie.blogspot.com/2017/06/toward-ultimate-reality-electronic-lsd.html) (research paper)  
- [2025 revisit on Medium](https://medium.com/@JasonZChristie/toward-ultimate-reality-revisiting-jason-z-christies-1998-predictions-in-2025-4791c6a8d31d)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Shader and preset PRs will be first-class once M2 lands.

## Safety

Immersive and flashing effects can cause discomfort. Always provide a hard **clear/sober** path, session limits, and avoid seizure-unfriendly defaults.

# ELSD — Framework Bookmarks

Last updated: 2026-07-19  
Purpose: Head-start links for the **open-source Android** ELSD stack (Electronic LSD / Ultimate Reality 1.0).

Local mirrors of key pages live in [`../frameworks/raw/`](../frameworks/raw/). Summaries and how we use each library: [`../frameworks/STACK.md`](../frameworks/STACK.md).

---

## Tier 1 — Core 1.0 stack (we build on these)

| Role | Project | License | Docs / Code |
|------|---------|---------|-------------|
| Stereo VR / Cardboard | **Google Cardboard SDK** | Apache-2.0 | [GitHub](https://github.com/googlevr/cardboard) · [Get started](https://developers.google.com/cardboard/develop/get-started) · [C/NDK quickstart](https://developers.google.com/cardboard/develop/c/quickstart) · [C API ref](https://developers.google.com/cardboard/reference/c) |
| Camera (world bus A) | **CameraX** | Apache-2.0 | [Overview](https://developer.android.com/training/camerax) · [Architecture](https://developer.android.com/media/camera/camerax/architecture) · [OpenGL interop](https://developer.android.com/media/camera/camerax/opengl-interop) · [Samples](https://github.com/android/camera-samples) |
| Media bus B (file + stream) | **Media3 / ExoPlayer** | Apache-2.0 | [ExoPlayer](https://developer.android.com/media/media3/exoplayer) · [Hello world](https://developer.android.com/media/media3/exoplayer/hello-world) · [HLS](https://developer.android.com/media/media3/exoplayer/hls) · [GitHub androidx/media](https://github.com/androidx/media) |
| Real-time shaders / FX | **OpenGL ES 3.x** (native) | Platform | [About OpenGL ES](https://developer.android.com/develop/ui/views/graphics/opengl/about-opengl) · [GLSurfaceView](https://developer.android.com/reference/android/opengl/GLSurfaceView) |
| Optional Compose shaders | **AGSL RuntimeShader** | Platform | [Runtime shaders](https://developer.android.com/develop/ui/compose/graphics/draw/runtime-shaders) |
| Offline voice (T-bar) | **Vosk** | Apache-2.0 | [Android page](https://alphacephei.com/vosk/android) · [vosk-api](https://github.com/alphacep/vosk-api) · [vosk-android-demo](https://github.com/alphacep/vosk-android-demo) · [Models](https://alphacephei.com/vosk/models) |
| Online/system voice (fallback) | **SpeechRecognizer** | Platform | [API](https://developer.android.com/reference/android/speech/SpeechRecognizer) |
| Audio reactive pulse | **Visualizer** + optional **Oboe** | Platform / Apache-2.0 | [Visualizer](https://developer.android.com/reference/android/media/audiofx/Visualizer) · [Oboe](https://github.com/google/oboe) |

---

## Tier 2 — Strong optional accelerators

| Role | Project | License | Link |
|------|---------|---------|------|
| Full rendering engine | **Filament** | Apache-2.0 | [github.com/google/filament](https://github.com/google/filament) |
| Filter graph shortcuts | **GPUImage for Android** | Apache-2.0 | [cats-oss/android-gpuimage](https://github.com/cats-oss/android-gpuimage) (legacy but useful reference) |
| Low-latency audio I/O | **Oboe** | Apache-2.0 | [github.com/google/oboe](https://github.com/google/oboe) |
| ML helpers (later styles) | **MediaPipe** / **ML Kit** | Apache-2.0 / proprietary bits | Evaluate post-1.0 for neural styles |
| Depth / planes (later) | **ARCore** | Proprietary runtime | Optional; **not** required for 1.0 “fake pulse” |

---

## Tier 3 — Reference only (not 1.0 dependencies)

| Topic | Link |
|-------|------|
| Cardboard viewer manufacturing / lens params | [Manufacturers](https://developers.google.com/cardboard/manufacturers) |
| WebXR Cardboard path (prototype only) | [immersive-web](https://github.com/immersive-web) — we ship **native Android** for 1.0 |
| Psychedelic VR art (inspiration) | [Tripp](https://www.tripp.com/) · [SoundSelf](https://soundself.com/) — closed; inspiration only |
| Original research | [1998 paper (blog)](https://jasonzchristie.blogspot.com/2017/06/toward-ultimate-reality-electronic-lsd.html) · [2025 revisit (Medium)](https://medium.com/@JasonZChristie/toward-ultimate-reality-revisiting-jason-z-christies-1998-predictions-in-2025-4791c6a8d31d) |

---

## Android platform baseline (project assumptions)

| Item | Recommendation for ELSD 1.0 |
|------|-----------------------------|
| Language | **Kotlin** + small **C/C++ NDK** for Cardboard + shaders |
| Min SDK | **API 26** (Android 8.0) unless Cardboard sample forces higher |
| Target SDK | Current Play target (track annually) |
| IDE | Android Studio (latest stable) |
| Build | Gradle + Version Catalog (`libs.versions.toml`) |
| UI shell | Jetpack Compose (menus) + GL surface (eyes) |
| License for ELSD code | **MIT** (deps mostly Apache-2.0 — compatible; see NOTICE) |

---

## Upstream samples we may vendor or learn from

Do **not** copy without license headers. Prefer submodule or documented dependency.

1. [googlevr/cardboard](https://github.com/googlevr/cardboard) — `sdk/` + Android hello VR  
2. [android/camera-samples](https://github.com/android/camera-samples) — CameraX + analysis / OpenGL paths  
3. [androidx/media](https://github.com/androidx/media) — ExoPlayer demos, Surface / GL texture output  
4. [alphacep/vosk-android-demo](https://github.com/alphacep/vosk-android-demo) — offline command spotting pattern  
5. Google Filament samples — only if we adopt Filament later  

---

## Checkout status

| Repo | Needed for coding? | Status |
|------|--------------------|--------|
| `googlevr/cardboard` | **Yes** — stereo distortion + head tracking | **Recommended checkout / JitPack or AAR** |
| `androidx/media` | Dependency via Maven only | No full clone needed |
| `alphacep/vosk-api` + android demo | **Yes** for voice integration patterns | **Recommended shallow clone of demo** |
| `android/camera-samples` | Reference | Optional clone |
| `google/filament` | No for 1.0 | Skip until needed |
| `google/oboe` | Optional | Skip until Visualizer is insufficient |

See [`../frameworks/CHECKOUTS.md`](../frameworks/CHECKOUTS.md).

# ELSD — Project plan (active: **1.0**)

> **Release map:** [`ROADMAP.md`](ROADMAP.md) — **1.0** ship · **2.0** deepen · **3.0** dream (DeepDream wishlist).  
> This file is the **1.0 execution plan only**. Do not park 3.0 work in these checkboxes.

## 1.0 goals

1. Ship a **usable 1.0 APK** that demos KEY + PULSE + PAINT + LSD on target hardware.  
2. Keep the repo **easy to fork**: **MIT** app code; Apache deps in NOTICE.  
3. **TOASTED** live desk: wet/dry, world-first, composable banks.  
4. **Voice only** + **Amy** (lean-in, FLAT / SPATIAL / MUTED) — [`SOUL.md`](SOUL.md).  
5. Eyes **and** ears can hallucinate; `clear` dries both.  
6. Imply heritage; no forbidden brand names — [`TRADEMARK_SAFE_LANGUAGE.md`](TRADEMARK_SAFE_LANGUAGE.md).  
7. **No neural DeepDream in 1.0** — that is **3.0** ([`ROADMAP.md`](ROADMAP.md)).

## Hardware baseline

- **Target:** OnePlus 12–class phone + large-tray Cardboard-compatible viewer — [`TARGET_HARDWARE.md`](TARGET_HARDWARE.md)

## Recommended repo layout

```
ELSD/
  README.md
  LICENSE                 # MIT
  NOTICE
  MISSION.md
  docs/design/ROADMAP.md  # 1.0 / 2.0 / 3.0
  app/                    # Application (may split modules later)
  docs/
```

Logical packages inside `app`: `bus`, `gl`, `fx-*`, `audio`, `bounce` (Amy), `voice`, `mix`, later `cardboard`.

## Module responsibilities (1.0)

| Module | Owns |
|--------|------|
| `camera-bus` | WORLD OES texture |
| `media-bus` | PLATE (file/HLS) |
| `core-gl` / `EffectGraph` | TOASTED pass graph |
| `fx` shaders | KEY · PULSE · PAINT · LSD (GLSL only in 1.0) |
| `audio` | BandEnergy, spatial ears engine |
| `voice` | Grammar + SpeechRecognizer / Vosk |
| `bounce` | Amy cursor + lean-in |
| `cardboard` | Stereo distortion (1.0 if ready; flat OK for alpha) |
| `app` | Permissions, legends, safety — **no touch mixer** |

## 1.0 technical milestones

### M0 — Repo & docs ✅

- [x] Framework bookmarks, stack, soul, mission, public GitHub  
- [x] Target hardware, spatial audio design, roadmap 1/2/3  

### M1 — Hello eyes ✅ (partial)

- [x] App skeleton, GLES, CameraX WORLD  
- [x] Amy lean-in on speech  
- [ ] Cardboard stereo on target viewer  

### M2 — TOASTED minimum

- [ ] Media3 local video → PLATE texture  
- [ ] Luma key composite  
- [ ] Global wet/dry + true trail feedback FBO  
- [ ] Window TV preset demo  

**Exit:** Window TV in a controlled room.

### M2.5 — Hallucinations eyes + ears

- [x] BandEnergy + Visualizer/mic sources (skeleton)  
- [x] SpatialHallucinationEngine + grammar (control)  
- [ ] Bands → shader uniforms every frame  
- [ ] Spatial engine → real wet audio output  
- [ ] `clear` dries eyes and ears  

**Exit:** Bass has the building; something can move behind your head.

### M3 — RAD banks (still 1.0)

- [ ] Chroma key + color pick  
- [ ] HLS plate  
- [ ] Full paint + LSD preset set  
- [ ] Butterchurn-inspired warp (MIT original GLSL — **not** DeepDream)  

**Exit:** Gogh Walk + City Pulse outdoors.

### M4 — Amy, voice, polish

- [ ] Vosk or robust offline path  
- [ ] Amy SPATIAL + MUTED polish; quip budget  
- [ ] TOASTED chrome legends  
- [ ] Session timer + soft landing  
- [ ] About: Grok + Jason credits  

**Exit:** Stranger demo, voice only, &lt; 60s.

### M5 — Tag **v1.0.0**

- [ ] Stereo Cardboard path on target headset  
- [ ] CI assembleDebug  
- [ ] Demo clip + README  
- [ ] Tag `v1.0.0`  

---

## After 1.0 (do not expand checkboxes here)

| Release | See |
|---------|-----|
| **2.0** Deeper mixer | [`ROADMAP.md`](ROADMAP.md#20--deeper-mixer-after-10) |
| **3.0** Dream machine + **DeepDream mode** | [`ROADMAP.md`](ROADMAP.md#30--dream-machine-wishlist) |

## Open source process

| Practice | Choice |
|----------|--------|
| Host | https://github.com/jasonzacherychristie-dev/ELSD |
| Branch | `main` |
| Versioning | SemVer — `v1.0.0` / `v2.0.0` / `v3.0.0` |
| Labels | `1.0` `2.0` `3.0` `wishlist` `neural` |
| CoC | CODE_OF_CONDUCT.md |
| Security | SECURITY.md — no default cloud of camera/mic |

## Risk register (1.0)

| Risk | Mitigation |
|------|------------|
| Scope creep (DeepDream, dome, iOS) | Roadmap buckets; reject into `3.0`/`wishlist` |
| Thermal / FPS | Effect budget; half-res feedback |
| Cardboard NDK pain | Flat alpha ships; stereo hard-gate only at M5 |
| GPL analysis libs | MIT BandEnergy + platform Visualizer |
| Trademark | Cardboard-compatible wording only |

## Decision log

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Platform | Android first | Target hardware + open stack |
| Engine | GLES 3 | TOASTED control |
| 1.0 paint | Shader presets | Real-time, small APK |
| DeepDream | **3.0 wishlist** | Neural cost / focus |
| Spatial ears | 1.0 control + wet path | Dual wet soul |
| License | MIT | Fork-friendly shaders |

## Next actions (1.0 focus)

1. Android SDK + build debug on OnePlus 12 class device.  
2. Finish M2 Window TV (plate + luma key + trail FBO).  
3. Wire BandEnergy → uniforms + spatial → AudioTrack.  
4. Large-tray headset QA per TARGET_HARDWARE.  
5. Keep DeepDream issues labeled `3.0` `wishlist` only.

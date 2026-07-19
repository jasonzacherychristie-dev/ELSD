# Keying methods + phone / headset sensors

How we cut infinity into the FOV, and which **phone sensors** can drive the mix (including **motion-tied trails**).

Trademark-safe product words only in UI. Historical switcher key names appear here as research.

---

## A. Keying methods (visual)

### 1.0 — already designed / partial

| Method | Status | Notes |
|--------|--------|--------|
| **Fractal FOV key** | ✅ design + code path | CHROMA / DARKS / BRIGHTS / FULL — [`FRACTAL_FOV.md`](FRACTAL_FOV.md) |
| **Wet/dry + phase** | ✅ | Soft “key amount” without a plate |
| **Stillness morph ROI** | 🔲 tracker | Hold still → deeper morph in ROI — [`STILLNESS_MORPH.md`](STILLNESS_MORPH.md) |

### 2.0 product plate key (parked)

| Method | Era inspiration | ELSD use |
|--------|-----------------|----------|
| **Luma key** | Classic desktop switcher luminance keyer | Key bright/dark wall region → plate / trip |
| **Chroma key** | Green/blue screen | Pick hue → window to plate or fractal |
| **Linear / soft key** | Soft edge matte | Feather keyed edges for comfort in HMD |
| **Downstream key (DSK)** | CG / titles over PGM | Amy, captions, status — already overlay-shaped |
| **External / garbage matte** | Manual mask | Later: touch-drawn matte in FOV |
| **Difference / holdout matte** | Frame difference | Stillness cousin — motion vs static plate |
| **Depth key** | Depth-from-mono / ToF | 2.0–3.0; phone depth when available |
| **Semantic / person key** | ML matte | 3.0 dream path — not 1.0 |

### Desk color map as “soft key”

Luminance mapping (historical color processor) is not a keyer, but **zone grades** (dark→blue, mid→red) behave like **selective keys** without a second bus. See [`DESK_COLOR_MAP.md`](DESK_COLOR_MAP.md).

**1.0 recommendation:** ship fractal FOV keys + desk luma maps; keep true plate luma/chroma for 2.0.

---

## B. Phone / headset sensors (drive the mix)

Hardware target: large-tray Cardboard-class + OP9 floor / OP12 ship. Sensors are **free expression inputs** when the phone is in the tray.

### Motion (highest value for ELSD)

| Sensor / API | What it gives | ELSD use ideas |
|--------------|---------------|----------------|
| **Gyroscope** | Angular velocity | **Trail streak direction** · melt warp · kaleido spin bias · Amy lean already speech-related |
| **Accelerometer** | Linear accel / shake | Trail burst on nod · wet punch · freeze-frame “clap” |
| **Rotation vector / game rotation** | Fused orientation | Cardboard head pose · **view-dependent trail lag** · parallax grade |
| **Gravity** | Up vector | Keep horizon-aware vertical mirrors · “upright” desk toys |
| **Linear acceleration** (no gravity) | True motion | Step bob → subtle wet pulse |

**Hero 1.0 idea — motion trails:**

```
trail UV offset += k * gyro.yz * wet
trail decay   *= 1 - clamp(|gyro| * d, 0, 0.5)   // whip clears or stretches
```

- Look left → afterimage smears right (or opposite — pick one and lock).  
- Still head → trails settle (pairs with stillness morph).  
- Shake → short **strobe / freeze** option (historical freeze button spirit).

`FramePacer.navBoost` already mentions motion → min fps; wire the same motion sample to **visual** wet, not only cadence.

### Environment

| Sensor | ELSD use |
|--------|----------|
| **Light / illuminance** | Auto mood night/day · key threshold for darks/brights fractal |
| **Proximity** | Headset on/off → pause heavy FX / CLEAR safety |
| **Ambient temperature** (if exposed) | Thermal honesty HUD — not a look driver |
| **Magnetometer** | Optional compass “north lock” for spatial audio later; low priority for visuals |
| **Barometer** | Ignore for 1.0 |

### Camera-as-sensor (not just WORLD texture)

| Signal | ELSD use |
|--------|----------|
| **WORLD frames** | Primary bus |
| **Exposure / ISO / AE state** (CameraX) | Drive auto wet / noise grain |
| **Face / subject ROI** (optional ML Kit later) | Soft key center; 2.0+ |
| **Dual camera / depth** (device-dependent) | Depth key 2.0 |

### Audio (ears path)

| Signal | ELSD use |
|--------|----------|
| **Mic / BandEnergy** | 2.0 ears product; bass→wet already in stack design |
| **Voice commands** | 1.0 switchboard verbs |

### Haptics / other

| Signal | ELSD use |
|--------|----------|
| **Vibrator** | Amy ack only — not a visual driver |
| **Touch / volume keys** | Board shortcuts when out of headset |

---

## C. Suggested wiring (implementation sketch)

```
MotionSample (gyro, rot, accel)  ──► MixState.motionVec, motionEnergy
StillnessTracker                 ──► stillScore (existing)
LightSensor (optional)           ──► MixState.ambientLux

toasted_composite.frag:
  uMotion (vec2/vec3)
  trail / melt / kaleido_spin sample uMotion
  optional: uLux shifts fractal key thresholds
```

**Permissions:** camera (have) · microphone (2.0 / voice) · **no special permission** for gyro/accel/light on modern Android (use when available; degrade gracefully).

**Safety:** high `motionEnergy` → optional CLEAR / reduce wet (match outdoor conservative defaults in roadmap).

---

## D. Priority

| # | Item | Release |
|---|------|---------|
| 1 | Motion → trail UV / decay | **1.0** |
| 2 | Motion → kaleido_spin bias + melt | 1.0 |
| 3 | Ambient light → darks/brights key bias | 1.0 optional |
| 4 | Proximity → headset present | 1.0 polish |
| 5 | Desk luma map engine (`solarize`, strips) | 1.0 |
| 6 | Plate luma/chroma key | **2.0** |
| 7 | Depth / semantic key | 2.0–3.0 |

---

## Related

- [`FRACTAL_FOV.md`](FRACTAL_FOV.md) · [`DESK_COLOR_MAP.md`](DESK_COLOR_MAP.md) · [`FRAMERATE.md`](FRAMERATE.md) · [`STEREO_3D.md`](../frameworks/STEREO_3D.md) · [`TARGET_HARDWARE.md`](TARGET_HARDWARE.md)

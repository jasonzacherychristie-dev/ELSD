# Stillness morph — focus earns the dream

**Insight:** Real-time full-field neural morph is expensive.  
**But** when the user (and the world under their gaze) **hold still**, we get free cycles — and permission psychologically: the mind is already staring.

> The longer they remain still and focus on still objects, the more CPU/GPU we invest in striking morphs of the video stream.

Not a separate app mode only — a **progressive intensity envelope** on top of the visual desk.

---

## Product feel

| User state | System behavior |
|------------|-----------------|
| Walking, panning, busy scene | Light path only — ELSD/ART/CINEMA/PALETTE/MOOD at normal cost |
| Phone steady + scene still (short hold) | Begin **soft morph** — feedback deepen, palette breathe, micro-warp |
| Hold continues on a still subject | **Strike morph** — stronger structure, pareidolia-friendly sharpen/warp |
| Long meditative hold | Optional **dream layer** (2.0/3.0) — DeepDream-class or multi-pass heavy |
| Motion returns | **Snap or soft-land** back to light path within ~100–300 ms |

Amy can lean *in* when they speak; the **world** can lean *into dream* when they stare.

Voice (optional): **STILLNESS ON/OFF**, **DREAM HOLD ON**, **CLEAR** always aborts morph stack.

---

## Signals (what “still” means)

### 1. Device still (user / headset)

| Signal | Source |
|--------|--------|
| Gyro magnitude low | `Sensor.TYPE_GYROSCOPE` |
| Accel variance low | `Sensor.TYPE_LINEAR_ACCELERATION` or gravity-removed |
| Optional: Cardboard head pose delta | when stereo |

**`userStill`:** EMA of motion &lt; threshold for N ms.

### 2. Scene still (objects)

| Signal | Source (1.0-friendly) |
|--------|------------------------|
| Frame difference energy | Downscaled luma absdiff vs previous |
| Optional optical flow magnitude | Later / half-res |
| Optional face/ROI stability | 2.0 |

**`sceneStill`:** mean absdiff &lt; threshold; reject if camera exposure hunting spikes.

### 3. Focus proxy (without eye tracking)

Phone-in-Cardboard rarely has eye tracking. **Focus ≈ center-weighted still:**

- Weight frame-diff lower in center crop (where attention usually sits)  
- Optional: user holds Amy / reticle on a region (later)  
- Optional: “STARE” mode — force treat center as ROI  

**`focusStill`:** center ROI still longer than periphery (or whole frame still).

### Combined score

```
stillScore = userStill * sceneStill * focusStill   // each 0..1
holdSec    = time stillScore > enterThreshold
morphBudget = f(holdSec, thermal, battery, targetFps)
```

Hysteresis: enter after ~0.4–0.8 s still; exit motion within 1–2 frames.

---

## Morph budget → visual investment

Map `holdSec` / `morphBudget` to **levels** (not one binary DeepDream):

| Level | Hold (approx) | Investment | Look |
|-------|----------------|------------|------|
| **L0 Live** | moving | baseline shaders only | desk as set |
| **L1 Settle** | 0.5–2 s | + feedback gain, slight melt, palette breathe | world softens |
| **L2 Structure** | 2–5 s | multi-pass edge/structure warp, stronger ART/CINEMA wet | forms crawl |
| **L3 Strike** | 5–15 s | heavy FBO recursion, kaleido/structure mix, higher internal res on ROI | striking morph |
| **L4 Dream** | 15 s+ or explicit | DeepDream / neural octave on ROI or full frame at low fps | 3.0 / optional 2.0 lite |

**Key:** L3 can look “DeepDream-adjacent” with **pure GLSL recursion** (iterative warp + edge-guided feedback) without a CNN — doable on 1.0–2.0.  
True CNN DeepDream remains **3.0** but **triggered by the same stillness engine**.

---

## Architecture

```
Camera frame
    │
    ├─► light PGM path (always ≤ N ms) ──► display
    │
    └─► StillnessTracker (gyro + frameDiff)
              │
              ▼
        MorphBudgetController
              │
              ├─ uniforms: uHoldSec, uMorphLevel, uRoiStill
              ├─ enable extra FBO passes only if budget allows
              └─ optional DreamWorker (async, 2.0/3.0) → blend when ready
```

**Hard rule:** Display path never blocks on dream worker.  
If heavy pass misses deadline → drop to previous level; never jank the headset.

### Thermal

- `PowerManager` / thermal status → cap max level  
- After L3, prefer **ROI-only** morph (center 50%) to save cycles  
- Stillness is when we *have* cycles; heat is when we *refuse* them

---

## Switchboard integration

| Verb / control | Role |
|----------------|------|
| **ADD EFFECT STILLNESS** or global **STILLNESS MORPH** toggle | Master enable |
| Layer wet on ELSD/ART still multiplies with morph level | |
| **PHASE** can sync with hold (morph swells while still) | |
| **CLEAR** | holdSec = 0, level = L0 immediately |

Preset example: `stare_dream` — stillness on, L2 max, soft_glow + melt.

---

## Release placement

| Piece | Release |
|-------|---------|
| StillnessTracker (gyro + frameDiff) | **1.0** if cheap, else early **2.0** |
| L0–L2 GLSL progressive morph | **1.0 stretch / 2.0** |
| L3 heavy FBO strike | **2.0** |
| L4 neural DeepDream on hold | **3.0** (same trigger) |
| Live video in/out | **2.0** (stillness works on plate too later) |

---

## Why this fits ELSD soul

- **Electronic LSD** was always about perception over time — stare and the world changes.  
- **Honest compute:** we morph hardest when motion doesn’t demand low latency.  
- **Safety:** motion or CLEAR snaps back; no trapping in a dream when you turn your head to walk.  
- **Amy line (jokes on):** *“Keep looking. I’m getting ideas.”* / on break: *“Thought so.”*

---

## Implementation sketch (packages)

```
elsd.vision.StillnessTracker   // sensors + frame energy
elsd.vision.MorphBudget        // holdSec → level + pass count
elsd.gl.MorphStack             // optional FBO passes gated by level
// 3.0: elsd.dream.DeepDreamWorker
```

Uniforms into `toasted_composite` / later multi-pass:

- `uHoldSec`, `uMorphLevel` (0–4), `uStillScore`

---

## Success test

1. Hold phone still on a lamp or poster.  
2. After ~1 s, image begins soft morph.  
3. After several seconds, morph becomes **striking** without app freeze.  
4. Pan quickly → back to clean live feel almost immediately.  
5. CLEAR → sober.

That’s the feature — **attention is the render budget.**

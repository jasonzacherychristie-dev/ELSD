# Framerate toggle & dropped frames

**Breathing room for the engine. A look for the eyes.**

ELSD is not only “max FPS forever.”  
A **framerate dial** plus **Enable Dropped Frames** lets us:

1. **Push limits** — stillness morph, heavy CINEMA, future dream/era passes get headroom  
2. **Change feel** — 12 fps silent reel, 24 cinema, 30 broadcast, 60 live glass  
3. **Stay honest on thermal** — OP12 in a sealed headset cooks; dropping is a feature  

---

## Product controls (simple vocabulary)

| Verb | Meaning |
|------|---------|
| **SET FRAMERATE &lt;n&gt;** | Cap display path to *n* fps (e.g. 12, 18, 24, 30, 60) |
| **ENABLE DROPPED FRAMES** | Allow the engine to **intentionally** skip frames under load (and/or hold last frame for aesthetic cadence) |
| **DISABLE DROPPED FRAMES** | Prefer smooth; may auto-reduce morph level instead of skipping |
| **FRAMERATE OFF** / **UNLOCK FRAMERATE** | Run as fast as path allows (device max) |

Switchboard: global strip on main board (not buried) — same words as voice.

---

## Two different “drops”

| Mode | Behavior | Look |
|------|----------|------|
| **Aesthetic cadence** | Always present frames only every `1/fps` sec; **hold** last frame between | Staccato, filmic, cartoon step |
| **Budget drops** | Target fps *or lower* when over budget; skip work + show last good PGM | Keeps latency/thermal safe when morph is heavy |

**ENABLE DROPPED FRAMES** turns on **budget drops** (and may strengthen aesthetic hold).  
**SET FRAMERATE** sets the **cadence ceiling** (and target for budget).

Recommended presets:

| Label | FPS | Dropped | Feel |
|-------|-----|---------|------|
| LIVE GLASS | 60 | off | Modern AR (OP12; OP9 only when light effects) |
| BROADCAST | 30 | on | Desk / TOASTED default |
| CINEMA | 24 | on | Feature film — **OP9 heavy-preset default** |
| SILENT REEL | 12–16 | on | Hand-crank cousin |
| STARE HEAVY | 12–20 | **on** | Morph/dream headroom — **OP9 fractal dives** |

Pair with CINEMA **SILENT ERA** at 12–16 fps for free authenticity.

---

## Interaction with stillness morph

```
high morph level + ENABLE DROPPED FRAMES
  → more FBO/neural budget per displayed frame
  → lower display rate is OK while user is still

user moves
  → optionally boost fps briefly (clarity for navigation)
  → or keep locked if user set hard FRAMERATE
```

Policy flag: **NAV BOOST** (default on) — when `stillScore` low, temporarily raise fps floor for safety.

---

## Implementation sketch

| Piece | Role |
|-------|------|
| `FramePacer.targetFps` | 0 = unlocked; else 12/18/24/30/60 |
| `FramePacer.allowDroppedFrames` | budget skip + present last |
| `FramePacer.navBoost` | motion → temporary min fps |
| GL thread | `if (!shouldPresent(now)) { skip heavy passes; egl swap last or skip }` |
| MorphBudget.maxLevel | raised when fps cap low + drops on |

**Display:** On Android `GLSurfaceView`, control via render gate (don’t call full graph every vsync) or `Choreographer` frame callback with skip.

---

## 1.0 scope

| In 1.0 | Later |
|--------|--------|
| SET FRAMERATE presets + unlock | Per-layer fps |
| ENABLE/DISABLE DROPPED FRAMES | Adaptive only (no user control) as optional default |
| Hold-last-frame aesthetic | Motion-blur compensation |
| Wire to morph budget headroom | Cloud era 4.0 at 4–8 fps display |

---

## Safety

- Outdoor walk + very low fps can disorient — default **30 + drops on** not 8  
- CLEAR does not reset fps (user choice) unless “reset all”  
- Document seizure note: strobing low fps + flicker effects stack carefully  

---

## Success test

1. SET FRAMERATE 12 + ENABLE DROPPED FRAMES + SILENT ERA → hand-crank world  
2. SET FRAMERATE 24 + SUSPIRIA → theatrical pulse  
3. STILLNESS MORPH on + 12 fps + drops → heavier morph without jank death  
4. UNLOCK + DISABLE DROPPED → smooth modern passthrough  

**One line:** *Fewer frames, more imagination per frame.*

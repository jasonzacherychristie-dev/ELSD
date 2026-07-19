# ELSD 1.0 — overall feature set

**Scope:** Visuals only · camera WORLD · switchboard + voice · OP9 floor / OP12 ship.  
**Not 1.0:** live video in, stream out, plate key product path, ears, DeepDream (3.0), time travel (4.0).

---

## User-facing banks (sifted)

Four creative banks + two globals.  
**Code families** match these names (see `EffectId`).

### 1. DESK — canonical live-desk FX  
*(Toaster-class spirit: real-time bus toys — imply, don’t brand)*

| ID | Status | Notes |
|----|--------|--------|
| `trail` | ✅ | Afterimage / feedback |
| `hue` | ✅ | Hue rotate |
| `split` | ✅ | RGB channel split |
| `negative` | ✅ | **Shader import pilot** — invert |
| `posterize` | ✅ | Quantize levels |
| `mirror` | ✅ | Horizontal mirror (L→R) |
| `mirror_v` | ✅ | Vertical mirror (T→B) |
| `mirror_quad` | ✅ | Four-fold desk fold |

### 2. ART — art styles

| ID | Status |
|----|--------|
| `gogh` | ✅ |
| `monet` | ✅ |
| `sketch` | ✅ |
| `comic` | ✅ |
| `cartoon` | ✅ |
| `hyperreal` | ✅ |
| `melt_paint` | ✅ |

### 3. FILM — film / cinema styles  
*(Noir-inspired one-word worlds + cadence)*

| ID | Status | Default FPS soft |
|----|--------|------------------|
| `noir` | ✅ | 24 |
| `neon` | ✅ | 30 |
| `bleach` | ✅ | 24 |
| `teal_orange` | ✅ | 24 |
| `anamorphic` | ✅ | 24 |
| `soft_glow` | ✅ | 24 |
| `technicolor` | ✅ | 24 |
| `suspiria` | ✅ | 24 |
| `silent_era` | ✅ | 12 |
| `expressionist` | ✅ | 16 |
| `giallo` | ✅ | 24 |
| `golden_age` | ✅ | 24 |

### 4. PSYCHEDELIC — trip geometry / infinity

| ID | Status | Notes |
|----|--------|--------|
| `kaleido` | ✅ | 6-fold default |
| `kaleido_4` / `_8` / `_12` | ✅ | Segment variants |
| `kaleido_spin` | ✅ | Rotating 6-fold |
| `tri_mirror` | ✅ | 3-fold crystal |
| `melt` | ✅ | Domain warp |
| `mandelbrot` | ✅ | Zoom rate + FOV key |
| `julia` | ✅ | Room steers seed |
| `stillness_morph` | 🔲 tracker | Budget engine; light L1 stretch |

**Hero stack:** mandelbrot + zoom rate + chroma FOV key + trail — [`FRACTAL_FOV.md`](FRACTAL_FOV.md).

### Globals (not “looks,” but 1.0 musts)

| System | Status |
|--------|--------|
| **PALETTE** (analog/digital medium) | ✅ film, vhs, digital clean/harsh, polaroid |
| **MOOD** (one overall toggle) | ✅ neutral, calm, warm, cold, night, fever, toasted |
| **Switchboard** | ✅ single page, drill-down, phase times |
| **PRESETS / USER SAVES** | ✅ factory + user dirs; switchboard hub; prefs voice — [`PRESETS.md`](PRESETS.md) |
| **RANDOM** | ✅ roll board — voice: random / surprise me / shuffle / chaos… |
| **SET FRAMERATE / DROPPED FRAMES** | ✅ film cadence + budget |
| **Amy** | ✅ lean + **bounce/roll/teleport/hop/spin/ack/pop**; mass **ACTIONS ON/OFF** — [`AMY_RENDER.md`](AMY_RENDER.md) |
| **Voice** = switchboard verbs | ✅ |
| **CLEAR / sober** | ✅ |
| **GO LIVE** (camera PGM) | ✅ |
| **Cardboard stereo** | 🔲 M5 |
| **Stillness L2+ / neural** | 2.0 / 3.0 |

---

## Layer order (render)

```
WORLD (camera)
  → PALETTE
  → MOOD
  → ART
  → FILM
  → DESK / PSYCHEDELIC stack (fractal key, trails, trip)
  → Amy
  → PGM
```

---

## Explicitly out of 1.0

| Feature | When |
|---------|------|
| Live video / plate **in** | 2.0 |
| Stream / record **out** | 2.0 |
| Product luma/chroma key of plate | 2.0 |
| Spatial ears | 2.0 |
| DeepDream | 3.0 |
| Time travel / era reskin | 4.0 |
| Fat APK (models, asset packs) | Never as silent default — see size budget |

---

## APK size budget (1.0)

Keep the craft small. Full table: [`PROJECT_PLAN.md`](PROJECT_PLAN.md).

| | Limit |
|--|--------|
| **Release hard cap** | **≤ 25 MB** |
| **Release soft target** | **≤ 15 MB** |
| **Debug** | **≤ 30 MB** |

Shaders stay in one composite pass (~tens of KB). Growth risk is **deps + ML/speech models**, not GLSL.

---

## Success (1.0 ship bar)

On **OnePlus 9**: switchboard → load `noir_night` or `mandel_key_trails` → GO LIVE → clear.  
On **OnePlus 12**: same, feels smoother.  
Stranger &lt; 60s to a look they can name.  
**Release APK ≤ 25 MB** (prefer ≤ 15 MB).

---

## Related

- Shader intake: [`SHADER_IMPORT.md`](SHADER_IMPORT.md)  
- Visual detail: [`VISUAL_SYSTEM.md`](VISUAL_SYSTEM.md)  
- Desk color map (luma treatments): [`DESK_COLOR_MAP.md`](DESK_COLOR_MAP.md)  
- Keying + sensors (motion trails): [`KEYING_AND_SENSORS.md`](KEYING_AND_SENSORS.md)  
- Roadmap: [`ROADMAP.md`](ROADMAP.md)  

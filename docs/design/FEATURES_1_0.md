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
| `posterize` | ✅ | Quantize levels (simple follow-on) |
| `mirror` | ✅ | Horizontal mirror blend |

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
| `kaleido` | ✅ | |
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
| **SAVE / LOAD PRESET** | ✅ JSON + factory seeds |
| **SET FRAMERATE / DROPPED FRAMES** | ✅ film cadence + budget |
| **Amy** lean-in | ✅ procedural quad (not live 3D mesh) — [`AMY_RENDER.md`](AMY_RENDER.md) |
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

---

## Success (1.0 ship bar)

On **OnePlus 9**: switchboard → load `noir_night` or `mandel_key_trails` → GO LIVE → clear.  
On **OnePlus 12**: same, feels smoother.  
Stranger &lt; 60s to a look they can name.

---

## Related

- Shader intake: [`SHADER_IMPORT.md`](SHADER_IMPORT.md)  
- Visual detail: [`VISUAL_SYSTEM.md`](VISUAL_SYSTEM.md)  
- Roadmap: [`ROADMAP.md`](ROADMAP.md)  

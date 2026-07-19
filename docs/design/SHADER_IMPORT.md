# Shader import process (1.0)

How we take an external GLSL idea and land it as an ELSD switchboard effect **without breaking the desk**.

---

## Goals

1. **Repeatable** — same steps every effect  
2. **License-safe** — MIT/Apache/CC-BY with attribution; rewrite if needed  
3. **Pipeline-fit** — WORLD UV, `uWet`, wet/dry, no full-screen hijack unless intentional  
4. **OP9-survivable** — budget loops, respect framerate drops  
5. **Catalogued** — EffectId + bank + preset optional  

---

## Pipeline contract (must obey)

| Uniform / input | Meaning |
|-----------------|--------|
| `uWorld` | `samplerExternalOES` live camera |
| `vUv` / `uStMatrix` | Corrected sample coords |
| `uTime` | Seconds |
| `uWet` | 0..1 global (and layer envelope later) |
| Optional params | `uRate`, key modes, etc. |

**Output:** `vec3` color for this stage, then  
`mix(previous, effectColor, uWet)` (or key mask × wet).

**Do not:** assume desktop GL, `texture2D`, random mouse, audio FFT unless wired.

---

## Banks (where does it go?)

| Bank | Put here if… |
|------|----------------|
| **DESK** | Live-desk / Toaster-class utility (negative, trail, split, posterize) |
| **ART** | Named art look (Gogh, cartoon…) |
| **FILM** | Cinema grade / era / director color |
| **PSYCHEDELIC** | Trip geometry, fractals, melt, kaleido |
| **PALETTE** | Whole-signal medium (VHS, film stock) |
| **MOOD** | Single global weather |

---

## Process checklist

### 0. Intake

- [ ] Name working title  
- [ ] Source URL / author  
- [ ] License (paste into `docs/shaders/import/<id>/SOURCE.md`)  
- [ ] Target bank  
- [ ] Complexity: **simple** / medium / heavy (OP9?)  

### 1. Normalize

- [ ] Convert to **GLSL ES 3.00** (`#version 300 es`, `in`/`out`, `texture()`)  
- [ ] Replace input with `texture(uWorld, uv)`  
- [ ] Remove mouse; map to `uTime` / world luma / `uRate`  
- [ ] Cap loops (e.g. fractal ≤ 48–56 on mobile)  

### 2. Massage to ELSD

- [ ] Pure function `vec3 applyXxx(vec3 c, vec2 uv)`  
- [ ] Respect `uWet`  
- [ ] No writing globals; no second framebuffer unless FBO pass approved  
- [ ] Optional: chromakey composite helper (see fractals)  

### 3. Wire

- [ ] `EffectId` + family  
- [ ] Index in `EffectGraph` (`paintIndex` / `lsdIndex` / desk index)  
- [ ] Call site in `toasted_composite.frag` stage order  
- [ ] Switchboard appears via `catalog1_0()`  
- [ ] Optional factory preset  
- [ ] Voice alias if natural (`add negative`)  

### 4. Verify

- [ ] Compiles on device (OP9 preferred)  
- [ ] CLEAR / wet 0 returns to identity  
- [ ] With FRAMERATE 24 + drops, no multi-second stall  
- [ ] Screenshot before/after in import folder (optional)  

### 5. Document

- [ ] One line in `FEATURES_1_0.md` status  
- [ ] `SOURCE.md` attribution  
- [ ] Note any param (`rate`, key mode)  

---

## Template layout

```
docs/shaders/import/<effect_id>/
  SOURCE.md          # origin + license + massage notes
  snippet.glsl       # applyXxx only (optional archive)
```

---

## Pilot: NEGATIVE

| Field | Value |
|-------|--------|
| **ID** | `negative` |
| **Bank** | DESK |
| **Why pilot** | Trivial math, full process path, classic desk invert |
| **Source** | Original ELSD (public domain / MIT project) — no third-party |
| **Status** | ✅ process test |

See `docs/shaders/import/negative/SOURCE.md`.

### Next candidates (after pilot)

1. **posterize** — still simple, very Toaster  
2. **mirror** — UV only  
3. External CC0 Shadertoy **only if** rewritten to contract  

---

## Anti-patterns

- Dropping a 200-line Shadertoy full-screen main() into the repo  
- Ignoring wet/dry  
- GPL code in core without license change  
- Heavy loops without framerate policy  

---

## Success

A new contributor can add **POSTERIZE** in one PR following this doc without asking “where does this go?”

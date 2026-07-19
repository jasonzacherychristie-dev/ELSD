# Visual system — 1.0 focus

**1.0 is eyes only.** Camera WORLD → switchboard layers → PGM.  
No live plate ingest, no stream-out, no spatial-ear product path in the critical 1.0 track.

| Release | Visuals | Media / IO |
|---------|---------|------------|
| **1.0** | ELSD · Art · Cinema · Palette · Mood | Camera only |
| **2.0** | + richer FBO, depth-pop optional | **Live video in, stream out, keying** |
| **3.0** | + DeepDream / neural | Dream machine |

---

## Switchboard families (1.0)

Simple vocabulary still: **ADD EFFECT**, **REMOVE EFFECT**, …  
Catalog is grouped so ADD EFFECT feels like choosing a **look**, not a protocol.

### 1. ELSD

Electronic LSD — trip geometry and time.

| ID | Feel |
|----|------|
| `trail` | Afterimage / feedback |
| `hue` | Hue rotate |
| `split` | RGB split |
| `kaleido` | Kaleidoscope |
| `melt` | Domain warp melt |

### 2. ART (art styles)

Painterly / graphic looks (GLSL, not neural).

| ID | Feel |
|----|------|
| `gogh` | Swirl, saturation |
| `monet` | Soft dabs |
| `sketch` | Ink / paper |
| `comic` | Hard ink + poster (optional) |
| `melt_paint` | Soft painterly melt |

### 3. CINEMA (cinematic styles)

Shot language, grade, lens character.

| ID | Feel |
|----|------|
| `noir` | High contrast B&W |
| `neon` | Night cyber teal/magenta |
| `bleach` | Bleach-bypass harsh mids |
| `teal_orange` | Blockbuster grade |
| `anamorphic` | Horizontal streak / squeeze vibe (shader) |
| `soft_glow` | Dream diffusion highlights |

### 4. PALETTE (analog ↔ digital)

Overall signal character — “what medium is this world on?”

| ID | Feel |
|----|------|
| `analog_film` | Grain, slight halation, soft roll-off |
| `analog_vhs` | Soft chroma, tracking noise, warm crush |
| `digital_clean` | Crisp, modern log-ish neutral |
| `digital_harsh` | Crushed blacks, edge enhance, cool |
| `polaroid` | Instant warm shift + vignette |

Only **one palette** fully owns the base grade at a time (last ADD wins, or single-select on board).

### 5. MOOD (overall toggle)

Global emotional weather — multiplies / steers the whole PGM.  
**One mood active** (toggle set). Not stacked like ELSD trails.

| ID | Feel |
|----|------|
| `mood_neutral` | Off / clear mood |
| `mood_calm` | Lower contrast, cool soft |
| `mood_warm` | Golden lift |
| `mood_cold` | Blue shadows |
| `mood_night` | Crushed, teal lift in darks |
| `mood_fever` | Hot saturation, slight pulse |
| `mood_toasted` | Pride wet default — desk “on air” warmth |

Voice / UI: **SET MOOD WARM**, **MOOD OFF**, or ADD EFFECT that replaces mood.

---

## Layering order (render)

```
WORLD (camera)
  → PALETTE (base medium)
  → MOOD (global grade)
  → ART (optional style)
  → CINEMA (optional shot look)
  → ELSD (trip on top)
  → Amy overlay
  → PGM
```

Wet/dry and phase envelopes still apply per layer where it makes sense; mood/palette are usually slow fades (ADD EFFECT TIME).

---

## 2.0 (parked — do not build now)

- Live **video in** (plate bus, HLS, files as second bus)  
- **Stream out** / cast / record PGM  
- Luma/chroma **key** of plate into world  
- Spatial **ears**, audio-reactive pulse as product focus  
- Depth-pop stereo enhance  

Those keep their design docs; they are not 1.0 ADD EFFECT groups.

---

## 3.0

- **DeepDream** and neural style — [`ROADMAP.md`](ROADMAP.md)

---

## Factory presets (visual-only)

| Preset | Layers |
|--------|--------|
| `gogh_walk` | ART gogh + ELSD trail + mood warm |
| `noir_night` | CINEMA noir + PALETTE digital_harsh + mood night |
| `vhs_fever` | PALETTE analog_vhs + ELSD hue + mood fever |
| `clean_calm` | PALETTE digital_clean + mood calm |
| `toasted_desk` | mood toasted + ELSD trail light + CINEMA soft_glow |

---

## Product sentence

**1.0:** Point the phone at the world. Paint it, grade it, trip it, set the mood.  
**2.0:** Pipe other video in and out.  
**3.0:** Dream.

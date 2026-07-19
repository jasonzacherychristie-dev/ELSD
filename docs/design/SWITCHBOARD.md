# Switchboard — single UI page

ELSD has **one** product UI surface: the **SWITCHBOARD**.  
Drill down into a layer. Come back. No settings maze.

**In the headset:** voice uses the **same vocabulary** (ADD EFFECT, REMOVE EFFECT, …).  
**On the glass (phone out / flat):** the switchboard is touchable so you can build a board before you go live.

Amy and the world stay the show. The board is the desk.

---

## Vocabulary (must stay tiny)

All user-facing actions use this lexicon (UI labels + voice):

| Verb | Meaning |
|------|---------|
| **ADD EFFECT** | Put a layer on the board (or enable if already present) |
| **REMOVE EFFECT** | Take a layer off the board |
| **TOGGLE EFFECT** | On ↔ off without removing |
| **ADD EFFECT TIME** | Set **fade-in** seconds for that effect |
| **REMOVE EFFECT TIME** | Set **fade-out** seconds |
| **PHASE TIME** | Set how long one phase cycle lasts (in→hold→out or LFO period) |
| **PHASE ON / PHASE OFF** | Enable/disable timed phasing for that layer |
| **SAVE PRESET** | Write current board to a named preset |
| **LOAD PRESET** | Replace board from a named preset |
| **CLEAR BOARD** | Remove all effects (soft sober of layers; not full STOP) |
| **PRESETS** | Hub: factory + **user saves**, load, delete user |
| **SAVE PREFS** | Write current board to USER save |
| **LOAD PREFS** / load row | Restore board (user preferred over factory) |
| **LIST PRESETS** | Voice/status summary |
| **DELETE PRESET** | User saves only |
| **RANDOM** | Roll a coherent random board (mood + palette + looks) |
| **SET FRAMERATE** | Cap cadence (12 / 16 / 24 / 30 / 60 / unlock) — **film language** |
| **ENABLE DROPPED FRAMES** | Allow budget + aesthetic frame holds |
| **DISABLE DROPPED FRAMES** | Prefer smooth; less morph headroom |
| **UNLOCK FRAMERATE** | No cap (cinema ADD won't auto-set fps until you pick a number again) |

Adding **CINEMA** effects soft-sets fps (silent→12, feature→24, neon→30) unless unlocked.
| **GO LIVE** | Open PGM view (camera / Cardboard session) |
| **BACK** | Leave drill-down; return to board list |

Framerate design: [`FRAMERATE.md`](FRAMERATE.md).

Optional short voice aliases map into the same verbs (`add trails` → ADD EFFECT TRAIL).

**Do not invent** a second vocabulary for menus (“enable filter”, “opacity slider”, “library”).

---

## Information architecture

```
SWITCHBOARD (root — only page)
├── Board list: each EFFECT as a row [ON/OFF] [name] [times…]
├── Actions: ADD EFFECT · SAVE PRESET · LOAD PRESET · CLEAR BOARD · GO LIVE
└── Drill-down: EFFECT detail
      ├── TOGGLE EFFECT
      ├── REMOVE EFFECT
      ├── ADD EFFECT TIME   (fade in)
      ├── REMOVE EFFECT TIME (fade out)
      ├── PHASE TIME
      ├── PHASE ON / OFF
      └── BACK
```

One stack. One back. No tabs-of-tabs.

---

## Layer model

Each board slot:

| Field | Role |
|-------|------|
| `id` | Catalog id (`trail`, `gogh`, `city_pulse`, `orbit`, …) |
| `family` | KEY · PULSE · PAINT · LSD · EARS |
| `enabled` | Hard on/off |
| `fadeInSec` | Phase-in duration when enabled or when phase cycle starts |
| `fadeOutSec` | Phase-out duration when disabled or cycle ends |
| `phaseSec` | Full cycle length if phasing (0 = no auto cycle) |
| `phaseEnabled` | Timed in/out running |
| `wet` | Layer strength 0–1 (default 1); global board wet still applies |

**Effective wet** for rendering:

```
layerGain = enabled ? envelope(t, fadeIn, fadeOut, phase) : 0
contrib = layerGain * layer.wet * globalWet
```

Envelope: linear or smoothstep ramp; if `phaseEnabled && phaseSec > 0`, triangle or sine in/out over the cycle.

---

## Catalog (1.0 — visuals only)

See [`VISUAL_SYSTEM.md`](VISUAL_SYSTEM.md).

| Family | Examples |
|--------|----------|
| **ELSD** | trail, hue, split, kaleido, melt, **mandelbrot**, **julia**, stillness_morph |
| **ART** | gogh, monet, sketch, comic, **cartoon**, **hyperreal**, melt_paint |
| **CINEMA** | noir, neon, bleach, teal_orange, anamorphic, soft_glow, **technicolor**, **suspiria**, **silent_era**, expressionist, giallo, golden_age |
| **PALETTE** | analog_film, analog_vhs, digital_clean, digital_harsh, polaroid (single-select) |
| **MOOD** | calm, warm, cold, night, fever, toasted, neutral (single-select overall toggle) |

**2.0 catalog adds:** plate/key, stream, ears, pulse-audio.  
**3.0:** `deepdream`.

---

## Presets (must)

- Stored as JSON under app files: `presets/<name>.json`  
- Payload: ordered layers + global wet + amy mode + meta  
- Factory presets seed on first run (window_tv, gogh_walk, …)  
- SAVE PRESET overwrites by name after confirm (UI) or voice “save preset walk”  
- LOAD PRESET replaces board (soft crossfade optional later)

---

## Timed phasing (ideal → 1.0 if cheap)

1. User ADD EFFECT TRAIL  
2. ADD EFFECT TIME 2 → fade in 2s  
3. REMOVE EFFECT TIME 3 → fade out 3s  
4. PHASE TIME 12 → every 12s cycle in/out  
5. PHASE ON  

Renderer/audio reads envelope each frame from board clock.

---

## Visual chrome (TOASTED)

- Dark charcoal board  
- Row = bank light + effect name + ON lamp  
- Drill-down = same type scale; big verb buttons  
- Labels: **ADD EFFECT**, not “+” alone without words  
- Amy can sit muted on switchboard; optional tiny lean if voice used on board  

---

## 1.0 vs later

| Must in 1.0 | Ideal / stretch |
|-------------|-----------------|
| Single page + drill-down | Pretty envelopes |
| ADD / REMOVE / TOGGLE EFFECT | Multi-select add |
| SAVE / LOAD PRESET | Cloud share |
| Fade in/out times on layer | Full PHASE cycle |
| Same vocab on voice | — |

---

## Anti-patterns

- Separate “settings app”  
- Nested “Effects > Artistic > Impressionism > Monet v2”  
- Different words on buttons vs voice  
- Touch-only features with no voice twin for HMD  

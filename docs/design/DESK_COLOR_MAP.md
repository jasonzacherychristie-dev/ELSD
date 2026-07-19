# Desk color map — luminance treatments (historical desk inspiration)

**Product language:** desk color map · treatments · palette strip · TOASTED grade.  
**History only:** classic Amiga-era desktop video color processors used **luminance mapping** (96 brightness levels → recolored). We implement the *idea* in GLSL; we do **not** ship trademarked product names in UI. See [`TRADEMARK_SAFE_LANGUAGE.md`](TRADEMARK_SAFE_LANGUAGE.md).

**Primary source (research):** public Video Toaster 4000 / System 4.0 manual ChromaFX chapter (Internet Archive full text) — *Color Video Processor*: brightness/contrast/saturation + sepia, line art, solarization, negative; built-in bank of ~100 numbered filters.

---

## How it works (carry into ELSD)

1. Split WORLD into **luma** (brightness) + optional chroma.  
2. Map luma through a **Color Strip** (dark ← left · bright → right).  
3. Optional **Color Table** blends a top strip → bottom strip (2D LUT feel).  
4. Modes:
   - **B&W base** — strip *replaces* original chroma (radical)  
   - **Color base** — strip *adds/tints* over live chroma (subtle)  
5. **Posterization** reduces luma steps (bands).  
6. **Color cycle** — animate strip rows (down / up / bounce · S/M/F).  
7. Apply as **filter** (full wet) or **transition** (T-bar / phase envelope).

This is the missing “desk soul” layer above simple HUE/POSTERIZE: **one engine, many looks**.

---

## Built-in bank (historical 00–99) → ELSD candidates

Grouped by intent. **Status:** ✅ already close in ELSD · 🔲 proposed · ⏸ 2.0+ / plate path.

### Core grades & exposure (cheap GLSL)

| Historical name | Intent | ELSD today | Proposal |
|-----------------|--------|------------|----------|
| Contrast / Brightness / Gamma | Exposure tools | partial via mood/palette | 🔲 `desk_contrast`, `desk_gamma` or fold into MOOD |
| SepiaTone | Warm mono tint | FILM / polaroid-ish | 🔲 `sepia` DESK or MOOD |
| Warm / Cool / WarmDark / CoolDark | Temperature | MOOD_WARM / COLD | ✅ moods cover |
| Daylight / Tungsten / Fluorescent | White-balance fiction | — | 🔲 palette slots or DESK |
| Fog | Lift blacks, haze | soft_glow cousin | 🔲 `fog` DESK |
| OldMovie | Scratch/flicker grade | SILENT_ERA | ✅ film family |

### Solarize / poster / invert (high “music video” value)

| Historical name | Intent | ELSD today | Proposal |
|-----------------|--------|------------|----------|
| Solar Filter / Solarize / Cycle Solar | Photo solarization | — | 🔲 **`solarize`** DESK — high priority |
| Nuke / Nuke Solar / Crazy Nuke | Extreme invert+solar | — | 🔲 hard solar + cycle |
| Fade to Neg / Fade→PostNeg | Animate to negative | NEGATIVE | ✅ + phase envelope |
| Posterize / Light / Heavy / B&W / PosterBrite | Quantize | POSTERIZE | ✅ expand amounts |
| Poster-Solar / Poster Cycle | Banded solar | — | 🔲 combo |
| Line Art / Charcoal / RedDraw | Edge-like mono | SKETCH | ✅ art family |
| Pastel / Light Poster | Soft poster | — | 🔲 wet poster |

### Metallic / chrome / neon (psych + neon film)

| Historical name | Intent | ELSD today | Proposal |
|-----------------|--------|------------|----------|
| Chrome / Red Chrome / Gold | Specular remap | NEON / hyperreal | 🔲 `chrome` DESK LUT |
| Green Neon / Neon Cycle | Neon bands | NEON | ✅ + cycle mode |
| Stripe Cycle / Stripe Filter / Band Filter / Zebra Stripe | Banded chroma | — | 🔲 **`luma_bands`** |
| Color Bands / ColorShade / Rainbo* | Rainbow stripes by luma | HUE | 🔲 **`rainbow_luma`** |
| Fire / Mars / Sunset / Ocean / Solar | Themed gradient maps | — | 🔲 **`luma_lut` presets** |

### Mid / dark / bright selective tints (key-adjacent)

| Historical name | Intent | ELSD today | Proposal |
|-----------------|--------|------------|----------|
| RedMids / BlueDarks / MidRangeTint | Zone tints | fractal darks/brights key | 🔲 general **zone grade** |
| Snow Mids/Lights/Darks/Cycle/Bands | High-key sparkle bands | — | 🔲 sparkle map |
| Flesh* / WarmSunset / Tobacco | Skin-biased maps | — | ⏸ careful / optional |
| USA-3color / BrnCyan-3col / Maroon* | 3-stop poster maps | posterize | 🔲 3-stop desk |

### Spreads & multi-color (false color)

| Historical name | Intent | ELSD today | Proposal |
|-----------------|--------|------------|----------|
| CoolSpread / WarmGraySprd / BlueGreen | Smooth false color | — | 🔲 false-color strip |
| BluePrint / Bronze / PearlyGates / Pinky | Signature looks | — | 🔲 factory DESK presets |
| Hue Filter / Hue Bands | Hue by luma | HUE | 🔲 **`hue_by_luma`** |

**Full historical index (00–99):** blank, Solar Filter, Sunset Filter, Ocean Filter, Hue Filter, Hue Bands, Mars Filter, SepiaTone, Contrast, Brightness, Gamma, Line Art, Charcoal, Pastel, Light Poster, Posterize, Heavy Poster, PosterBrite, B&W Poster, Red Poster, Fire, Solarize, Cycle Solar, Nuke, Nuke Solar, Crazy Nuke, Fade to Neg, Fade→PostNeg, Chrome, Gold, Red Chrome, Yellow Light, RedMids, BlueDarks, MidRangeTint, MidRangeFilt, Red-Grn-Blu, Poster-Solar, Poster Cycle, Snow Mids/Lights/Darks/Cycle/Bands, Green Neon, Neon Cycle, Stripe Cycle, Stripe Filter, Band Filter, Zebra Stripe, Color Bands, ColorShade, RainboStripe, RainboBands, Soft Rainbow, Fog, OldMovie, B&WPoster, BlueMetal, Daylight, Tungsten, Fluorescent, Red, RedCyanField, Purple, PurpleWhtPoster, Yellow, Moonlight, WarmGraySprd, Chartreuse, CoolGraySprd, CoolSpread, BlueGreen, Flesh-3col, FleshPoster, FleshSpread, Warm, Cool, Brown, CoolDark, WarmDark, BrnCyan-3col, DarkWood, BlueVelSpred, OrangeWhite, WarmSunset, MagentaBands, Maroon-3col, MaroonLavSpd, Tobacco, BlueRedSpred, BluePrint, USA-3color, Bronze, PearlyGates, BluePoster, PastelSpread, BritePoster3, RedDraw, Pinky.

---

## Priority import for ELSD 1.0

Implement as **one shader path** (`uDesk` / `uColorMap` + strip texture or analytic LUT), not 100 separate IDs.

| Priority | ELSD ID (proposed) | Covers historical spirit |
|----------|--------------------|---------------------------|
| P0 | `solarize` | Solarize, Solar Filter, Nuke family |
| P0 | `luma_map` + factory strips | Sepia, Chrome, Fire, Ocean, Sunset, false color |
| P1 | `hue_by_luma` | Hue Filter, Hue Bands, rainbow family |
| P1 | `luma_bands` / zebra | Stripe, Band, Zebra, Neon bands |
| P1 | `zone_tint` | RedMids, BlueDarks, MidRange* |
| P2 | Color **cycle** rate | Cycle Solar, Poster Cycle, Neon Cycle, Snow Cycle |
| P2 | Soft **fog** | Fog |

Voice: `add solarize`, `desk map chrome`, `cycle color slow`, `zone tint darks blue`.

---

## Related ELSD docs

- Banks: [`FEATURES_1_0.md`](FEATURES_1_0.md) · [`VISUAL_SYSTEM.md`](VISUAL_SYSTEM.md)  
- Import process: [`SHADER_IMPORT.md`](SHADER_IMPORT.md)  
- Keying (where map meets FOV): [`KEYING_AND_SENSORS.md`](KEYING_AND_SENSORS.md) · [`FRACTAL_FOV.md`](FRACTAL_FOV.md)

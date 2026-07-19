# Fractal in the FOV — adjustable zoom · chromakey · trails

The monorepo fantasy:

> **Adjustable-rate Mandelbrot zoom, chromakeyed into your field of vision, with trails.**

That’s a **stack**, not one lonely effect — and it’s 1.0-doable in pure GLSL.

---

## Recipe (switchboard)

```
ADD EFFECT MANDELBROT
SET ZOOM RATE 1.25          (layer rate — dive speed)
FRACTAL KEY → CHROMA        (or DARKS / BRIGHTS / FULL)
ADD EFFECT TRAIL
SET FRAMERATE 24
ENABLE DROPPED FRAMES       (optional headroom)
GO LIVE
```

Factory presets:

| Preset | Stack |
|--------|--------|
| **`mandel_key_trails`** | Mandelbrot rate 1.25 + chroma key + trail + mood toasted |
| **`mandel_shadow_dive`** | Mandelbrot rate 2.0 + key darks + trail + mood night |

Voice:

- `add mandelbrot` · `add trail`  
- `zoom rate 2` / `set zoom 0.5`  
- `chromakey fractal` · `fractal in darks` · `fractal in brights` · `fractal full`  

---

## How it composites

1. **Mandelbrot / Julia color** computed with **log zoom** `exp(time × rate)` (adjustable).  
2. **World steers** plane / seed so the room still matters.  
3. **Key mask** decides *where* infinity sits in the FOV:  
   - **CHROMA** — keyed hue (default green) becomes a window into the set  
   - **DARKS** — shadows open to the set  
   - **BRIGHTS** — highlights open to the set  
   - **FULL** — classic wet mix over whole frame  
4. **TRAIL** stacks afterimages on world *and* fractal UV drift.

```
WORLD → palette/mood/art/cinema → [fractal chromakey] → [trails] → PGM
```

---

## Why it’s striking

- You’re not replacing reality; you’re **keying infinity into parts of it**  
- Zoom rate is a performance instrument (slow stare vs dive)  
- Trails make the dive feel continuous even at 24 fps with drops  
- Pair with SILENT ERA / SUSPIRIA / hyperreal for free genre  

---

## Related

- Stillness morph: longer hold can later boost rate or key softness  
- Framerate: lower fps + drops = more iter budget per presented frame  
- 3.0: neural detail *inside* the keyed region  

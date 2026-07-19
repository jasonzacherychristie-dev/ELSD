# Type & color — TOASTED desk system

**Product face:** desktop-video / live-desk spirit.  
**Not product copy:** vintage platform or switcher brand names ([`TRADEMARK_SAFE_LANGUAGE.md`](TRADEMARK_SAFE_LANGUAGE.md)).  
**Rule:** only **open-licensed** fonts and project-owned color tokens.

---

## Design intent

| Feel | How we get it without cloning a brand |
|------|----------------------------------------|
| **On-air desk** | Near-black panels, cream phosphor text, one red that means *live* |
| **Workstation joy** | Condensed display headers + mono labels (one “system font” energy) |
| **Checker soul** | Red + cream locked as Amy’s pair — never pure #FFF |
| **Listen / wet** | Lime attention (not cyan cyberpunk); amber for caution / T-bar / brand |
| **Preview bus** | Cool blue selection (Workbench-adjacent *hue family*, not a logo) |

We are **not** shipping Topaz, proprietary CG faces, or scraped UI bitmaps.  
Those were era signals; we rebuild the *pressure* with open tools.

---

## Typography (open)

### Why not “Amiga Topaz”?

Classic system faces from that era are **not freely redistributable** for app bundling.  
Using them would be a license landmine. We pick **OFL** (or Apache-2.0) faces that rhyme with the same jobs:

| Job | Font | License | Role |
|-----|------|---------|------|
| **Display / titles** | **Russo One** | OFL | `SWITCHBOARD`, `ELSD`, Praxis wordmark — condensed industrial / lower-third energy |
| **UI labels / verbs** | **Share Tech Mono** | OFL | ADD EFFECT, PRESETS, FPS rows — machine desk, slightly narrow |
| **Body / status / About** | **Space Mono** Regular–Bold | OFL | Longer lines, credits, mission text — readable mono |

### Hierarchy

```
DISPLAY   Russo One         28–40sp   brand, page titles
TITLE     Russo One         20–24sp   section headers
LABEL     Share Tech Mono   14–16sp   verbs, layer names
BODY      Space Mono        12–14sp   help, credits, status
CAPTION   Space Mono        11sp      dim meta, version
```

### Tracking & case

- Verbs and bus names: **UPPERCASE**; slight letter-spacing on Russo display only.  
- Mono labels: normal tracking; Share Tech is single weight — don’t fake bold with stroke.  
- Avoid pure white type — cream on void always.

### Fallback

If a font fails to load: `monospace` → still looks like a desk.  
Do **not** fall back to Roboto for titles (breaks the soul).

---

## Color tokens

Named for **role**, not nostalgia marketing.

### Core surfaces

| Token | Hex | Use |
|-------|-----|-----|
| `void` | `#0A0A0C` | Window background, GO LIVE underlay |
| `panel` | `#141418` | Cards, button faces |
| `panel_hi` | `#1E1E26` | Raised / pressed |
| `line` | `#2A2A32` | Hairline rules |

### Ink

| Token | Hex | Use |
|-------|-----|-----|
| `cream` | `#F2E8D5` | Primary text (warm phosphor, not sRGB white) |
| `cream_dim` | `#8A8378` | Secondary / disabled |
| `ink_inverse` | `#0A0A0C` | Text on lime/amber chips |

### Signal (semantic)

| Token | Hex | Use |
|-------|-----|-----|
| `check_red` | `#E11D26` | Amy checkers, hard stop sting |
| `on_air` | `#C41E3A` | Live / GO LIVE emphasis |
| `listen` | `#7DFF4D` | Speech lean-in, active layer, “on” |
| `amber` | `#E8A020` | T-bar energy, Praxis tag, caution, FPS |
| `bus_blue` | `#3A6FB0` | Selection, preview bus, focus ring |
| `wet_gold` | `#D4A017` | Wet/dry high, TOASTED sting |

### Pairing rules

1. **Amy** = only `check_red` + `cream` (never recolor checks to brand amber).  
2. **One lime job** — listening / enabled. Don’t use lime for errors.  
3. **Amber ≠ error** — amber is *instrument*; red is *danger/live*.  
4. **Blue is cool bus**, not hyperlink decoration.  
5. Max **3 signal colors** on one screen region (cream + 2 signals).

### WCAG note

Cream on void and cream on panel pass for body sizes.  
Lime on void is for **status accents**, not long paragraphs.

---

## Component recipes

| Component | Type | Colors |
|-----------|------|--------|
| Switchboard title | DISPLAY Russo One | cream |
| Praxis house line | CAPTION Space | amber |
| Verb button | LABEL Share Tech | cream on panel; listen text when armed |
| GO LIVE | LABEL Share Tech Bold-feel | cream on on_air or on_air text on panel_hi |
| Layer row on | LABEL | listen name + cream meta |
| Layer row off | LABEL | cream_dim |
| About splash brand | DISPLAY | cream + amber tag |
| Live status strip | BODY Space | cream / cream_dim on void |

---

## What we deliberately skip

| Temptation | Why skip |
|------------|----------|
| Bundled Topaz / “Amiga font packs” | License unclear or non-free |
| Pure cyberpunk magenta/cyan stack | Wrong decade energy for *desk* |
| Flat Material purple primary | Generic phone app |
| Heavy skeuomorph brushed metal | Unreadable in HMD; thermal noise |
| Scanline overlay on all UI | Optional FX later; not chrome |

---

## Files

| Path | Role |
|------|------|
| `app/src/main/res/values/colors.xml` | Tokens |
| `app/src/main/res/font/*` | Bundled OFL TTFs |
| `app/src/main/res/values/themes.xml` | Theme + text appearances |
| `docs/design/fonts/OFL_NOTICES.md` | Font copyright notices |

---

## Related

- [`SOUL.md`](SOUL.md) — character & pillars  
- [`SWITCHBOARD.md`](SWITCHBOARD.md) — IA  
- [`TRADEMARK_SAFE_LANGUAGE.md`](TRADEMARK_SAFE_LANGUAGE.md)

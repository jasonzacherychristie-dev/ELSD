# Soul of the Machine — Look, Feel, Spirit

**Public language rule:** Never say the old platform names or the old switcher product name in UI, marketing, store copy, or voice lines.  
**We imply.** Checkerboard physics. Broadcast chrome. Desktop-video mischief. 1980s–90s workstation joy.  
**Internal research notes** may still cite history; **the product face** does not.

---

## One-line soul

> A **voice-only perception mixer** with a **spirited checker-orb cursor** that lives in your eyes, can bounce into space, roast the fourth wall, and go quiet when the trip needs to be pure. The FX side is **TOASTED** — hot, live, switcher-souled.

---

## Brand pillars

| Pillar | Meaning | Never do |
|--------|---------|----------|
| **Spirited** | Playful, a little cocky, glad to be alive | Corporate sterile HUD |
| **Live** | Always feels like a desk on-air, not a gallery app | Modal settings labyrinth |
| **Dual wet** | Eyes *and* ears can hallucinate (spatial audio mode) | Wet eyes with silent dry ears only as a choice, not a missing feature |
| **Implied heritage** | Red/cream checks, bounce physics, program/preview, wet/dry | Trademarked names, logos, or slogans from the past |
| **Voice-first** | Mouth is the T-bar | Finger-first menus as primary UX |
| **Consent to weird** | Fourth wall is opt-in personality; clear always wins | Trapping the user in a bit |

### Type & color

Open fonts + TOASTED tokens only — **no** proprietary classic system faces.  
Full ramp: [`TYPE_AND_COLOR.md`](TYPE_AND_COLOR.md) · OFL notices: [`fonts/OFL_NOTICES.md`](fonts/OFL_NOTICES.md).

| | |
|--|--|
| Display | Russo One (OFL) |
| Labels | Share Tech Mono (OFL) |
| Body | Space Mono (OFL) |
| Ink | Cream phosphor on void — not pure white |
| Live | On-air red · listen lime · amber instruments · bus blue selection |

---

## The cursor: **Amy** (the checker-orb pilot)

**Character name:** **Amy** — public, spoken, lovable.  
**Code / bank names:** Bounce still OK (`BounceCursor`, `bounce on`) as technical aliases; voice can accept both `amy` and `bounce`.  
**Look:** Classic **boing-ball** checker energy (red/cream), never branded to a restricted platform name.

### What Amy is

| Mode | Appearance | Behavior |
|------|------------|----------|
| **2D primary** | Flat-ish checker disc / soft circle with **red–white (or cream) grid**; slight 3D bevel | Default UI: points, selects, reacts, *has attitude* |
| **3D spatial** | Full **checker sphere** with light roll / squash-stretch | Enters the stereo scene; bounces on “planes” (floor hint, window rect, key matte edge) |
| **Muted / background** | Shrinks, desaturates, parks in a corner or halo-fades | No chatter; no steal of focus; FX continue |
| **On-air** | Crisp, high contrast, optional scanline kiss | When mix is live / wet high |
| **Lean-in (listening)** | Larger in FOV, slides toward center, tips toward viewer, frontal light + soft lime attention | **Fires on beginning of speech** — Amy physically leans toward the user before words resolve |

**Not a mouse pointer.** Not a reticle. Not a laser.  
**A character that happens to aim — and listens with her whole face.**

### Lean-in (signature beat)

When the recognizer fires *beginning of speech* (or partials):

1. Amy **grows** (closer to the eyes)  
2. She **slides toward center** and nods slightly  
3. Lighting swings **frontal** (eye-contact glint)  
4. Soft **lime** attention rim  
5. On end-of-speech / result / error she **eases back** to idle float  

This is the difference between a widget and a pilot. Ship it.

### Personality (fourth wall allowed)

Amy may:

- Answer voice with a short quip (*“Keyed. Looking crispy.”*)
- Admit she’s software (*“I’m Amy. Checker optional. Don’t overthink it.”*)
- React to the world bus (*“That window wants to be a TV.”*)
- Celebrate a good mix (*“TOASTED.”* as a one-word win sting)
- Refuse unsafe chaos gently (*“Nope. Soft landing.”* when sober/clear)
- Lean in when they speak — silent body language first, words second

Amy must **never**:

- Ignore `clear` / `sober` / `stop` / `mute amy` / `mute bounce`
- Talk over emergency exits
- Shame the user
- Imply corporate endorsement of any vintage brand

### Control plane: SWITCHBOARD + voice (same vocabulary)

| Domain | How it works |
|--------|----------------|
| **One UI page** | **SWITCHBOARD** — list layers, drill down, SAVE/LOAD PRESET, GO LIVE ([`SWITCHBOARD.md`](SWITCHBOARD.md)) |
| **Vocabulary** | **ADD EFFECT**, **REMOVE EFFECT**, **ADD EFFECT TIME**, **PHASE TIME**, **SAVE PRESET**, … |
| **In headset** | Voice uses the **same verbs** (no second language) |
| **On glass** | Touch the switchboard before GO LIVE; not a settings maze |
| **No extra apps** | No nested “settings”; BACK returns to the board |
| **Lean-in** | `onBeginningOfSpeech` → Amy leans; end/error/result → ease back |

**Mute Amy** ≠ mute audio world.  
Commands:

- `mute amy` / `mute bounce` / `quiet` → background mode  
- `amy on` / `hey amy` / `bounce on` → restore personality + cursor  
- `clear` / `sober` / `stop` → hard dry FX **and** Amy stands down chatter  

### Spatial interaction (stereo)

When Amy is 3D:

- Sphere **casts a soft attention cone** (what we’re keying / painting)
- Can **bounce-roll** toward bright regions (Window TV assist) or sky patch (Sky Cinema)
- Voice: `bounce left` / `higher` / `on the window` / `follow bass`  
- Collision is **suggestive**, not hard physics sim — spirit of the classic bounce demo, not a game engine showcase

When muted: sphere becomes a tiny **status pip** (wet level as pulse size) or vanishes entirely per preset.

---

## TOASTED — the ELSD visual language

**TOASTED** is our word for the mixer soul: hot bus, live desk, slightly dangerous fun.  
Say it. Own it. It replaces any need to name the old switcher.

### Aesthetic tokens

| Token | Spec (spirit) |
|-------|----------------|
| **Checks** | High-contrast two-tone grid (classic orb DNA); use on Bounce, rare UI flourishes — not full-screen seizure wallpaper |
| **Broadcast chrome** | Dark charcoal panels, hairline highlights, “on air” red when wet > threshold |
| **Program / Preview** | Mental model: *world* vs *media* vs *program out* — labels can be `WORLD` · `PLATE` · `PGM` |
| **Wet / Dry** | Primary mix metaphor (not “opacity slider”) |
| **Bank lights** | KEY · PULSE · PAINT · LSD as illuminated legends when active |
| **Scan / CRT kiss** | Optional 2–5% on UI chrome only; never force on the world bus |
| **Type** | Compact mono or near-mono for bank labels; humanist sans for Bounce subtitles if shown |
| **Color** | Checker red + off-white; accent amber for “preview”; cyan for “keyed plate”; lime sparingly for “voice heard” |

### Motion language

- **Snappy crossfades** (desk switcher, not Instagram ease-everything)
- **Bounce squash** on acknowledge (voice heard)
- **Soft landing** always for trip exit (1–3s wet→dry), never hard cut unless `stop now`
- **Pulse** is architectural, not UI jitter

### Sound (optional stingers)

- Mic-on tick when listening  
- Soft “bank” click when family engages  
- One-shot **TOASTED** sting (user can `mute stingers`)  
No licensed sample packs that imply old vendors.

### Copy tone

- Short. Desk humor. Competent.  
- Good: *“Luma key up. Plate on brights.”*  
- Bad: *“Welcome to your immersive wellness journey.”*  
- Fourth wall: light seasoning, not a podcast in your HMD.

---

## Screen grammar (what you actually see)

```
┌─────────────────────────────────────────────┐
│  [live world + FX — almost full FOV]        │
│         ○ Bounce (2D default)               │
│                                             │
│  optional edge legends (auto-hide):         │
│  PGM ●  WET 0.62  BANK:PAINT/gogh           │
│  LISTENING …                                │
└─────────────────────────────────────────────┘
```

- **No permanent settings sheet**  
- Legends **auto-hide** when Bounce is muted or after N seconds of silence  
- **Voice heard** = lime rim flash on Bounce, not a toast notification stack  

### Fourth-wall moments (scripted, rare)

Examples (not exhaustive):

- First launch: *“I’m Bounce. You talk. I mix. Say ‘help’ if you’re shy.”*  
- After Window TV locks: *“Fourth wall? That’s the window. We fixed it.”*  
- Overheat / FPS drop: *“I’m sweating checks. Dropping trail.”*  
- `who are you`: *“Your pilot light. Checker optional.”*

---

## Interaction map (voice-only)

### Always-on grammar (safety)

| Phrase | Result |
|--------|--------|
| `clear` / `sober` / `stop` | Hard priority: dry FX, calm Bounce |
| `mute bounce` / `quiet` | Cursor background |
| `bounce on` | Restore |
| `help` | Spoken cheatsheet (short) |

### Mixer grammar

| Phrase | Result |
|--------|--------|
| `wet` / `dry` / `half wet` / `toasted` | Global mix (`toasted` ≈ high wet + pride) |
| `key luma` / `key chroma` / `pick color` | KEY family |
| `city pulse` / `pulse off` | PULSE |
| `gogh` / `noir` / … | PAINT |
| `trails` / `kaleido` / `melt` | LSD |
| `window tv` / `sky cinema` / `gogh walk` | Macro presets |
| `plate from file` / `plate from stream` | Bus B source (spoken flow + system picker if OS forces) |
| `bounce 3d` / `bounce flat` | Cursor dimension |
| `bounce mute` | alias quiet |

OS may still force a **system file picker** — Bounce narrates: *“Pick a plate. I’ll wait.”* That is not a touch UI we designed; it’s the platform.

---

## What we imply (moodboard words)

Use these in design critique and art direction:

- Checkerboard sphere, gravity joke, demo-scene grin  
- Late-night edit suite, genlock anxiety, “we’re live”  
- Consumer magic on cheap glass  
- Psychedelic **without** defaulting to purple fractal cliché — TOASTED can be clean and sharp  
- Desktop video democratized: *you* are the TD  

Avoid:

- Naming vintage computers, vendors, or switcher SKUs  
- Recreating protected logos or box art  
- “Official” or “legacy compatible with [brand]” claims  

---

## Implementation hooks (for later code)

| System | Hook |
|--------|------|
| `BounceCursor` | 2D billboard + 3D sphere mesh; `mode: FLAT \| SPATIAL \| MUTED` |
| `BounceVoice` | TTS or baked one-shots; personality pack JSON |
| `ToasterLook` / `ToastedTheme` | colors, bank lights, PGM edge — class names OK; user-facing string is **TOASTED** |
| `VoiceRouter` | only input path for app commands |
| `SoftLanding` | mandatory fade graph |

**Do not** name packages or public strings after forbidden trademarks.

---

## Success test (soul QA)

Sit a stranger in Cardboard. No tutorial screen.

1. They hear Bounce once.  
2. They say something; the world answers.  
3. They say `toasted` or a paint name; it feels like a **desk** did it, not a filter app.  
4. They say `quiet`; Bounce vanishes; trip remains.  
5. They never needed a finger.  
6. Nobody said a banned brand name — and someone still mutters *“this feels familiar.”*

That’s the soul.

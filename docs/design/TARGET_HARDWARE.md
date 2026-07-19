# Target hardware — ELSD 1.0

ELSD is an **Android perception mixer** first — **app + Amy + switchboard** must deserve cool eyes before we chain the hardware.

We optimize for **real phones we hold** — not boutique PC VR, not tiny 2015 cardboard shells.

**Public pitch:** prove on **OnePlus 9** · ship feel on **OnePlus 12** · large-tray stereo when ready · USB-C glasses as personal dream display.

**Sideload / adb process:** [`../DEV_DEPLOY.md`](../DEV_DEPLOY.md)

---

## Product approach (paths)

**Order of development, not of ego:**

| Order | Path | What it is | Role |
|-------|------|------------|------|
| **1** | **Desk / phone glass** | Flat landscape GO LIVE on OP9 / OP12 / Pixel | **Ship the app** — switchboard, Amy, banks, presets |
| **2** | **Large plastic tray** | Phone-in-viewer, SBS + Cardboard-class distortion | **Stereo eyes** lab / optional 1.0 M5 |
| **3** | **USB-C XR glasses** | XREAL / Viture-class; phone as DP Alt Mode source | **Personal high-end PGM** — cinematic, not required for open-source 1.0 |

**Philosophy**

- Putting **Amy on a big, legible desk UI** first means we build software that **deserves** dreamy glass later.  
- Folding **Cardboard no longer fits** modern flagships — tray path must be **Ultra / 6.8–7.0"** plastic only.  
- **High-end glasses** (120 Hz-class micro-OLED, electrochromic) are a **personal / ship-feel** treat on OP12 — not a gate for contributors.  
- Glasses path ≠ tray path: glasses are usually a **huge virtual monitor** (dreamy cinema), not phone-screen passthrough through lenses.

```
ELSD core (Amy · switchboard · WORLD · banks)
        │
        ├─► A. Phone display (default QA)
        ├─► B. Large tray stereo (Cardboard OSS when ready)
        └─► C. USB-C glasses PGM (OP12 personal / cinema)
```

---

## Lab devices (Jason)

| Device | Role in lab |
|--------|-------------|
| **OnePlus 9** | Floor / thermal gate / tray torture (lighter) |
| **OnePlus 12** | Ship reference / “sizzle” / **glasses host** |
| **Pixel 7** | Clean Android reference (flat desk; ~6.3" tray fit varies) |
| **USB-C XR glasses** (e.g. XREAL One / One Pro / Air 2 Pro class) | Personal dream display on OP12 — optional |
| **Large plastic tray** | Stereo / FOV experiments when Ultra-fit shell arrives |

---

## Performance ladder (test downward)

| Tier | Device | Role |
|------|--------|------|
| **Floor / torture test** | **OnePlus 9** | If it runs here, everything above sizzles |
| **Ship** | **OnePlus 12** | Primary “it should feel great” target |
| **API reference** | **Pixel 7** | Stock-ish behavior; not the design-center size |
| **Peers** | Large flagships ≥ ~2021 | Snapdragon 8-gen class, 6.5"+ AMOLED |

**Rule:** Feature work is not “done” until it survives the **OnePlus 9** with usable fps, heat, and comfort — then we polish on OP12. Pixel 7 proves we’re not OEM-locked.

---

## Floor device — OnePlus 9 (POS, beloved)

| Spec | Value |
|------|--------|
| **Display** | **6.55"** Fluid AMOLED, 1080×2400, up to 120 Hz |
| **Body** | **160.0 × 74.2 × 8.7 mm** |
| **Weight** | **192 g** (easier on the neck than OP12) |
| **SoC** | **Snapdragon 888** |
| **GPU** | **Adreno 660** |
| **RAM** | 8 / 12 GB |
| **Android** | Launched 11; expect **13–14** if updated |
| **GLES** | 3.2 capable — meets ELSD min |

### Why this is the right floor

- Still a real flagship GPU (not a budget Mali from 2018)  
- Smaller/lighter in a cardboard shell  
- Thermals and 888 throttling are **honest** — forces framerate drops, effect budget, fractal iter discipline  
- Fits many “6.5–6.7"” trays that OP12 might stretch  

### OP9 QA gates (must pass)

- [ ] Switchboard + GO LIVE + camera WORLD  
- [ ] Amy lean-in on speech  
- [ ] CINEMA **NOIR** / **SUSPIRIA** / **SILENT ERA** usable  
- [ ] ART **CARTOON** / **HYPERREAL** without multi-second freezes  
- [ ] **MANDELBROT** + trails at **24 fps + DROPPED FRAMES ON** (or 12–20 if needed)  
- [ ] Preset `mandel_key_trails` runs ≥ ~15–20 present fps with drops, no ANR  
- [ ] 10+ minutes in headset without thermal death (throttle OK; crash not OK)  
- [ ] CLEAR / sober still instant  

### OP9 default recommendations (ship hints)

| Setting | OP9 default bias |
|---------|------------------|
| Framerate | **24** or **30**, not unlocked 60 under heavy ELSD |
| Dropped frames | **ON** |
| Fractal zoom rate | ≤ **1.25** for comfort; 2.0 only in “dive” presets |
| Mandel iterations | Shader ~56; if needed later, quality tier scales iters |
| Stillness morph | Cap max level lower than OP12 |

---

## Ship device — OnePlus 12

| Spec | Value |
|------|--------|
| **Display** | ~**6.82"** AMOLED |
| **Body** | **164.3 × 75.8 × 9.15 mm** |
| **Weight** | ~**220 g** |
| **Class** | Current large flagship |

**Peer phones (same ship tier):**  
S23/S24 Ultra class, large Pixels, other ~6.7–6.9" Androids.

When OP9 is green, OP12 should **sizzle**: higher unlocked fps, richer wet, faster zoom rates, longer sessions.

---

## Reference viewer (primary) — order this class

| Spec | Target |
|------|--------|
| **Class** | Plastic **Cardboard-compatible** phone VR headset (lenses + tray + strap) |
| **Not required** | Meta Quest / Pico / PC PCVR for 1.0 |
| **Phone fit** | **6.5–7.0"** — must fit **OP9 6.55"** and **OP12 ~6.82"** |
| **Tray** | Prefer interior **≥ ~170 × 85 mm** (or listing that explicitly includes 6.7–7.0") |
| **Reject** | Boxes that only list “up to 6.0–6.3 inch” (Pixel 7 may fit; OP12 will not) |
| **Optics** | Focus dial preferred; dual IPD nicer but optional |
| **Mount** | Head **strap** (not nose-balance only) |
| **Access** | **USB-C cutout** for charge during long dives |
| **Comfort** | Foam + some venting for 20–30 min (SD888 throttles when sealed) |

### Shopping checklist (before you click buy)

1. **Max phone size** includes **≥ 6.8"** or lists S23 Ultra / large flagship explicitly.  
2. Photos show a **deep tray** and side walls that won’t crush a 75+ mm-wide body.  
3. **Strap** included (or cheap to add).  
4. **USB-C / cable notch** if you can get it.  
5. Avoid “mini” / kids / 4.7–6.0-only shells.  
6. Cardboard **QR / viewer profile** is optional for 1.0 (we can start flat or generic distortion later).

**One order covers the lab:** buy **large enough for OP12**; OP9 and most large phones will fit.  
Pixel 7 (~6.3") is a **desk test** phone first — don’t size the headset around it.

### What we call it in public copy

- “**Large-tray Cardboard-compatible phone headset**”  
- “**Phone-in-viewer**” / “**stereo eyes**”  
- Not “official Google Cardboard product” · not Quest

---

## Software target profile

| Item | Target |
|------|--------|
| **minSdk** | **26** (Android 8) |
| **Test OS** | OP9 on latest available; OP12 on 14/15 |
| **GPU** | OpenGL ES **3.0+** (888 / Adreno 660 OK) |
| **Stereo** | Cardboard NDK when ready; flat landscape for early QA |
| **Permissions** | Camera, mic |

---

## Thermal & power

- Sealed headset + **888** = throttle city — **ENABLE DROPPED FRAMES** is a feature  
- Prefer flap open / USB-C charge during long fractal dives  
- Effect budget: reduce wet / fractal rate / present fps before crash  
- Session timer = product + hardware mercy  

---

## Amy / UX

- Voice + switchboard same vocabulary  
- Lean-in on speech  
- Status secondary  

---

## Non-targets

| Hardware | Status |
|----------|--------|
| Quest / Pico / Vision Pro as required | Out of 1.0 |
| Daydream controllers | Not required |
| iOS | Post–Android 1.0 |
| Sub-6.0" phones as design center | Best-effort only |

---

## QA checklist (session on OP9)

- [ ] Install debug APK on **OnePlus 9**  
- [ ] Switchboard loads; factory presets present  
- [ ] `silent_reel` @ 12 fps feels intentional, not broken  
- [ ] `mandel_key_trails` @ 24 + drops survives 2+ minutes  
- [ ] `suspiria_red` / `noir_night` look correct  
- [ ] CLEAR recovers immediately  
- [ ] Heat: warm OK; reboot/thermal kill = fail  
- [ ] Then re-check same presets on **OnePlus 12** for “sizzle”  

---

## One-line pitch

> **Prove it on the OnePlus 9. Ship it feeling great on the OnePlus 12. Pixel for clean API checks. Large-tray Cardboard-compatible headset. Voice + switchboard. World as canvas.**

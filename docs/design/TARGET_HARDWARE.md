# Target hardware — ELSD 1.0

ELSD is a **phone-in-viewer** perception mixer first.  
We optimize FOV, thermal notes, and QA for **real OnePlus devices we can hold** — not boutique PC VR, not tiny 2015 Cardboard shells.

---

## Performance ladder (test downward)

| Tier | Device | Role |
|------|--------|------|
| **Floor / torture test** | **OnePlus 9** | If it runs here, everything above sizzles |
| **Reference / ship** | **OnePlus 12** | Primary “it should feel great” target |
| **Peers** | Large flagships ≥ ~2021 | Snapdragon 8-gen class, 6.5"+ AMOLED |

**Rule:** Feature work is not “done” until it survives the **OnePlus 9** with usable fps, heat, and comfort — then we polish on OP12.

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

## Reference viewer (primary)

| Spec | Target |
|------|--------|
| **Class** | Plastic **Cardboard-compatible** phone VR headset |
| **Not** | Meta Quest / PC PCVR as 1.0 required hardware |
| **Phone fit** | **6.5–7.0"** (must fit **OP9 6.55"** and preferably **OP12 6.82"**) |
| **Tray** | Prefer max size **≥ 170 × 85 mm** if OP12 is daily; OP9 alone allows slightly smaller trays |
| **Reject** | Viewers that only list “up to 6.0–6.3 inch” |
| **Optics** | Focus adjust preferred; IPD nice |
| **Mount** | Head **strap**; OP9 is lighter — still prefer strap |
| **Access** | USB-C cutout preferred |
| **Comfort** | Venting for 20–30 min (888 throttles hard when sealed) |

**Shopping rule:**  
- Must fit **OnePlus 9**.  
- Ideally also **OnePlus 12** / S23 Ultra class.  
If forced to choose one tray for both: buy large enough for OP12.

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

> **Prove it on the OnePlus 9. Ship it feeling great on the OnePlus 12. Large-tray Cardboard-compatible viewer. Voice + switchboard. World as canvas.**

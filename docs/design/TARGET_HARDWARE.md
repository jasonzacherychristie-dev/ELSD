# Target hardware — ELSD 1.0

ELSD is a **phone-in-viewer** perception mixer first.  
We optimize the product, FOV assumptions, thermal notes, and QA matrix for this class of device — not for boutique PC VR and not for tiny 2015 Cardboard shells.

---

## Reference phone (primary)

| Spec | Target |
|------|--------|
| **Class** | Large modern Android flagship |
| **Reference device** | **OnePlus 12** (and peers) |
| **Display** | ~**6.7–6.9"** AMOLED, high refresh OK |
| **Body (no case)** | ~**164 × 76 × 9 mm** (OP12: 164.3 × 75.8 × 9.15 mm) |
| **With thin case** | ~**166–168 × 78–80 × 11–13 mm** |
| **Weight** | ~**220 g** (+ case) |
| **Sensors** | Gyro + accelerometer required; magnetometer nice |
| **I/O** | USB-C; mic + **headphones or sealed headset speakers** (spatial audio hallucination); rear camera usable while seated in tray if open-back or when testing flat |

**Peer phones (same QA tier):**  
Samsung Galaxy S23/S24 Ultra class, Pixel large flagships, other ~6.7–6.9" Androids with similar footprint.

**Secondary (should still run):**  
Smaller phones (6.1–6.5") in the same headset with foam/spacer if needed — but we do **not** shrink the tray requirement to fit only them.

---

## Reference viewer (primary)

| Spec | Target |
|------|--------|
| **Class** | Plastic **Cardboard-compatible** phone VR headset |
| **Not** | Meta Quest / PC PCVR as 1.0 required hardware |
| **Phone fit** | Explicitly **6.7–7.0"** (or max tray **≥ 170 × 85 mm**) |
| **Reject** | Viewers that only list “up to 6.0–6.3 inch” |
| **Optics** | Biconvex aspheric or equivalent; user **focus** adjust preferred |
| **IPD** | Adjustable preferred; fixed ~63 mm acceptable for v1 |
| **Mount** | Head **strap** (not temple-only); supports ~250 g phone+case |
| **Access** | Side/top opening for phone; **USB-C cutout** preferred |
| **Comfort** | Replaceable foam; vented enough for 20–30 min sessions |
| **Button** | Optional magnetic/conductive Button for “next preset” later |

**Shopping rule of thumb:**  
If reviews say **S23 Ultra / 6.8" fits**, the OnePlus 12 class is in.

**Examples of the class** (availability changes; buy by tray size, not nostalgia):

- Large-phone BOBOVR Z4/Z5-style plastic headsets  
- Merge-class goggles **if** current SKU lists large-phone fit  
- Any solid “3D VR glasses 4.7–**6.7+** inch” with verified big-phone reviews  

---

## Software target profile

| Item | Target |
|------|--------|
| **OS** | Android **8.0+** (minSdk 26); develop/test on **Android 14/15** flagships |
| **GPU** | OpenGL ES **3.0+** |
| **Stereo** | Side-by-side Cardboard distortion (M5); **flat landscape** valid for M1–M4 |
| **Audio** | Mic for voice + optional hallucination drive; media session FFT when plate plays |
| **Permissions** | Camera, mic; no mandatory accounts |

---

## Thermal & power (design constraints)

Large flagships in a closed shell get hot. Target hardware assumes:

- Prefer **front flap open** or ventilated designs during long TOASTED sessions  
- USB-C **pass-through charge** when possible  
- Effect budget: drop feedback resolution / FX count before cooking the SoC  
- Session timer (M4) is a product feature **and** a hardware mercy  

---

## Amy / UX assumptions on this hardware

- Amy is drawn in **stereo or flat** over a full-bleed camera (or PGM)  
- Lean-in is toward the **viewer’s face** (screen-space center / scale-up)  
- User cannot touch UI easily → **voice-only** is not optional flavor; it matches the headset  
- Status text is secondary; most feedback is Amy + world FX  

---

## Non-targets (explicit)

| Hardware | Status |
|----------|--------|
| Meta Quest / Pico / Vision Pro as required runtime | **Out of 1.0 scope** (possible later ports) |
| Daydream-only controllers | Not required |
| Foldables unfolded as primary | Best-effort only |
| iOS | Post–Android 1.0 |
| PC + SteamVR | Not ELSD 1.0 |

---

## QA checklist (buy / accept a headset)

- [ ] OP12 (or peer) **clicks into tray** without forcing  
- [ ] Lenses cover most of the panel; no huge black bars from undersized tray  
- [ ] Focus can reach sharp text/UI legends  
- [ ] Straps hold 10+ minutes without face pain  
- [ ] USB-C accessible or flap allows quick removal  
- [ ] Can launch ELSD, grant camera/mic, see WORLD bus, Amy lean-in on speech  

---

## One-line product hardware pitch

> **ELSD 1.0 targets a large Android flagship (OnePlus 12 class) in a large-tray Cardboard-compatible plastic headset — voice-first, phone-in-box, world as canvas.**

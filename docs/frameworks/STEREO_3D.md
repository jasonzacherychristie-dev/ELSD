# Stereo 3D enhancements — state of the art & open code

Reference for ELSD planning. **Not all of this is 1.0.**  
See [`../design/ROADMAP.md`](../design/ROADMAP.md): classical SBS + Cardboard = **1.0**; depth-from-mono stereo = **2.0–3.0**.

---

## What “stereo 3D enhancement” means (four stacks)

| Stack | Problem | Typical open approach |
|-------|---------|------------------------|
| **A. Display stereo** | Show L/R to two eyes | Side-by-side (SBS) + lens distortion (Cardboard / OpenXR) |
| **B. Capture stereo** | Two real cameras / stereo match | Classical + deep stereo matching |
| **C. Mono → depth** | One RGB → depth map | Foundation monocular depth models |
| **D. Depth → SBS** | Depth + RGB → synthetic L/R | Mesh/warp, dual-pixel, inpaint holes |

ELSD today: **A** (phone in large-tray viewer) + single camera.  
“Enhancement” for us usually means **C+D** (fake stereo from WORLD bus) or better **A** (correct Cardboard mesh).

---

## A. Display / runtime (open)

| Project | License | Role |
|---------|---------|------|
| **[Google Cardboard SDK](https://github.com/googlevr/cardboard)** | Apache-2.0 | Distortion meshes, head tracking, QR viewer params — **ELSD 1.0 path** |
| **OpenXR** (Khronos) | Spec + vendor runtimes | Modern XR standard; phone Cardboard is still the hobbyist path |
| **Godot / Filament / custom GLES** | Various OSS | Stereo cameras / dual viewport |

**SOTA for phone-in-box:** still Cardboard-class optics + correct barrel distortion + IPD; not a research frontier, but **must be correct** for comfort.

---

## B. True stereo matching (two views) — open SOTA direction

When you have **two** synchronized views (not OP12 single rear cam):

| Lineage | Notes |
|---------|--------|
| Classical: block matching, SGM (OpenCV) | Real-time CPU/GPU; brittle outdoors |
| Deep stereo: PSMNet, GANet, RAFT-Stereo, etc. | Higher quality; heavier |
| Surveys | [Awesome-Deep-Stereo-Matching](https://github.com/fabiotosi92/Awesome-Deep-Stereo-Matching) |
| **FoundationStereo**-class work | 2024–25 foundation models for stereo; research code quality varies |

**ELSD hardware note:** OnePlus 12 is **monocular** for the WORLD bus unless we use dual rear / ultrawide tricks (not reliable stereo baseline).

---

## C. Monocular depth — current open SOTA (2024–2026)

This is where “AI 2D→3D” energy is.

| Model / project | Strength | Real-time mobile? | License (check repo) |
|-----------------|----------|-------------------|----------------------|
| **[Depth Anything V2](https://depth-anything.github.io/)** | Strong zero-shot relative depth; multiple sizes | Small variants more plausible | Check official repo (often Apache-friendly research) |
| **[Depth Anything](https://depth-anything.github.io/)** (V1) | Large unlabeled training; better than older MiDaS in many scenes | Mid | Check repo |
| **[MiDaS](https://github.com/isl-org/MiDaS)** / DPT | Workhorse open depth; robust cross-dataset | Small models yes-ish | MIT-style research code historically |
| **Depth Pro** (Apple research paper/code waves) | Sharp metric depth, &lt;1s claims on desktop | Not phone-first | Check license carefully |
| **Metric3D v2**, **UniDepth**, **MoGe-2** | Metric scale / geometry focus | Desktop/research | Per-project |
| **DepthCrafter** | **Video** depth, temporal consistency | Offline / heavy | Diffusion-based → not live ELSD |
| **Marigold** | Diffusion depth quality | Slow | Research |

**Trend:** foundation **ViT** depth models + optional metric heads; **video depth** separate (consistency without flicker).  
**Mobile SOTA practice:** distill/quantize (TFLite / ONNX / NNAPI / QNN) small depth nets; 5–20 fps depth on half-res is a win; composite over 30–60 fps passthrough.

---

## D. Depth → stereoscopic pair (open techniques)

Pipeline used by open 2D→3D tools:

1. Estimate depth `Z(u,v)`  
2. Choose baseline `B` and focal `f`  
3. Warp RGB to left/right: disparity `d ≈ f·B/Z`  
4. Fill disocclusions (inpaint / depth-aware stretch)  
5. Output **SBS** or dual texture for Cardboard  

**Open code patterns:**

| Project / type | Notes |
|----------------|-------|
| OpenCV remapping + inpaint | Simple, controllable, MIT/Apache OpenCV |
| 3D photo / layered depth image (LDI) papers + OSS ports | Better holes; more complex |
| **Stereo from depth** shaders | GLES: sample with horizontal offset by depth — **ELSD-friendly** |
| Desktop converters (various GitHub “2D to 3D SBS”) | Often Python + MiDaS/DepthAnything; mixed licenses; many **not** mobile real-time |

Commercial/hybrid tools exist (e.g. creator-focused converters); prefer **re-implement warp in-engine** for MIT ELSD core.

---

## Other “3D enhancement” open areas

| Area | Open examples | ELSD relevance |
|------|---------------|----------------|
| **SLAM / pose** | ORB-SLAM3, OpenVSLAM, ARCore (not fully open) | World-lock audio/FX; registration |
| **Gaussian splatting / NeRF** | Many OSS viewers | Too heavy for live WORLD bus 1.0 |
| **Immersive video** | MV-HEVC, spatial video pipelines | Plate bus future |
| **Light field / multi-view** | Research | Out of phone-tray scope |

---

## What is actually “SOTA” for *live phone VR* (practical)

| Priority | Approach | Quality | Cost |
|----------|----------|---------|------|
| 1 | Correct **Cardboard SBS** of same image (no depth) | Low 3D, high comfort if optics good | Low — **1.0** |
| 2 | **Fixed parallax** / toe-in dual sample (fake depth) | Cheap pop | Low — **1.0 stretch** |
| 3 | **Mono depth (tiny)** + disparity warp @ reduced res | Real pop | Mid — **2.0** |
| 4 | Strong Depth Anything-class @ interactive rates + hole fill | Best mono→stereo | High — **2.0–3.0** |
| 5 | Dual cameras / true stereo match | Best geometry | Hardware — optional |
| 6 | Video diffusion depth | Offline plates | **3.0** plate tooling |

Comfort SOTA ≠ network SOTA: bad stereo (wrong IPD, infinite depth, window violations) causes nausea faster than flat.

---

## ELSD mapping

| Release | Stereo 3D work |
|---------|----------------|
| **1.0** | Cardboard SDK stereo submit; shared WORLD texture both eyes; optional mild fixed parallax; focus on **optics fit** (OP12 + large tray) |
| **2.0** | Optional **STEREO ENHANCE** switchboard effect: mobile depth (TFLite) + warp; phaseable; wet/dry |
| **3.0** | Higher-quality depth foundation models; DeepDream-in-depth; offline plate 2D→3D |

Switchboard verb (later): **ADD EFFECT STEREO** / **DEPTH POP** — not 1.0 critical path.

---

## Bookmark list (start here)

1. https://github.com/googlevr/cardboard  
2. https://github.com/isl-org/MiDaS  
3. https://depth-anything.github.io/ (V1/V2 papers + code links)  
4. https://github.com/fabiotosi92/Awesome-Deep-Stereo-Matching  
5. OpenCV stereo + remapping docs  
6. DepthCrafter (video, offline reference)

**License rule:** verify each weight/file before shipping in MIT APK; prefer Apache/MIT; document in NOTICE.

---

## Bottom line

- **Research SOTA:** monocular **foundation depth** (Depth Anything family, metric variants) + **video-consistent depth** for offline; deep **stereo matching** when two views exist.  
- **Open mobile SOTA practice:** small/quantized depth + disparity warp into SBS, or honest dual-render with Cardboard distortion.  
- **ELSD 1.0 SOTA we can own:** excellent Cardboard path + TOASTED effects; treat neural stereo as a **switchboard layer** after the desk ships.

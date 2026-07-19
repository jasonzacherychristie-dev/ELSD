# Amy — lowest-CPU render strategy

**Question:** Do we need a real-time 3D boing ball every frame?  
**Short answer:** **No.** And we already don’t.

---

## What Amy is *today* (and why it’s cheap)

Current `BounceCursor` is **not** a mesh scene, skinning, or PBR:

| Cost | Detail |
|------|--------|
| **Geometry** | One **triangle strip quad** (4 verts) |
| **Shading** | Tiny fragment shader: disc/sphere SDF + checker + light + lean |
| **Draw calls** | **1** after the WORLD pass |
| **Fill** | Small on-screen (size ~0.12–0.2 NDC) |

On a OnePlus 9, Amy is **noise** next to camera + Mandelbrot.  
Replacing her with a full real-time 3D ball (UV sphere, many fragments, maybe MSAA) would **cost more**, not less, for little gain.

**Lean-in** is free: uniforms (`uLean`) scale/tip the same quad.

So: **don’t “upgrade” to live 3D mesh** for performance. Only for fidelity — and even then, prefer sprites.

---

## Cost ladder (lowest → highest)

| Approach | Runtime CPU/GPU | Fidelity | When |
|----------|-----------------|----------|------|
| **A. Procedural quad (now)** | Lowest practical | Good stylized ball | **1.0 default** |
| **B. Pre-rendered sprite atlas / flipbook** | Extremely low (sample texture) | Highest control (Blender lighting, squash) | **1.0 polish / 1.1** after action catalog |
| **C. Pre-rendered WebP/APNG sequences** | Low; decode + blit | Cinematic | Optional |
| **D. Live 3D mesh ball** | Higher than A/B | “Real” 3D | **Avoid** for cursor |
| **E. Live 3D + physics + particles** | Wasteful for UI pilot | Overkill | **No** |

**Recommendation:** Keep **A** for 1.0 shipping path.  
Plan **B** once we freeze **Amy’s action list** and test timings on OP9.  
Prerender in **Blender** (or any DCC) → export sprite sheets → GLES samples atlas UVs.

---

## Blender prerender pipeline (planned)

### 1. Catalog actions first (don’t render yet)

Lock names, duration, loop vs one-shot:

| Action ID | Trigger | Length (hint) | Loop? |
|-----------|---------|---------------|-------|
| `idle_float` | default | 1–2 s cycle | yes |
| `lean_in` | speech start | 0.2–0.4 s | no → hold |
| `lean_hold` | listening | — | yes subtle |
| `lean_out` | speech end | 0.3–0.5 s | no → idle |
| `ack` | command understood | 0.15–0.25 s | no (squash) |
| `toasted_pride` | high wet / “toasted” | 0.4 s | no |
| `muted` | mute amy | static or micro | yes |
| `think` | optional stillness | slow | yes |
| `clear` | sober | settle | no |

**Process:** ship procedural + log real timings from OP9 sessions → adjust table → **then** Blender batch.

### 2. Blender scene (once catalog frozen)

- Classic **red/white check** sphere (procedural material or baked UVs)  
- Soft key light (match FOV-friendly look)  
- Transparent background (RGBA)  
- Orthographic or mild perspective, fixed camera  
- Render **square** frames (e.g. 256² or 128² — cursor size)  
- Actions as actions/NLA; export frame ranges  

### 3. Runtime pack

```
assets/amy/
  atlas.webp          # or PNG sheet
  amy.json            # frames: action → {x,y,w,h} or start,count,fps
```

`AmySprite` class:

- State machine: `idle` → `lean_in` → `lean_hold` → `lean_out`  
- Each frame: bind atlas, set UV rect, draw **same one quad**  
- Cost: **one textured quad** — usually **cheaper** than procedural checkers+lighting in frag

### 4. Hybrid (best of both)

| Layer | Source |
|-------|--------|
| Body ball | Pre-rendered idle / lean sheets |
| Listen rim | Procedural lime ring (1 uniform) or extra atlas ring |
| Fallback | Procedural if atlas missing (dev builds) |

---

## When to switch from procedural → sprites

| Gate | Ready? |
|------|--------|
| Action list agreed and timed on device | After GO LIVE testing |
| Lean-in / mute / ack feel right in procedural | Tuning first |
| OP9 still has headroom on WORLD+FX | Yes — Amy isn’t the bottleneck |
| Want “film” lighting / perfect squash | Blender time |

**Don’t block 1.0** on Blender. Amy’s job is readable pilot + lean-in; procedural does that.

---

## Decision

| Decision | Choice |
|----------|--------|
| 1.0 Amy | **Procedural checker quad** (current) |
| Live 3D mesh ball | **No** — wasteful for this role |
| Blender prerender | **Yes, after actions cataloged & tested** |
| Runtime after prerender | **Atlas flipbook on one quad** |

**One line:** *Amy should cost a stamp, not a scene. Procedural now; Blender flipbooks when her performance is locked.*

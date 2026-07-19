# 4.0 — Time travel

**Theme:** Transform the **local world** (live camera / eventual plate) into **any era** — past or future — while the user still stands in this room.

Not a cutscene. Not a skybox game level.  
**The same geometry you see**, re-skinned through time.

> ADD EFFECT TIME TRAVEL. SET ERA 1890. SET ERA 2140. CLEAR — back to now.

---

## Product definition

| Term | Meaning |
|------|---------|
| **Time travel** | Continuous visual (and later audio) remapping of WORLD into a chosen **era** |
| **Era** | Named temporal style + structure prior (Victorian street, 1970s interior, neon megacity, deep future ruin…) |
| **Local world** | What the camera sees *here* — walls, windows, people, sky — not a teleported empty void |
| **Anchor** | Pose / depth / planes so furniture stays furniture, doors stay doors |

**Lineage:** Ultimate Reality paper — “instantly transport yourself to another point in time or space.”  
**4.0 is time.** Space/telepresence can share the stack later.

---

## How it differs from earlier releases

| Release | What changes |
|---------|----------------|
| **1.0** | Grade / style / mood of *now* (Noir, Suspiria, Cartoon…) |
| **2.0** | Other video in/out; still *now* |
| **3.0** | Dream *into* the image (DeepDream, recursive morph) |
| **4.0** | Re-**era** the local world — structure-preserving temporal translation |

Silent Era and Technicolor in 1.0 are **grades**.  
4.0 **Silent City 1927** would rebuild materials, signage density, vehicles, atmosphere *as if that decade owned this street*.

---

## Switchboard vocabulary (4.0)

| Verb | Meaning |
|------|---------|
| **ADD EFFECT TIME TRAVEL** | Enable era engine |
| **REMOVE EFFECT TIME TRAVEL** | Off |
| **SET ERA &lt;name\|year&gt;** | Target epoch |
| **ERA STRENGTH** | Wet for temporal mix (0 = now, 1 = full era) |
| **ERA LOCK STRUCTURE** | Prefer geometry-safe warp (default on) |
| **ERA FREE HALLUCINATE** | Allow looser generative fill (riskier, striking) |
| **SAVE PRESET** / **LOAD PRESET** | e.g. `paris_1889`, `blade_//_this_room` |
| **CLEAR** | Now. Immediately. |

Amy (jokes on): *“Don’t step off the curb in 2140.”* / CLEAR: *“Welcome back.”*

---

## Technical sketch (not 1.0 work)

### Inputs

- LIVE WORLD (camera) or 2.0 plate  
- Pose / optional mono depth / planes (2.0–3.0 stereo path helps)  
- **Era token** (year, decade, named pack)  
- Stillness budget (hold still → finer temporal detail, same as morph levels)

### Pipeline (conceptual)

```
WORLD
  → structure (depth / normals / segments)     // keep layout
  → era prior (materials, light, props, sky)   // generative / retrieval
  → structure-preserving re-render / composite
  → optional 1.0 CINEMA/PALETTE on top of era
  → PGM
```

### Open research neighbors (track, don’t ship yet)

- Image/video **style + semantic** transfer with layout control  
- Depth / Gaussian / mesh proxies of the room  
- Text-conditioned video (era prompt) with **mask / depth conditioning**  
- On-device vs edge cloud (privacy: default **on-device or explicit opt-in cloud**)

**License / privacy:** camera never leaves device without opt-in; models in NOTICE.

### Safety

| Rule | Why |
|------|-----|
| CLEAR / motion can force **now** | Navigation safety |
| Era wet default &lt; 1 in outdoor walk | Don’t erase traffic reality by default |
| No “undismissable past” | Ethics of immersion |
| People: opt-in face/body era (default: grade only, not identity rewrite) | Consent |

---

## Era packs (examples — wishlist content)

| Pack | Vibe |
|------|------|
| `silent_1927` | Silver streets, signage, no modern glass glare story |
| `technicolor_1955` | Full dye-transfer suburb/city |
| `suspiria_1977` | Not just grade — theater-gel architecture fantasy |
| `fin_de_siecle` | Gaslight, iron, fog |
| `y2k_now` | Irony pack — slightly wrong 2000 |
| `near_future_2140` | Soft mega-structures, clean energy haze |
| `deep_time` | Geological / ruin / reclaimed city |

User can also **SET ERA 1842** as a year token if the model allows continuous time.

---

## Relationship to stillness morph

Staring at a still room in **TIME TRAVEL** mode:

- Hold longer → higher era strength / finer generative passes  
- Same MorphBudget L0–L4, but L4 becomes **era refine**, not only DeepDream  

DeepDream (3.0) can still run *inside* an era (dream in 1890).

---

## Release gate

Do **not** start 4.0 implementation until:

1. **1.0** visual desk ships  
2. **2.0** media + structure helpers exist (depth/key useful)  
3. **3.0** dream path proves neural composite is safe/exit-able  

4.0 is a **horizon major**, documented so it doesn’t pollute 1.0 tickets.

---

## Success test (4.0)

1. Stand in your living room. SET ERA 1927.  
2. Walls and windows remain navigable; light and materials become period.  
3. Walk; era holds without nausea-level lag.  
4. SET ERA 2140 — same room, future skin.  
5. CLEAR — present returns in under a second.

**One line:** *The room stays. The century changes.*

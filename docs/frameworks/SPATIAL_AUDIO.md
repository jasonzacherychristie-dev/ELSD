# Spatial audio hallucination mode

**Visual PULSE** makes buildings thump.  
**Spatial audio hallucination** makes the *room’s sound field lie* — sources orbit, walls answer, bass crawls behind your head, the plate becomes a ghost choir in 3D.

Same soul as Electronic LSD: **modulate perception of the real (or keyed) world**, don’t replace everything with a sealed game level.

Companion: [`VISUALIZERS.md`](VISUALIZERS.md) (analysis → bands).  
This doc: **bands + pose + synthesis → binaural / spatialized ears**.

---

## Product definition

| Term | Meaning |
|------|---------|
| **Spatial audio hallucination** | Intentional distortion of *where* and *how* sound exists around the listener |
| **Dry ears** | Normal stereo / mono device playback |
| **Wet ears** | Spatially processed mix (HRTF / panners / ambisonic field) driven by trip params |
| **Head-locked** | Field sticks to head (cheap, always works) |
| **World-locked** | Field sticks to room via gyro (true “ghost in the corner”) |

**Hardware note:** Target is **phone in large-tray headset** ([`TARGET_HARDWARE.md`](../design/TARGET_HARDWARE.md)).  
Spatial magic is best on **headphones / headset speakers sealed to ears**. Phone speakers alone are a weak fallback (stereo widen only).

---

## Modes (ship vocabulary)

| Mode ID | Voice-ish | What the user hears |
|---------|-----------|---------------------|
| `off` | `ears dry` / `spatial off` | Normal playback |
| `orbit` | `orbit` / `sounds spin` | Sources / reverb tail circle the head with bass rate |
| `behind` | `something behind me` | Energy and ghosts bias rear HRTF |
| `cathedral` | `cathedral` / `huge room` | Long wet reverb, elevated early reflections |
| `insect` | `insect ears` / `tiny` | Hyper-near field, skittering highs around skull |
| `ghost_choir` | `ghost choir` | Plate or grain layers duplicated as spatial voices |
| `bass_crawl` | `bass crawl` | Sub energy slowly pans world-locked around yaw |
| `amy_whisper` | `amy whisper` | Soft spatialized voice near lean-in cheek (opt-in, not default spam) |
| `world_lock` | `world lock audio` | Gyro-stabilize field to room |
| `hallucinate_ears` | `hallucinate ears` | Macro: wet ears + mild orbit + pulse-coupled |

**Couple to vision (optional dual wet):**

- Bass onset → visual City Pulse **and** spatial `behind` kick  
- Amy lean-in → slight **proximity** boost on a “listen” bed (not drowning commands)  
- `clear` / `sober` → **ears dry** hard-cut or soft-land with visuals  

---

## Signal architecture

```
  Mic (ambience) ──► BandEnergy ──┐
  Media3 plate   ──► PCM / stems ─┼──► SpatialHallucinationEngine
  Gyro / Cardboard pose ──────────┘         │
                                            ▼
                              wet binaural L/R  → AudioTrack / Oboe
                              (or Media3 + AudioProcessor chain)

  Voice commands ──► mix.spatialMode (separate from Visualizer drive)
```

**Do not** put voice recognition through the wet trip bus in a way that breaks intelligibility of `clear`.  
Command path stays dry or lightly ducked.

---

## Open stack (compatible with MIT app)

### Tier A — ship early (platform / simple)

| Tool | License | Use |
|------|---------|-----|
| **Stereo pan + delay + simple HRTF-ish filters** (our MIT) | MIT | M2.5 “fake spatial” — ITD/ILD, rear EQ, orbit LFO |
| **Android `AudioAttributes` / spatializer** (API 31+) | Platform | System spatializer when content is multi-channel |
| **Media3 `AudioProcessor`** | Apache-2.0 | Insert wet chain on plate bus |

Enough for: orbit, behind, bass crawl (head-locked).

### Tier B — real spatial (recommended 1.0 path)

| Tool | License | Use |
|------|---------|-----|
| **Google Resonance Audio** (open sourced) | Apache-2.0 | Object sources + ambisonics + HRTF, mobile-oriented |
| **Repo / docs** | | https://github.com/resonance-audio/resonance-audio · https://resonance-audio.github.io/resonance-audio/ |
| **Media3 spatial / ambisonics playback** | Apache-2.0 | Plate as ambisonic bed if we author FOA files |
| **Oboe** | Apache-2.0 | Low-latency wet output if AudioTrack is laggy |

### Tier C — research / later

| Tool | Note |
|------|------|
| **libspatialaudio / Ambix** | Ambisonic tooling; license check per component |
| **SOFA HRTF sets** | Personalization later; keep default MIT/Apache HRTF |
| **Steam Audio** | Powerful; license/engine weight — not required for 1.0 |
| **Jetpack XR spatial audio** | XR devices; not phone-in-Cardboard primary |

**Avoid in core:** GPL-only spatial stacks that force license change.

---

## Engine API (logical)

```text
SpatialHallucinationEngine
  setMode(id)
  setWet(0..1)          // couples to mix.wet or independent earWet
  setPose(yaw, pitch, roll)   // from SensorManager / Cardboard
  setBands(bass, mid, high, beat)
  submitPlate(pcm) / connectExoPlayer(session)
  submitAmbience(pcm)   // optional mic bed
  setMuted(safety)      // clear → dry
```

Presets may set `visualPulse + spatialMode` together (`quiet_toasted` can be eyes-wet ears-wet or eyes-wet ears-dry).

---

## Safety & ethics

| Rule | Why |
|------|-----|
| `clear` / `sober` / `stop` dry **ears and eyes** | Exit must work |
| No default continuous whispering that masks alarms | Real-world safety |
| Cap sudden rear jumps | Startle / vestibular distress |
| Session timer applies to wet ears | Fatigue |
| Document headphone recommendation | Expectation management |
| Don’t claim clinical “therapy audio” | Mission non-goal |

---

## Milestone hookup

| Milestone | Spatial audio deliverable |
|-----------|---------------------------|
| **M2.5** | MIT stereo orbit/behind + BandEnergy-driven pan; voice `hallucinate ears` / `ears dry` |
| **M3** | Couple City Pulse ↔ ear wet; gyro yaw for world-ish crawl |
| **M4** | Soft-land ears; Amy proximity bed optional |
| **M5** | Cardboard head pose → full world-lock; evaluate Resonance Audio NDK |

---

## Voice grammar (additions)

```
hallucinate ears / spatial on / wet ears
ears dry / spatial off / dry ears
orbit / something behind me / cathedral / insect ears
bass crawl / ghost choir / world lock audio
```

---

## Soul line

Visual hallucination: *“Bass has the building.”*  
Spatial hallucination: *“Something moved behind the drywall.”*

Both are TOASTED. Both answer to **clear**.

# PRESETS / USER SAVES

First-class on the **switchboard** and via **prefs-style voice**.

---

## Storage

| Kind | Path | Notes |
|------|------|--------|
| **FACTORY** | `files/presets/factory/*.json` | Shipped looks; re-seeded if missing; not deleted by user UI |
| **USER** | `files/presets/user/*.json` | **SAVE PREFS** / **SAVE PRESET** |

Load prefers **user** over factory when names collide.

---

## Switchboard

Main board shows:

```
PRESETS / USER SAVES  ·  factory N  ·  user M
Active: NOIR_NIGHT
```

| Control | Action |
|---------|--------|
| **PRESETS** | Hub: list USER + FACTORY, load, delete user |
| **SAVE PREFS** | Name → write USER save |
| Load row | Apply board + return to switchboard |
| Delete row | USER only |

---

## Voice / prefs vocabulary

| Say | Does |
|-----|------|
| `save prefs` | USER save as `user_save` |
| `save prefs walk` / `save preset walk` / `save as walk` | USER save named |
| `save my prefs night` | USER save |
| `load prefs` / `load last` / `load my prefs` | Load `user_save` |
| `load preset noir_night` / `load prefs noir_night` / `open preset …` / `recall …` | Load by name |
| `list presets` / `my presets` / `show presets` | Summarize factory + user |
| `delete preset walk` / `delete save walk` | Delete USER only |

---

## 1.0 rule

Presets are **boards** (layers + fps + drops + fractal key + wet) — full switchboard snapshot, not app settings noise.

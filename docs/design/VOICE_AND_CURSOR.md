# Voice & Bounce — Interaction Spec (1.0 target)

Companion to [`SOUL.md`](SOUL.md). Engineering-facing.

## Principles

1. **Voice is the only first-class input** for ELSD commands.  
2. **Bounce** is the only first-class pointer / companion.  
3. Touch is not offered as a control surface (platform dialogs excepted).  
4. Bounce can be **muted to background** without killing the mix.  
5. Safety phrases preempt personality.

## Bounce states

```
FLAT ──voice "bounce 3d"──► SPATIAL
  ▲                            │
  └──── "bounce flat" ─────────┘
  │
  ├── "mute bounce" / "quiet" ──► MUTED
  └── "bounce on" / "hey bounce" ◄── MUTED
```

| State | Draw | Voice lines | Aim assist |
|-------|------|-------------|------------|
| FLAT | 2D checker disc, stereo-matched | Full personality | Screen-space |
| SPATIAL | 3D checker sphere + light bounce | Full | World-ish (bright/sky/edge magnets) |
| MUTED | Tiny pip or hidden | None (except safety confirm once) | None |

## Listening UX

- Continuous or push-to-listen via phrase `hey bounce` (decide in M4; prefer low-power duty cycle).  
- **Lime check flash** = partial/final heard.  
- Fail: Bounce *once* — *“Say again?”* — no loop nagging.

## Command priority

1. Safety: `stop` / `clear` / `sober`  
2. Bounce presence: mute/on  
3. Macro presets  
4. Bank + param  
5. Chatter / help  

## TOASTED feedback

When wet crosses a high threshold or user says `toasted`:

- Brief bank-light cascade  
- Optional sting  
- Bounce squash  

## Fourth wall budget

- Max **one** unsolicited line per 60s  
- Never during soft landing  
- `serious mode` / `no jokes` → disable quips until `fun mode`

## Accessibility

- Voice-only must still work if TTS is off (tone stings + visual Bounce only)  
- Captions optional: open captions along bottom when `captions on`  
- High-contrast Bounce alternate: gold/black checks if red/white fails contrast

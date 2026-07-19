# Import: negative

## Intake

| | |
|--|--|
| **Working title** | Negative |
| **Bank** | DESK (canonical live-desk FX) |
| **Source** | Original for ELSD — classic invert, no third-party code |
| **License** | MIT (project) |
| **Complexity** | Simple |
| **Role** | **Pilot** for [`SHADER_IMPORT.md`](../../../design/SHADER_IMPORT.md) |

## Normalize

- GLSL ES 3.00 stage function  
- Input: current color `c` (already WORLD-sampled upstream)  
- No loops, no extra textures  

## Massage

```glsl
vec3 applyNegative(vec3 c) {
    return mix(c, 1.0 - c, uWet);
}
```

## Wire

- `EffectId.NEGATIVE` · family `DESK`  
- `uDesk` index or via `uLsd` / dedicated desk path — see `toasted_composite.frag`  
- Voice: `add negative`  

## Verify

- [ ] Wet 0 = identity  
- [ ] Wet 1 = full invert  
- [ ] OP9 fine at 60  

## Notes

No third-party attribution required. Established the folder + checklist.  
Next full import folder after pilot: **solarize** — [`../solarize/SOURCE.md`](../solarize/SOURCE.md).

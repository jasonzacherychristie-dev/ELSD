# Import: solarize

## Intake

| | |
|--|--|
| **Working title** | Solarize |
| **Bank** | DESK |
| **Source** | Photographic **Sabattier / solarization** process (19th c. technique — not a software copyright). GLSL written original for ELSD. |
| **Third-party code** | **None** — no Shadertoy paste; algorithm is public-domain knowledge |
| **License** | MIT with project (original implementation) |
| **Complexity** | Simple |
| **Role** | First **post-pilot** full import folder after `negative` — exercises checklist under size budget |

## Normalize

- GLSL ES 3.00 stage fragment only  
- Input: graded color `c` already in desk stage  
- No extra textures, no loops  

## Massage

```glsl
// Luma-threshold solarize: brights fold toward invert (Sabattier-ish)
vec3 applySolarize(vec3 c, float w) {
    float l = dot(c, vec3(0.299, 0.587, 0.114));
    float thr = mix(0.72, 0.38, w);          // wet lowers threshold → more solar
    float k = smoothstep(thr - 0.08, thr + 0.12, l);
    vec3 inv = 1.0 - c;
    vec3 sol = mix(c, inv, k);
    // slight mid contrast for classic print bite
    sol = mix(sol, sol * sol * (3.0 - 2.0 * sol), 0.15 * w);
    return mix(c, sol, w);
}
```

## Wire

- `EffectId.SOLARIZE` · `DESK`  
- `uDesk == 8` in `toasted_composite.frag`  
- Voice: `add solarize` / `solarize`  
- RANDOM desk toys include solarize  

## Verify

- [x] Wet 0 ≈ identity  
- [x] Wet 1 = strong solar on brights  
- [x] No extra FBO / no heavy loops (OP9-safe)  
- [ ] Device smoke on OP9/OP12 when next sideload  

## Notes

Complements `negative` (full invert) and `posterize` (bands). Desk-color-map P0 from historical chroma-treatment lists — not a trademarked filter name in UI.

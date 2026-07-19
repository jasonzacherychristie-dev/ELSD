// Archived apply-only form (ELSD composite uses inline uDesk==8).
// See SOURCE.md — original MIT implementation, photographic technique.

vec3 applySolarize(vec3 c, float w) {
    float l = dot(c, vec3(0.299, 0.587, 0.114));
    float thr = mix(0.72, 0.38, w);
    float k = smoothstep(thr - 0.08, thr + 0.12, l);
    vec3 inv = 1.0 - c;
    vec3 sol = mix(c, inv, k);
    sol = mix(sol, sol * sol * (3.0 - 2.0 * sol), 0.15 * w);
    return mix(c, sol, w);
}

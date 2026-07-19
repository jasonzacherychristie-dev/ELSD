#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

uniform samplerExternalOES uWorld;
uniform sampler2D uPlate;
uniform int uHasPlate;
uniform float uTime;
uniform float uWet;
uniform float uBass;
uniform int uKeyMode;   // 0 off, 1 luma, 2 chroma
uniform float uChromaHue;
uniform int uPulse;
uniform int uPaint;     // 0 none, 1 gogh, 2 monet, 3 noir, 4 neon, 5 sketch, 6 melt
uniform int uLsd;       // 0 none, 1 trail, 2 hue, 3 split, 4 kaleido, 5 melt

in vec2 vUv;
out vec4 oColor;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec3 p = abs(fract(c.xxx + vec3(0.0, 2.0 / 3.0, 1.0 / 3.0)) * 6.0 - 3.0);
    return c.z * mix(vec3(1.0), clamp(p - 1.0, 0.0, 1.0), c.y);
}

float luma(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

vec3 applyPaint(vec3 c, vec2 uv) {
    if (uPaint == 0) return c;
    if (uPaint == 1) {
        // Gogh-ish: swirl + saturation
        vec2 d = uv - 0.5;
        float a = 0.35 * uWet * sin(uTime * 0.7 + length(d) * 12.0);
        float ca = cos(a), sa = sin(a);
        vec2 suv = vec2(ca * d.x - sa * d.y, sa * d.x + ca * d.y) + 0.5;
        c = texture(uWorld, suv).rgb;
        vec3 hsv = rgb2hsv(c);
        hsv.y = clamp(hsv.y * 1.35, 0.0, 1.0);
        hsv.z = pow(hsv.z, 0.9);
        return hsv2rgb(hsv);
    }
    if (uPaint == 2) {
        // Monet soft dabs
        vec2 px = vec2(0.004, 0.004);
        vec3 acc = c;
        acc += texture(uWorld, uv + px).rgb;
        acc += texture(uWorld, uv - px).rgb;
        acc += texture(uWorld, uv + px.yx).rgb;
        acc *= 0.25;
        vec3 hsv = rgb2hsv(acc);
        hsv.y *= 0.85;
        return hsv2rgb(hsv);
    }
    if (uPaint == 3) {
        float y = luma(c);
        y = smoothstep(0.1, 0.9, y);
        return vec3(y);
    }
    if (uPaint == 4) {
        vec3 hsv = rgb2hsv(c);
        hsv.x = fract(hsv.x + 0.08);
        hsv.y = clamp(hsv.y * 1.4, 0.0, 1.0);
        vec3 n = hsv2rgb(hsv);
        float b = smoothstep(0.6, 1.0, luma(c));
        return n + b * vec3(0.2, 0.35, 0.5) * uWet;
    }
    if (uPaint == 5) {
        float e = length(vec2(
            luma(texture(uWorld, uv + vec2(0.002, 0.0)).rgb) - luma(texture(uWorld, uv - vec2(0.002, 0.0)).rgb),
            luma(texture(uWorld, uv + vec2(0.0, 0.002)).rgb) - luma(texture(uWorld, uv - vec2(0.0, 0.002)).rgb)
        ));
        float ink = smoothstep(0.02, 0.12, e);
        return mix(vec3(0.94, 0.92, 0.88), vec3(0.08), ink);
    }
    if (uPaint == 6) {
        vec2 d = uv - 0.5;
        float w = 0.02 * uWet * sin(uTime + d.y * 20.0);
        return texture(uWorld, uv + vec2(w, -w * 0.5)).rgb;
    }
    return c;
}

vec3 applyLsd(vec3 c, vec2 uv) {
    if (uLsd == 0) return c;
    float w = uWet;
    if (uLsd == 1) {
        // Pseudo trail via time-shifted UV (true feedback FBO in M2+)
        vec2 off = 0.01 * w * vec2(sin(uTime * 2.0), cos(uTime * 1.7));
        vec3 past = texture(uWorld, uv - off).rgb;
        return mix(c, max(c, past), 0.55 * w);
    }
    if (uLsd == 2) {
        vec3 hsv = rgb2hsv(c);
        hsv.x = fract(hsv.x + uTime * 0.05 * w);
        return hsv2rgb(hsv);
    }
    if (uLsd == 3) {
        float s = 0.006 * w;
        return vec3(
            texture(uWorld, uv + vec2(s, 0.0)).r,
            c.g,
            texture(uWorld, uv - vec2(s, 0.0)).b
        );
    }
    if (uLsd == 4) {
        vec2 d = uv - 0.5;
        float r = length(d);
        float ang = atan(d.y, d.x);
        float segments = 6.0;
        ang = mod(ang, 6.28318 / segments);
        ang = abs(ang - 3.14159 / segments);
        vec2 kuv = vec2(cos(ang), sin(ang)) * r + 0.5;
        return mix(c, texture(uWorld, kuv).rgb, w);
    }
    if (uLsd == 5) {
        vec2 d = uv - 0.5;
        float m = 0.03 * w * sin(10.0 * d.x + uTime) * cos(8.0 * d.y - uTime);
        return texture(uWorld, uv + d * m).rgb;
    }
    return c;
}

vec3 applyPulse(vec3 c, vec2 uv) {
    if (uPulse == 0) return c;
    float e = length(vec2(
        luma(texture(uWorld, uv + vec2(0.003, 0.0)).rgb) - luma(texture(uWorld, uv - vec2(0.003, 0.0)).rgb),
        luma(texture(uWorld, uv + vec2(0.0, 0.003)).rgb) - luma(texture(uWorld, uv - vec2(0.0, 0.003)).rgb)
    ));
    float thump = 0.5 + 0.5 * sin(uTime * 4.0 + uBass * 6.0);
    float edge = smoothstep(0.02, 0.15, e) * thump * uWet;
    float bright = smoothstep(0.55, 0.95, luma(c)) * (0.15 + 0.25 * thump) * uWet;
    return c * (1.0 + edge * 0.45 + bright * 0.35);
}

void main() {
    vec2 uv = vUv;
    vec3 world = texture(uWorld, uv).rgb;
    vec3 plate = world;
    if (uHasPlate == 1) {
        plate = texture(uPlate, uv).rgb;
    }

    vec3 base = world;
    // KEY
    if (uKeyMode == 1 && uHasPlate == 1) {
        float m = smoothstep(0.45, 0.75, luma(world));
        base = mix(world, plate, m * uWet);
    } else if (uKeyMode == 2 && uHasPlate == 1) {
        vec3 hsv = rgb2hsv(world);
        float dist = abs(hsv.x - uChromaHue);
        dist = min(dist, 1.0 - dist);
        float m = smoothstep(0.12, 0.02, dist) * smoothstep(0.15, 0.45, hsv.y);
        base = mix(world, plate, m * uWet);
    }

    vec3 painted = applyPaint(base, uv);
    // When paint samples world itself, blend with wet
    painted = mix(base, painted, uWet);

    vec3 pulsed = applyPulse(painted, uv);
    vec3 tripped = applyLsd(pulsed, uv);
    vec3 pgm = mix(world, tripped, clamp(uWet, 0.0, 1.0));

    oColor = vec4(pgm, 1.0);
}

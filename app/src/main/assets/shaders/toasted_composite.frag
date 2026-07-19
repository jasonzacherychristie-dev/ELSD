#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

// 1.0 visual path: WORLD → PALETTE → MOOD → ART → CINEMA → ELSD → PGM
uniform samplerExternalOES uWorld;
uniform sampler2D uPlate;
uniform int uHasPlate;
uniform float uTime;
uniform float uWet;
uniform float uBass;
uniform int uKeyMode;
uniform float uChromaHue;
uniform int uPulse;
// ART: 0 none, 1 gogh, 2 monet, 3 noir, 4 neon, 5 sketch, 6 melt, 7 comic, 8 cartoon, 9 hyperreal
uniform int uPaint;
uniform int uLsd;       // ELSD: 0 none, 1 trail, 2 hue, 3 split, 4 kaleido, 5 melt
// cinema: 0 none, 1 noir, 2 neon, 3 bleach, 4 teal_orange, 5 anamorphic, 6 soft_glow,
// 7 technicolor, 8 suspiria, 9 silent_era, 10 expressionist, 11 giallo, 12 golden_age
uniform int uCinema;
uniform int uPalette;   // 0 none, 1 film, 2 vhs, 3 digital_clean, 4 digital_harsh, 5 polaroid
uniform int uMood;      // 0 neutral, 1 calm, 2 warm, 3 cold, 4 night, 5 fever, 6 toasted

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

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec3 applyPalette(vec3 c, vec2 uv) {
    if (uPalette == 0) return c;
    float g = hash(uv * vec2(640.0, 360.0) + floor(uTime * 24.0));
    if (uPalette == 1) {
        // analog film
        c *= vec3(1.05, 1.0, 0.95);
        c = mix(c, c * c, 0.12);
        c += (g - 0.5) * 0.06;
        float v = smoothstep(0.85, 0.4, length(uv - 0.5));
        c *= mix(0.75, 1.0, v);
        return c;
    }
    if (uPalette == 2) {
        // VHS
        float line = hash(vec2(floor(uv.y * 240.0), floor(uTime * 30.0)));
        c.r = texture(uWorld, uv + vec2(0.003 * uWet, 0.0)).r;
        c.b = texture(uWorld, uv - vec2(0.003 * uWet, 0.0)).b;
        c = mix(c, vec3(luma(c)), 0.15);
        c *= vec3(1.08, 0.98, 0.9);
        c += (line - 0.5) * 0.08 * uWet;
        return c;
    }
    if (uPalette == 3) {
        // digital clean
        c = (c - 0.5) * 1.05 + 0.5;
        return clamp(c, 0.0, 1.0);
    }
    if (uPalette == 4) {
        // digital harsh
        c = pow(max(c, 0.0), vec3(0.85));
        float e = length(vec2(
            luma(texture(uWorld, uv + vec2(0.002, 0.0)).rgb) - luma(c),
            luma(texture(uWorld, uv + vec2(0.0, 0.002)).rgb) - luma(c)
        ));
        c += e * 0.35;
        c.b *= 1.05;
        return c;
    }
    if (uPalette == 5) {
        // polaroid
        c *= vec3(1.12, 1.02, 0.88);
        c = mix(c, vec3(1.0, 0.95, 0.85), 0.08);
        float v = smoothstep(0.9, 0.35, length(uv - 0.5));
        c = mix(vec3(0.15, 0.12, 0.1), c, v);
        return c;
    }
    return c;
}

vec3 applyMood(vec3 c) {
    if (uMood == 0) return c;
    vec3 hsv = rgb2hsv(c);
    if (uMood == 1) {
        // calm
        hsv.y *= 0.75;
        hsv.z = mix(hsv.z, 0.55, 0.15);
        return hsv2rgb(hsv);
    }
    if (uMood == 2) {
        // warm
        c *= vec3(1.12, 1.02, 0.88);
        return c;
    }
    if (uMood == 3) {
        // cold
        c *= vec3(0.9, 0.98, 1.12);
        return c;
    }
    if (uMood == 4) {
        // night
        c = pow(max(c, 0.0), vec3(1.25));
        c *= vec3(0.85, 0.95, 1.15);
        return c;
    }
    if (uMood == 5) {
        // fever
        hsv.y = clamp(hsv.y * 1.35, 0.0, 1.0);
        hsv.x = fract(hsv.x + 0.02 * sin(uTime * 2.0));
        return hsv2rgb(hsv);
    }
    if (uMood == 6) {
        // toasted — on-air warmth
        c *= vec3(1.08, 0.98, 0.9);
        c = mix(c, c * vec3(1.1, 0.85, 0.7), 0.12);
        return c;
    }
    return c;
}

vec3 applyPaint(vec3 c, vec2 uv) {
    if (uPaint == 0) return c;
    if (uPaint == 1) {
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
        vec2 px = vec2(0.004);
        vec3 acc = c + texture(uWorld, uv + px).rgb + texture(uWorld, uv - px).rgb + texture(uWorld, uv + px.yx).rgb;
        acc *= 0.25;
        vec3 hsv = rgb2hsv(acc);
        hsv.y *= 0.85;
        return hsv2rgb(hsv);
    }
    if (uPaint == 3) {
        float y = smoothstep(0.1, 0.9, luma(c));
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
    if (uPaint == 7) {
        // comic / Lichtenstein-ish: hard ink + flat cells
        float e = length(vec2(
            luma(texture(uWorld, uv + vec2(0.002, 0.0)).rgb) - luma(texture(uWorld, uv - vec2(0.002, 0.0)).rgb),
            luma(texture(uWorld, uv + vec2(0.0, 0.002)).rgb) - luma(texture(uWorld, uv - vec2(0.0, 0.002)).rgb)
        ));
        float ink = smoothstep(0.03, 0.1, e);
        vec3 flat = floor(c * 4.0) / 4.0;
        float dots = step(0.55, hash(floor(uv * vec2(90.0, 90.0))));
        flat = mix(flat, flat * 0.85, dots * 0.35);
        return mix(c, mix(flat, vec3(0.05), ink), uWet);
    }
    if (uPaint == 8) {
        // cartoon — cel shade, bold outlines, flat fills
        float y = luma(c);
        // quantize value into cel bands
        float bands = 4.0;
        float cel = floor(y * bands) / bands + 0.5 / bands;
        vec3 hsv = rgb2hsv(c);
        hsv.y = clamp(hsv.y * 1.25, 0.0, 1.0);
        hsv.z = cel;
        vec3 fill = hsv2rgb(hsv);
        // thick outline from depth-of-edge
        float e = length(vec2(
            luma(texture(uWorld, uv + vec2(0.0035, 0.0)).rgb) - y,
            luma(texture(uWorld, uv + vec2(0.0, 0.0035)).rgb) - y
        ));
        float line = smoothstep(0.04, 0.12, e);
        // slight “toon” specular disk
        float spec = smoothstep(0.75, 0.95, y) * 0.15;
        fill += spec;
        vec3 toon = mix(fill, vec3(0.08, 0.07, 0.1), line);
        return mix(c, toon, uWet);
    }
    if (uPaint == 9) {
        // hyperreal — micro-contrast, clarity, pore-sharp, mild HDR pop
        float y = luma(c);
        // unsharp / local contrast
        vec3 blur = c;
        blur += texture(uWorld, uv + vec2(0.002, 0.0)).rgb;
        blur += texture(uWorld, uv - vec2(0.002, 0.0)).rgb;
        blur += texture(uWorld, uv + vec2(0.0, 0.002)).rgb;
        blur += texture(uWorld, uv - vec2(0.0, 0.002)).rgb;
        blur *= 0.2;
        vec3 hi = c + (c - blur) * (1.4 * uWet);
        // clarity curve
        hi = (hi - 0.5) * (1.0 + 0.25 * uWet) + 0.5;
        // selective sat: midtones richer, not clownish
        vec3 hsv = rgb2hsv(clamp(hi, 0.0, 1.0));
        float mid = smoothstep(0.15, 0.4, y) * (1.0 - smoothstep(0.65, 0.95, y));
        hsv.y = clamp(hsv.y * (1.0 + 0.2 * mid * uWet), 0.0, 1.0);
        hsv.z = pow(hsv.z, 0.95);
        vec3 hr = hsv2rgb(hsv);
        // fine grain of “sensor” detail
        hr += (hash(uv * vec2(1200.0, 800.0) + uTime) - 0.5) * 0.02 * uWet;
        return mix(c, clamp(hr, 0.0, 1.0), uWet);
    }
    return c;
}

vec3 applyCinema(vec3 c, vec2 uv) {
    if (uCinema == 0) return c;
    if (uCinema == 1) {
        float y = smoothstep(0.08, 0.92, luma(c));
        return vec3(y);
    }
    if (uCinema == 2) {
        vec3 hsv = rgb2hsv(c);
        hsv.y = clamp(hsv.y * 1.3, 0.0, 1.0);
        vec3 n = hsv2rgb(hsv);
        float b = smoothstep(0.55, 1.0, luma(c));
        return n + b * vec3(0.25, 0.1, 0.4) * uWet;
    }
    if (uCinema == 3) {
        // bleach bypass
        float y = luma(c);
        return mix(c, vec3(y), 0.55) * 1.1;
    }
    if (uCinema == 4) {
        // teal orange
        float y = luma(c);
        vec3 shadows = mix(c, vec3(0.2, 0.45, 0.5), 0.35);
        vec3 highs = mix(c, vec3(1.0, 0.75, 0.45), 0.35);
        return mix(shadows, highs, smoothstep(0.25, 0.75, y));
    }
    if (uCinema == 5) {
        // anamorphic streak on brights
        float b = smoothstep(0.7, 1.0, luma(c));
        float streak = 0.0;
        for (int i = 1; i <= 4; i++) {
            float o = float(i) * 0.004;
            streak += luma(texture(uWorld, uv + vec2(o, 0.0)).rgb);
            streak += luma(texture(uWorld, uv - vec2(o, 0.0)).rgb);
        }
        streak = (streak / 8.0) * b * uWet;
        return c + vec3(streak * 0.9, streak * 0.75, streak * 0.5);
    }
    if (uCinema == 6) {
        // soft glow
        vec3 blur = c;
        blur += texture(uWorld, uv + vec2(0.003, 0.0)).rgb;
        blur += texture(uWorld, uv - vec2(0.003, 0.0)).rgb;
        blur += texture(uWorld, uv + vec2(0.0, 0.003)).rgb;
        blur += texture(uWorld, uv - vec2(0.0, 0.003)).rgb;
        blur *= 0.2;
        float b = smoothstep(0.5, 1.0, luma(c));
        return mix(c, max(c, blur), 0.45 * b * uWet);
    }
    if (uCinema == 7) {
        // Technicolor 3-strip: push primaries, slight dye-transfer bloom
        vec3 p = c;
        p.r = pow(clamp(p.r * 1.15, 0.0, 1.0), 0.92);
        p.g = pow(clamp(p.g * 1.08, 0.0, 1.0), 0.95);
        p.b = pow(clamp(p.b * 1.12, 0.0, 1.0), 0.9);
        // separate dye feel
        p = mix(p, vec3(p.r, luma(p), p.b), 0.12);
        float b = smoothstep(0.55, 1.0, luma(p));
        p += b * 0.06 * vec3(1.0, 0.9, 0.7);
        return mix(c, p, uWet);
    }
    if (uCinema == 8) {
        // Suspiria — Argento: blood reds, unnatural primaries, expressionist pools
        vec3 hsv = rgb2hsv(c);
        float redBias = smoothstep(0.9, 0.0, min(abs(hsv.x - 0.0), abs(hsv.x - 1.0)));
        float blueBias = smoothstep(0.15, 0.0, abs(hsv.x - 0.6));
        hsv.y = clamp(hsv.y * (1.25 + 0.55 * redBias), 0.0, 1.0);
        hsv.z = pow(hsv.z, 0.88);
        vec3 p = hsv2rgb(hsv);
        p.r = clamp(p.r * (1.15 + 0.35 * redBias * uWet), 0.0, 1.0);
        p.b = clamp(p.b * (1.05 + 0.25 * blueBias), 0.0, 1.0);
        p.g *= 0.92;
        // crushed corners like theatrical gels
        float v = smoothstep(0.95, 0.35, length(uv - 0.5));
        p *= mix(vec3(0.55, 0.15, 0.35), vec3(1.0), v);
        return mix(c, p, uWet);
    }
    if (uCinema == 9) {
        // Silent era — silver screen, flicker, soft iris, slight gate weave
        float flicker = 0.92 + 0.08 * hash(vec2(floor(uTime * 16.0), 0.3));
        float weave = (hash(vec2(floor(uTime * 12.0), 1.7)) - 0.5) * 0.004 * uWet;
        vec3 src = texture(uWorld, uv + vec2(weave, 0.0)).rgb;
        float y = luma(src);
        // orthochromatic-ish: darken reds slightly in the grey
        y = mix(y, y * 0.85, smoothstep(0.5, 0.0, abs(rgb2hsv(src).x)));
        y = smoothstep(0.05, 0.92, y);
        y *= flicker;
        float iris = smoothstep(0.98, 0.42, length(uv - 0.5));
        y *= mix(0.15, 1.0, iris);
        // film scratches
        float scratch = step(0.997, hash(vec2(uv.x * 3.0, floor(uTime * 24.0))));
        y = max(y, scratch * 0.85);
        return mix(c, vec3(y), uWet);
    }
    if (uCinema == 10) {
        // German Expressionist — hard shadows, tilted contrast, sickly mid lift
        float y = luma(c);
        float hard = smoothstep(0.25, 0.55, y);
        vec3 p = mix(vec3(0.02, 0.03, 0.05), vec3(0.95, 0.92, 0.85), hard);
        // jagged edge emphasis
        float e = length(vec2(
            luma(texture(uWorld, uv + vec2(0.003, 0.0)).rgb) - y,
            luma(texture(uWorld, uv + vec2(0.0, 0.003)).rgb) - y
        ));
        p = mix(p, vec3(0.0), smoothstep(0.05, 0.2, e) * 0.65 * uWet);
        float v = smoothstep(1.0, 0.3, length(uv - vec2(0.45, 0.4)));
        p *= mix(0.4, 1.0, v);
        return mix(c, p, uWet);
    }
    if (uCinema == 11) {
        // Giallo — yellow title heat, stylish violence of color (cousin to Suspiria)
        vec3 hsv = rgb2hsv(c);
        hsv.y = clamp(hsv.y * 1.3, 0.0, 1.0);
        vec3 p = hsv2rgb(hsv);
        p = mix(p, vec3(1.0, 0.9, 0.2), 0.08 * uWet);
        p.r *= 1.1;
        p.b *= 0.95;
        float b = smoothstep(0.6, 1.0, luma(p));
        p += b * vec3(0.15, 0.05, 0.1) * uWet;
        return mix(c, p, uWet);
    }
    if (uCinema == 12) {
        // Golden Age studio — soft glam, warm key, gentle veil
        vec3 p = c * vec3(1.08, 1.02, 0.92);
        vec3 blur = p;
        blur += texture(uWorld, uv + vec2(0.004, 0.0)).rgb;
        blur += texture(uWorld, uv - vec2(0.004, 0.0)).rgb;
        blur *= 0.33;
        float skin = smoothstep(0.35, 0.75, luma(c));
        p = mix(p, max(p, blur), 0.35 * skin * uWet);
        float v = smoothstep(0.9, 0.45, length(uv - 0.5));
        p *= mix(0.85, 1.0, v);
        return mix(c, p, uWet);
    }
    return c;
}

vec3 applyLsd(vec3 c, vec2 uv) {
    if (uLsd == 0) return c;
    float w = uWet;
    if (uLsd == 1) {
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

void main() {
    vec2 uv = vUv;
    vec3 world = texture(uWorld, uv).rgb;

    vec3 graded = applyPalette(world, uv);
    graded = applyMood(graded);

    vec3 art = applyPaint(graded, uv);
    art = mix(graded, art, uWet);

    vec3 cine = applyCinema(art, uv);
    cine = mix(art, cine, uWet * 0.85);

    vec3 tripped = applyLsd(cine, uv);
    vec3 pgm = mix(world, tripped, clamp(uWet, 0.0, 1.0));

    oColor = vec4(pgm, 1.0);
}

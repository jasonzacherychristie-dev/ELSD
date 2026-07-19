# Security Policy

## Supported versions

ELSD is in public alpha. Security fixes target the default `main` branch.

## Reporting a vulnerability

Please **do not** open a public issue for sensitive security reports.

Prefer:

1. GitHub **Private vulnerability reporting** on this repository (if enabled), or  
2. Contact the maintainer via their GitHub profile: [jasonzacherychristie-dev](https://github.com/jasonzacherychristie-dev)

Include:

- Description and impact  
- Reproduction steps  
- Affected commit / APK if known  

We will acknowledge when we can and work on a fix before any coordinated disclosure.

## Product safety (immersive media)

ELSD alters perception (camera FX, flashing, audio-reactive effects). That is not a “CVE” class issue, but it is a **user safety** concern:

- Defaults must not trap users without `clear` / `sober` / `stop`  
- Seizure-adjacent strobe defaults are forbidden  
- Camera/mic data must not leave the device without explicit, documented, opt-in design  

Report product-safety design flaws as security or high-priority bugs.

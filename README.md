# GCB Atlas Fix

Minecraft 26.3 (Fabric) client mod for players whose GPU cannot hold the huge texture atlases built from large server resource packs (e.g. GTX 970: max texture size 16384).

Symptoms it fixes: crash `Texture view does not exist` / `GL_INVALID_VALUE Invalid texture dimensions`, terrain rendered as flat brown/black blocks, crashes while loading the resource pack.

## What it does
1. Caps the atlas size at `maxAtlasSize` (default 16384). Some older NVIDIA drivers report that 32768 works when creating it actually fails.
2. If the sprites do not fit, halves every non-vanilla sprite (frame by frame, animation metadata kept) and tries again, up to 6 times, until the atlas fits.

If everything already fits, nothing changes. Only install it if you have the problem.

## Install
Fabric Loader 0.19+, Minecraft 26.3, Java 25. Put `gcbatlasfix-1.0.0.jar` into `mods/`.

## Config (`config/gcbatlasfix.json`)
- `maxAtlasSize` — largest atlas side in pixels (default 16384)
- `minSpriteSize` — sprites are not shrunk below this frame size (default 16)
- `keepNamespaces` — namespaces never shrunk (default `["minecraft"]`)

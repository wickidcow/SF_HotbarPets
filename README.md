# SF_HotbarPets

Maintained HotbarPets fork for modern Slimefun servers.

HotbarPets adds inventory pets that grant effects, prevent certain damage, or produce items while carried in the hotbar and supplied with their preferred food.

## Compatibility

Primary Slimefun targets:
- Slimefun Legacy
- Slimefun United

Additional compatibility targets:
- SlimefunGuguProject/Slimefun4
- Original Slimefun4-compatible API implementations

Server software:
- Paper
- Purpur
- Folia
- Leaf

Minecraft target: **1.21.11+**. Builds use Java 25 with Java 21 bytecode.

The maintained fork replaces the old global Bukkit repeating task with Paper's global-region plus per-player entity schedulers. That keeps periodic pet inventory processing on the player's owning scheduler and avoids cross-region player access on Folia.

## Dependency policy

This addon does not directly depend on GuizhanLib and does not use GuizhanLib APIs. Gugu compatibility is provided through the shared Slimefun addon API.

## Maintenance

Original project by TheBusyBiscuit. Current maintenance and modern compatibility work are provided by `wickidcow`.

Report issues at this repository so fixes can be validated against current Slimefun Legacy and Paper-family versions.

<div align="center">

# SF_HotbarPets — Slimefun Legacy
### Maintained inventory pets for modern Paper servers

HotbarPets adds inventory pets that grant effects, prevent certain damage, or produce items while carried in the hotbar and supplied with their preferred food.

[![Build](https://github.com/wickidcow/SF_HotbarPets/actions/workflows/maven.yml/badge.svg)](https://github.com/wickidcow/SF_HotbarPets/actions/workflows/maven.yml)
[![Slimefun Legacy](https://img.shields.io/badge/Slimefun-Legacy-6bd425)](https://github.com/wickidcow/Slimefun-Legacy)
[![License](https://img.shields.io/badge/License-GPLv3-blue)](https://github.com/wickidcow/Slimefun-Legacy/blob/master/LICENSE)

[Releases](https://github.com/wickidcow/SF_HotbarPets/releases) · [Builds](https://github.com/wickidcow/SF_HotbarPets/actions) · [Issues](https://github.com/wickidcow/SF_HotbarPets/issues)

</div>

> [!IMPORTANT]
> **SF_HotbarPets is an unofficial, independently maintained downstream fork of HotbarPets.** It is maintained by `wickidcow` for AlbionMC and the wider Slimefun community. It is not an official release of the original HotbarPets project, Slimefun, Slimefun United, or the SlimefunGuguProject.
>
> **NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

## Preserved gameplay

The classic HotbarPets roster, recipes, item IDs, favorite foods, passive abilities and active abilities are preserved. The maintained scheduler uses Paper global-region and player-owned scheduling for Paper/Purpur/Folia/Leaf compatibility.

## 1.0.1 modernization

The final preservation-focused pass adds only three optional modern pets using the original feed-and-use model:

- **Breeze Pet** — consumes Wind Charges and launches a Wind Charge.
- **Copper Golem Pet** — consumes Copper Ingots and grants Haste.
- **Nautilus Pet** — consumes Raw Cod and grants Conduit Power.

All three can be disabled independently in `config.yml`. The classic producer-pet cadence is also configurable through `production-interval-ticks` and remains **2000 ticks** by default. Startup UI construction no longer uses the Slimefun Dough item helper.

## Download and compatibility

Current official release: [HotbarPets v1.0.5](https://github.com/wickidcow/SF_HotbarPets/releases/tag/v1.0.5), distributed as `SF_HotbarPets1.0.5.jar`. The immutable Slimefun Legacy 4.1.78 addon bundle still contains the earlier HotbarPets 1.0.4 JAR.

**Upcoming 1.0.6 (unreleased):** The default optional ability permission prefix becomes `slimefun.hotbarpet.`, while custom prefixes and all pet gameplay remain unchanged. This candidate builds as `SF_HotbarPets1.0.6.jar` and does **not** republish any previous release. Master-only release publication requires an explicit `workflow_dispatch` with `publish=true` and exact version confirmation after CI.

Built with **Java 25** targeting **Java 21 bytecode**. Slimefun Legacy is the primary target, with the shared Slimefun addon API retained for Slimefun United, SlimefunGuguProject/Slimefun4 and original Slimefun4-compatible implementations. Paper is primary; Purpur, Folia and Leaf are compatibility targets.

This maintained fork has no direct GuizhanLib dependency.

## 1.0.4 — pet-food messages and optional ability permissions

Hungry Hotbar Pets now explain how to feed them: **“Your [Pet Name] would have helped you, but it's hungry! Keep its favorite food in your inventory.”** The notification preserves the pet name's colored text, fixes missing spaces, and shares one string with special-case Pig and Magma Cube reminders.

## Optional per-pet ability permissions (Slimefun namespace, Albion-compatible)

The addon can optionally restrict the **use of each Hotbar Pet ability** to a
specific permission, independently of Slimefun research, crafting, item ownership
or player hotbar slots. Existing servers are unchanged unless they opt in.

```yaml
# plugins/HotbarPets/config.yml
permission-gating:
  enabled: true
  prefix: 'slimefun.hotbarpet.'
```

Permission names use the registered Slimefun ID without `HOTBAR_PET_`, in
lowercase: `HOTBAR_PET_PIG` requires `slimefun.hotbarpet.pig`,
`HOTBAR_PET_COW` requires `slimefun.hotbarpet.cow`,
`HOTBAR_PET_ENDER_MAN` requires `slimefun.hotbarpet.ender_man` and
`HOTBAR_PET_DRAGON` requires `slimefun.hotbarpet.dragon`.
Grant nodes with LuckPerms, e.g.:

```text
lp user ExamplePlayer permission set slimefun.hotbarpet.pig true
```

Both click-activated and passive hotbar abilities are checked, including the
Magma Cube lava shield, Pig/Zombie food effects and Eyamaz Soul Pie drop.
The pet's preferred food is still consumed normally after the permission check.

**Existing Albion installations:** Your saved `plugins/HotbarPets/config.yml`
is not replaced during an upgrade. Keeping `prefix: 'albion.hotbarpet.'` makes
all existing NPC grants continue to work unchanged. If you intentionally switch
to `slimefun.hotbarpet.`, update the NPC's LuckPerms grants to match. Both
namespaces are supported as explicit choices, but **only the configured one**
is required; the plugin does not silently accept old and new permissions together.

A player may receive or trade a pet without its permission. To verify ability
gating, use a **non-OP** account and check its effective permissions (including
inherited group wildcards) with LuckPerms. OP or wildcard users are not valid
negative test cases. The existing Albion NPC can continue to give the real
Slimefun Hotbar Pet and grant the chosen permission after purchase.

## Credits and project lineage

Original HotbarPets project and authorship belong to **TheBusyBiscuit** and its contributors. Modern compatibility and Slimefun Legacy maintenance are by **wickidcow**. This fork preserves upstream authorship and does not claim the original work as its own.

## Independence, trademarks and non-affiliation

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

SF_HotbarPets is an independent community project. It is not affiliated with, endorsed by, sponsored by, approved by, or operated by Mojang Studios or Microsoft Corporation. Minecraft and other third-party names and trademarks remain the property of their respective owners.

## License

SF_HotbarPets is distributed under the [GNU General Public License v3.0](https://github.com/wickidcow/Slimefun-Legacy/blob/master/LICENSE), consistent with the GPLv3 licensing of the upstream project. Upstream authorship and copyright remain with the original authors and contributors.

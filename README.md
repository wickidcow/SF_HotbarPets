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

Release JAR: `SF_HotbarPets1.0.1.jar`

Built with **Java 25** targeting **Java 21 bytecode**. Slimefun Legacy is the primary target, with the shared Slimefun addon API retained for Slimefun United, SlimefunGuguProject/Slimefun4 and original Slimefun4-compatible implementations. Paper is primary; Purpur, Folia and Leaf are compatibility targets.

This maintained fork has no direct GuizhanLib dependency.

## Credits and project lineage

Original HotbarPets project and authorship belong to **TheBusyBiscuit** and its contributors. Modern compatibility and Slimefun Legacy maintenance are by **wickidcow**. This fork preserves upstream authorship and does not claim the original work as its own.

## Independence, trademarks and non-affiliation

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

SF_HotbarPets is an independent community project. It is not affiliated with, endorsed by, sponsored by, approved by, or operated by Mojang Studios or Microsoft Corporation. Minecraft and other third-party names and trademarks remain the property of their respective owners.

## License

SF_HotbarPets is distributed under the [GNU General Public License v3.0](https://github.com/wickidcow/Slimefun-Legacy/blob/master/LICENSE), consistent with the GPLv3 licensing of the upstream project. Upstream authorship and copyright remain with the original authors and contributors.

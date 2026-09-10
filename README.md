<div align="center">

# SF_HotbarPets — Slimefun Legacy
### Maintained inventory pets for modern Paper servers

HotbarPets adds inventory pets that grant effects, prevent certain damage, or produce items while carried in the hotbar and supplied with their preferred food.

[![Build](https://github.com/wickidcow/SF_HotbarPets/actions/workflows/maven.yml/badge.svg)](https://github.com/wickidcow/SF_HotbarPets/actions/workflows/maven.yml)
[![Slimefun Legacy](https://img.shields.io/badge/Slimefun-Legacy-6bd425)](https://github.com/wickidcow/Slimefun-Legacy)
[![Paper](https://img.shields.io/badge/Server-Paper%2026.2-blue)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Build-Java%2025-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-GPLv3-blue)](https://github.com/wickidcow/Slimefun-Legacy/blob/master/LICENSE)

[Releases](https://github.com/wickidcow/SF_HotbarPets/releases) · [Builds](https://github.com/wickidcow/SF_HotbarPets/actions) · [Issues](https://github.com/wickidcow/SF_HotbarPets/issues)

</div>

> [!IMPORTANT]
> **SF_HotbarPets is an unofficial, independently maintained downstream fork of HotbarPets.** It is maintained by `wickidcow` for [AlbionMC.com](https://albionmc.com) and the wider Slimefun community. It is not an official release of the original HotbarPets project, the original Slimefun project, Slimefun United, or the SlimefunGuguProject.
>
> **NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

---
## What is SF_HotbarPets?

HotbarPets provides collectible inventory pets with active and passive abilities while preserving the familiar original addon progression. The maintained fork updates the project for Minecraft 1.21.11+, current Paper-family servers, and modern Slimefun APIs.

The maintained scheduler path uses Paper global-region and player-owned entity scheduling instead of an old global repeating Bukkit task. TNT metadata handling is also kept on the owning event thread, avoiding unnecessary cross-region scheduler access on Folia.

---
## Download and build

Release JARs use the maintained Slimefun-addon naming convention:

`SF_HotbarPets1.0.0.jar`

The project builds with **Java 25** while targeting **Java 21 bytecode**. The production compatibility baseline is **Slimefun Legacy 4.1.48 on Paper 26.2**. Development builds are available from GitHub Actions and versioned release JARs are published on the Releases page.

---
## Compatibility

Primary target: **Slimefun Legacy**.

Compatibility is also validated against Slimefun United, SlimefunGuguProject/Slimefun4, and original Slimefun4-compatible APIs. Paper is the primary server family; Purpur, Folia and Leaf are compatibility targets. Folia support assumes the installed Slimefun implementation is itself Folia-compatible.

This maintained fork avoids external Slimefun utility-library dependencies and keeps cross-fork support on the shared Slimefun addon API wherever practical.

---
## Credits and project lineage

Original HotbarPets project and authorship belong to **TheBusyBiscuit** and its contributors. Modern compatibility, scheduler maintenance, and Slimefun Legacy integration are maintained by **wickidcow**.

This fork exists to preserve and maintain that work for current servers—not to replace the original developers or claim their work as its own. Upstream authorship, copyright notices, and license obligations remain respected.

---
## Independence, trademarks and non-affiliation

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

SF_HotbarPets and this maintenance fork are independent community projects. They are not affiliated with, endorsed by, sponsored by, approved by, or operated by Mojang Studios or Microsoft Corporation. References to Minecraft, Slimefun, Paper, upstream projects, companies, products, or communities are for identification, compatibility, attribution, and interoperability only.

Minecraft, Mojang Studios, Microsoft, and other third-party names, logos, brands, and trademarks remain the property of their respective owners. No sponsorship, partnership, ownership, or endorsement is claimed or implied.

---
## License

SF_HotbarPets is distributed under the [GNU General Public License v3.0](https://github.com/wickidcow/Slimefun-Legacy/blob/master/LICENSE), consistent with the GPLv3 licensing of the upstream project.

Upstream authorship and copyright remain with the original HotbarPets authors and contributors. Copyright in later modifications remains with the contributors who authored those changes.

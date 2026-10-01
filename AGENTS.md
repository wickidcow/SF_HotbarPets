# Maintained addon rules

Preserve existing item/research IDs, persistent keys, recipes, storage and gameplay identities. Minecraft1.21.11 and Java21 plugin bytecode are the runtime floor; use Java25 for builds. Keep Paper26.3 primary and retain26.2 compatibility.

Build against the checksum-verified published Slimefun Legacy API as configured in the workflow, installed locally as com.github.slimefun:Slimefun:4.1.63. Run `mvn -B clean verify`; test libraries are test-only and must never ship. Fixed archive timestamps support repeatable packaging. Do not suppress failed assertions or claim mocks prove live-server/Folia behavior.

Ship a raw versioned JAR. Never overwrite a published version with different bytes. Keep old releases and branches containing unique work intact.

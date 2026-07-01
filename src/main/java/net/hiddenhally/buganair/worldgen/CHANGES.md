# Fix pass — 6 files changed

## 1. Islands everywhere → now rare, high, and injected only in overworld biomes

**`BuganairWorldgenInit.java`**: `BiomeSelectors.foundInOverworld()` →
`BiomeSelectors.tag(BiomeTags.IS_OVERWORLD)`. Also removed the separate
tree-feature injection (see #3).

**`BuganairFeatures.java`**: `bootstrapPlaced()` now uses
`RarityFilterPlacementModifier.of(24)` (1-in-24 chunk attempts) instead of
`CountPlacementModifier.of(1)` (guaranteed every chunk), and the height
range moved from y=90–160 up to **y=140–190** — clearly above normal
terrain instead of overlapping it.

## 2. Islands bigger, fewer → radius increased

**`BuganairIslandFeature.java`**: ellipsoid radii went from 6–11/3–5/6–11
to **10–19 / 5–8 / 10–19**. Combined with the rarity filter above, this
gives the End-island feel you asked for: sparse, large landmarks instead
of frequent small blobs.

## 3. Trees on ground/water with dirt underneath → trees now self-contained in the island feature

This was the trickiest one. The old setup used a separate
`SKYWOOD_TREE_PLACED` PlacedFeature with `HeightmapPlacementModifier`,
which snaps to *whatever the world heightmap says is surface* — normal
ground, not your islands. That's also almost certainly why dirt was
appearing: vanilla's generic tree placement enforces a "valid soil" check
and can substitute/require dirt-like blocks under the trunk since Skygrass
isn't recognized as valid soil.

**Fix**: `BuganairIslandFeature.generate()` now places trees itself, right
after building the Skygrass cap, using only the exact column positions it
just created (`grassCapPositions`). It hand-builds a simple trunk + rounded
canopy with direct `setBlockState` calls — no soil check, no substitution,
no possibility of spawning anywhere but on this island's own grass. The
old `SKYWOOD_TREE_PLACED` biome-wide injection is removed from
`BuganairWorldgenInit`. The cherry-style `ConfiguredFeature` is left
registered but unused for now — see the new file's javadoc if you want to
switch back to it later (requires adding Skygrass to a soil tag).

## 4. Old worlds show nothing → disabled the unfinished structure bootstrap

**`BuganairDataGenerator.java`**: registry bootstraps run at **world load**,
not just `runDatagen`. `BuganairStructures` is still a work-in-progress
jigsaw scaffold — if any bootstrap in that chain throws, the whole
registry-build pass for the world can fail or come out incomplete, which
would explain nothing generating at all. Structure + template pool
registration are commented out until `BuganairStructures` is finished and
tested standalone. You don't need it right now — the Aether Forge already
places correctly via `BuganairIslandFeature` directly.

**Also note**: even with everything fixed, modded worldgen changes never
retroactively apply to already-generated chunks in an existing world —
only new chunks from that point on. If you want to check the fix in an
existing world, explore unvisited chunks or test in a fresh one.

## 5. Bonus fixes: two API bugs in your tag provider edits

**`BuganairBiomeTagProvider.java`** and **`BuganairBlockTagProvider.java`**:
your edits changed `getOrCreateTagBuilder` → `getTagBuilder` (not the
actual protected method) and added `.getRegistry()` calls that don't
return a valid `RegistryKey<T>` for `.add(...)`. Reverted to
`getOrCreateTagBuilder(...).add(...)` using the `RegistryKey`/`Block`
instances directly, which is what the API actually expects.

## What to do next

1. Replace these 6 files in your project.
2. Run `runDatagen`, fix any remaining compile errors.
3. Test in a **fresh world** first to confirm islands now spawn rare/high/
   big with self-contained trees.
4. If it still doesn't generate in an old world, that's very likely just
   the "already-generated chunks don't retroactively change" behavior —
   confirm by traveling far enough to reach ungenerated chunks.

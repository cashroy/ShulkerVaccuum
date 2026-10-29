**Placed boxes now look custom too.** On Minecraft 1.21.4+, a placed Vacuum or Trash box shows its own 3D model, and opening it lifts and twists a custom lid like a real shulker box.

**New in 2.2**
- Custom look for placed boxes, with an animated lid (1.21.4+).
- The plugin sends its texture pack to players itself, alongside other server packs (works with The Sift), so `server.properties` no longer needs editing.
- Placed boxes remember whether they're Vacuum or Trash, so breaking them, blowing them up or hitting them with a piston always gives back the right box with its contents.
- New `config.yml`: turn the placed look on or off, and make the pack optional or required.

**Features**
- **Shulker Box of Vacuum** (any shulker box + hopper): off-hand stores blocks you mine, main hand stores items you pick up. A full box hands extras to your inventory.
- **Shulker Box of Trash** (any shulker box + lava bucket): off-hand deletes blocks you mine, main hand deletes items you pick up.
- Any colour of shulker box works, and crafting keeps the box's colour and contents.
- Runs on Spigot, Paper and Purpur from 1.17 to 26.3. Custom textures need 1.21.4+.

Marked beta: it's built and compile-checked against the oldest and newest APIs, but hasn't been play-tested on every version in between. Test on a copy of your world first, and report problems on GitHub.

# ShulkerVaccuum

A Paper plugin for Minecraft **26.3** that adds two special shulker boxes.

| Box | Recipe (shapeless) | In your **off-hand** | In your **main hand** |
| --- | --- | --- | --- |
| **Shulker Box of Vacuum** | any shulker box + hopper | Blocks you mine go straight into the box | Items you pick up go into the box |
| **Shulker Box of Trash** | any shulker box + lava bucket | Blocks you mine are deleted | Items you pick up are deleted |

- Any colour of shulker box works, and the crafted box **keeps its colour and contents**.
- A full Vacuum box hands the rest to your normal inventory, so nothing is lost.
- Shulker boxes are never stored or deleted, and breaking a chest, barrel or other container is left alone, so a box in your hand can't swallow or destroy a container's contents.
- You can convert a Vacuum box into a Trash box (or back) by crafting it again with the other item.
- Boxes made with version 1 of the plugin still work.

## Requirements

- Paper **26.3** (or newer 26.x)
- Java **25** (Paper 26.1+ requires it)

## Building

```bash
mvn package
```

The plugin jar is written to `target/ShulkerVaccuum-2.0.jar`. Copy it into your server's `plugins` folder and restart.

If Maven can't find the Paper API version, set `paper.version` in `pom.xml` to the newest `26.3.build.N` listed on Paper's repository (see the [Paper project setup docs](https://docs.papermc.io/paper/dev/project-setup/)).

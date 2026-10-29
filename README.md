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

## Textures

The boxes have their own look in your inventory and hands (a placed box still shows as a normal shulker box of its colour):

![Vacuum and Trash box textures: side, top and bottom](docs/textures.png)

The textures come from a small resource pack, `ShulkerVaccuum-pack.zip`, in this repo. To have the server send it to everyone, add these lines to `server.properties` and restart:

```properties
resource-pack=https://raw.githubusercontent.com/cashroy/ShulkerVaccuum/main/ShulkerVaccuum-pack.zip
resource-pack-sha1=54a885f62a2117b02fccf38fa44cd8aa750a2a29
require-resource-pack=false
```

Players accept the pack when they join. (Or drop the zip into your own `.minecraft/resourcepacks` folder instead.) Boxes crafted before the textures were added switch to the new look when you join or use them.

If you change anything in `resourcepack/`, rebuild the zip with `python3 tools/build-pack.py`, then update `resource-pack-sha1` to the hash it prints.

## Requirements

- Paper **26.3** (or newer 26.x)
- Java **25** (Paper 26.1+ requires it)

## Building

```bash
mvn package
```

The plugin jar is written to `target/ShulkerVaccuum-2.0.jar`. Copy it into your server's `plugins` folder and restart.

If Maven can't find the Paper API version, set `paper.version` in `pom.xml` to the newest `26.3.build.N` listed on Paper's repository (see the [Paper project setup docs](https://docs.papermc.io/paper/dev/project-setup/)).

# Menu container defaults — rendered evidence

Backing the visual evidence in `agent/menu-container-defaults`, held on its own
branch so the images have a stable URL without carrying binaries into `main`.

* `menu-container.png` — before / after, light.
* `renders/` — every `DropdownMenuSticker` variant on each side.

1,182 pixels change out of 497,696, all of them the trailing chevrons going from
10dp to the kit's 20dp. The container itself is byte-identical: the tonal
elevation that was dropped had never been drawing anything.

# Kit axes batch 3 — rendered evidence

Backing `agent/kit-axes-batch3`, on its own branch so the images have a stable
URL without carrying binaries into `main`.

* `new-axes.png` — each new cell beside its base.
* `renders/` — the affected stickers.

Every cell was diffed against its own base before it was kept. The app bar's
first implementation — a scroll behaviour seeded with a content offset — came
out at zero changed pixels and was replaced.

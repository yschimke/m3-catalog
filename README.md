# Kit axes batch 2 — rendered evidence

Backing `agent/kit-axes-batch2`, on its own branch so the images have a stable
URL without carrying binaries into `main`.

* `new-axes.png` — each new cell beside its base.
* `renders/` — the affected stickers.

The navigation rail is the one to look at: its `middle` variant moves 32,538
pixels, while the `top` variant this batch started with moved none — which is
how the component's reference turned out to be pointing at the wrong node.

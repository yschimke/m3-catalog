#!/usr/bin/env node
// List the Material 3 Design Kit's Figma pages as a markdown table.
//
// WHY THIS EXISTS
//
// Page ids are the coordinate for anything that addresses a whole page rather
// than a component: a design-parity **page backdrop** import
// (`design-pages.json` wants a frame node id), or simply working out which page
// one of `design-map.json`'s ~39 refs actually lives on.
//
// Those ids are as undiscoverable as the node ids `resolve-figma-refs.mjs`
// exists for, and for the same reason — quoting that script: *"the Figma MCP
// server exposes only the page a user is looking at."* Enumerating pages
// through the MCP server means one full subtree dump per page; the kit's
// `Buttons` page alone is ~448 KB of metadata, and `Chips` is not far behind.
// That is an absurd way to learn 31 names.
//
// The REST API answers the whole question in **one request**: `GET
// /v1/files/:key?depth=1` returns the document with its children truncated to
// the page level — every page's `id` and `name`, and nothing else. That is what
// this script does.
//
// USAGE
//
//   FIGMA_TOKEN=figd_... node scripts/list-figma-pages.mjs
//   FIGMA_TOKEN=figd_... node scripts/list-figma-pages.mjs --file <fileKey>
//
// The token needs `file_content:read` — the same scope `resolve-figma-refs.mjs`
// already documents, so a token that works for one works for the other. Output
// is a markdown table ready to paste into `docs/FIGMA_PAGES.md`, plus the
// figma.com deep link for each page.
//
// This script is READ-ONLY, like every other Figma interaction in this repo.

const KIT_FILE_KEY = "ocdacdEsnHipMJD3egzxKb";
const KIT_FILE_SLUG = "Material-3-Design-Kit--Community-";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const fileKey = arg("file", KIT_FILE_KEY);
const token = process.env.FIGMA_TOKEN;

if (!token) {
  console.error("FIGMA_TOKEN is not set. A read-only PAT with `file_content:read` is enough.");
  process.exit(1);
}

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

/**
 * One REST call, with backoff. Only a single request is needed, but a 429 is
 * still possible when a token is shared with `resolve-figma-refs.mjs`, and
 * retrying beats failing a one-request script.
 */
async function get(path, attempt = 0) {
  const res = await fetch(`https://api.figma.com${path}`, {
    headers: { "X-Figma-Token": token },
  });
  if (res.ok) return res.json();

  const retryable = res.status === 429 || res.status >= 500;
  if (retryable && attempt < 4) {
    const after = Number(res.headers.get("retry-after"));
    const waitMs = Number.isFinite(after) && after > 0 ? after * 1000 : 2 ** attempt * 1000;
    console.error(`  ${res.status} — retrying in ${Math.round(waitMs / 1000)}s`);
    await sleep(waitMs);
    return get(path, attempt + 1);
  }
  throw new Error(`figma: GET ${path} failed (${res.status}) — ${await res.text().catch(() => "")}`);
}

// `depth=1` truncates the document to its immediate children: the pages.
const doc = await get(`/v1/files/${fileKey}?depth=1`);
const pages = (doc.document?.children ?? []).filter((n) => n.type === "CANVAS");

if (pages.length === 0) {
  console.error("No pages returned. Is the file key right, and does the token have access?");
  process.exit(1);
}

const link = (id) =>
  `https://www.figma.com/design/${fileKey}/${KIT_FILE_SLUG}?node-id=${id.replace(":", "-")}`;

console.log(`<!-- ${pages.length} pages in ${fileKey}; regenerate with scripts/list-figma-pages.mjs -->`);
console.log("");
console.log("| Page | Node id | Link |");
console.log("| --- | --- | --- |");
for (const p of pages) {
  console.log(`| ${p.name} | \`${p.id}\` | [open](${link(p.id)}) |`);
}

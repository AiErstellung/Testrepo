const http = require("http");
const path = require("path");
const fs = require("fs");
const { pipeline } = require("stream/promises");
const {
  S3Client,
  PutObjectCommand,
  ListObjectsV2Command,
  GetObjectCommand,
} = require("@aws-sdk/client-s3");

const DEFAULT_PORT = Number(process.env.UPLOAD_BRIDGE_PORT || 5178);
const DEFAULT_CACHE_DIR = path.resolve(__dirname, "assets-cache");
const DEFAULT_AI_PROPERTIES = path.resolve(__dirname, "local.properties");
const DEFAULT_PRODUSA_IMAGE_DIR = path.resolve(__dirname, "produsa-images");
const DEFAULT_LOCAL_PROPERTIES = path.resolve(
  __dirname,
  "..",
  "..",
  "ELMON",
  "APP",
  "local.properties"
);

function readLocalProperties(filePath) {
  if (!fs.existsSync(filePath)) return {};
  const lines = fs.readFileSync(filePath, "utf8").split(/\r?\n/);
  const result = {};
  for (const raw of lines) {
    const line = raw.trim();
    if (!line || line.startsWith("#")) continue;
    const idx = line.indexOf("=");
    if (idx === -1) continue;
    const key = line.slice(0, idx).trim();
    const value = line.slice(idx + 1).trim();
    result[key] = value;
  }
  return result;
}

function getConfig() {
  const filePath = process.env.ELMON_LOCAL_PROPERTIES || DEFAULT_LOCAL_PROPERTIES;
  const props = readLocalProperties(filePath);
  return {
    accessKeyId: process.env.ELMON_STORAGE_ACCESS_KEY || props.ELMON_STORAGE_ACCESS_KEY,
    secretAccessKey:
      process.env.ELMON_STORAGE_SECRET_KEY || props.ELMON_STORAGE_SECRET_KEY,
    sessionToken:
      process.env.ELMON_STORAGE_SESSION_TOKEN || props.ELMON_STORAGE_SESSION_TOKEN,
    region: process.env.ELMON_STORAGE_REGION || props.ELMON_STORAGE_REGION,
    bucket: process.env.ELMON_STORAGE_BUCKET || props.ELMON_STORAGE_BUCKET,
    endpoint: process.env.ELMON_STORAGE_ENDPOINT || props.ELMON_STORAGE_ENDPOINT,
  };
}

function getAiConfig() {
  const filePath = process.env.OPENAI_LOCAL_PROPERTIES || DEFAULT_AI_PROPERTIES;
  const props = readLocalProperties(filePath);
  return {
    apiKey: process.env.OPENAI_API_KEY || props.OPENAI_API_KEY,
    model: process.env.OPENAI_MODEL || props.OPENAI_MODEL || "gpt-4o-mini",
    baseUrl: process.env.OPENAI_BASE_URL || props.OPENAI_BASE_URL || "https://api.openai.com/v1",
    temperature: Number(process.env.OPENAI_TEMPERATURE || props.OPENAI_TEMPERATURE || 0.8),
  };
}

function getImageConfig() {
  const filePath = process.env.OPENAI_LOCAL_PROPERTIES || DEFAULT_AI_PROPERTIES;
  const props = readLocalProperties(filePath);
  return {
    apiKey: process.env.OPENAI_API_KEY || props.OPENAI_API_KEY,
    model: process.env.OPENAI_IMAGE_MODEL || props.OPENAI_IMAGE_MODEL || "gpt-image-1",
    baseUrl: process.env.OPENAI_BASE_URL || props.OPENAI_BASE_URL || "https://api.openai.com/v1",
    size: normalizeImageSize(process.env.OPENAI_IMAGE_SIZE || props.OPENAI_IMAGE_SIZE || "1024x1536"),
    quality: process.env.OPENAI_IMAGE_QUALITY || props.OPENAI_IMAGE_QUALITY || "medium",
    background: process.env.OPENAI_IMAGE_BACKGROUND || props.OPENAI_IMAGE_BACKGROUND || "auto",
  };
}

function buildS3Client(config) {
  if (!config.accessKeyId || !config.secretAccessKey) {
    throw new Error("Missing ELMON_STORAGE_ACCESS_KEY/ELMON_STORAGE_SECRET_KEY.");
  }
  if (!config.bucket) {
    throw new Error("Missing ELMON_STORAGE_BUCKET.");
  }
  if (!config.endpoint) {
    throw new Error("Missing ELMON_STORAGE_ENDPOINT.");
  }
  return new S3Client({
    region: config.region || "us-east-1",
    endpoint: config.endpoint,
    credentials: {
      accessKeyId: config.accessKeyId,
      secretAccessKey: config.secretAccessKey,
      sessionToken: config.sessionToken || undefined,
    },
    forcePathStyle: true,
  });
}

function setCors(res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type");
}

function setCorsForGet(res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Range");
  res.setHeader("Access-Control-Expose-Headers", "Content-Range, Accept-Ranges, Content-Length");
}

function normalizeFileName(value, fallback) {
  const raw = (value || "").toString().trim();
  const base =
    raw
      .toLowerCase()
      .replace(/[^a-z0-9._-]+/g, "-")
      .replace(/^-+|-+$/g, "")
      .slice(0, 64) || fallback;
  return base.endsWith(".json") ? base : `${base}.json`;
}

function readJsonBody(req) {
  return new Promise((resolve, reject) => {
    let data = "";
    req.on("data", (chunk) => {
      data += chunk.toString("utf8");
      if (data.length > 5_000_000) {
        reject(new Error("Payload too large."));
        req.destroy();
      }
    });
    req.on("end", () => {
      try {
        resolve(JSON.parse(data || "{}"));
      } catch (error) {
        reject(new Error("Invalid JSON payload."));
      }
    });
    req.on("error", reject);
  });
}

function normalizePrefix(prefix) {
  const trimmed = (prefix || "").trim().replace(/^\/+|\/+$/g, "");
  return trimmed || "Assets/Videos";
}

function isVideoKey(key) {
  return /\.(mp4|mov|webm|mkv|m4v)$/i.test(key || "");
}

function contentTypeForPath(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  switch (ext) {
    case ".png":
      return "image/png";
    case ".jpg":
    case ".jpeg":
      return "image/jpeg";
    case ".webp":
      return "image/webp";
    case ".mp4":
      return "video/mp4";
    case ".mov":
      return "video/quicktime";
    case ".webm":
      return "video/webm";
    case ".mkv":
      return "video/x-matroska";
    case ".m4v":
      return "video/x-m4v";
    default:
      return "application/octet-stream";
  }
}

function getCacheDir() {
  return process.env.ASSET_CACHE_DIR || DEFAULT_CACHE_DIR;
}

function getProdusaImageDir() {
  return process.env.PRODUSA_IMAGE_DIR || DEFAULT_PRODUSA_IMAGE_DIR;
}

function normalizeImageSize(value) {
  const size = (value || "").trim();
  if (size === "1080x1920") return "1024x1536";
  const allowed = new Set(["1024x1024", "1024x1536", "1536x1024", "auto"]);
  return allowed.has(size) ? size : "1024x1536";
}

async function listVideoAssets(prefix, limit = 200) {
  const config = getConfig();
  const client = buildS3Client(config);
  const safePrefix = normalizePrefix(prefix);

  const command = new ListObjectsV2Command({
    Bucket: config.bucket,
    Prefix: safePrefix.endsWith("/") ? safePrefix : `${safePrefix}/`,
    MaxKeys: limit,
  });

  const response = await client.send(command);
  const objects = (response.Contents || []).filter((item) => isVideoKey(item.Key));

  return objects.map((object) => ({
    key: object.Key,
    name: path.basename(object.Key || ""),
    size: object.Size || 0,
    lastModified: object.LastModified ? object.LastModified.toISOString() : null,
  }));
}

function listCachedAssets() {
  const cacheDir = getCacheDir();
  if (!fs.existsSync(cacheDir)) return [];

  const manifestPath = path.join(cacheDir, "_manifest.json");
  if (fs.existsSync(manifestPath)) {
    try {
      const manifest = JSON.parse(fs.readFileSync(manifestPath, "utf8"));
      return (manifest.items || []).map((item) => ({
        key: item.key,
        name: item.name,
        url: item.url,
        localPath: item.localPath,
        relativePath: item.relativePath,
      }));
    } catch (error) {
      // fall through to filesystem scan
    }
  }

  const items = [];
  const stack = [cacheDir];
  while (stack.length) {
    const current = stack.pop();
    const entries = fs.readdirSync(current, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(current, entry.name);
      if (entry.isDirectory()) {
        stack.push(fullPath);
      } else if (isVideoKey(entry.name)) {
        const relativePath = path
          .relative(cacheDir, fullPath)
          .replace(/\\/g, "/");
        items.push({
          name: entry.name,
          key: null,
          url: `http://localhost:${DEFAULT_PORT}/assets/cache/${encodeURIComponent(
            relativePath
          )}`,
          localPath: fullPath,
          relativePath,
        });
      }
    }
  }

  return items;
}

async function syncVideoAssets(prefix, limit = 200) {
  const config = getConfig();
  const client = buildS3Client(config);
  const safePrefix = normalizePrefix(prefix);
  const prefixWithSlash = safePrefix.endsWith("/") ? safePrefix : `${safePrefix}/`;
  const cacheDir = getCacheDir();

  fs.rmSync(cacheDir, { recursive: true, force: true });
  fs.mkdirSync(cacheDir, { recursive: true });

  const command = new ListObjectsV2Command({
    Bucket: config.bucket,
    Prefix: prefixWithSlash,
    MaxKeys: limit,
  });

  const response = await client.send(command);
  const objects = (response.Contents || []).filter((item) => isVideoKey(item.Key));
  const items = [];

  for (const object of objects) {
    const key = object.Key;
    const relativeKey = key.startsWith(prefixWithSlash)
      ? key.slice(prefixWithSlash.length)
      : path.basename(key);
    const relativePath = relativeKey.replace(/\\/g, "/");
    const localPath = path.join(cacheDir, relativeKey);
    fs.mkdirSync(path.dirname(localPath), { recursive: true });

    const getCommand = new GetObjectCommand({
      Bucket: config.bucket,
      Key: key,
    });
    const data = await client.send(getCommand);
    await pipeline(data.Body, fs.createWriteStream(localPath));

    items.push({
      key,
      name: path.basename(key),
      localPath,
      relativePath,
      url: `http://localhost:${DEFAULT_PORT}/assets/cache/${encodeURIComponent(
        relativePath
      )}`,
      size: object.Size || 0,
      lastModified: object.LastModified ? object.LastModified.toISOString() : null,
    });
  }

  const manifestPath = path.join(cacheDir, "_manifest.json");
  fs.writeFileSync(manifestPath, JSON.stringify({ items }, null, 2), "utf8");
  return { items, cacheDir };
}

async function uploadJson({ name, data, prefix }) {
  const config = getConfig();
  const client = buildS3Client(config);
  const fileName = normalizeFileName(name, "payload");
  const objectKey = `${prefix}/${fileName}`;
  const body = JSON.stringify(data ?? {}, null, 2);

  const command = new PutObjectCommand({
    Bucket: config.bucket,
    Key: objectKey,
    Body: body,
    ContentType: "application/json",
    ACL: "bucket-owner-full-control",
  });

  await client.send(command);
  return { bucket: config.bucket, key: objectKey, filename: fileName };
}

async function generateImage(prompt, config) {
  const response = await fetch(`${config.baseUrl}/images/generations`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${config.apiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: config.model,
      prompt,
      size: config.size,
      quality: config.quality,
      background: config.background,
    }),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`OpenAI image API error (${response.status}): ${text}`);
  }

  const data = await response.json();
  const b64 = data?.data?.[0]?.b64_json;
  if (!b64) {
    throw new Error("OpenAI image API did not return b64_json.");
  }
  return Buffer.from(b64, "base64");
}

async function editImage(prompt, imageBuffer, config) {
  const form = new FormData();
  const blob = new Blob([imageBuffer], { type: "image/png" });
  form.append("model", config.model);
  form.append("prompt", prompt);
  form.append("size", config.size);
  form.append("quality", config.quality);
  form.append("background", config.background);
  form.append("image[]", blob, "character.png");

  const response = await fetch(`${config.baseUrl}/images/edits`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${config.apiKey}`,
    },
    body: form,
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`OpenAI image edit error (${response.status}): ${text}`);
  }

  const data = await response.json();
  const b64 = data?.data?.[0]?.b64_json;
  if (!b64) {
    throw new Error("OpenAI image edit did not return b64_json.");
  }
  return Buffer.from(b64, "base64");
}

function ensureDir(dirPath) {
  fs.mkdirSync(dirPath, { recursive: true });
}

function slug(value, fallback) {
  const base =
    (value || "")
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-+|-+$/g, "")
      .slice(0, 64) || fallback;
  return base;
}

function saveProdusaImage(buffer, fileName) {
  const imageDir = getProdusaImageDir();
  ensureDir(imageDir);
  const safeName = `${fileName}.png`;
  const fullPath = path.join(imageDir, safeName);
  fs.writeFileSync(fullPath, buffer);
  return {
    key: safeName,
    path: fullPath,
    url: `http://localhost:${DEFAULT_PORT}/produsa/images/${encodeURIComponent(safeName)}`,
  };
}

async function generateCharacterReference(story, theme) {
  const imageConfig = getImageConfig();
  if (!imageConfig.apiKey) {
    throw new Error("Missing OPENAI_API_KEY for image generation.");
  }

  const prompt = [
    "Erzeuge ein Character Reference Bild fuer die Hauptfigur der Story.",
    "Eine einzelne Figur, full-body, neutral pose, klare Sichtbarkeit der Kleidung, Gesicht und Farben.",
    "Kein Text, keine Logos, kein Hintergrund-Drama.",
    `Story-Titel: ${story.title || "Unbekannt"}`,
    `Hook: ${story.hook || ""}`,
    theme ? `Thema: ${theme}` : "",
  ]
    .filter(Boolean)
    .join("\n");

  const buffer = await generateImage(prompt, imageConfig);
  return saveProdusaImage(buffer, `char-${slug(story.story_id, "story")}`);
}

async function generateSceneImage(story, scene, characterBuffer) {
  const imageConfig = getImageConfig();
  if (!imageConfig.apiKey) {
    throw new Error("Missing OPENAI_API_KEY for image generation.");
  }

  const prompt = [
    scene.visual_prompt || scene.description || "Szene",
    "Nutze die Referenzfigur aus dem Bild als denselben Charakter.",
    "Charakter-Identitaet, Kleidung und Farben muessen konsistent bleiben.",
    "Hochformat 9:16, kinoreifes Licht, klare Komposition.",
  ]
    .filter(Boolean)
    .join("\n");

  const buffer = await editImage(prompt, characterBuffer, imageConfig);
  return saveProdusaImage(
    buffer,
    `scene-${slug(story.story_id, "story")}-${slug(scene.scene_id, "scene")}`
  );
}

async function generateProdusaStories(payload) {
  const ai = getAiConfig();
  if (!ai.apiKey) {
    throw new Error("Missing OPENAI_API_KEY in tools/upload-bridge/local.properties.");
  }

  const theme = (payload.prompt || "").trim();
  const count = Math.max(1, Math.min(20, Number(payload.count || 1)));
  const generalPrompt = (payload.general_prompt || "").trim();
  const style = (payload.style || "").trim();
  const tone = (payload.tone || "").trim();
  const platform = (payload.platform || "").trim();
  const audience = (payload.audience || "").trim();

  const schema = {
    type: "object",
    additionalProperties: false,
    required: ["stories"],
    properties: {
      stories: {
        type: "array",
        minItems: count,
        maxItems: count,
        items: {
          type: "object",
          additionalProperties: false,
          required: [
            "story_id",
            "title",
            "hook",
            "estimated_duration_sec",
            "scenes",
            "tags",
            "risk_flags",
            "language",
          ],
          properties: {
            story_id: { type: "string" },
            title: { type: "string" },
            hook: { type: "string" },
            estimated_duration_sec: { type: "integer" },
            tags: { type: "array", items: { type: "string" } },
            risk_flags: { type: "array", items: { type: "string" } },
            language: { type: "string" },
            scenes: {
              type: "array",
              minItems: 2,
              maxItems: 6,
              items: {
                type: "object",
                additionalProperties: false,
                required: ["scene_id", "description", "visual_prompt", "action", "voiceover"],
                properties: {
                  scene_id: { type: "string" },
                  description: { type: "string" },
                  visual_prompt: { type: "string" },
                  action: { type: "string" },
                  voiceover: { type: "string" },
                },
              },
            },
          },
        },
      },
    },
  };

  const systemPrompt = [
    "Du bist ein deutscher Story-Generator fuer kurze Social-Video-Skripte.",
    "Antworte ausschließlich mit JSON im geforderten Schema.",
    "Die Ausgabe muss auf Deutsch sein.",
  ].join(" ");

  const userPrompt = [
    `Thema: ${theme || "Keine Angabe"}`,
    `Anzahl Videos: ${count}`,
    generalPrompt ? `Genereller Prompt: ${generalPrompt}` : "",
    style ? `Stil: ${style}` : "",
    tone ? `Ton: ${tone}` : "",
    platform ? `Plattform: ${platform}` : "",
    audience ? `Zielgruppe: ${audience}` : "",
    "Erstelle pro Video eine Story mit 2-6 Szenen.",
    "Halte story_id und scene_id eindeutig und kurz.",
    "Tags und risk_flags koennen leer sein.",
  ]
    .filter(Boolean)
    .join("\n");

  const response = await fetch(`${ai.baseUrl}/responses`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${ai.apiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: ai.model,
      input: [
        { role: "system", content: systemPrompt },
        { role: "user", content: userPrompt },
      ],
      text: {
        format: {
          type: "json_schema",
          name: "produsa_stories",
          strict: true,
          schema,
        },
      },
      temperature: ai.temperature,
    }),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`OpenAI API error (${response.status}): ${text}`);
  }

  const data = await response.json();
  const jsonText = extractResponseText(data);
  if (!jsonText) {
    throw new Error("OpenAI response did not include output_text.");
  }

  let parsed;
  try {
    parsed = JSON.parse(jsonText);
  } catch (error) {
    throw new Error("OpenAI output was not valid JSON.");
  }

  const stories = parsed.stories || [];
  const generateImages = payload.generate_images !== false;
  if (generateImages && stories.length) {
    const theme = (payload.prompt || "").trim();
    for (const story of stories) {
      const characterRef = await generateCharacterReference(story, theme);
      story.character_ref_url = characterRef.url;
      story.character_ref_key = characterRef.key;
      const characterBuffer = fs.readFileSync(characterRef.path);
      for (const scene of story.scenes || []) {
        const sceneImage = await generateSceneImage(story, scene, characterBuffer);
        scene.image_url = sceneImage.url;
        scene.image_key = sceneImage.key;
      }
    }
  }

  const imageConfig = getImageConfig();
  return {
    stories,
    model: ai.model,
    temperature: ai.temperature,
    image_model: imageConfig.model,
    image_size: imageConfig.size,
    image_quality: imageConfig.quality,
  };
}

function extractResponseText(data) {
  if (data?.output_text) return data.output_text;
  const output = data?.output;
  if (!Array.isArray(output)) return "";
  for (const item of output) {
    const content = item?.content;
    if (!Array.isArray(content)) continue;
    for (const part of content) {
      if (part?.type === "output_text" && typeof part?.text === "string") {
        return part.text;
      }
    }
  }
  return "";
}

const server = http.createServer(async (req, res) => {
  if (req.method === "OPTIONS") {
    setCors(res);
    setCorsForGet(res);
    res.writeHead(204);
    return res.end();
  }

  const url = new URL(req.url, `http://localhost:${DEFAULT_PORT}`);
  if (req.method === "GET" && url.pathname === "/assets/sync") {
    setCorsForGet(res);
    try {
      const prefix = url.searchParams.get("prefix");
      const limit = Number(url.searchParams.get("limit") || 200);
      const { items } = await syncVideoAssets(prefix, limit);
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ ok: true, count: items.length }));
    } catch (error) {
      res.writeHead(500, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ error: error.message || "Sync failed." }));
    }
  }

  if (req.method === "GET" && url.pathname === "/assets/videos") {
    setCorsForGet(res);
    try {
      const items = listCachedAssets();
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ ok: true, items }));
    } catch (error) {
      res.writeHead(500, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ error: error.message || "List failed." }));
    }
  }

  if (req.method === "GET" && url.pathname.startsWith("/assets/cache/")) {
    setCorsForGet(res);
    const cacheDir = getCacheDir();
    const rel = decodeURIComponent(url.pathname.replace("/assets/cache/", ""));
    const safePath = path.normalize(path.join(cacheDir, rel));
    if (!safePath.startsWith(cacheDir)) {
      res.writeHead(400, { "Content-Type": "text/plain" });
      return res.end("Invalid path.");
    }
    if (!fs.existsSync(safePath)) {
      res.writeHead(404, { "Content-Type": "text/plain" });
      return res.end("Not found.");
    }
    const stat = fs.statSync(safePath);
    const range = req.headers.range;
    const contentType = contentTypeForPath(safePath);

    if (range) {
      const match = /bytes=(\d*)-(\d*)/.exec(range);
      const start = match && match[1] ? Number(match[1]) : 0;
      const end = match && match[2] ? Number(match[2]) : stat.size - 1;
      if (start >= stat.size || end >= stat.size) {
        res.writeHead(416, {
          "Content-Range": `bytes */${stat.size}`,
        });
        return res.end();
      }
      const chunkSize = end - start + 1;
      res.writeHead(206, {
        "Content-Type": contentType,
        "Content-Length": chunkSize,
        "Content-Range": `bytes ${start}-${end}/${stat.size}`,
        "Accept-Ranges": "bytes",
      });
      return fs.createReadStream(safePath, { start, end }).pipe(res);
    }

    res.writeHead(200, {
      "Content-Type": contentType,
      "Content-Length": stat.size,
      "Accept-Ranges": "bytes",
    });
    return fs.createReadStream(safePath).pipe(res);
  }

  if (req.method === "GET" && url.pathname.startsWith("/produsa/images/")) {
    setCorsForGet(res);
    const imageDir = getProdusaImageDir();
    const rel = decodeURIComponent(url.pathname.replace("/produsa/images/", ""));
    const safePath = path.normalize(path.join(imageDir, rel));
    if (!safePath.startsWith(imageDir)) {
      res.writeHead(400, { "Content-Type": "text/plain" });
      return res.end("Invalid path.");
    }
    if (!fs.existsSync(safePath)) {
      res.writeHead(404, { "Content-Type": "text/plain" });
      return res.end("Not found.");
    }
    const stat = fs.statSync(safePath);
    const range = req.headers.range;
    const contentType = contentTypeForPath(safePath);

    if (range) {
      const match = /bytes=(\d*)-(\d*)/.exec(range);
      const start = match && match[1] ? Number(match[1]) : 0;
      const end = match && match[2] ? Number(match[2]) : stat.size - 1;
      if (start >= stat.size || end >= stat.size) {
        res.writeHead(416, {
          "Content-Range": `bytes */${stat.size}`,
        });
        return res.end();
      }
      const chunkSize = end - start + 1;
      res.writeHead(206, {
        "Content-Type": contentType,
        "Content-Length": chunkSize,
        "Content-Range": `bytes ${start}-${end}/${stat.size}`,
        "Accept-Ranges": "bytes",
      });
      return fs.createReadStream(safePath, { start, end }).pipe(res);
    }

    res.writeHead(200, {
      "Content-Type": contentType,
      "Content-Length": stat.size,
      "Accept-Ranges": "bytes",
    });
    return fs.createReadStream(safePath).pipe(res);
  }

  if (req.method !== "POST") {
    setCors(res);
    res.writeHead(405, { "Content-Type": "application/json" });
    return res.end(JSON.stringify({ error: "Method not allowed." }));
  }
  setCors(res);

  if (req.url === "/produsa/generate") {
    try {
      const payload = await readJsonBody(req);
      const result = await generateProdusaStories(payload);
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ ok: true, ...result }));
    } catch (error) {
      res.writeHead(500, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ error: error.message || "Generation failed." }));
    }
  }

  if (req.url === "/produsa/scene-image") {
    try {
      const payload = await readJsonBody(req);
      const story = payload.story || {};
      const scene = payload.scene || {};
      const theme = (payload.theme || "").trim();

      let characterBuffer = null;
      let characterInfo = null;
      if (payload.character_ref_key) {
        const imageDir = getProdusaImageDir();
        const refPath = path.join(imageDir, payload.character_ref_key);
        if (fs.existsSync(refPath)) {
          characterBuffer = fs.readFileSync(refPath);
          characterInfo = {
            key: payload.character_ref_key,
            url: `http://localhost:${DEFAULT_PORT}/produsa/images/${encodeURIComponent(
              payload.character_ref_key
            )}`,
          };
        }
      }

      if (!characterBuffer) {
        characterInfo = await generateCharacterReference(story, theme);
        characterBuffer = fs.readFileSync(characterInfo.path);
      }

      const sceneImage = await generateSceneImage(story, scene, characterBuffer);
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(
        JSON.stringify({
          ok: true,
          character_ref: {
            key: characterInfo.key,
            url: characterInfo.url,
          },
          scene_image: {
            key: sceneImage.key,
            url: sceneImage.url,
          },
        })
      );
    } catch (error) {
      res.writeHead(500, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ error: error.message || "Image failed." }));
    }
  }

  if (req.url === "/upload/krieta") {
    try {
      const payload = await readJsonBody(req);
      const result = await uploadJson({
        name: payload.name,
        data: payload.data,
        prefix: "Krieta",
      });
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ ok: true, ...result }));
    } catch (error) {
      res.writeHead(500, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ error: error.message || "Upload failed." }));
    }
  }

  if (req.url === "/upload/produsa") {
    try {
      const payload = await readJsonBody(req);
      const result = await uploadJson({
        name: payload.name,
        data: payload.data,
        prefix: "Produsa",
      });
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ ok: true, ...result }));
    } catch (error) {
      res.writeHead(500, { "Content-Type": "application/json" });
      return res.end(JSON.stringify({ error: error.message || "Upload failed." }));
    }
  }

  res.writeHead(404, { "Content-Type": "application/json" });
  return res.end(JSON.stringify({ error: "Not found." }));
});

server.listen(DEFAULT_PORT, () => {
  console.log(`IONOS upload bridge listening on http://localhost:${DEFAULT_PORT}`);
});

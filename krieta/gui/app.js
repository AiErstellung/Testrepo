const stateKey = "krieta_gui_state_v1";

const seedFormat = {
  id: "format-1",
  name: "Split Top/Bottom",
  data: {
    krieta_version: "1.0",
    canvas: { aspect_ratio: "9:16", resolution: "1080x1920", fps: 30 },
    layout: {
      type: "vertical_split",
      zones: [
        {
          id: "top_zone",
          source: "video_1",
          position: { x: "0%", y: "0%" },
          size: { width: "100%", height: "50%" },
          z_index: 0,
        },
        {
          id: "bottom_zone",
          source: "video_2",
          position: { x: "0%", y: "50%" },
          size: { width: "100%", height: "50%" },
          z_index: 0,
        },
      ],
    },
    overlays: [
      {
        id: "hook_text",
        type: "text",
        position: { x: "5%", y: "5%" },
        max_width: "90%",
        style: {
          font: "Inter-Bold",
          font_size: 64,
          color: "#FFFFFF",
          background: "rgba(0,0,0,0.4)",
          padding: 16,
        },
        animation: { in: "slide_down", out: "fade" },
      },
    ],
    platform: {
      target: "tiktok",
      safe_zones: { top: "10%", bottom: "15%" },
    },
  },
};

const state = {
  formats: [],
  activeFormatId: null,
  selectedZoneId: null,
  selectedOverlayId: null,
};

const dom = {
  formatList: document.getElementById("format-list"),
  addFormat: document.getElementById("add-format"),
  deleteFormat: document.getElementById("delete-format"),
  duplicateFormat: document.getElementById("duplicate-format"),
  assetList: document.getElementById("asset-list"),
  assetSearch: document.getElementById("asset-search"),
  refreshAssets: document.getElementById("refresh-assets"),
  canvas: document.getElementById("canvas"),
  formatTitle: document.getElementById("format-title"),
  formatSubtitle: document.getElementById("format-subtitle"),
  addZone: document.getElementById("add-zone"),
  addOverlay: document.getElementById("add-overlay"),
  canvasAspect: document.getElementById("canvas-aspect"),
  canvasResolution: document.getElementById("canvas-resolution"),
  canvasFps: document.getElementById("canvas-fps"),
  layoutType: document.getElementById("layout-type"),
  layoutName: document.getElementById("layout-name"),
  zoneId: document.getElementById("zone-id"),
  zoneSource: document.getElementById("zone-source"),
  zoneX: document.getElementById("zone-x"),
  zoneY: document.getElementById("zone-y"),
  zoneW: document.getElementById("zone-w"),
  zoneH: document.getElementById("zone-h"),
  zoneZ: document.getElementById("zone-z"),
  zoneAsset: document.getElementById("zone-asset"),
  zoneAssetKey: document.getElementById("zone-asset-key"),
  zoneAssetPreview: document.getElementById("zone-asset-preview"),
  deleteZone: document.getElementById("delete-zone"),
  overlayId: document.getElementById("overlay-id"),
  overlayType: document.getElementById("overlay-type"),
  overlayX: document.getElementById("overlay-x"),
  overlayY: document.getElementById("overlay-y"),
  overlayWidth: document.getElementById("overlay-width"),
  overlayFont: document.getElementById("overlay-font"),
  overlaySize: document.getElementById("overlay-size"),
  overlayColor: document.getElementById("overlay-color"),
  overlayOutline: document.getElementById("overlay-outline"),
  overlayBg: document.getElementById("overlay-bg"),
  deleteOverlay: document.getElementById("delete-overlay"),
  jsonOutput: document.getElementById("json-output"),
  copyJson: document.getElementById("copy-json"),
  importJson: document.getElementById("import-json"),
  exportS3: document.getElementById("export-s3"),
  uploadIonos: document.getElementById("upload-ionos"),
  exportStatus: document.getElementById("export-status"),
};

const assetState = {
  items: [],
  filtered: [],
  loading: false,
  error: null,
};

const tabs = Array.from(document.querySelectorAll(".tab"));
const tabContents = Array.from(document.querySelectorAll(".tab-content"));
const assetPrefix = "Assets";

tabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    tabs.forEach((item) => item.classList.remove("active"));
    tabContents.forEach((item) => item.classList.remove("active"));
    tab.classList.add("active");
    document.getElementById(`tab-${tab.dataset.tab}`).classList.add("active");
  });
});

function loadState() {
  const saved = localStorage.getItem(stateKey);
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      state.formats = parsed.formats || [seedFormat];
      state.activeFormatId = parsed.activeFormatId || state.formats[0].id;
      return;
    } catch (error) {
      console.warn("Failed to parse state", error);
    }
  }
  state.formats = [seedFormat];
  state.activeFormatId = seedFormat.id;
}

function saveState() {
  localStorage.setItem(
    stateKey,
    JSON.stringify({
      formats: state.formats,
      activeFormatId: state.activeFormatId,
    })
  );
}

function activeFormat() {
  return state.formats.find((format) => format.id === state.activeFormatId);
}

function formatAspectRatio(aspect) {
  switch (aspect) {
    case "1:1":
      return "1 / 1";
    case "16:9":
      return "16 / 9";
    default:
      return "9 / 16";
  }
}

function renderFormatList() {
  dom.formatList.innerHTML = "";
  state.formats.forEach((format) => {
    const item = document.createElement("li");
    item.className = "format-item";
    if (format.id === state.activeFormatId) item.classList.add("active");
    item.textContent = format.name;
    item.addEventListener("click", () => {
      state.activeFormatId = format.id;
      state.selectedZoneId = null;
      state.selectedOverlayId = null;
      renderAll();
    });
    dom.formatList.appendChild(item);
  });
}

function renderCanvas() {
  const format = activeFormat();
  if (!format) return;

  dom.canvas.innerHTML = "<div class=\"canvas-grid\"></div>";
  dom.canvas.style.aspectRatio = formatAspectRatio(format.data.canvas.aspect_ratio);

  format.data.layout.zones.forEach((zone) => {
    const zoneEl = document.createElement("div");
    zoneEl.className = "zone";
    if (zone.id === state.selectedZoneId) zoneEl.classList.add("selected");
    zoneEl.style.left = zone.position.x;
    zoneEl.style.top = zone.position.y;
    zoneEl.style.width = zone.size.width;
    zoneEl.style.height = zone.size.height;
    zoneEl.style.zIndex = zone.z_index || 0;
    zoneEl.textContent = zone.id;
    zoneEl.dataset.zoneId = zone.id;
    zoneEl.dataset.assetUrl = zone.asset_url || "";
    zoneEl.dataset.assetKey = zone.asset_key || "";

    const handle = document.createElement("div");
    handle.className = "zone-handle";
    zoneEl.appendChild(handle);

    attachZoneDrag(zoneEl, zone, handle);
    zoneEl.addEventListener("dragover", (event) => {
      event.preventDefault();
    });
    zoneEl.addEventListener("drop", (event) => {
      event.preventDefault();
      const assetUrl = event.dataTransfer.getData("text/asset-url");
      const assetKey = event.dataTransfer.getData("text/asset-key");
      if (!assetUrl) return;
      zone.asset_url = assetUrl;
      if (assetKey) {
        zone.asset_key = assetKey;
      }
      state.selectedZoneId = zone.id;
      state.selectedOverlayId = null;
      renderAll();
    });
    zoneEl.addEventListener("click", (event) => {
      event.stopPropagation();
      state.selectedZoneId = zone.id;
      state.selectedOverlayId = null;
      renderAll();
    });
    dom.canvas.appendChild(zoneEl);
  });

  (format.data.overlays || []).forEach((overlay) => {
    const overlayEl = document.createElement("div");
    overlayEl.className = "overlay";
    if (overlay.id === state.selectedOverlayId) overlayEl.classList.add("selected");
    overlayEl.textContent = overlay.id;
    overlayEl.style.left = overlay.position.x;
    overlayEl.style.top = overlay.position.y;
    overlayEl.style.maxWidth = overlay.max_width || "80%";
    overlayEl.dataset.overlayId = overlay.id;
    overlayEl.addEventListener("click", (event) => {
      event.stopPropagation();
      state.selectedOverlayId = overlay.id;
      state.selectedZoneId = null;
      renderAll();
    });
    dom.canvas.appendChild(overlayEl);
  });

  dom.canvas.addEventListener("click", () => {
    state.selectedZoneId = null;
    state.selectedOverlayId = null;
    renderAll();
  });
}

function attachZoneDrag(zoneEl, zone, handle) {
  let dragMode = null;
  let start = null;

  const onPointerMove = (event) => {
    if (!dragMode || !start) return;
    const rect = dom.canvas.getBoundingClientRect();
    const dx = ((event.clientX - start.x) / rect.width) * 100;
    const dy = ((event.clientY - start.y) / rect.height) * 100;

    if (dragMode === "move") {
      const x = clamp(start.zoneX + dx, 0, 100);
      const y = clamp(start.zoneY + dy, 0, 100);
      zone.position.x = `${round(x)}%`;
      zone.position.y = `${round(y)}%`;
    } else if (dragMode === "resize") {
      const width = clamp(start.zoneW + dx, 5, 100);
      const height = clamp(start.zoneH + dy, 5, 100);
      zone.size.width = `${round(width)}%`;
      zone.size.height = `${round(height)}%`;
    }
    renderAll(false);
  };

  const stopDrag = () => {
    dragMode = null;
    start = null;
    window.removeEventListener("pointermove", onPointerMove);
    window.removeEventListener("pointerup", stopDrag);
    saveState();
  };

  zoneEl.addEventListener("pointerdown", (event) => {
    if (event.target === handle) return;
    state.selectedZoneId = zone.id;
    state.selectedOverlayId = null;
    dragMode = "move";
    const rect = dom.canvas.getBoundingClientRect();
    start = {
      x: event.clientX,
      y: event.clientY,
      zoneX: parseFloat(zone.position.x),
      zoneY: parseFloat(zone.position.y),
      rect,
    };
    window.addEventListener("pointermove", onPointerMove);
    window.addEventListener("pointerup", stopDrag);
  });

  handle.addEventListener("pointerdown", (event) => {
    event.stopPropagation();
    state.selectedZoneId = zone.id;
    state.selectedOverlayId = null;
    dragMode = "resize";
    start = {
      x: event.clientX,
      y: event.clientY,
      zoneW: parseFloat(zone.size.width),
      zoneH: parseFloat(zone.size.height),
    };
    window.addEventListener("pointermove", onPointerMove);
    window.addEventListener("pointerup", stopDrag);
  });
}

function clamp(value, min, max) {
  return Math.min(Math.max(value, min), max);
}

function round(value) {
  return Math.round(value * 10) / 10;
}

function renderInspector() {
  const format = activeFormat();
  if (!format) return;

  dom.formatTitle.textContent = format.name;
  dom.formatSubtitle.textContent = format.data.canvas.aspect_ratio;

  dom.canvasAspect.value = format.data.canvas.aspect_ratio;
  dom.canvasResolution.value = format.data.canvas.resolution || "";
  dom.canvasFps.value = format.data.canvas.fps || 30;

  dom.layoutType.value = format.data.layout.type || "freeform";
  dom.layoutName.value = format.name;

  const zone = format.data.layout.zones.find((z) => z.id === state.selectedZoneId);
  dom.zoneId.value = zone ? zone.id : "";
  dom.zoneSource.value = zone ? zone.source : "";
  dom.zoneX.value = zone ? parseFloat(zone.position.x) : "";
  dom.zoneY.value = zone ? parseFloat(zone.position.y) : "";
  dom.zoneW.value = zone ? parseFloat(zone.size.width) : "";
  dom.zoneH.value = zone ? parseFloat(zone.size.height) : "";
  dom.zoneZ.value = zone ? zone.z_index || 0 : "";
  dom.zoneAsset.value = zone?.asset_url || "";
  dom.zoneAssetKey.value = zone?.asset_key || "";
  if (dom.zoneAssetPreview) {
    dom.zoneAssetPreview.src = zone?.asset_url || "";
  }

  const overlay = (format.data.overlays || []).find(
    (item) => item.id === state.selectedOverlayId
  );
  dom.overlayId.value = overlay ? overlay.id : "";
  dom.overlayType.value = overlay ? overlay.type : "text";
  dom.overlayX.value = overlay ? parseFloat(overlay.position.x) : "";
  dom.overlayY.value = overlay ? parseFloat(overlay.position.y) : "";
  dom.overlayWidth.value = overlay ? parseFloat(overlay.max_width || "") : "";
  dom.overlayFont.value = overlay?.style?.font || "";
  dom.overlaySize.value = overlay?.style?.font_size || "";
  dom.overlayColor.value = overlay?.style?.color || "";
  dom.overlayOutline.value = overlay?.style?.outline || "";
  dom.overlayBg.value = overlay?.style?.background || "";

  dom.jsonOutput.value = JSON.stringify(format.data, null, 2);
}

function renderAssetList() {
  if (!dom.assetList) return;
  dom.assetList.innerHTML = "";
  assetState.filtered.forEach((asset) => {
    const item = document.createElement("div");
    item.className = "asset-item";
    item.draggable = true;

    const name = document.createElement("div");
    name.className = "asset-name";
    name.textContent = asset.name;

    const player = document.createElement("video");
    player.className = "asset-player";
    player.src = asset.url;
    player.controls = true;
    player.preload = "metadata";

    const meta = document.createElement("div");
    meta.className = "asset-meta";
    meta.textContent = asset.key || asset.relativePath || "";

    item.appendChild(name);
    item.appendChild(meta);
    item.appendChild(player);

    item.addEventListener("click", () => {
      const zone = getSelectedZone();
      if (!zone) {
        setExportStatus("Bitte zuerst eine Zone auswaehlen.", "error");
        return;
      }
      zone.asset_url = asset.url;
      if (asset.key || asset.relativePath) {
        zone.asset_key = asset.key || asset.relativePath;
      }
      renderAll();
      setExportStatus(`Asset gesetzt: ${asset.name}`, "success");
    });

    item.addEventListener("dragstart", (event) => {
      event.dataTransfer.setData("text/asset-url", asset.url);
      event.dataTransfer.setData("text/asset-name", asset.name);
      if (asset.key || asset.relativePath) {
        event.dataTransfer.setData("text/asset-key", asset.key || asset.relativePath);
      }
    });

    dom.assetList.appendChild(item);
  });
}

function filterAssets() {
  const query = (dom.assetSearch?.value || "").trim().toLowerCase();
  if (!query) {
    assetState.filtered = [...assetState.items];
    return;
  }
  assetState.filtered = assetState.items.filter((asset) =>
    asset.name.toLowerCase().includes(query)
  );
}

async function loadAssets() {
  if (assetState.loading) return;
  assetState.loading = true;
  setExportStatus("Assets werden synchronisiert...", "");
  try {
    const syncResponse = await fetch(
      `http://localhost:5178/assets/sync?prefix=${encodeURIComponent(assetPrefix)}`
    );
    if (!syncResponse.ok) {
      const text = await syncResponse.text();
      throw new Error(text || "Asset-Sync fehlgeschlagen.");
    }

    const listResponse = await fetch("http://localhost:5178/assets/videos");
    if (!listResponse.ok) {
      const text = await listResponse.text();
      throw new Error(text || "Asset-Load fehlgeschlagen.");
    }
    const payload = await listResponse.json();
    assetState.items = payload.items || [];
    filterAssets();
    renderAssetList();
    setExportStatus(`Assets geladen: ${assetState.items.length}`, "success");
  } catch (error) {
    setExportStatus(
      `Assets konnten nicht geladen werden: ${error?.message || "Fehler"}`,
      "error"
    );
  } finally {
    assetState.loading = false;
  }
}

function renderAll(save = true) {
  renderFormatList();
  renderCanvas();
  renderInspector();
  if (save) saveState();
}

function addFormat() {
  const id = `format-${Date.now()}`;
  const format = {
    id,
    name: `Neues Format ${state.formats.length + 1}`,
    data: JSON.parse(JSON.stringify(seedFormat.data)),
  };
  format.data.layout.zones = [];
  state.formats.push(format);
  state.activeFormatId = id;
  renderAll();
}

function deleteFormat() {
  if (state.formats.length <= 1) return;
  state.formats = state.formats.filter((f) => f.id !== state.activeFormatId);
  state.activeFormatId = state.formats[0].id;
  state.selectedZoneId = null;
  state.selectedOverlayId = null;
  renderAll();
}

function duplicateFormat() {
  const format = activeFormat();
  if (!format) return;
  const copy = {
    id: `format-${Date.now()}`,
    name: `${format.name} Copy`,
    data: JSON.parse(JSON.stringify(format.data)),
  };
  state.formats.push(copy);
  state.activeFormatId = copy.id;
  renderAll();
}

function addZone() {
  const format = activeFormat();
  const count = format.data.layout.zones.length + 1;
  const zone = {
    id: `zone_${count}`,
    source: `video_${count}`,
    position: { x: "10%", y: `${10 + count * 5}%` },
    size: { width: "80%", height: "30%" },
    z_index: 0,
  };
  format.data.layout.zones.push(zone);
  state.selectedZoneId = zone.id;
  state.selectedOverlayId = null;
  renderAll();
}

function addOverlay() {
  const format = activeFormat();
  if (!format.data.overlays) format.data.overlays = [];
  const count = format.data.overlays.length + 1;
  const overlay = {
    id: `overlay_${count}`,
    type: "text",
    position: { x: "6%", y: "6%" },
    max_width: "80%",
    style: { font: "Space Grotesk", font_size: 36, color: "#FFFFFF" },
  };
  format.data.overlays.push(overlay);
  state.selectedOverlayId = overlay.id;
  state.selectedZoneId = null;
  renderAll();
}

function deleteZone() {
  const format = activeFormat();
  const zoneId = resolveZoneId();
  if (!zoneId) return;
  format.data.layout.zones = format.data.layout.zones.filter(
    (zone) => zone.id !== zoneId
  );
  state.selectedZoneId = null;
  renderAll();
}

function deleteOverlay() {
  const format = activeFormat();
  const overlayId = resolveOverlayId();
  if (!overlayId) return;
  format.data.overlays = (format.data.overlays || []).filter(
    (overlay) => overlay.id !== overlayId
  );
  state.selectedOverlayId = null;
  renderAll();
}

function bindInputs() {
  dom.canvasAspect.addEventListener("change", (event) => {
    activeFormat().data.canvas.aspect_ratio = event.target.value;
    renderAll();
  });

  dom.canvasResolution.addEventListener("input", (event) => {
    activeFormat().data.canvas.resolution = event.target.value;
    saveState();
  });

  dom.canvasFps.addEventListener("input", (event) => {
    activeFormat().data.canvas.fps = Number(event.target.value || 30);
    saveState();
  });

  dom.layoutType.addEventListener("change", (event) => {
    activeFormat().data.layout.type = event.target.value;
    saveState();
  });

  dom.layoutName.addEventListener("input", (event) => {
    activeFormat().name = event.target.value || "Format";
    renderAll();
  });

  dom.zoneId.addEventListener("input", (event) => {
    const zone = getSelectedZone();
    if (!zone) return;
    zone.id = event.target.value;
    state.selectedZoneId = zone.id;
    renderAll();
  });

  dom.zoneSource.addEventListener("input", (event) => {
    const zone = getSelectedZone();
    if (!zone) return;
    zone.source = event.target.value;
    saveState();
  });

  [dom.zoneX, dom.zoneY, dom.zoneW, dom.zoneH].forEach((input) => {
    input.addEventListener("input", () => updateZoneGeometry());
  });

  dom.zoneZ.addEventListener("input", (event) => {
    const zone = getSelectedZone();
    if (!zone) return;
    zone.z_index = Number(event.target.value || 0);
    renderAll();
  });

  dom.zoneAsset.addEventListener("input", (event) => {
    const zone = getSelectedZone();
    if (!zone) return;
    zone.asset_url = event.target.value.trim();
    renderAll();
  });

  dom.zoneAssetKey.addEventListener("input", (event) => {
    const zone = getSelectedZone();
    if (!zone) return;
    zone.asset_key = event.target.value.trim();
    renderAll();
  });

  dom.overlayId.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.id = event.target.value;
    state.selectedOverlayId = overlay.id;
    renderAll();
  });

  dom.overlayType.addEventListener("change", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.type = event.target.value;
    saveState();
  });

  dom.overlayX.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.position.x = `${event.target.value}%`;
    renderAll();
  });

  dom.overlayY.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.position.y = `${event.target.value}%`;
    renderAll();
  });

  dom.overlayWidth.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.max_width = `${event.target.value}%`;
    saveState();
  });

  dom.overlayFont.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.style = overlay.style || {};
    overlay.style.font = event.target.value;
    saveState();
  });

  dom.overlaySize.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.style = overlay.style || {};
    overlay.style.font_size = Number(event.target.value || 0);
    saveState();
  });

  dom.overlayColor.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.style = overlay.style || {};
    overlay.style.color = event.target.value;
    saveState();
  });

  dom.overlayOutline.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.style = overlay.style || {};
    overlay.style.outline = event.target.value;
    saveState();
  });

  dom.overlayBg.addEventListener("input", (event) => {
    const overlay = getSelectedOverlay();
    if (!overlay) return;
    overlay.style = overlay.style || {};
    overlay.style.background = event.target.value;
    saveState();
  });

  dom.copyJson.addEventListener("click", () => {
    navigator.clipboard.writeText(dom.jsonOutput.value || "");
  });

  dom.importJson.addEventListener("click", () => {
    try {
      const data = JSON.parse(dom.jsonOutput.value || "{}");
      const format = activeFormat();
      format.data = data;
      format.name = data.layout?.type ? `${data.layout.type} Layout` : format.name;
      renderAll();
    } catch (error) {
      alert("Ungültiges JSON");
    }
  });

  dom.exportS3.addEventListener("click", () => {
    const format = activeFormat();
    if (!format) return;
    const filename = `${slugify(format.name)}.json`;
    const payload = JSON.stringify(format.data, null, 2);
    const blob = new Blob([payload], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
    setExportStatus(`Exportiert: ${filename}`, "success");
  });

  dom.uploadIonos.addEventListener("click", async () => {
    const format = activeFormat();
    if (!format) return;
    const filename = `${slugify(format.name)}.json`;
    const payload = {
      name: filename,
      data: format.data,
    };

    setExportStatus("Upload laeuft...", "");
    try {
      const response = await fetch("http://localhost:5178/upload/krieta", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Upload fehlgeschlagen.");
      }
      setExportStatus(`Hochgeladen: ${filename}`, "success");
    } catch (error) {
      setExportStatus(
        `Upload fehlgeschlagen: ${error?.message || "Unbekannter Fehler"}`,
        "error"
      );
    }
  });
}

function updateZoneGeometry() {
  const zone = getSelectedZone();
  if (!zone) return;
  zone.position.x = `${dom.zoneX.value}%`;
  zone.position.y = `${dom.zoneY.value}%`;
  zone.size.width = `${dom.zoneW.value}%`;
  zone.size.height = `${dom.zoneH.value}%`;
  renderAll();
}

function getSelectedZone() {
  const format = activeFormat();
  if (!format) return null;
  return format.data.layout.zones.find((z) => z.id === state.selectedZoneId) || null;
}

function getSelectedOverlay() {
  const format = activeFormat();
  if (!format) return null;
  return (
    format.data.overlays || []
  ).find((o) => o.id === state.selectedOverlayId) || null;
}

function resolveZoneId() {
  const format = activeFormat();
  if (!format) return null;
  if (state.selectedZoneId) return state.selectedZoneId;
  const inputId = (dom.zoneId.value || "").trim();
  if (!inputId) return null;
  const exists = format.data.layout.zones.some((zone) => zone.id === inputId);
  if (!exists) return null;
  state.selectedZoneId = inputId;
  return inputId;
}

function resolveOverlayId() {
  const format = activeFormat();
  if (!format) return null;
  if (state.selectedOverlayId) return state.selectedOverlayId;
  const inputId = (dom.overlayId.value || "").trim();
  if (!inputId) return null;
  const exists = (format.data.overlays || []).some((overlay) => overlay.id === inputId);
  if (!exists) return null;
  state.selectedOverlayId = inputId;
  return inputId;
}

function bindActions() {
  dom.addFormat.addEventListener("click", addFormat);
  dom.deleteFormat.addEventListener("click", deleteFormat);
  dom.duplicateFormat.addEventListener("click", duplicateFormat);
  dom.addZone.addEventListener("click", addZone);
  dom.addOverlay.addEventListener("click", addOverlay);
  dom.deleteZone.addEventListener("click", deleteZone);
  dom.deleteOverlay.addEventListener("click", deleteOverlay);
  dom.refreshAssets.addEventListener("click", loadAssets);
  dom.assetSearch.addEventListener("input", () => {
    filterAssets();
    renderAssetList();
  });
}

function setExportStatus(message, type = "") {
  if (!dom.exportStatus) return;
  dom.exportStatus.textContent = message;
  dom.exportStatus.classList.remove("success", "error");
  if (type) dom.exportStatus.classList.add(type);
}

function slugify(value) {
  return (value || "krieta-format")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 64) || "krieta-format";
}

loadState();
bindActions();
bindInputs();
renderAll();
loadAssets();

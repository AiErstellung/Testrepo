const stateKey = "produsa_gui_state_v1";

const seedProject = {
  produsa_version: "1.0",
  input: {
    prompt: "Waldtiere erleben Abenteuer",
    requested_videos: 5,
    generator_prompt: "",
    style: "spannend",
    tone: "warm",
    platform: "tiktok",
    audience: "familien",
  },
  stories: [
    {
      story_id: "story_001",
      title: "Der neugierige Fuchs",
      hook: "Dieser Fuchs hat etwas Unglaubliches entdeckt",
      estimated_duration_sec: 18,
      scenes: [
        {
          scene_id: "s1",
          description: "Ein Fuchs schleicht neugierig durch einen nebligen Wald",
          visual_prompt: "realistic fox in misty forest, cinematic lighting",
          action: "Der Fuchs bleibt stehen und lauscht",
          voiceover: "Mitten im Wald hoerte der Fuchs ein seltsames Geraeusch.",
        },
        {
          scene_id: "s2",
          description: "Der Fuchs entdeckt ein glitzerndes Objekt",
          visual_prompt: "fox discovering glowing object on forest floor",
          action: "Er naehert sich vorsichtig",
          voiceover: "Was er dann sah, haette er nie erwartet.",
        },
      ],
      tags: ["tier", "abenteuer", "wald"],
      risk_flags: [],
      language: "de",
    },
  ],
  meta: {
    generated_at: "2026-01-30T12:00:00Z",
    model: "gpt-4.x",
    temperature: 0.8,
  },
};

const state = {
  project: null,
  selectedStoryId: null,
  selectedSceneId: null,
};

const dom = {
  storyList: document.getElementById("story-list"),
  addStory: document.getElementById("add-story"),
  deleteStory: document.getElementById("delete-story"),
  duplicateStory: document.getElementById("duplicate-story"),
  addScene: document.getElementById("add-scene"),
  deleteScene: document.getElementById("delete-scene"),
  projectTitle: document.getElementById("project-title"),
  projectSubtitle: document.getElementById("project-subtitle"),
  inputPrompt: document.getElementById("input-prompt"),
  inputCount: document.getElementById("input-count"),
  inputGenerator: document.getElementById("input-generator"),
  inputStyle: document.getElementById("input-style"),
  inputTone: document.getElementById("input-tone"),
  inputPlatform: document.getElementById("input-platform"),
  inputAudience: document.getElementById("input-audience"),
  storyId: document.getElementById("story-id"),
  storyTitle: document.getElementById("story-title"),
  storyHook: document.getElementById("story-hook"),
  storyDuration: document.getElementById("story-duration"),
  storyTags: document.getElementById("story-tags"),
  storyRisk: document.getElementById("story-risk"),
  storyLang: document.getElementById("story-lang"),
  sceneBoard: document.getElementById("scene-board"),
  sceneId: document.getElementById("scene-id"),
  sceneDesc: document.getElementById("scene-desc"),
  sceneVisual: document.getElementById("scene-visual"),
  sceneAction: document.getElementById("scene-action"),
  sceneVoice: document.getElementById("scene-voice"),
  sceneImage: document.getElementById("scene-image"),
  sceneImagePreview: document.getElementById("scene-image-preview"),
  regenerateSceneImage: document.getElementById("regenerate-scene-image"),
  metaGenerated: document.getElementById("meta-generated"),
  metaModel: document.getElementById("meta-model"),
  metaTemp: document.getElementById("meta-temp"),
  jsonOutput: document.getElementById("json-output"),
  copyJson: document.getElementById("copy-json"),
  importJson: document.getElementById("import-json"),
  exportJson: document.getElementById("export-json"),
  uploadIonos: document.getElementById("upload-ionos"),
  generateStories: document.getElementById("generate-stories"),
  exportStatus: document.getElementById("export-status"),
};

const tabs = Array.from(document.querySelectorAll(".tab"));
const tabContents = Array.from(document.querySelectorAll(".tab-content"));

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
      state.project = parsed.project || seedProject;
      state.selectedStoryId = parsed.selectedStoryId || state.project.stories[0]?.story_id;
      state.selectedSceneId = parsed.selectedSceneId || state.project.stories[0]?.scenes[0]?.scene_id;
      return;
    } catch (error) {
      console.warn("Failed to parse state", error);
    }
  }
  state.project = JSON.parse(JSON.stringify(seedProject));
  state.selectedStoryId = state.project.stories[0]?.story_id || null;
  state.selectedSceneId = state.project.stories[0]?.scenes[0]?.scene_id || null;
}

function saveState() {
  localStorage.setItem(
    stateKey,
    JSON.stringify({
      project: state.project,
      selectedStoryId: state.selectedStoryId,
      selectedSceneId: state.selectedSceneId,
    })
  );
}

function activeStory() {
  return state.project.stories.find((story) => story.story_id === state.selectedStoryId);
}

function activeScene() {
  const story = activeStory();
  if (!story) return null;
  return story.scenes.find((scene) => scene.scene_id === state.selectedSceneId);
}

function renderStoryList() {
  dom.storyList.innerHTML = "";
  state.project.stories.forEach((story) => {
    const item = document.createElement("li");
    item.className = "story-item";
    if (story.story_id === state.selectedStoryId) item.classList.add("active");
    item.textContent = story.title || story.story_id;
    item.addEventListener("click", () => {
      state.selectedStoryId = story.story_id;
      state.selectedSceneId = story.scenes[0]?.scene_id || null;
      renderAll();
    });
    dom.storyList.appendChild(item);
  });
}

function renderScenes() {
  const story = activeStory();
  dom.sceneBoard.innerHTML = "";
  if (!story) return;

  story.scenes.forEach((scene) => {
    const card = document.createElement("div");
    card.className = "scene-card";
    if (scene.scene_id === state.selectedSceneId) card.classList.add("selected");
    card.draggable = true;
    card.dataset.sceneId = scene.scene_id;

    if (scene.image_url) {
      const thumb = document.createElement("img");
      thumb.className = "scene-thumb";
      thumb.src = scene.image_url;
      thumb.alt = scene.scene_id;
      thumb.addEventListener("click", (event) => {
        event.stopPropagation();
        openImage(scene.image_url);
      });
      card.appendChild(thumb);
    } else {
      const placeholder = document.createElement("div");
      placeholder.className = "scene-thumb-placeholder";
      placeholder.textContent = "Kein Bild";
      card.appendChild(placeholder);
    }

    const title = document.createElement("div");
    title.className = "scene-title";
    title.textContent = scene.scene_id;

    const note = document.createElement("div");
    note.className = "scene-note";
    note.textContent = scene.description || "Keine Beschreibung";

    const meta = document.createElement("div");
    meta.className = "scene-meta";
    meta.textContent = scene.visual_prompt ? "Visual vorhanden" : "Kein Visual";

    card.appendChild(title);
    card.appendChild(note);
    card.appendChild(meta);

    const regenerate = document.createElement("button");
    regenerate.className = "btn";
    regenerate.type = "button";
    regenerate.textContent = "Bild neu";
    regenerate.addEventListener("click", (event) => {
      event.stopPropagation();
      regenerateSceneImage(scene);
    });
    card.appendChild(regenerate);

    card.addEventListener("click", () => {
      state.selectedSceneId = scene.scene_id;
      renderAll();
    });

    card.addEventListener("dragstart", (event) => {
      event.dataTransfer.setData("text/plain", scene.scene_id);
    });

    card.addEventListener("dragover", (event) => {
      event.preventDefault();
    });

    card.addEventListener("drop", (event) => {
      event.preventDefault();
      const draggedId = event.dataTransfer.getData("text/plain");
      reorderScenes(draggedId, scene.scene_id);
    });

    dom.sceneBoard.appendChild(card);
  });
}

function reorderScenes(fromId, toId) {
  const story = activeStory();
  if (!story || fromId === toId) return;
  const fromIndex = story.scenes.findIndex((scene) => scene.scene_id === fromId);
  const toIndex = story.scenes.findIndex((scene) => scene.scene_id === toId);
  if (fromIndex === -1 || toIndex === -1) return;
  const [moved] = story.scenes.splice(fromIndex, 1);
  story.scenes.splice(toIndex, 0, moved);
  state.selectedSceneId = moved.scene_id;
  renderAll();
}

function renderInputs() {
  const project = state.project;
  dom.projectTitle.textContent = "Produsa Batch";
  dom.projectSubtitle.textContent = `Stories: ${project.stories.length}`;

  dom.inputPrompt.value = project.input.prompt || "";
  dom.inputCount.value = project.input.requested_videos || 1;
  dom.inputGenerator.value = project.input.generator_prompt || "";
  dom.inputStyle.value = project.input.style || "";
  dom.inputTone.value = project.input.tone || "";
  dom.inputPlatform.value = project.input.platform || "";
  dom.inputAudience.value = project.input.audience || "";

  const story = activeStory();
  dom.storyId.value = story ? story.story_id : "";
  dom.storyTitle.value = story ? story.title : "";
  dom.storyHook.value = story ? story.hook : "";
  dom.storyDuration.value = story ? story.estimated_duration_sec || "" : "";
  dom.storyTags.value = story ? (story.tags || []).join(", ") : "";
  dom.storyRisk.value = story ? (story.risk_flags || []).join(", ") : "";
  dom.storyLang.value = story ? story.language || "" : "";

  const scene = activeScene();
  dom.sceneId.value = scene ? scene.scene_id : "";
  dom.sceneDesc.value = scene ? scene.description || "" : "";
  dom.sceneVisual.value = scene ? scene.visual_prompt || "" : "";
  dom.sceneAction.value = scene ? scene.action || "" : "";
  dom.sceneVoice.value = scene ? scene.voiceover || "" : "";
  dom.sceneImage.value = scene ? scene.image_url || "" : "";
  if (dom.sceneImagePreview) {
    dom.sceneImagePreview.src = scene?.image_url || "";
  }

  dom.metaGenerated.value = project.meta.generated_at || "";
  dom.metaModel.value = project.meta.model || "";
  dom.metaTemp.value = project.meta.temperature ?? "";

  dom.jsonOutput.value = JSON.stringify(project, null, 2);
}

function renderAll(save = true) {
  renderStoryList();
  renderScenes();
  renderInputs();
  if (save) saveState();
}

function addStory() {
  const index = state.project.stories.length + 1;
  const story = {
    story_id: `story_${String(index).padStart(3, "0")}`,
    title: `Neue Story ${index}`,
    hook: "",
    estimated_duration_sec: 15,
    scenes: [],
    tags: [],
    risk_flags: [],
    language: "de",
  };
  state.project.stories.push(story);
  state.selectedStoryId = story.story_id;
  state.selectedSceneId = null;
  renderAll();
}

function duplicateStory() {
  const story = activeStory();
  if (!story) return;
  const copy = JSON.parse(JSON.stringify(story));
  copy.story_id = `story_${Date.now()}`;
  copy.title = `${story.title} Copy`;
  state.project.stories.push(copy);
  state.selectedStoryId = copy.story_id;
  state.selectedSceneId = copy.scenes[0]?.scene_id || null;
  renderAll();
}

function deleteStory() {
  if (state.project.stories.length <= 1) return;
  state.project.stories = state.project.stories.filter(
    (story) => story.story_id !== state.selectedStoryId
  );
  state.selectedStoryId = state.project.stories[0]?.story_id || null;
  state.selectedSceneId = state.project.stories[0]?.scenes[0]?.scene_id || null;
  renderAll();
}

function addScene() {
  const story = activeStory();
  if (!story) return;
  const index = story.scenes.length + 1;
  const scene = {
    scene_id: `s${index}`,
    description: "",
    visual_prompt: "",
    action: "",
    voiceover: "",
  };
  story.scenes.push(scene);
  state.selectedSceneId = scene.scene_id;
  renderAll();
}

function deleteScene() {
  const story = activeStory();
  if (!story || !state.selectedSceneId) return;
  story.scenes = story.scenes.filter((scene) => scene.scene_id !== state.selectedSceneId);
  state.selectedSceneId = story.scenes[0]?.scene_id || null;
  renderAll();
}

function bindInputs() {
  dom.inputPrompt.addEventListener("input", (event) => {
    state.project.input.prompt = event.target.value;
    saveState();
  });

  dom.inputCount.addEventListener("input", (event) => {
    state.project.input.requested_videos = Number(event.target.value || 1);
    saveState();
  });

  dom.inputGenerator.addEventListener("input", (event) => {
    state.project.input.generator_prompt = event.target.value;
    saveState();
  });

  dom.inputStyle.addEventListener("input", (event) => {
    state.project.input.style = event.target.value;
    saveState();
  });

  dom.inputTone.addEventListener("input", (event) => {
    state.project.input.tone = event.target.value;
    saveState();
  });

  dom.inputPlatform.addEventListener("change", (event) => {
    state.project.input.platform = event.target.value;
    saveState();
  });

  dom.inputAudience.addEventListener("input", (event) => {
    state.project.input.audience = event.target.value;
    saveState();
  });

  dom.storyId.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.story_id = event.target.value;
    state.selectedStoryId = story.story_id;
    renderAll();
  });

  dom.storyTitle.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.title = event.target.value;
    renderAll();
  });

  dom.storyHook.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.hook = event.target.value;
    saveState();
  });

  dom.storyDuration.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.estimated_duration_sec = Number(event.target.value || 0);
    saveState();
  });

  dom.storyTags.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.tags = toList(event.target.value);
    saveState();
  });

  dom.storyRisk.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.risk_flags = toList(event.target.value);
    saveState();
  });

  dom.storyLang.addEventListener("input", (event) => {
    const story = activeStory();
    if (!story) return;
    story.language = event.target.value;
    saveState();
  });

  dom.sceneId.addEventListener("input", (event) => {
    const scene = activeScene();
    if (!scene) return;
    scene.scene_id = event.target.value;
    state.selectedSceneId = scene.scene_id;
    renderAll();
  });

  dom.sceneDesc.addEventListener("input", (event) => {
    const scene = activeScene();
    if (!scene) return;
    scene.description = event.target.value;
    saveState();
  });

  dom.sceneVisual.addEventListener("input", (event) => {
    const scene = activeScene();
    if (!scene) return;
    scene.visual_prompt = event.target.value;
    saveState();
  });

  dom.sceneAction.addEventListener("input", (event) => {
    const scene = activeScene();
    if (!scene) return;
    scene.action = event.target.value;
    saveState();
  });

  dom.sceneVoice.addEventListener("input", (event) => {
    const scene = activeScene();
    if (!scene) return;
    scene.voiceover = event.target.value;
    saveState();
  });

  dom.sceneImage.addEventListener("input", (event) => {
    const scene = activeScene();
    if (!scene) return;
    scene.image_url = event.target.value;
    renderAll();
  });

  dom.sceneImagePreview.addEventListener("click", () => {
    const scene = activeScene();
    if (!scene?.image_url) return;
    openImage(scene.image_url);
  });

  dom.metaGenerated.addEventListener("input", (event) => {
    state.project.meta.generated_at = event.target.value;
    saveState();
  });

  dom.metaModel.addEventListener("input", (event) => {
    state.project.meta.model = event.target.value;
    saveState();
  });

  dom.metaTemp.addEventListener("input", (event) => {
    state.project.meta.temperature = Number(event.target.value || 0);
    saveState();
  });

  dom.copyJson.addEventListener("click", () => {
    navigator.clipboard.writeText(dom.jsonOutput.value || "");
    setExportStatus("JSON kopiert", "success");
  });

  dom.importJson.addEventListener("click", () => {
    try {
      const data = JSON.parse(dom.jsonOutput.value || "{}");
      state.project = data;
      state.selectedStoryId = data.stories?.[0]?.story_id || null;
      state.selectedSceneId = data.stories?.[0]?.scenes?.[0]?.scene_id || null;
      renderAll();
      setExportStatus("JSON importiert", "success");
    } catch (error) {
      setExportStatus("Ungueltiges JSON", "error");
    }
  });

  dom.exportJson.addEventListener("click", () => {
    const payload = JSON.stringify(state.project, null, 2);
    const blob = new Blob([payload], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const filename = `produsa-${slugify(state.project.input.prompt)}.json`;
    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
    setExportStatus(`Exportiert: ${filename}`, "success");
  });

  dom.regenerateSceneImage.addEventListener("click", () => {
    const scene = activeScene();
    if (!scene) return;
    regenerateSceneImage(scene);
  });

  dom.generateStories.addEventListener("click", async () => {
    const prompt = (state.project.input.prompt || "").trim();
    const count = Number(state.project.input.requested_videos || 1);
    const generalPrompt = (state.project.input.generator_prompt || "").trim();

    if (!prompt) {
      setExportStatus("Bitte Prompt / Thema ausfuellen.", "error");
      return;
    }

    setExportStatus("Generiere Stories mit ChatGPT...", "");
    try {
      const response = await fetch("http://localhost:5178/produsa/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          prompt,
          count,
          general_prompt: generalPrompt,
          style: state.project.input.style,
          tone: state.project.input.tone,
          platform: state.project.input.platform,
          audience: state.project.input.audience,
          generate_images: true,
        }),
      });
      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Generierung fehlgeschlagen.");
      }
      const payload = await response.json();
      state.project.stories = payload.stories || [];
      state.project.meta.generated_at = new Date().toISOString();
      state.project.meta.model = payload.model || state.project.meta.model;
      state.project.meta.temperature = payload.temperature ?? state.project.meta.temperature;
      state.selectedStoryId = state.project.stories[0]?.story_id || null;
      state.selectedSceneId = state.project.stories[0]?.scenes?.[0]?.scene_id || null;
      renderAll();
      setExportStatus(
        `Generiert: ${state.project.stories.length} Stories`,
        "success"
      );
    } catch (error) {
      setExportStatus(
        `Generierung fehlgeschlagen: ${error?.message || "Fehler"}`,
        "error"
      );
    }
  });

  dom.uploadIonos.addEventListener("click", async () => {
    const filename = `produsa-${slugify(state.project.input.prompt)}.json`;
    const payload = {
      name: filename,
      data: state.project,
    };

    setExportStatus("Upload laeuft...", "");
    try {
      const response = await fetch("http://localhost:5178/upload/produsa", {
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

function bindActions() {
  dom.addStory.addEventListener("click", addStory);
  dom.deleteStory.addEventListener("click", deleteStory);
  dom.duplicateStory.addEventListener("click", duplicateStory);
  dom.addScene.addEventListener("click", addScene);
  dom.deleteScene.addEventListener("click", deleteScene);
}

function toList(value) {
  return (value || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function slugify(value) {
  return (value || "story-batch")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "")
    .slice(0, 48) || "story-batch";
}

function setExportStatus(message, type = "") {
  dom.exportStatus.textContent = message;
  dom.exportStatus.classList.remove("success", "error");
  if (type) dom.exportStatus.classList.add(type);
}

function openImage(url) {
  if (!url) return;
  window.open(url, "_blank", "noopener");
}

async function regenerateSceneImage(scene) {
  const story = activeStory();
  if (!story || !scene) return;
  setExportStatus(`Bild fuer ${scene.scene_id} wird generiert...`, "");
  try {
    const response = await fetch("http://localhost:5178/produsa/scene-image", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        theme: state.project.input.prompt,
        story: {
          story_id: story.story_id,
          title: story.title,
          hook: story.hook,
        },
        scene: {
          scene_id: scene.scene_id,
          description: scene.description,
          visual_prompt: scene.visual_prompt,
          action: scene.action,
          voiceover: scene.voiceover,
        },
        character_ref_key: story.character_ref_key || null,
      }),
    });
    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || "Bild-Generierung fehlgeschlagen.");
    }
    const payload = await response.json();
    if (payload.character_ref?.key) {
      story.character_ref_key = payload.character_ref.key;
      story.character_ref_url = payload.character_ref.url;
    }
    if (payload.scene_image?.url) {
      scene.image_url = payload.scene_image.url;
      scene.image_key = payload.scene_image.key;
    }
    renderAll();
    setExportStatus(`Bild aktualisiert: ${scene.scene_id}`, "success");
  } catch (error) {
    setExportStatus(
      `Bild-Generierung fehlgeschlagen: ${error?.message || "Fehler"}`,
      "error"
    );
  }
}

loadState();
bindActions();
bindInputs();
renderAll();

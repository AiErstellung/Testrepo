IONOS Upload Bridge
===================

Lokaler HTTP-Server, der Krieta/Produsa-JSON direkt per AWS S3 API zu IONOS pusht.

Start
-----
1) Abhaengigkeiten installieren:
   npm install

2) Server starten:
   node server.js

Optional kann der Port gesetzt werden:
   set UPLOAD_BRIDGE_PORT=5178

Der Server liest die IONOS Daten aus:
   ELMON/APP/local.properties

Wenn du andere Werte willst, kannst du diese env vars setzen:
   ELMON_STORAGE_ACCESS_KEY
   ELMON_STORAGE_SECRET_KEY
   ELMON_STORAGE_SESSION_TOKEN
   ELMON_STORAGE_REGION
   ELMON_STORAGE_BUCKET
   ELMON_STORAGE_ENDPOINT

Assets
------
GET http://localhost:5178/assets/sync?prefix=Assets/Videos
laedt alle Video-Assets in den lokalen Cache.

GET http://localhost:5178/assets/videos
liefert eine Liste der lokal gecachten Assets mit lokalen URLs.

OpenAI
------
Lege `tools/upload-bridge/local.properties` an (siehe `local.properties.example`) und setze:
  OPENAI_API_KEY
  OPENAI_MODEL
  OPENAI_TEMPERATURE
  OPENAI_BASE_URL
  OPENAI_IMAGE_MODEL
  OPENAI_IMAGE_SIZE
  OPENAI_IMAGE_QUALITY
  OPENAI_IMAGE_BACKGROUND

# mejourney-server

Backend server for **[MeJourney](https://github.com/merail/mejourney)** — a travel-blog Android app by [merail](https://github.com/merail). The server stores the posts (covers and their content), serves them to the app, and hosts the post images.

## Overview

- Kotlin/JVM, built on [Ktor](https://ktor.io) (Netty engine) with `kotlinx.serialization`.
- A single SQLite database (`mejourney.db`) with two tables:
  - `cover(id, year, country, place, title, description, imageUrl)`;
  - `content(id, cover_id, title, body)`.
- Serves the post images as static files under `/images`; `/content` lists them sorted by the trailing number in the filename.
- Runs behind nginx in production; nginx checks the `X-Mejourney-Token` header on requests (the app sends it on every request).

## Endpoints

| Route | Response |
|-------|----------|
| `GET /health` (and `HEAD`) | `200 OK` |
| `GET /images/...` | Static files served from the configured images directory |
| `GET /covers` | JSON array of covers (`CoverDto`) |
| `GET /content?coverId=<id>` | JSON content with `imagesUrls`; `400` if `coverId` is missing, `404` if not found |

## Requirements

- JDK 17+ (the Gradle wrapper downloads Gradle 9.2.1).

## Build & run

```bash
./gradlew test                                              # Run the tests
./gradlew build                                             # Build everything
./gradlew buildFatJar                                       # Executable fat jar (build/libs/mejourney-server-all.jar)
./gradlew run                                               # Run with application.yaml (production paths)
./gradlew run --args="-config=application-dev.yaml"         # Run with application-dev.yaml (local data/)
```

On a successful start the server listens on port `8080`.

## Configuration

Configuration lives in `src/main/resources/application.yaml` (production, the default) and `application-dev.yaml` (local development, loaded with `-config=application-dev.yaml`):

| Key | Production | Dev |
|-----|------------|-----|
| `ktor.deployment.port` | `8080` | `8080` |
| `mejourney.db.path` | `/opt/mejourney/data/mejourney.db` | `data/mejourney.db` |
| `mejourney.images.path` | `/opt/mejourney/images` | `images` |

`data/` and `images/` are gitignored; local SQLite files and images live there in dev.

## Project structure

```
src/main/kotlin/
├── Application.kt              # Entry point (EngineMain) and module wiring
├── Routing.kt                  # HTTP routes
├── Serialization.kt            # ContentNegotiation + JSON
├── db/
│   └── DatabaseFactory.kt      # SQLite connection
├── dto/
│   └── Dtos.kt                 # CoverDto(id, year, country, place, title, description, imageUrl)
│                               # ContentDto(id, coverId, title, body, imagesUrls)
└── repository/
    └── HomeRepository.kt       # Cover and content queries
```

## Deployment

Production runs on an Ubuntu 24.04 VPS (`mejourney.duckdns.org`) behind nginx. Ktor listens only on `127.0.0.1:8080`; nginx is the sole entry point and checks the `X-Mejourney-Token` header. The jar is run by the systemd service `mejourney.service` from `/opt/mejourney/app/mejourney.jar`; the SQLite database lives at `/opt/mejourney/data/mejourney.db` and the images at `/opt/mejourney/images`. SQLite backups run nightly into `/opt/mejourney/backups/`. The nginx, systemd, and backup files are kept in [`deploy/`](deploy/README.md).

## Related

- [MeJourney](https://github.com/merail/mejourney) — the Android app this server backs.

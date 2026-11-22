# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MeJourney Server is the backend for the [MeJourney](https://github.com/merail/mejourney) Android travel-blog app (author [merail](https://github.com/merail)). It is a small Kotlin/JVM service built on Ktor (Netty engine) with a single SQLite database, serving the posts (covers and their content) and the post images to the app.

- **Stack**: Kotlin 2.2.21, Ktor 3.3.2 (Netty), kotlinx-serialization, SQLite (sqlite-jdbc 3.46.0.0), Logback 1.5.25
- **Build**: Gradle 9.2.1 (wrapper), JDK 17+
- **Public GitHub**: https://github.com/merail/mejourney-server (branch `main`)
- **Companion Android app**: https://github.com/merail/mejourney
- **Production**: Ubuntu 24.04 VPS (`mejourney.duckdns.org`) behind nginx; Ktor listens on `127.0.0.1:8080`

## Build Commands

```bash
./gradlew test                                              # Run the tests
./gradlew build                                             # Build everything
./gradlew buildFatJar                                       # Executable fat jar (build/libs/mejourney-server-all.jar)
./gradlew run                                               # Run with application.yaml (production paths)
./gradlew run --args="-config=application-dev.yaml"         # Run with application-dev.yaml (local data/)
```

## Architecture

Single-module app (`group = "app.mejourney"`, `version = "0.0.1"`). The entry point is Ktor's `EngineMain`; the module `app.mejourney.ApplicationKt.module` is declared in `application.yaml`.

| File | Role |
|------|------|
| `Application.kt` | `main()` → `EngineMain`; `module()` initializes `DatabaseFactory`, then `configureRouting()` and `configureSerialization()` |
| `Routing.kt` | All HTTP routes (`/health`, `/images` static files, `/covers`, `/content`) |
| `Serialization.kt` | ContentNegotiation + kotlinx-serialization JSON (pretty-printed, UTF-8) |
| `db/DatabaseFactory.kt` | Opens the SQLite connection (`jdbc:sqlite:<path>`) from `mejourney.db.path` |
| `repository/HomeRepository.kt` | DB queries for covers and content |
| `dto/Dtos.kt` | `CoverDto`, `ContentDto` |

The DB path and the images root come from config (`mejourney.db.path`, `mejourney.images.path`).

## Database

A single SQLite database, opened as a plain JDBC connection at startup (`DriverManager`).

- `cover(id, year, country, place, title, description, imageUrl)`
- `content(id, cover_id → cover.id, title, body)`

## Server API

Auth is enforced by nginx, not Ktor: requests must carry a valid `X-Mejourney-Token` header (the app sends it on every request).

| Route | Response |
|-------|----------|
| `GET /health` (and `HEAD`) | `200 OK` (plain text `OK` for `GET`) |
| `GET /images/...` | Static files served from `mejourney.images.path` |
| `GET /covers` | JSON array of `CoverDto` |
| `GET /content?coverId=<id>` | JSON `ContentDto` with `imagesUrls` filled in; `400` if `coverId` is missing, `404` if not found |

`/content` lists the image files under `images/contents/<content.id>/`, keeps only supported extensions (`jpg`, `jpeg`, `png`, `gif`, `webp`, `heic`), and sorts them by the trailing number in the filename.

## Configuration

`src/main/resources/application.yaml` (production, loaded by default) and `application-dev.yaml` (local development, loaded with `-config=application-dev.yaml`):

- `ktor.deployment.host` — `127.0.0.1` in production (not set in dev, so `0.0.0.0`)
- `ktor.deployment.port` — `8080`
- `mejourney.db.path` — SQLite file path (`/opt/mejourney/data/mejourney.db` in production, `data/mejourney.db` in dev)
- `mejourney.images.path` — images root (`/opt/mejourney/images` in production, `images/` in dev)

`data/` and `images/` are gitignored; local files live there in dev.

## Deployment

Production runs on an Ubuntu 24.04 VPS at `mejourney.duckdns.org`, behind nginx:

- nginx is the sole entry point and checks the `X-Mejourney-Token` header.
- Ktor listens only on `127.0.0.1:8080`.
- The jar runs as the systemd service `mejourney.service` from `/opt/mejourney/app/mejourney.jar`.
- DB: `/opt/mejourney/data/mejourney.db`; images: `/opt/mejourney/images`.
- SQLite backups run nightly (03:35 UTC) into `/opt/mejourney/backups/` (kept 30 days).

The nginx, systemd, and backup files are kept in `deploy/README.md`, which also documents the apply steps and the server↔repo file mapping. Real token values are never committed.

## Code Style

- Kotlin official code style (`kotlin.code.style=official`)
- Code comments: minimal, English only
- Commit messages: single line, English, no body / no Co-Authored-By

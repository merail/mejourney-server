# mejourney-server

Backend server for **[MeJourney](https://github.com/merail/mejourney)** — a travel-blog Android app by [merail](https://github.com/merail). It stores the posts (covers and their content) and serves them, along with the post images, to the app.

## Overview

- Kotlin/JVM on [Ktor](https://ktor.io) (Netty engine) with `kotlinx.serialization`.
- A single SQLite database (`mejourney.db`) with two tables: `cover` (the posts) and `content` (their bodies).
- Serves the post images as static files under `/images`.

## Endpoints

| Route | Response |
|-------|----------|
| `GET /health` | `200 OK` |
| `GET /images/...` | Static files from the configured images directory |
| `GET /covers` | JSON array of covers |
| `GET /content?coverId=<id>` | JSON content with `imagesUrls`; `400` if `coverId` is missing, `404` if not found |

All endpoints except `/health` require authentication, enforced by nginx in production.

## Requirements

- JDK 17+ (the Gradle wrapper downloads Gradle 9.2.1).

## Build & run

```bash
./gradlew test                                              # Run the tests
./gradlew build                                             # Build everything
./gradlew buildFatJar                                       # Executable fat jar (build/libs/mejourney-server-all.jar)
./gradlew run                                               # Run the server
./gradlew run --args="-config=application-dev.yaml"         # Run with the dev configuration
```

## Configuration

Configuration lives in `src/main/resources/application.yaml` (production, the default) and `application-dev.yaml` (local development, loaded with `-config=application-dev.yaml`).

## Deployment

Production runs behind nginx; see [`deploy/`](deploy/README.md) for the server configuration.

# Server configuration

Configuration for the production VPS (Ubuntu 24.04, `mejourney.duckdns.org`). The VPS also hosts wordme.

| Repository                               | Server                                    |
|------------------------------------------|-------------------------------------------|
| `nginx/conf.d/mejourney.conf`            | `/etc/nginx/conf.d/mejourney.conf`        |
| `nginx/sites-available/mejourney`        | `/etc/nginx/sites-available/mejourney`, symlinked from `sites-enabled` |
| `nginx/secrets/*.map.example`            | `/etc/nginx/secrets/*.map` (root, `700` dir, `600` files) |
| `systemd/mejourney.service`              | `/etc/systemd/system/mejourney.service`   |
| `backup/backup_sqlite.sh`                | `/opt/mejourney/backup_sqlite.sh`         |
| `backup/sqlite-backups.cron`             | a line in `/etc/cron.d/sqlite-backups`, shared with wordme |

## Secrets

Token values are never committed. `/etc/nginx/secrets/mejourney-token.map` holds one line in the format of its `.example` file, included by `nginx/conf.d/mejourney.conf`.

## Shared parts of `/etc/nginx/nginx.conf`

Not in the repository, since wordme uses them too:

- `geo $ip_allowed`: requests from `127.0.0.1` pass without `X-Mejourney-Token`.
- `limit_req_zone ... zone=api_light` (10 r/m per IP) and `zone=api_medium` (60 r/m per IP).

## Access rules

- `/covers`, `/content` and `/images/` return 403 without a valid `X-Mejourney-Token`.
- `/images/` is served directly by nginx (`alias /opt/mejourney/images/`) with `expires 30d` and immutable `Cache-Control`, not proxied through Ktor.
- `/covers` uses `api_light` (burst 5); `/content` uses `api_medium` (burst 10).
- Ktor listens on `127.0.0.1:8080` only, so it is reachable through nginx alone.

## Deploying

```sh
./gradlew buildFatJar
scp build/libs/mejourney-server-all.jar wordme:/tmp/
ssh wordme 'mv /tmp/mejourney-server-all.jar /opt/mejourney/app/mejourney.jar && systemctl restart mejourney'
```

Check `/health` afterwards.

## Applying nginx changes

```sh
scp deploy/nginx/conf.d/mejourney.conf wordme:/etc/nginx/conf.d/mejourney.conf
scp deploy/nginx/sites-available/mejourney wordme:/etc/nginx/sites-available/mejourney
ssh wordme 'nginx -t && systemctl reload nginx'
```

Back up the server file first (`<file>.bak-<date>` next to it).

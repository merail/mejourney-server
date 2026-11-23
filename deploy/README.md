# Server configuration

Files from the production VPS (Ubuntu 24.04).

| Repository                               | Server                                    |
|------------------------------------------|-------------------------------------------|
| `nginx/conf.d/mejourney.conf`            | `/etc/nginx/conf.d/mejourney.conf`        |
| `nginx/sites-available/mejourney`        | `/etc/nginx/sites-available/mejourney`, symlinked from `sites-enabled` |
| `nginx/secrets/*.map.example`            | `/etc/nginx/secrets/*.map` (root, `700` dir, `600` files) |
| `systemd/mejourney.service`              | `/etc/systemd/system/mejourney.service`   |
| `backup/backup_sqlite.sh`                | `/opt/mejourney/backup_sqlite.sh`         |
| `backup/sqlite-backups.cron`             | a line in `/etc/cron.d/sqlite-backups`, shared with another site |

## Secrets

Token values are never committed. Each `/etc/nginx/secrets/*.map` holds one line in the format of its `.example` file.

## Shared parts of `/etc/nginx/nginx.conf`

Not in the repository, since they are shared with another site on the host:

- a `geo` block that marks trusted source IPs;
- a shared `limit_req_zone` used by both sites.

## Access rules

- `/covers`, `/content` and `/images/` return 403 without a valid `X-Mejourney-Token`.
- Ktor listens on `127.0.0.1:8080` only, so it is reachable through nginx alone.

## Applying changes

```sh
scp deploy/nginx/conf.d/mejourney.conf wordme:/etc/nginx/conf.d/mejourney.conf
scp deploy/nginx/sites-available/mejourney wordme:/etc/nginx/sites-available/mejourney
ssh wordme 'nginx -t && systemctl reload nginx'
```

Back up the server file first (`<file>.bak-<date>` next to it).

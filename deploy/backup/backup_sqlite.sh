#!/bin/bash
set -e

DB_PATH="/opt/mejourney/data/mejourney.db"
BACKUP_DIR="/opt/mejourney/backups"
KEEP_DAYS=30

mkdir -p "$BACKUP_DIR"

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M")
BACKUP_FILE="$BACKUP_DIR/mejourney_$TIMESTAMP.db"

sqlite3 "$DB_PATH" ".backup '$BACKUP_FILE'"

echo "Backup created: $BACKUP_FILE"

find "$BACKUP_DIR" -name 'mejourney_*.db' -mtime +$KEEP_DAYS -delete

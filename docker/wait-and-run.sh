#!/bin/bash
set -e

host="${DB_HOST:-mysql}"
port="${DB_PORT:-3306}"

echo "Waiting for MySQL at ${host}:${port}..."
until bash -lc ">/dev/tcp/${host}/${port}" 2>/dev/null; do
  sleep 2
done
echo "MySQL is ready."

exec catalina.sh run

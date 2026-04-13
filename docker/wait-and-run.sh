#!/bin/bash
set -e

host="${DB_HOST:-mysql}"
port="${DB_PORT:-3306}"
app_port="${PORT:-8080}"

echo "Waiting for MySQL at ${host}:${port}..."
until bash -lc ">/dev/tcp/${host}/${port}" 2>/dev/null; do
  sleep 2
done
echo "MySQL is ready."

if [ -f /usr/local/tomcat/conf/server.xml ]; then
  sed -i "0,/port=\"8080\"/s/port=\"8080\"/port=\"${app_port}\"/" /usr/local/tomcat/conf/server.xml
fi

exec catalina.sh run

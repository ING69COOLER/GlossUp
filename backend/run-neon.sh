#!/usr/bin/env bash
# Arranca el backend apuntando a la base de datos Neon.
# Lee las credenciales de .env (no versionado) y ejecuta Spring Boot.
set -euo pipefail
cd "$(dirname "$0")"

if [ ! -f .env ]; then
  echo "ERROR: falta el archivo .env con las credenciales de Neon." >&2
  exit 1
fi

set -a
# shellcheck disable=SC1091
source .env
set +a

echo "Conectando a: ${GLOSSUP_DB_URL}"
exec ./mvnw spring-boot:run

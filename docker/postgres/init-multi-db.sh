#!/bin/bash
# Crea multiples bases de datos a partir de la variable POSTGRES_MULTIPLE_DATABASES.
# Las BD se separan por coma. Se crean con el usuario indicado en POSTGRES_USER,
# asignandole ownership al usuario por defecto para que Flyway pueda operar sin
# permisos extra.
set -e
set -u

if [ -n "${POSTGRES_MULTIPLE_DATABASES:-}" ]; then
  echo "Multiple databases requested: $POSTGRES_MULTIPLE_DATABASES"
  for db in $(echo "$POSTGRES_MULTIPLE_DATABASES" | tr ',' ' '); do
    echo "  Creating database '$db'"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        CREATE DATABASE "$db";
        GRANT ALL PRIVILEGES ON DATABASE "$db" TO "$POSTGRES_USER";
EOSQL
  done
  echo "All databases created."
fi

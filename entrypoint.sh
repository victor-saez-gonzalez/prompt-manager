#!/bin/bash
set -e

echo "Starting PostgreSQL..."
su postgres -c "/usr/lib/postgresql/15/bin/pg_ctl -D /var/lib/postgresql/data -w start"

echo "Creating database if it doesn't exist..."
su postgres -c "psql -tc \"SELECT 1 FROM pg_database WHERE datname = '$POSTGRES_DB'\" | grep -q 1 || psql -c \"CREATE DATABASE $POSTGRES_DB\""

echo "Waiting for PostgreSQL to be available..."
until pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  sleep 1
done

echo "PostgreSQL is up. Starting Spring Boot..."
exec java -jar /app/app.jar

#!/bin/bash

##################################   CONFIGURATIONS   ##################################

PROJECT="instagram"
ENV="development"
DATABASE_NAME="${PROJECT}_${ENV}"
MIGRATION_SCRIPT_PATH="migrate.sql"
USERS_CSV_PATH="data/users.csv"
MEDIA_CSV_PATH="data/media.csv"

##################################  HELPER FUNCTIONS  ##################################

function exit_if_unsuccessful_last_command {
  if [[ "$?" == '1' ]]; then
    exit 1
  fi
}

################################## TERMINATION CHECKS ##################################

# Check if PostgreSQL is installed
command -v psql >/dev/null 2>&1 || { echo >&2 "❌  Cannot create database: PostgreSQL is not installed"; exit 1; }

##################################       LOGIC        ##################################

echo "💥  Dropping database..."
# Drop PostgreSQL database
dropdb --if-exists $DATABASE_NAME
exit_if_unsuccessful_last_command

echo "✨  Creating new database..."
# Create new PostgreSQL database
createdb $DATABASE_NAME
exit_if_unsuccessful_last_command

echo "📦  Migrating database..."
# Run migrations
psql $DATABASE_NAME --file $MIGRATION_SCRIPT_PATH --quiet
exit_if_unsuccessful_last_command

echo "📦  Seeding database..."
# Run seeds
psql $DATABASE_NAME --command "\COPY users (id, username, email, password_hash, is_private, full_name, created_at) FROM '$USERS_CSV_PATH' (FORMAT CSV);" --quiet
psql $DATABASE_NAME --command "\COPY media (id, class, url, created_at) FROM '$MEDIA_CSV_PATH' (FORMAT CSV);" --quiet
exit_if_unsuccessful_last_command

echo "✅  Done!"

#!/usr/bin/env bash

PORT=${1:-12345}

cities=("Iasi" "New York" "Barcelona")

ZIP_PATH="TEST_ZIP/test.zip"

if ! [[ "$PORT" =~ ^[0-9]+$ ]]; then
  echo "ERROR: '$PORT' is not a valid number"
  exit 1
fi

echo "----------------------------------------"

RANDOM_OPTION=$(( (RANDOM % 4) + 1 ))
EXTRA_ARG=""

if [ $RANDOM_OPTION -eq 3 ]; then
  RANDOM_NUMBER=$(( RANDOM % 3 ))
  EXTRA_ARG="${cities[$RANDOM_NUMBER]}"
elif [ $RANDOM_OPTION -eq 4 ]; then
  EXTRA_ARG="$ZIP_PATH"
fi

exec java -jar client.jar "$PORT" "$RANDOM_OPTION" "$EXTRA_ARG"

echo "----------------------------------------"

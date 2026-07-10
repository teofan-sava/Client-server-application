#!/usr/bin/env bash

PORT=${1:-8080}

cities=("Iasi" "New York" "Barcelona")

ZIP_PATH="/home/teofan/test.zip"

if ! [[ "$PORT" =~ ^[0-9]+$ ]]; then
  echo "ERROR: '$PORT' is not a valid number"
  exit 1
fi

TOTAL_CLIENTS=100

echo "It launches $TOTAL_CLIENTS instances"
echo "Instances run in non-interactive mode"
echo "----------------------------------------"

for ((i=1; i<=TOTAL_CLIENTS; i++))
do
  RANDOM_OPTION=$(( (RANDOM % 4) + 1 ))
  EXTRA_ARG=""

  if [ $RANDOM_OPTION -eq 3 ]; then
    RANDOM_NUMBER=$(( RANDOM % 3 ))
    EXTRA_ARG="${cities[$RANDOM_NUMBER]}"
  elif [ $RANDOM_OPTION -eq 4 ]; then
    EXTRA_ARG="$ZIP_PATH"
  fi
  ./gradlew run --args="$PORT $RANDOM_OPTION $EXTRA_ARG" > /dev/null 2>&1 &
done

echo "----------------------------------------"
echo "All $TOTAL_CLIENTS instances started on port $PORT!"

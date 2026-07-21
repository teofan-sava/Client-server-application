#!/usr/bin/env bash

IMAGE_NAME="summerpractice/client:v1.0"
PORT=12345

echo "Spinning up 10 client containers..."

for i in {1..10}; do
  docker run -d \
    --rm \
    --name "client-$i" \
    --network my-app-network \
    -e PORT="$PORT" \
    -e SERVER_HOST="server-container" \
    "$IMAGE_NAME"

  echo "Started client-$i"
done

echo "All 10 containers are running in the background!"

#!/usr/bin/env bash

if [ -z "$1" ]; then
  echo "ERROR: The port number wasn't specified"
  echo "Usage: $0 <PORT>"
  exit 1
fi

PORT=$1

if ! [[ "$PORT" =~ ^[0-9]+$ ]]; then
  echo "ERROR: '$PORT' is not a valid number"
  exit 1
fi

echo "The server starts on port $PORT"
echo "Press CTRL+C to stop the server"
echo "-------------------------------"

exec java -jar server.jar "$PORT"

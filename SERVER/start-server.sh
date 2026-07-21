#!/usr/bin/env bash

docker run --rm -d --name server-container --network my-app-network summerpractice/server:v1.0

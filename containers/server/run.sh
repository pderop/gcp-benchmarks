#!/usr/bin/env bash

[[ $# -ne 3 ]] && echo "Usage: $0 <projectid> <bucket name> <app name>" && exit 1

PROJECT_ID=$1
BUCKET=$2
APP=$3

echo "Starting server for Project ID: ${PROJECT_ID} Bucket: ${BUCKET} App: ${APP}"

gsutil cp "gs://${BUCKET}/apps/${APP}.jar" .
java -jar ${APP}.jar

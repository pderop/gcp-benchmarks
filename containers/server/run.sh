#!/usr/bin/env bash

[[ $# -ne 4 ]] && echo "Usage: $0 <projectid> <bucket name> <app name>" && exit 1

PROJECT_ID=$1
BUCKET=$2
APP=$3
PROTOCOL=$4

echo "Starting server for Project ID: ${PROJECT_ID} Bucket: ${BUCKET} App: ${APP} Protocol: ${PROTOCOL}"

gsutil cp "gs://${BUCKET}/apps/${APP}.jar" .
java -DPROTOCOL=$PROTOCOL -jar ${APP}.jar

echo "Server exit ($?)"
exit 0


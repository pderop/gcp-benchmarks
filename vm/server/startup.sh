#!/usr/bin/env bash

set -ev

PROJECTID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
BUCKET=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/BUCKET" -H "Metadata-Flavor: Google")

echo "Project ID: ${PROJECTID} Bucket: ${BUCKET} Application"

sudo apt-get update
sudo apt-get install -yq openjdk-17-jdk
sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java

gsutil cp gs://${BUCKET}/apps/*.jar /tmp/

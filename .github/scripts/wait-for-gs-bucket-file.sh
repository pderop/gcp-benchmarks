#!/bin/bash

# wait for some files to be put in a google cloud storage bucket entry

if [ $# -ne 2 ]; then
  echo "Usage: $0 <bucket url path> <max-wait in minutes>"
  exit 1
fi

bucket=$1
found_total=0
# since we poll every 10 seconds, adjust the max-wait param
max_wait=$(expr $2 \* 6)

for ((i = 1; i <= $max_wait; i++)); do
  found=$(gsutil ls $bucket 2>/dev/null | wc -l)
  if [ "$found" -gt 0 ]; then
    exit 0
  fi
  sleep 3
done

echo "Timeout while waiting for $bucket"
exit 1

#!/bin/bash

# wait for some files to be put in a google cloud storage bucket entry

if [ $# -ne 3 ]; then
  echo "Usage: $0 <bucket url path> <num-of-expected-files> <max-wait in minutes>"
  exit 1
fi

bucket=$1
expected=$2
found_total=0
# since we poll every 10 seconds, adjust the max-wait param
max_wait=$(expr $3 \* 6)

for ((i = 1; i <= $max_wait; i++)); do
  found=$(gsutil ls $bucket 2>/dev/null | wc -l)
  if [ "$found" -gt 0 ]; then
    found_total=$(expr $found_total + $found)
    if [ $found_total -ge $expected ]; then
      exit 0
    fi
  fi
  sleep 10
done

echo "Timeout (entries found=$found_total)"
exit 1

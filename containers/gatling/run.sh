#!/usr/bin/env bash

set -v

SERVER_HOST=$1
SERVER_PORT=$2
SIMULATIONS=$3
INCREMENT=$4
STEPS=$5
DURATION=$6
APP_NAME=$7
PROTOCOL=$8
BENCHDIR=${9:-/opt/bench}

echo "Running simulations: ${SIMULATIONS} for application $APP_NAME"
java -version
mkdir -p $BENCHDIR/results

# initialize our custom json output (we will fill it for each simulation results, see below)
echo "[]" > $BENCHDIR/results/gh-benchmark.json

# Create a tmp file that will be used to create the json file)
tmp=$(mktemp)
trap 'rm -f "$tmp"' EXIT

# Server is expected to have started the app, but check it's available
curl http://$SERVER_HOST:$SERVER_PORT/text \
      --silent \
      --fail \
      --location \
      --retry 30 \
      --retry-connrefused \
      --retry-delay 6 \
      --retry-max-time 300

# Now, start gatling for this app
for simulation in $(echo ${SIMULATIONS} | tr ";" "\n"); do
  JOPTS="-DHOST=${SERVER_HOST} -DPORT=${SERVER_PORT} -DINCREMENT=${INCREMENT} -DSTEPS=${STEPS} -DDURATION=${DURATION} -DPROTOCOL=${PROTOCOL}"

  name="$APP_NAME-$simulation"
  mean=$(java ${JOPTS} -jar gatling-*-all.jar "$name" $simulation | grep "mean requests/sec"|awk  '{print $4}')
  unit="mean requests/sec"
  value="$mean"

  cat $BENCHDIR/results/gh-benchmark.json | jq --argjson name '"'"$name"'"' --argjson unit '"'"$unit"'"' --argjson value "$value" '. += [{
                      "name": $name,
                      "unit": $unit,
                      "value": $value
                 }]' > $tmp
  cp $tmp $BENCHDIR/results/gh-benchmark.json

  simulation_lower=$(echo "$simulation" | tr '[:upper:]' '[:lower:]')
  rm -f test-reports/$simulation_lower*/simulation.log
  mkdir -p $BENCHDIR/results/bench/$APP_NAME/$simulation
  find test-reports
  mv test-reports/$simulation_lower*/* $BENCHDIR/results/bench/$APP_NAME/$simulation/
done

cd $BENCHDIR/results/
tar zcf bench-${APP_NAME}.tgz bench gh-benchmark.json

# all done, copy results into bucket
gsutil cp $BENCHDIR/results/bench-${APP_NAME}.tgz gs://${BUCKET}/results/

# ask the server to exit
curl -s http://$SERVER_HOST:$SERVER_PORT/exit
exit 0


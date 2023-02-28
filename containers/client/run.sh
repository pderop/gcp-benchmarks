#!/usr/bin/env bash

set -v

SERVER_HOST=$1
SIMULATIONS=$2
INCREMENT=$3
STEPS=$4
DURATION=$5
APP=$6
BENCHDIR=${7:-/opt/bench}

echo "Running simulations: ${SIMULATIONS} for application $APP"
mkdir -p $BENCHDIR/results

# initialize our custom json output (we will fill it for each simulation results, see below)
echo "[]" > $BENCHDIR/results/gh-benchmark.json

# Create a tmp file that will be used to create the json file)
tmp=$(mktemp)
trap 'rm -f "$tmp"' EXIT

# Server is expected to have started the app, but check it's available
curl http://$SERVER_HOST/text \
      --silent \
      --fail \
      --location \
      --retry 30 \
      --retry-connrefused \
      --retry-delay 6 \
      --retry-max-time 300

# Now, start gatling for this app
for simulation in $(echo ${SIMULATIONS} | tr ";" "\n"); do
  export JAVA_OPTS="-DbaseUrl=http://${SERVER_HOST} -Dincrement=${INCREMENT} -Dsteps=${STEPS} -Dduration=${DURATION}"
  mean=$($BENCHDIR/gatling/bin/gatling.sh --run-description "Benchmark for ${APP}:${simulation}" -s ${simulation} -bm --run-mode local | grep "mean requests/sec"|awk  '{print $4}')

  name="$APP-$simulation"
  unit="mean requests/sec"
  value="$mean"

  cat $BENCHDIR/results/gh-benchmark.json | jq --argjson name '"'"$name"'"' --argjson unit '"'"$unit"'"' --argjson value "$value" '. += [{
                      "name": $name,
                      "unit": $unit,
                      "value": $value
                 }]' > $tmp
  cp $tmp $BENCHDIR/results/gh-benchmark.json

  simulation_lower=$(echo "$simulation" | tr '[:upper:]' '[:lower:]')
  rm -f $BENCHDIR/gatling/results/$simulation_lower*/simulation.log
  mkdir -p $BENCHDIR/results/bench/$APP/$simulation
  mv $BENCHDIR/gatling/results/$simulation_lower*/* $BENCHDIR/results/bench/$APP/$simulation/
done

cd $BENCHDIR/results/
tar zcf bench.tgz bench

# all done, copy results into bucket
gsutil cp $BENCHDIR/results/bench.tgz gs://${BUCKET}/results/
gsutil cp $BENCHDIR/results/gh-benchmark.json gs://${BUCKET}/results/


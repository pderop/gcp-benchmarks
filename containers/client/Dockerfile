FROM google/cloud-sdk:alpine

RUN apk add --no-cache openjdk17 unzip jq


# Install client application
WORKDIR /opt/bench/

COPY gatling-1.0-SNAPSHOT-all.jar /opt/bench/
COPY run.sh /opt/bench/

# Replace with inline env variables when running the container
ENV SERVER_HOST=localhost
ENV SERVER_PORT=8080
ENV SIMULATIONS="TextPlain"
ENV INCREMENT=128
ENV STEPS=8
ENV BUCKET=default
ENV DURATION=20
ENV APP=default
ENV PROTOCOL=H1

CMD ["sh", "-c", "/opt/bench/run.sh ${SERVER_HOST} ${SERVER_PORT} ${SIMULATIONS} ${INCREMENT} ${STEPS} ${DURATION} ${APP} ${PROTOCOL}"]
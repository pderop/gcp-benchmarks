# Stage 1: Install OpenJDK 19
FROM eclipse-temurin:19-alpine As openjdk

# Stage 2: Install Google Cloud-SDK as the base image
FROM google/cloud-sdk:alpine

# Copy OpenJDK 19 files from the previous stage
COPY --from=openjdk /opt/java/openjdk /opt/java/openjdk

# Install "jq" tool (for building json files from bash)
RUN apk add --no-cache unzip jq

# Install client application
WORKDIR /opt/bench/

COPY gatling-1.0-SNAPSHOT-all.jar /opt/bench/
COPY run.sh /opt/bench/

# Replace with inline env variables when running the container
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$PATH:$JAVA_HOME/bin"
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
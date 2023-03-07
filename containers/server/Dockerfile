# Stage 1: Install OpenJDK 19
FROM eclipse-temurin:19-alpine As openjdk

FROM google/cloud-sdk:alpine

# Copy OpenJDK 19 files from the previous stage
COPY --from=openjdk /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$PATH:$JAVA_HOME/bin"

# Install client application
WORKDIR /opt/bench/

COPY run.sh /opt/bench/

ENV PROJECT_ID=default
ENV BUCKET=default
ENV APP=default
ENV PROTOCOL=H1

EXPOSE 8080

CMD ["sh", "-c", "/opt/bench/run.sh ${PROJECT_ID} ${BUCKET} ${APP} ${PROTOCOL}"]
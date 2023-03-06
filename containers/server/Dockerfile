FROM eclipse-temurin:19-alpine

RUN apk add --no-cache openjdk19 unzip

# Install client application
WORKDIR /opt/bench/

COPY run.sh /opt/bench/

ENV PROJECT_ID=default
ENV BUCKET=default
ENV APP=default
ENV PROTOCOL=H1

EXPOSE 8080

CMD ["sh", "-c", "/opt/bench/run.sh ${PROJECT_ID} ${BUCKET} ${APP} ${PROTOCOL}"]
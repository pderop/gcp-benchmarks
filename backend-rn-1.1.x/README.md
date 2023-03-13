## How to run the application

```shell
./gradlew run
```

This command will run the application with
- Reactor Core 3.5.3
- Reactor Netty 1.1.3
- HTTP/1.1 as a supported protocol

## How to change Reactor Core/Netty dependency version

```shell
./gradlew run -PforceReactorNettyVersion=1.1.4-SNAPSHOT
```

```shell
./gradlew run -PforceReactorCoreVersion=3.5.4-SNAPSHOT
```

## How to enable HTTP/2

```shell
./gradlew run -Phttp2
```

By default, the server is configured to support `HTTP/1.1`

## How to change the port on which the server listens

```shell
./gradlew run -Pport=7070
```

By default, the server listens on port `9090`.

## How to change the server host

```shell
./gradlew run -Phost=x.x.x.x
```

By default, the server host is configured to be `127.0.0.1`.
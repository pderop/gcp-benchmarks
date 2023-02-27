# Servers shootout board
## Results

| Application  | TextPlain | Trends |
| ---  | :---: | :---: |
| reactornetty-benchmark-1.0.x-SNAPSHOT | [**result**](/tmp/gcp-benchmarks/bench/reactornetty-benchmark-1.0.x-SNAPSHOT/TextPlain/index.html) | [**result**](/tmp/gcp-benchmarks/bench/reactornetty-benchmark-1.0.x-SNAPSHOT/Trends/index.html) |

## Scenario

Each benchmark case starts with no traffic and does the following:

- increase the concurrency by 128 users (1 users = 1 connection) in 10 seconds
- hold that concurrency level for 20 seconds
- go to 1), unless the maximum concurrency of 4096 is reached

## Benchmark cases
- PlainText: server sends "text/plain" response body
- Echo: clients sends "text/plain" and the server echoes that in the response
- JsonGet: server sends a JSON payload
- JsonPost: client sends a JSON payload, server deserializes it and replies with a JSON payload
- HtmlGet: the server renders an HTML view with a templating engine

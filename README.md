# Servers shootout board
## Results

| Application  | TextPlain | Trends |
| ---  | :---: | :---: |
| benchmark-rn-1.1.x-H1 | [**result**](bench/benchmark-rn-1.1.x-H1/TextPlain/index.html) | [**result**](bench/benchmark-rn-1.1.x-H1/Trends/index.html) |

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

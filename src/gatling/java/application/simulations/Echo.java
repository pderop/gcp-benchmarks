package application.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Echo extends Simulation {
	static final String HOST = System.getProperty("HOST", "127.0.0.1");
	static final String PROTOCOL = System.getProperty("PROTOCOL", "H1");
	static final int PORT = Integer.parseInt(System.getProperty("PORT", "8080"));
	static final int INCREMENT = Integer.parseInt(System.getProperty("INCREMENT", "128"));
	static final int STEPS = Integer.parseInt(System.getProperty("STEPS", "32"));
	static final int DURATION = Integer.parseInt(System.getProperty("DURATION", "1"));

	{
		String scheme = switch (PROTOCOL) {
			case "H1", "H2C" -> "http";
			case "H1S", "H2" -> "https";
			default -> throw new IllegalArgumentException("Invalid protocol: " + PROTOCOL);
		};

		boolean isHTTP2 = switch (PROTOCOL) {
			case "H2", "H2C" -> true;
			default -> false;
		};

		HttpProtocolBuilder httpProtocolBuilder =
				http.baseUrl(scheme + "://" + HOST + ":" + PORT)
						.acceptHeader("text/plain")
						.acceptLanguageHeader("en-US,en;q=0.5")
						.acceptEncodingHeader("gzip, deflate")
						.userAgentHeader("Gatling");

		String body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer at orci mollis, accumsan ligula elementum, auctor dui. Donec eleifend risus vel turpis egestas, vitae pretium metus tincidunt. Proin aliquet ipsum vel dolor rutrum semper. Morbi et sem eu lorem egestas ullamcorper non a massa.";

		ScenarioBuilder scenarioBuilder =
				scenario("Send text to an echo endpoint")
						.forever().on(exec(http("echo").post("/echo").header("Content-Type", "text/plain").body(StringBody(body))));

		if (isHTTP2) {
			httpProtocolBuilder = httpProtocolBuilder.enableHttp2();
		}

		setUp(scenarioBuilder
				.injectClosed(
						incrementConcurrentUsers(INCREMENT) //32x8 (256), 128x8 (1024), 512x8 (4096)
								.times(STEPS)
								.eachLevelLasting(Duration.ofSeconds(20))
								.separatedByRampsLasting(Duration.ofSeconds(10))
								.startingFrom(0)))
				.maxDuration(Duration.ofMinutes(DURATION))
				.protocols(httpProtocolBuilder);
	}
}

package application.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.incrementConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class HtmlGet extends Simulation {
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
						.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
						.acceptLanguageHeader("en-US,en;q=0.5")
						.acceptEncodingHeader("gzip, deflate")
						.userAgentHeader("Gatling");

		ScenarioBuilder scenarioBuilder =
				scenario("Get HTML view")
						.forever().on(exec(http("htmlGet").get("/home")));

		if (isHTTP2) {
			httpProtocolBuilder = httpProtocolBuilder.enableHttp2();
		}

		// by default, generate closed workload with level 0, 128, 256, ... 4096)
		// each level lasting 20 seconds separated by ramps lasting 10 seconds
		setUp(scenarioBuilder
				.injectClosed(
						incrementConcurrentUsers(INCREMENT)
								.times(STEPS)
								.eachLevelLasting(Duration.ofSeconds(20))
								.separatedByRampsLasting(Duration.ofSeconds(10))
								.startingFrom(0)))
				.maxDuration(Duration.ofMinutes(DURATION))
				.protocols(httpProtocolBuilder);
	}
}

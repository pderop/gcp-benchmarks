package application;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.javaapi.core.Simulation;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {

  static String runDescription;

  public static void main(String[] args) {
    if (args.length < 1) {
      throw new IllegalArgumentException("Usage: Application <run description> simulation1 simulation2 ...");
    }

    runDescription = args[0];
    String[] simulationsArgs = Arrays.copyOfRange(args, 1, args.length);

    final Set<String> simulations =
        findAllSimulations()
            .filter(simulation -> args.length == 1 || Arrays.stream(simulationsArgs).anyMatch(simulation::endsWith))
            .collect(Collectors.toSet());

    if (simulations.isEmpty()) throw new RuntimeException("Unable to find any simulation to run");
    System.out.println("Will run simulations: " + simulations);

    simulations.forEach(Application::runGatlingSimulation);
  }

  private static Stream<String> findAllSimulations() {
    final String packageName = Application.class.getPackageName();
    System.out.printf("Finding simulations in %s package%n", packageName);

    final Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
    return reflections.getSubTypesOf(Simulation.class).stream().map(Class::getName);
  }

  private static void runGatlingSimulation(String simulationFileName) {
    System.out.printf("Starting %s simulation (DURATION=%s)%n", simulationFileName, System.getProperty("DURATION"));
    final GatlingPropertiesBuilder gatlingPropertiesBuilder = new GatlingPropertiesBuilder();

    gatlingPropertiesBuilder.simulationClass(simulationFileName);
    gatlingPropertiesBuilder.runDescription(runDescription);
    gatlingPropertiesBuilder.resultsDirectory("test-reports");
    try {
      Gatling.fromMap(gatlingPropertiesBuilder.build());
    } catch (Exception exception) {
      System.err.printf(
          "Something went wrong for simulation %s %s%n", simulationFileName, exception);
    }
  }
}

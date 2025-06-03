package dev.marcgil.vanilla.logger;

import java.lang.management.ManagementFactory;
import java.util.Locale;

public class StartupLogger {

  private final long jvmStartMillis;
  private final long appStartNanos;

  public StartupLogger() {
    this.jvmStartMillis = ManagementFactory.getRuntimeMXBean().getStartTime();
    this.appStartNanos = System.nanoTime();
  }

  public void logStartedApplication() {
    long appEndNanos = System.nanoTime();
    long currentMillis = System.currentTimeMillis();

    double appStartupSeconds = (appEndNanos - appStartNanos) / 1_000_000_000.0;
    double processUptimeSeconds = (currentMillis - jvmStartMillis) / 1000.0;

    System.out.printf(Locale.US,
        "Started Application in %.3f seconds (process running for %.3f)%n", appStartupSeconds,
        processUptimeSeconds
    );
  }

}

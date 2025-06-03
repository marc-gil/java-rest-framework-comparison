package dev.marcgil.vanilla.docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class DockerComposeRunner {

  private final File moduleRoot;

  public DockerComposeRunner() {
    String moduleRootPath = Paths.get("vanilla-java").toAbsolutePath().toString();
    this.moduleRoot = new File(moduleRootPath);
  }

  public void dockerComposeUp() {
    System.out.println("Running docker-compose in: " + moduleRoot.getAbsolutePath());
    if (!new File(moduleRoot, "docker-compose.yaml").exists()) {
      System.err.println("docker-compose.yaml NOT FOUND at " + moduleRoot.getAbsolutePath());
      return;
    }
    executeDockerComposeUp();
    Runtime.getRuntime().addShutdownHook(new Thread(this::executeDockerComposeStop));
  }

  private void executeDockerComposeUp() {
    ProcessBuilder builder = new ProcessBuilder("docker-compose", "up", "-d", "--wait");
    builder.directory(moduleRoot);
    builder.redirectErrorStream(true);
    try {
      int exitCode = executeDockerComposeCommand(builder);
      if (exitCode != 0) {
        System.err.println("docker-compose exited with code: " + exitCode);
        throw new RuntimeException("docker-compose couldn't be started");
      } else {
        System.out.println("docker-compose started successfully.");
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void executeDockerComposeStop() {
    ProcessBuilder builder = new ProcessBuilder("docker-compose", "stop");
    builder.directory(moduleRoot);
    builder.redirectErrorStream(true);
    try {
      int exitCode = executeDockerComposeCommand(builder);
      if (exitCode != 0) {
        System.err.println("docker-compose exited with code: " + exitCode);
        throw new RuntimeException("docker-compose couldn't be stopped");
      } else {
        System.out.println("docker-compose stopped successfully.");
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private int executeDockerComposeCommand(ProcessBuilder builder)
      throws IOException, InterruptedException {
    Process process = builder.start();

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println("[docker-compose] " + line);
      }
    }

    return process.waitFor();
  }

}

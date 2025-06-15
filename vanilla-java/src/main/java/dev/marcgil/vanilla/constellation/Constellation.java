package dev.marcgil.vanilla.constellation;

import java.util.Objects;

public class Constellation {

  private int id;
  private final String name;
  private final String hemisphere;
  private final String description;

  public Constellation(int id, String name, String hemisphere, String description) {
    this.id = id;
    this.name = name;
    this.hemisphere = hemisphere;
    this.description = description;
  }

  public int getId() {
    return id;
  }

  void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getHemisphere() {
    return hemisphere;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Constellation that = (Constellation) o;
    return id == that.id && Objects.equals(name, that.name) && Objects.equals(
        hemisphere, that.hemisphere) && Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, hemisphere, description);
  }

}

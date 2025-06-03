package dev.marcgil.vanilla.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class Json {

  private Json() {
  }

  public static <T> T to(String json, Class<T> clazz) {
    try {
      json = json.trim();
      if (json.startsWith("{")) {
        json = json.substring(1);
      }
      if (json.endsWith("}")) {
        json = json.substring(0, json.length() - 1);
      }

      Map<String, String> keyValueMap = new HashMap<>();
      for (String pair : json.split(",")) {
        String[] kv = pair.split(":", 2);
        if (kv.length == 2) {
          String key = kv[0].trim().replaceAll("^\"|\"$", "");
          String value = kv[1].trim().replaceAll("^\"|\"$", "");
          keyValueMap.put(key, value);
        }
      }

      T obj = clazz.getDeclaredConstructor().newInstance();

      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        String fieldName = field.getName();
        if (keyValueMap.containsKey(fieldName)) {
          String value = keyValueMap.get(fieldName);
          Class<?> type = field.getType();

          Object parsedValue = switch (type.getSimpleName()) {
            case "int", "Integer" -> Integer.parseInt(value);
            case "double", "Double" -> Double.parseDouble(value);
            case "boolean", "Boolean" -> Boolean.parseBoolean(value);
            case "String" -> value;
            default -> null;
          };

          if (parsedValue != null) {
            field.set(obj, parsedValue);
          }
        }
      }
      return obj;
    } catch (Exception e) {
      throw new RuntimeException("Error parsing JSON to object", e);
    }
  }

  public static String from(Object obj) {
    StringBuilder json = new StringBuilder();
    json.append("{");

    Field[] fields = obj.getClass().getDeclaredFields();

    boolean first = true;
    for (Field field : fields) {
      field.setAccessible(true); // Allows access to private fields
      try {
        Object value = field.get(obj);
        if (value != null) {
          if (!first) {
            json.append(",");
          }
          json.append("\"").append(field.getName()).append("\":");
          if (value instanceof String stringValue) {
            json.append("\"").append(escapeJson(stringValue)).append("\"");
          } else {
            json.append(value);
          }
          first = false;
        }
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Error converting to JSON", e);
      }
    }

    json.append("}");
    return json.toString();
  }

  private static String escapeJson(String value) {
    return value.replace("\"", "\\\"");
  }

}

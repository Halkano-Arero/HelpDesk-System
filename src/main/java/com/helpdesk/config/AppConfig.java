package com.helpdesk.config;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPERTIES = loadProperties();
    private static final Properties DOT_ENV = loadDotEnv();

    private AppConfig() {
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String envValue = lookupEnvironmentValue(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        String dotenvValue = DOT_ENV.getProperty(key);
        if (dotenvValue != null && !dotenvValue.isBlank()) {
            return dotenvValue.trim();
        }

        String propertyValue = PROPERTIES.getProperty(key);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        return propertyValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("helpdesk.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ignored) {
            // Defaults still work through environment variables and system properties.
        }
        return properties;
    }

    private static Properties loadDotEnv() {
        Properties properties = new Properties();
        for (Path candidate : candidateEnvFiles()) {
            if (candidate == null || !Files.isRegularFile(candidate)) {
                continue;
            }

            try (Reader reader = Files.newBufferedReader(candidate)) {
                properties.load(reader);
                break;
            } catch (IOException ignored) {
                // If one candidate fails, keep trying the others.
            }
        }
        return properties;
    }

    private static String lookupEnvironmentValue(String key) {
        String direct = System.getenv(key);
        if (direct != null && !direct.isBlank()) {
            return direct;
        }

        String envKey = toEnvironmentKey(key);
        String converted = System.getenv(envKey);
        if (converted != null && !converted.isBlank()) {
            return converted;
        }

        return null;
    }

    private static Path[] candidateEnvFiles() {
        return new Path[] {
            pathFromProperty("helpdesk.env.file"),
            pathFromEnvironment("HELPDESK_ENV_FILE"),
            pathFromEnvironment("HELPDESK_ENV"),
            pathFromCatalinaBase("conf", "helpdesk.env"),
            pathFromUserDir(".env")
        };
    }

    private static Path pathFromProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        return value == null || value.isBlank() ? null : Paths.get(value.trim());
    }

    private static Path pathFromEnvironment(String envName) {
        String value = System.getenv(envName);
        return value == null || value.isBlank() ? null : Paths.get(value.trim());
    }

    private static Path pathFromUserDir(String fileName) {
        String userDir = System.getProperty("user.dir");
        return userDir == null || userDir.isBlank() ? null : Paths.get(userDir, fileName);
    }

    private static Path pathFromCatalinaBase(String first, String second) {
        String catalinaBase = System.getProperty("catalina.base");
        return catalinaBase == null || catalinaBase.isBlank() ? null : Paths.get(catalinaBase, first, second);
    }

    private static String toEnvironmentKey(String key) {
        StringBuilder builder = new StringBuilder();
        char previous = 0;

        for (int i = 0; i < key.length(); i++) {
            char current = key.charAt(i);
            if (current == '.') {
                builder.append('_');
                previous = current;
                continue;
            }
            if (Character.isUpperCase(current) && i > 0 && previous != '_' && previous != '.') {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(current));
            previous = current;
        }

        return builder.toString();
    }
}

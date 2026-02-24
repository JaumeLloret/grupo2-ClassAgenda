package com.classagendag2.shared.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class EnvLoader {
    private static final String ENV_FILE_NAME = ".env";
    private static final Map<String, String> ENV_VALUES = new HashMap<>();

    // Se ejecuta automáticamente al cargar la clase
    static {
        loadEnvFile();
    }

    private EnvLoader() {}

    private static void loadEnvFile() {
        java.io.File envFile = new java.io.File(ENV_FILE_NAME);
        if (!envFile.exists()) {
            return;
        }

        // BufferReader ayuda a leer rápido línea a línea
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE_NAME))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                currentLine = currentLine.trim();

                // Ignoramos líneas vacías o comentarios
                if (currentLine.isEmpty() || currentLine.startsWith("#")) {
                    continue;
                }

                // Separamos la clave del valor
                String[] keyAndValue = currentLine.split("=", 2);
                if (keyAndValue.length == 2) {
                    String key = keyAndValue[0].trim();
                    String value = keyAndValue[1].trim();
                    ENV_VALUES.put(key, value);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se puede leer el archivo .env", exception);
        }
    }

    public static String getRequired(String key) {
        // Obtenemos la variable, pero si falta lanzamos un error que detenga la aplicación
        String value = ENV_VALUES.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la clave en el .env: " + key);
        }
        return value;
    }
}
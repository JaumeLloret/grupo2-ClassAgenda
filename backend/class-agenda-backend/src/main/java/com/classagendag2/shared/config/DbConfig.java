package com.classagendag2.shared.config;

import com.classagendag2.shared.utilities.EnvLoader;

public final class DbConfig {
    private DbConfig() {}

    // Pide la variable y si no existe la app falla rápido
    public static String url() {
        return EnvLoader.getRequired("CLASSAGENDA_DB_URL");
    }
    public static String user() {
        return EnvLoader.getRequired("CLASSAGENDA_DB_USER");
    }
    public static String password() {
        return EnvLoader.getRequired("CLASSAGENDA_DB_PASSWORD");
    }
}
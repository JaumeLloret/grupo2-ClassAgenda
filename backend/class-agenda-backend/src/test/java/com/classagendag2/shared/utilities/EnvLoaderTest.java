package com.classagendag2.shared.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvLoaderTest {
    @Test
    void getRequired_throwsException_whenKeyDoesNotExist() {
        // Verifica que salta IllegalStateException si pedimos algo que no existe
        assertThrows(IllegalStateException.class, () -> EnvLoader.getRequired("THIS_KEY_DOES_NOT_EXIST"));
    }
}
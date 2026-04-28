package com.classagendag2.features.task.presentation.dto;

public final class TaskRequestDto {
    public String title;
    public String description;
    public String priority;
    // ¡Nota importante! No pedimos el ownerId en el JSON.
    // El ownerId lo sacaremos de forma segura de las cabeceras HTTP de la petición, para que nadie pueda falsificarlo.
}

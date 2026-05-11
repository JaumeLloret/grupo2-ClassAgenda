package com.classagendag2.features.event.domain.model;

public enum PermissionLevel {
    READ,  // Puede ver el evento, pero no puede alterarlo
    EDIT   // Puede ver y modificar título/fechas, pero NO puede borrarlo ni compartirlo
}
package com.classagendag2.features.event.presentation.dto;

public final class ShareRequestDto {
    public Long targetUserId;  // El ID del compañero al que queremos invitar
    public String permission;  // "READ" o "EDIT"
}
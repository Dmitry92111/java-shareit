package ru.practicum.shareit.gateway.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateRequest {
    @NotBlank
    private String description;
}

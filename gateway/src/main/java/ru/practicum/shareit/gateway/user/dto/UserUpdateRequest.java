package ru.practicum.shareit.gateway.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;

    @Email
    private String email;
}

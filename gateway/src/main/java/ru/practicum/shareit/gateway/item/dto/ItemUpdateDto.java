package ru.practicum.shareit.gateway.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateDto {

    private String name;
    private String description;
    private Boolean available;

    @Positive
    private Long requestId;
}


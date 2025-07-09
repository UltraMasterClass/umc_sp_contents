package com.umc.sp.contents.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private UUID id;
    private String code;
    private String description;
    private GenreDto parent;
}

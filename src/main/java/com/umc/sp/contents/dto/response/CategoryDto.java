package com.umc.sp.contents.dto.response;

import com.umc.sp.contents.persistence.model.type.CategoryType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    private CategoryType type;
    private CategoryDto parent;
    private String description;
    private String code;
}

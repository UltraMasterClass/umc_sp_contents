package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.CategoryDto;
import com.umc.sp.contents.persistence.model.Category;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;

@Component
public class CategoryMapper {

    public Optional<CategoryDto> convertToDto(final Category category) {
        if (isNull(category)) {
            return Optional.empty();
        }

        var parentCategory = convertToDto(category.getParent());
        return Optional.of(CategoryDto.builder()
                                      .id(category.getId().getId())
                                      .type(category.getType())
                                      .code(category.getCode())
                                      .description(category.getDescription())
                                      .parent(parentCategory.orElse(null))
                                      .build());
    }
}

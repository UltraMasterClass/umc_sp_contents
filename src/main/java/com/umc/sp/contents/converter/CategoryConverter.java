package com.umc.sp.contents.converter;

import com.umc.sp.contents.controller.dto.response.CategoryDto;
import com.umc.sp.contents.persistence.model.Category;
import org.springframework.stereotype.Component;
import static java.util.Objects.nonNull;

@Component
public class CategoryConverter {

    public CategoryDto convertToDto(final Category category) {
        var parentCategory = nonNull(category.getParent()) ? convertToDto(category.getParent()) : null;
        return CategoryDto.builder()
                          .id(category.getId().getId())
                          .type(category.getType())
                          .code(category.getCode())
                          .description(category.getDescription())
                          .parent(parentCategory)
                          .build();
    }
}

package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.CategoryDto;
import com.umc.sp.contents.persistence.model.Category;
import org.springframework.stereotype.Component;
import static java.util.Objects.nonNull;

@Component
public class CategoryMapper {

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

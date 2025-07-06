package com.umc.sp.contents.controller.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDetailDto extends ContentDto{
    private CategoryDto category;
    private GenreDto genre;
    private List<TagDto> tags;
}

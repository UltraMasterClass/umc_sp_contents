package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.GenreDto;
import com.umc.sp.contents.persistence.model.Genre;
import org.springframework.stereotype.Component;
import static java.util.Objects.nonNull;

@Component
public class GenreMapper {

    public GenreDto convertToDto(final Genre genre) {
        var parent = nonNull(genre.getParent()) ? convertToDto(genre.getParent()) : null;
        return GenreDto.builder().id(genre.getId().getId()).code(genre.getCode()).description(genre.getDescription()).parent(parent).build();
    }
}

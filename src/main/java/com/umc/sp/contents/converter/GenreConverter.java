package com.umc.sp.contents.converter;

import com.umc.sp.contents.controller.dto.response.GenreDto;
import com.umc.sp.contents.persistence.model.Genre;
import org.springframework.stereotype.Component;
import static java.util.Objects.nonNull;

@Component
public class GenreConverter {

    public GenreDto convertToDto(final Genre genre) {
        var parent = nonNull(genre.getParent()) ? convertToDto(genre) : null;
        return GenreDto.builder().id(genre.getId().getId()).code(genre.getCode()).description(genre.getDescription()).parent(parent).build();
    }
}

package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.GenreDto;
import com.umc.sp.contents.persistence.model.Genre;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;

@Component
public class GenreMapper {

    public Optional<GenreDto> convertToDto(final Genre genre) {
        if (isNull(genre)) {
            return Optional.empty();
        }
        var parent = convertToDto(genre.getParent());
        return Optional.of(GenreDto.builder()
                                   .id(genre.getId().getId())
                                   .code(genre.getCode())
                                   .description(genre.getDescription())
                                   .parent(parent.orElse(null))
                                   .build());
    }
}

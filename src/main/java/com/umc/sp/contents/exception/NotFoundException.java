package com.umc.sp.contents.exception;

import com.umc.sp.contents.persistence.model.id.ContentId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final ContentId contentId) {
        super(String.format("Content with id %s not found",contentId));
    }
}

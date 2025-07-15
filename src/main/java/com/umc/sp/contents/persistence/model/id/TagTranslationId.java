package com.umc.sp.contents.persistence.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagTranslationId implements Serializable {

    @Column(name = "tag_id")
    private TagId tagId;

    @Column(name = "language_code")
    private String languageCode;
}

package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.id.LanguageCode;
import com.umc.sp.contents.persistence.model.id.SubtitleId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subtitles")
public class Subtitle {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private SubtitleId id;

    @Column(name = "content_id", nullable = false)
    @Convert(converter = ContentId.DbConverter.class)
    private ContentId contentId;

    @Column(name = "language_code", nullable = false)
    @Convert(converter = LanguageCode.DbConverter.class)
    private LanguageCode languageCode;

    @Column(name = "priority", nullable = false)
    private int priority;
}

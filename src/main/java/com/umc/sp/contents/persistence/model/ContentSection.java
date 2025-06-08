package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import com.umc.sp.contents.persistence.model.type.ContentSectionSortType;
import com.umc.sp.contents.persistence.model.type.ContentSectionType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "content_sections")
public class ContentSection {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private ContentSectionId id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentSectionType type;

    @Column(name = "title")
    private String title;

    @Column(name = "title_code", nullable = false, length = 50)
    private String titleCode;

    @Column(name = "number_of_elements", nullable = false)
    private int numberOfElements;

    @Column(name = "sort_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentSectionSortType sortType;
}

package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentSectionCriteriaId;
import com.umc.sp.contents.persistence.model.id.ContentSectionId;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_section_criteria")
public class ContentSectionCriteria {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private ContentSectionCriteriaId id;

    @Column(name = "content_section_id", nullable = false)
    @Convert(converter = ContentSectionId.DbConverter.class)
    private ContentSectionId contentSectionId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentSectionCriteriaType type;

    @Column(name = "relation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentSectionCriteriaRelationType relationType;

    @Column(name = "reference_ids")
    private String referenceIds;

    @Column(name = "disabled_date")
    private LocalDateTime disabledDate;
}
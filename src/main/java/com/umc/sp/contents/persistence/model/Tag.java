package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.TagId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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
@Table(name = "tags")
public class Tag {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private TagId id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "description")
    private String description;
}

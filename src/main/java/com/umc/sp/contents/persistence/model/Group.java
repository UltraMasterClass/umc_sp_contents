package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.CategoryId;
import com.umc.sp.contents.persistence.model.id.GroupId;
import com.umc.sp.contents.persistence.model.type.GroupType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "groups")
public class Group {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private GroupId id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GroupType type;

    @Column(name = "category_id")
    @Convert(converter = CategoryId.DbConverter.class)
    private CategoryId categoryId;

    @Column(name = "featured", nullable = false)
    private boolean featured;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "gender_id", nullable = false)
    private Genders genders;

    @Column(name = "episodes")
    private Integer episodes;

    @Column(name = "duration", length = 20)
    private String duration;

}

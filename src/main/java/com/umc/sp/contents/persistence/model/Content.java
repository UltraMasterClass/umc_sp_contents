package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contents")
public class Content {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private ContentId id;

    @Column(name = "featured", nullable = false)
    private boolean featured;

    @Column(name = "type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Column(name = "structure_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ContentStructureType structureType;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(name = "speciality_id", nullable = false)
    private UUID specialityId;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentInfo> contentInfos;

    @ManyToMany
    @JoinTable(
            name = "content_categories",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
}

package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.type.ContentType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "content")
public class Content {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private ContentId id;

    @Column(name = "featured", nullable = false)
    private boolean featured;

    @Column(name = "type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ContentType type;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;

    @Column(name = "especialidad_id", nullable = false)
    private UUID especialidadId;

    @Column(name = "resource_url", nullable = false)
    private String resourceUrl;

    @Column(name = "cdn_url")
    private String cdnUrl;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "duration", length = 20)
    private String duration;
}

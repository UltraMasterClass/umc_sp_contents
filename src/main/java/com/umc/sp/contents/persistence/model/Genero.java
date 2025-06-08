package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.GeneroId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "generos")
public class Genero {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private GeneroId id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Genero parent;
}

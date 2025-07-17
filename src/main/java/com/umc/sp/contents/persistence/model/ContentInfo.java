package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentInfoId;
import com.umc.sp.contents.persistence.model.type.ContentInfoType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_info")
public class ContentInfo {

    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(name = "id"))
    private ContentInfoId id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentInfoType type;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "disabled_date")
    private LocalDateTime disabledDate;

}

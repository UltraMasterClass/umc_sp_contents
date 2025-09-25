package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentTagId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
@Table(name = "content_tags")
public class ContentTag {

    @EmbeddedId
    private ContentTagId id;

    @Column(name = "disabled_date")
    private LocalDateTime disabledDate;
}

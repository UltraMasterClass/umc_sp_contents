package com.umc.sp.contents.persistence.model;

import com.umc.sp.contents.persistence.model.id.ContentGroupId;
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
@Table(name = "content_groups")
public class ContentGroup {

    @EmbeddedId
    private ContentGroupId id;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "disabled_date")
    private LocalDateTime disabledDate;
}
